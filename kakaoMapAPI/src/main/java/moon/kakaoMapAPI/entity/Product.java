package moon.kakaoMapAPI.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = true)
    private String productImgUrl;

    @Builder
    public Product(String productName, String productImgUrl) {
        this.productName = productName;
        this.productImgUrl = productImgUrl;
    }
}
