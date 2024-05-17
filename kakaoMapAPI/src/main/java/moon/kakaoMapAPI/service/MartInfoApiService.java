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

    public Mono<MartResponseDto> saveMartInfo(MartInfoApiDto martInfoDto) {
        log.info("진입 save");
        return Mono.fromCallable(() -> convertToResponseDto(martInfoDto))
                .subscribeOn(Schedulers.boundedElastic())
                .doOnNext(mart -> log.info("saved mart: {}", mart.getMartName()));
    }

    private MartResponseDto convertToResponseDto(MartInfoApiDto martInfoDto) {
        log.info("진입 con");

        Mart mart = Mart.builder()
                .martName(martInfoDto.getEntpName())
                .address(martInfoDto.getRoadAddrBasic())
                .entpId(martInfoDto.getEntpId())
                .entpAreaCode(martInfoDto.getEntpAreaCode())
                .entpTelNo(martInfoDto.getEntpTelNo())
                .build();
        Mart savedMart = martRepository.save(mart);
        log.info("진입 con2");

        return new MartResponseDto(
            savedMart.getMartName(),
            savedMart.getAddress(),
            savedMart.getEntpId(),
            savedMart.getEntpAreaCode(),
            savedMart.getEntpTelNo());
    }

    public Flux<MartResponseDto> getAndSaveMartInfo() {
        log.info("진입 get");

        return getMartDetails()
                .flatMap(this::saveMartInfo)
                .flatMapMany(Flux::just);
//        return Flux.range(1, 1)
//                .flatMap(this::getMartDetails)
//                .flatMap(this::saveMartInfo);
    }

    private Mono<MartInfoApiDto> getMartDetails() {
        log.info("진입 getMart");

        return webClientConfig.openApiWebClient().get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("ServiceKey", openApiKey)
//                        .queryParam("entpId", String.valueOf(entpId))
                        .build())
                .retrieve()
                .bodyToMono(MartInfoApiDto.class)
                .doOnNext(response -> log.info("Received response: {}", response))//
//                .onErrorResume(e -> Mono.empty());
                .onErrorResume(e -> {
                    log.error("Error fetching mart details: {}", e.getMessage());
                    return Mono.empty();
                });
    }
}
