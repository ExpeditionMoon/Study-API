package moon.kakaoMapAPI.controller;

import lombok.RequiredArgsConstructor;
import moon.kakaoMapAPI.dto.MartResponseDto;
import moon.kakaoMapAPI.service.MartInfoApiService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
public class MartInfoApiController {

    private final MartInfoApiService martInfoService;

    @GetMapping("/info")
    public Flux<MartResponseDto> getEntpInfo() {
        return martInfoService.getAndSaveMartInfo();
    }
}
