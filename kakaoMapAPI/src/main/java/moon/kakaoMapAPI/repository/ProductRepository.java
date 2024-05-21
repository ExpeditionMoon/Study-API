package moon.kakaoMapAPI.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import moon.kakaoMapAPI.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
