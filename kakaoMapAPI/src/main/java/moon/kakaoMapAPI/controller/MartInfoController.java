package moon.kakaoMapAPI.controller;

import lombok.RequiredArgsConstructor;
import moon.kakaoMapAPI.dto.MartInfoDto;
import moon.kakaoMapAPI.dto.MartResponseDto;
import moon.kakaoMapAPI.service.MartInfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class MartInfoController {

    private final MartInfoService martInfoService;

    @GetMapping("/info")
    public Flux<MartResponseDto> getEntpInfo() {
        return martInfoService.getAndSaveMartInfo();
    }
}
