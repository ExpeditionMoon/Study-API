package moon.kakaoMapAPI.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moon.kakaoMapAPI.config.WebClientConfig;
import moon.kakaoMapAPI.dto.MartInfoApiDto;
import moon.kakaoMapAPI.dto.MartResponseDto;
import moon.kakaoMapAPI.entity.Mart;
import moon.kakaoMapAPI.repository.MartRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
@Slf4j
public class MartInfoApiService {

    private final WebClientConfig webClientConfig;
    private final MartRepository martRepository;

    @Value("${open.api.key}")
    private String openApiKey;

    public Mono<String> fetchInfo() {
        return webClientConfig.openApiWebClient().get()
                .uri(uriBuilder -> uriBuilder
                        .path("/getStoreInfoSvc.do")
                        .queryParam("ServiceKey", openApiKey)
                        .build())
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<String> fetchProduct() {
        return webClientConfig.openApiWebClient().get()
                .uri(uriBuilder -> uriBuilder
                        .path("/getProductInfoSvc.do")
                        .queryParam("ServiceKey", openApiKey)
                        .build())
                .retrieve()
                .bodyToMono(String.class);
    }

    public Flux<MartResponseDto> getAndSaveMartInfo() {
        return getMartDetails()
                .flatMap(this::saveMartInfo)
                .flatMapMany(Flux::just);
    }

    private Mono<MartInfoApiDto> getMartDetails() {
        return webClientConfig.openApiWebClient().get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("ServiceKey", openApiKey)
                        .build())
                .retrieve()
                .bodyToMono(MartInfoApiDto.class)
                .onErrorResume(e -> Mono.empty());
    }

    public Mono<MartResponseDto> saveMartInfo(MartInfoApiDto martInfoDto) {
        return Mono.fromCallable(() -> convertToResponseDto(martInfoDto))
                .subscribeOn(Schedulers.boundedElastic());
    }

    private MartResponseDto convertToResponseDto(MartInfoApiDto martInfoDto) {
        Mart mart = Mart.builder()
                .martName(martInfoDto.getEntpName())
                .martAddress(martInfoDto.getRoadAddrBasic())
                .entpId(martInfoDto.getEntpId())
                .entpAreaCode(martInfoDto.getEntpAreaCode())
                .entpTelNo(martInfoDto.getEntpTelNo())
                .build();
        Mart savedMart = martRepository.save(mart);

        return new MartResponseDto(
                savedMart.getMartName(),
                savedMart.getMartAddress(),
                savedMart.getEntpId(),
                savedMart.getEntpAreaCode(),
                savedMart.getEntpTelNo());
    }
}
