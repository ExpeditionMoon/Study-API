package moon.kakaoMapAPI.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long martId;

    @Column(nullable = false)
    private String martName;

    @Column(nullable = false)
    private String address;
}
