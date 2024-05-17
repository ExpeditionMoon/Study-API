package moon.kakaoMapAPI.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import moon.kakaoMapAPI.dto.ApiResponse;
import moon.kakaoMapAPI.dto.ProductDto;
import moon.kakaoMapAPI.entity.Product;
import moon.kakaoMapAPI.exception.NoContentFoundException;
import moon.kakaoMapAPI.repository.ProductRepository;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * 상품 리스트 가져오기
     * 변환 과정에서 상품의 이미지는 Base64 형식으로 인코딩됩니다.
     *
     * @return 이미지 파일 경로를 Base64로 인코딩 하여 ApiResponse 형태로 반환
     */
    public ApiResponse<List<ProductDto>> findAllProductDtos() {
            List<ProductDto> productDto = productRepository.findAll().stream()
                .map(this::convertToProductDto)
                .collect(Collectors.toList());
        return ApiResponse.success(productDto, "상품 목록을 성공적으로 불러왔습니다.");
    }

    /** 이미지를 Base64로 인코딩하고, ProductDto로 반환 */
    private ProductDto convertToProductDto(Product product) {
        String base64Image =
                "data:image/jpeg;base64," + encodeImageToBase64(product.getProductImgUrl());
        return new ProductDto(product.getProductName(), base64Image);
    }

    /**
     * 이미지 파일을 Base64로 인코딩
     *
     * @param imagePath 이미지 파일의 경로
     * @return Base64로 인코딩하여 반환
     * @throws RuntimeException 이미지 파일 처리 중 오류 발생
     */
    private String encodeImageToBase64(String imagePath) {
        try {
            Resource resource = new ClassPathResource("static/images" + imagePath);
            if (!resource.exists()) {
                throw new NoContentFoundException("이미지가 비어있습니다.");
            }
            byte[] imageData = StreamUtils.copyToByteArray(resource.getInputStream());
            return Base64.getEncoder().encodeToString(imageData);
        } catch (IOException e) {
            throw new RuntimeException("이미지 파일 처리 중 오류가 발생했습니다.");
        }
    }
}
