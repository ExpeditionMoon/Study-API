package moon.kakaoMapAPI.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MartDataDto {
    private String id;
    private String placeName;
    private String address;
    private String roadAddress;
    private String phone;
    private String distance;
}
