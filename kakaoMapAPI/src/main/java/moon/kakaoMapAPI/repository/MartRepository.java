package moon.kakaoMapAPI.repository;

import moon.kakaoMapAPI.entity.JoinMart;
import moon.kakaoMapAPI.entity.Mart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MartRepository extends JpaRepository<Mart, Long> {

    @Query("""
            SELECT jm FROM JoinMart jm
            WHERE :placeName LIKE CONCAT('%', jm.store, '%')
            """)
    Optional<JoinMart> findJoinMartByNameContaining(String placeName);

    @Query("""
            SELECT m.joinMart.joinId FROM Mart m
            WHERE m.martId = :martId
            """)
    Optional<Long> findJoinIdByMartId(Long martId);

    List<Mart> findByJoinMartJoinId(Long joinId);

    Optional<Mart> findByMartName(String placeName);
}
