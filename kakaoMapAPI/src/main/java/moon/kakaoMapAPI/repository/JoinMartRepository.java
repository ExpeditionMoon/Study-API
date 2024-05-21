package moon.kakaoMapAPI.repository;

import moon.kakaoMapAPI.entity.JoinMart;
import moon.kakaoMapAPI.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JoinMartRepository extends JpaRepository<JoinMart, Long> {

    @Query("""
            SELECT mp.joinMart FROM MartProduct mp WHERE mp.product IN :products
            GROUP BY mp.joinMart HAVING COUNT(DISTINCT mp.product) = :numberOfProducts
            """)
    List<JoinMart> findJoinMartsSellingAllProducts(List<Product> products, Long numberOfProducts);
}
