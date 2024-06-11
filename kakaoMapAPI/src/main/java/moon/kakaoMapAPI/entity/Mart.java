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

    @Column(nullable = false, unique = true)
    private String martName; // 업체_명

    @Column(nullable = false)
    private String martAddress; // 도로_기본_주소_명

    @Column(nullable = true)
    private String entpId; // 업체_아이디

    @Column(nullable = true)
    private String entpAreaCode; // 업체_지역_코드

    @Column(nullable = true)
    private String entpTelNo; // 전화번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "join_id")
    private JoinMart joinMart;

    public Mart(String martName, String martAddress, String entpId, String entpAreaCode, String entpTelNo) {
        this.martName = martName;
        this.martAddress = martAddress;
        this.entpId = entpId;
        this.entpAreaCode = entpAreaCode;
        this.entpTelNo = entpTelNo;
    }
}
