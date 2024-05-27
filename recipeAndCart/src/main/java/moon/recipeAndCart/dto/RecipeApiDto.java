package moon.recipeAndCart.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Slf4j
public class RecipeApiDto {
    @JsonProperty("RCP_SEQ")
    private Long recipeApiNo;
    @JsonProperty("RCP_NM")
    private String recipeName;
    @JsonProperty("RCP_PAT2")
    private String recipeType;
    @JsonProperty("RCP_PARTS_DTLS")
    private String recipeParts;

    private List<RecipeManualDto> manual;

    @JsonProperty("RCP_NA_TIP")
    private String recipeNaTip;

    public void parseManuals(JsonNode node) {
        this.manual = new ArrayList<>();
        log.info("메뉴얼 파싱 시작");
        for (int i = 1; i <= 20; i++) {
            String manualKey = "MANUAL" + String.format("%02d", i);
            String manualImgKey = "MANUAL_IMG" + String.format("%02d", i);

            String manualTxt = node.path(manualKey).asText(null);
            String manualImgUrl = node.path(manualImgKey).asText(null);

            log.info("스텝 {}: 텍스트={}, 이미지={}", i, manualTxt, manualImgUrl);

            if (manualTxt != null && !manualTxt.isEmpty()) {
                RecipeManualDto manualDto = new RecipeManualDto();
                manualDto.setStep(i);
                manualDto.setManualTxt(manualTxt);
                manualDto.setManualImgUrl(manualImgUrl);
                this.manual.add(manualDto);
            }
            log.info("파싱된 메뉴얼: {}", this.manual);
        }
    }
}
