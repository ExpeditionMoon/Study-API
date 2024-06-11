package moon.kakaoMapAPI.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MartResponseDto {
    private Long martId;
    private String martName;
    private String address;
    private String entpId; // 업체_아이디
    private String entpAreaCode; // 업체_지역_코드
    private String entpTelNo; // 전화번호

    public MartResponseDto(Long martId, String martName, String address) {
        this.martId = martId;
        this.martName = martName;
        this.address = address;
    }
}
