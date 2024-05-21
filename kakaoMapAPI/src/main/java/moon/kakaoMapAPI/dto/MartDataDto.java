package moon.kakaoMapAPI.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MartDataDto {
    private String id; // 예: 1684215841
    private String placeName; // 예: 홈플러스익스프레스 명륜점
    private String address; // 예: 서울 종로구 명륜1가 36-13
    private String roadAddress; // 예: 서울 종로구 명륜1가 36-13
    private String distance; // 예: 2243
    private String phone; // 예: 02-000-0000

    public MartDataDto(String id, String placeName, String address, String roadAddress, String distance, String phone) {
        this.id = id;
        this.placeName = placeName;
        this.address = address;
        this.roadAddress = roadAddress;
        this.distance = distance;
        this.phone = phone;
    }
}
