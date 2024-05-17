package moon.kakaoMapAPI.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import moon.kakaoMapAPI.dto.ApiResponse;
import moon.kakaoMapAPI.dto.ProductDto;
import moon.kakaoMapAPI.service.ProductService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    /** 상품 리스트 가져오기 */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductDto>>> getAllProductDtos() {
        ApiResponse<List<ProductDto>> productList = productService.findAllProductDtos();
        return ResponseEntity.ok(productList);
    }
}