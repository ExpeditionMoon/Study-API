package moon.kakaoMapAPI.entity;

import ch.qos.logback.classic.spi.LoggingEventVO;
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
    private String martName; // 업체_명

    @Column(nullable = false)
    private String address; // 도로_기본_주소_명

    private String entpId; // 업체_아이디

    private String entpAreaCode; // 업체_지역_코드

    private String entpTelNo; // 전화번호

    public Mart(String martName, String address) {
        this.martName = martName;
        this.address = address;
    }

    public Mart(String martName, String address, String entpId, String entpAreaCode, String entpTelNo) {
        this.martName = martName;
        this.address = address;
        this.entpId = entpId;
        this.entpAreaCode = entpAreaCode;
        this.entpTelNo = entpTelNo;
    }
}
