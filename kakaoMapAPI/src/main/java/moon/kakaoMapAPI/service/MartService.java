package moon.kakaoMapAPI.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import moon.kakaoMapAPI.dto.MartResponseDto;
import moon.kakaoMapAPI.dto.MartDataDto;
import moon.kakaoMapAPI.dto.MartLocationDto;
import moon.kakaoMapAPI.entity.Mart;
import moon.kakaoMapAPI.repository.MartRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MartService {
    private final WebClient webClient;
    private final MartRepository martRepository;

    public Flux<MartResponseDto> findAndSaveMarts(String address, int radius) {
        return findLocation(address)
                .flatMapMany(location -> searchMarts(
                        location.getLatitude(),
                        location.getLongitude(),
                        radius))
                .flatMap(this::saveMart);
    }

    public Mono<MartLocationDto> findLocation(String address) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/v2/local/search/address.json")
                        .queryParam("query", address)
                        .build())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(this::convertToMartLocationDto);
    }

    private MartLocationDto convertToMartLocationDto(JsonNode jsonNode) {
        return Optional.ofNullable(jsonNode)
                .map(node -> node.get("documents"))
                .filter(JsonNode::isArray)
                .filter(documents -> !documents.isEmpty())
                .map(documents -> documents.get(0))
                .map(this::parseDocumentToLocationDto)
                .orElse(null);
    }

    private MartLocationDto parseDocumentToLocationDto(JsonNode document) {
        double latitude = document.get("y").asDouble();
        double longitude = document.get("x").asDouble();
        return new MartLocationDto(latitude, longitude);
    }

    public Flux<MartDataDto> searchMarts(double latitude, double longitude, int radius) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/v2/local/search/category.json")
                        .queryParam("category_group_code", "MT1")
                        .queryParam("radius", radius)
                        .queryParam("x", longitude)
                        .queryParam("y", latitude)
                        .queryParam("sort", "distance")
                        .build())
                .retrieve()
                .bodyToFlux(JsonNode.class)
                .flatMapIterable(this::convertToMartJoinContentDtos);
    }

    private List<MartDataDto> convertToMartJoinContentDtos(JsonNode jsonNode) {
        List<MartDataDto> marts = new ArrayList<>();
        if (jsonNode.has("documents")) {
            for (JsonNode document : jsonNode.get("documents")) {
                MartDataDto mart = new MartDataDto(
                        document.get("id").asText(),
                        document.get("distance").asText(),
                        document.get("place_name").asText(),
                        document.get("address_name").asText(),
                        document.get("road_address_name").asText(),
                        document.get("phone").asText()
                );
                marts.add(mart);
            }
        }
        return marts;
    }

    public Mono<MartResponseDto> saveMart(MartDataDto contentDto) {
        return Mono.fromCallable(() -> {
            Mart mart = Mart.builder()
                    .martName(contentDto.getAddress())
                    .address(contentDto.getRoadAddress())
                    .build();
            Mart savedMart = martRepository.save(mart);
            return new MartResponseDto(savedMart.getMartName(), savedMart.getAddress());
        }).subscribeOn(Schedulers.boundedElastic());
    }
}
