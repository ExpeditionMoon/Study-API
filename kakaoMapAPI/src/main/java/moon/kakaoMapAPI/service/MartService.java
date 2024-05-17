package moon.kakaoMapAPI.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import moon.kakaoMapAPI.config.WebClientConfig;
import moon.kakaoMapAPI.dto.MartResponseDto;
import moon.kakaoMapAPI.dto.MartDataDto;
import moon.kakaoMapAPI.dto.MartLocationDto;
import moon.kakaoMapAPI.entity.Mart;
import moon.kakaoMapAPI.repository.MartRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MartService {
    private final WebClientConfig webClientConfig;
    private final MartRepository martRepository;

    /**
     * 주소 주변 마트를 찾고 저장
     *
     * @param address 검색할 주소
     * @param radius 검색 반경(미터 단위)
     * @return MartResponseDto에 저장된 마트 정보들을 담음
     */
    public Flux<MartResponseDto> findAndSaveMarts(String address, int radius) {
        return findLocation(address)
                .flatMapMany(location -> searchMarts(
                        location.getLatitude(),
                        location.getLongitude(),
                        radius))
                .flatMap(this::saveMart);
    }

    /**
     * 주소로 위치 정보를 조회
     *
     * @param address 조회할 주소
     * @return 주소에 해당하는 위도와 경도를 MartLocationDto에 담음
     */
    public Mono<MartLocationDto> findLocation(String address) {
        return webClientConfig.kakaoWebClient().get()
                .uri(uriBuilder -> uriBuilder.path("/v2/local/search/address.json")
                        .queryParam("query", address)
                        .build())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(this::convertToMartLocationDto);
    }

    /**
     * 주변의 마트 검색
     *
     * @param latitude 검색 중심 위도
     * @param longitude 검색 중심 경도
     * @param radius 검색 반경(미터 단위)
     * @return 검색된 마트 정보를 MartDataDto 리스트로 변환
     */
    public Flux<MartDataDto> searchMarts(double latitude, double longitude, int radius) {
        return webClientConfig.kakaoWebClient().get()
                .uri(uriBuilder -> uriBuilder.path("/v2/local/search/category.json")
                        .queryParam("category_group_code", "MT1")
                        .queryParam("radius", radius)
                        .queryParam("x", longitude)
                        .queryParam("y", latitude)
                        .queryParam("sort", "distance")
                        .build())
                .retrieve()
                .bodyToFlux(JsonNode.class)
                .flatMapIterable(this::convertToMartDataDtos);
    }

    /**
     * 검색된 마트 정보를 저장
     *
     * @param contentDto 저장할 마트 정보
     * @return MartResponseDto로 저장된 마트 정보 반환
     */
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

    /**
     * 위치 정보가 담긴 JsonNode를 MartLocationDto로 변환
     *
     * @param jsonNode 위치 정보 담김
     * @return 변환된 위치 정보
     */
    private MartLocationDto convertToMartLocationDto(JsonNode jsonNode) {
        return Optional.ofNullable(jsonNode)
                .map(node -> node.get("documents"))
                .filter(JsonNode::isArray)
                .filter(documents -> !documents.isEmpty())
                .map(documents -> documents.get(0))
                .map(this::parseDocumentToLocationDto)
                .orElse(null);
    }

    /**
     * JsonNode(위도, 경도) -> MartLocationDto로 반환
     *
     * @param document 위치 정보가 담긴 JsonNode 문서
     * @return 위도와 경도 정보를 담은 MartLocationDto
     */
    private MartLocationDto parseDocumentToLocationDto(JsonNode document) {
        double latitude = document.get("y").asDouble();
        double longitude = document.get("x").asDouble();
        return new MartLocationDto(latitude, longitude);
    }

    /**
     * JsonNode에서 마트 정보를 추출하여 MartDataDto 리스트로 변환
     *
     * @param jsonNode 마트 정보가 담긴 JsonNode
     * @return 변환된 마트 데이터 리스트
     */
    private List<MartDataDto> convertToMartDataDtos(JsonNode jsonNode) {
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
}
