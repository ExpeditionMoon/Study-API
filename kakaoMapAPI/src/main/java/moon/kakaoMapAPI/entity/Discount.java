package moon.kakaoMapAPI.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long discountId;

    @Column(precision = 3, scale = 1, nullable = false)
    private BigDecimal discountRate;

    @Builder
    public Discount(BigDecimal discountRate) {
        this.discountRate = discountRate;
    }
}
