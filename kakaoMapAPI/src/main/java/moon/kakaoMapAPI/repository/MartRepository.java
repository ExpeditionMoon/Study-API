package moon.kakaoMapAPI.repository;

import moon.kakaoMapAPI.entity.Mart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MartRepository extends JpaRepository<Mart, Long> {
}
