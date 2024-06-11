package moon.kakaoMapAPI.repository;

import moon.kakaoMapAPI.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
