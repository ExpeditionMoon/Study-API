package moon.kakaoMapAPI.controller;

import lombok.RequiredArgsConstructor;
import moon.kakaoMapAPI.dto.MartResponseDto;
import moon.kakaoMapAPI.service.MartInfoApiService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class MartInfoApiController {

    private final MartInfoApiService martInfoApiService;

    /**
     * 마트 정보 가져오기
     */
    @GetMapping("/store")
    public Mono<String> getStoreInfo() {
        return martInfoApiService.fetchInfo();
    }

    /**
     * 상품 정보 가져오기
     */
    @GetMapping("/product")
    public Mono<String> getProductInfo() {
        return martInfoApiService.fetchProduct();
    }

    // TODO. 가져온 API 정보 저장하는 로직 완성하기

    /**
     * 가져온 정보 저장
     */
    @GetMapping("/api")
    public Flux<MartResponseDto> getEntpInfo() {
        return martInfoApiService.getAndSaveMartInfo();
    }
}
