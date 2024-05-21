package moon.kakaoMapAPI.repository;

import moon.kakaoMapAPI.dto.DiscountInfoDto;
import moon.kakaoMapAPI.entity.JoinMart;
import moon.kakaoMapAPI.entity.MartProduct;
import moon.kakaoMapAPI.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MartProductRepository extends JpaRepository<MartProduct, Long> {
    @Query("""
            SELECT new moon.kakaoMapAPI.dto.DiscountInfoDto(
            mp.product.productId, mp.product.productName, mp.joinMart.joinId, mp.stock, mp.price, d.discountRate)
            FROM MartProduct mp
            LEFT JOIN mp.discount d
            WHERE mp.product IN :products AND mp.joinMart IN :joinMarts
            """)
    List<DiscountInfoDto> findDiscountsByProductsAndJoinMarts(List<Product> products, List<JoinMart> joinMarts);
}
