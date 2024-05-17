package moon.kakaoMapAPI.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MartResponseDto {
    @Column(nullable = false)
    private String martName;

    @Column(nullable = false)
    private String address;

    private String entpId; // 업체_아이디

    private String entpAreaCode; // 업체_지역_코드

    private String entpTelNo; // 전화번호

    public MartResponseDto(String martName, String address) {
        this.martName = martName;
        this.address = address;
    }
}
