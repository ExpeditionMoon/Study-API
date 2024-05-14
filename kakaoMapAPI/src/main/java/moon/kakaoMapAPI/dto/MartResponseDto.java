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
}
