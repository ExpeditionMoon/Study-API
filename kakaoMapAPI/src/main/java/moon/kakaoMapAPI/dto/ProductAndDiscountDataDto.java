package moon.kakaoMapAPI.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import moon.kakaoMapAPI.entity.Product;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductAndDiscountDataDto {
    private List<Product> productList;
    private List<DiscountInfoDto> discountInfoList;
    private List<CartProductDto> cartProductList;
}
