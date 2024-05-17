package moon.kakaoMapAPI.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import moon.kakaoMapAPI.dto.CartProductDto;
import moon.kakaoMapAPI.entity.Cart;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    @Query("SELECT new moon.kakaoMapAPI.dto.CartProductDto(c.quantity, c.user.userId, c.product.productId) " +
            "FROM Cart c WHERE c.user.userId = :memberId")
    List<CartProductDto> findCartsByMemberMemberId(Long memberId);
}
