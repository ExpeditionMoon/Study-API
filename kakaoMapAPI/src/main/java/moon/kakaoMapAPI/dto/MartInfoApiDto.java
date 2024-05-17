package moon.kakaoMapAPI.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MartInfoApiDto {
    private String resultCode; // 결과코드
    private String resultMsg; // 결과메시지
    private String entpId; // 업체_아이디
    private String entpName; // 업체_명
    private String entpTypeCode; // 업체_업테_코드
    private String entpAreaCode; // 업체_지역_코드
    private String areaDetailCode; // 지역_상세_코드
    private String entpTelNo; // 전화번호
    private String postNo; // 우편번호
    private String plmkAddrBasic; // 지번_기본_주소_명
    private String plmkAddrDetail; // 지번_상세_주소_명
    private String roadAddrBasic; // 도로_기본_주소_명
    private String roadAddrDetail; // 도로_상세_주소_명
    private double xMapCoord; // X_지도_좌표
    private double yMapCoord; // Y_지도_좌표
}
