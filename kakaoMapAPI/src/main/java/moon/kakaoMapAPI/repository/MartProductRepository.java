package moon.kakaoMapAPI.repository;

import moon.kakaoMapAPI.dto.DiscountInfoDto;
import moon.kakaoMapAPI.entity.Mart;
import moon.kakaoMapAPI.entity.MartProduct;
import moon.kakaoMapAPI.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MartProductRepository extends JpaRepository<MartProduct, Long> {

    @Query("SELECT mp.mart FROM MartProduct mp WHERE mp.product IN :products " +
            "GROUP BY mp.mart HAVING COUNT(DISTINCT mp.product) = :numberOfProducts")
    List<Mart> findMartsSellingAllProducts(List<Product> products, Long numberOfProducts);

    @Query("SELECT new moon.kakaoMapAPI.dto.DiscountInfoDto(" +
            "mp.product.productId, mp.product.productName, mp.mart.martId, mp.stock, mp.price, d.discountRate) " +
            "FROM MartProduct mp LEFT JOIN mp.discount d " +
            "WHERE mp.product IN :products AND mp.mart IN :marts")
    List<DiscountInfoDto> findDiscountsByProductsAndMarts(List<Product> products, List<Mart> marts);
}
