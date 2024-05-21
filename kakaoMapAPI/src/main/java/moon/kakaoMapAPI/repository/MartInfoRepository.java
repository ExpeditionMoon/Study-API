package moon.kakaoMapAPI.repository;

import moon.kakaoMapAPI.entity.MartInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MartInfoRepository extends JpaRepository<MartInfo, Long> {
}
