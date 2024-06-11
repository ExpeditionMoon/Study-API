package moon.kakaoMapAPI.controller;

import lombok.RequiredArgsConstructor;
import moon.kakaoMapAPI.dto.MartResponseDto;
import moon.kakaoMapAPI.service.MartService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MartController {

    private final MartService martService;

    @Value("${kakao.js.key}")
    private String jsKey;

    @GetMapping
    public String home(Model model) {
        model.addAttribute("jsKey", jsKey);
        return "index";
    }

    @GetMapping("/marts")
    public Mono<ResponseEntity<List<MartResponseDto>>> getMarts() {
        return martService.findAndSaveMarts("서울 종로구 종로 80-2", 20000)
                .collectList()
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList()))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()));
    }
}
