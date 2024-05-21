package moon.kakaoMapAPI.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import moon.kakaoMapAPI.dto.ApiResponse;
import moon.kakaoMapAPI.dto.DiscountInfoDto;
import moon.kakaoMapAPI.service.MartProductService;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/marts/selling")
public class MartProductController {

    private final MartProductService martProductService;

    /**
     * 장바구니에 있는 모든 상품이 존재하는 마트 목록 + 마트별 합계 출력
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Map<Long, Long>>> getMartsForCart(
    ) {
        ApiResponse<Map<Long, Long>> productTotalPrices =
                martProductService.findMartsByProductIds();
        return ResponseEntity.ok(productTotalPrices);
    }

    /**
     * 마트별 상품 가격 세부사항 조회
     */
    @GetMapping("/{martId}")
    public ResponseEntity<ApiResponse<List<DiscountInfoDto>>> getMartInfoForMartId(
            @PathVariable("martId") Long martId
    ) {
        ApiResponse<List<DiscountInfoDto>> martInfo =
                martProductService.findMartInfoByMartId(martId);
        return ResponseEntity.ok(martInfo);
    }
}
