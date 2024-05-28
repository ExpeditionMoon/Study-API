package moon.recipeAndCart.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RecipeApiDto {
    @JsonProperty("RCP_SEQ")
    private Long recipeApiNo;
    @JsonProperty("RCP_NM")
    private String recipeName;
    @JsonProperty("RCP_PAT2")
    private String recipeType;
    @JsonProperty("RCP_PARTS_DTLS")
    private String recipeParts;

    private List<RecipeManualDto> manual = new ArrayList<>();

    @JsonProperty("RCP_NA_TIP")
    private String recipeNaTip;

    @JsonAnySetter
    public void parseManuals(String key, Object value) {
        if (key.startsWith("MANUAL")) {
            int step = Integer.parseInt(key.replaceAll("[^0-9]", ""));
            if (value != null && !value.toString().isEmpty()) {
                while (manual.size() < step) {
                    manual.add(new RecipeManualDto());
                }
                if (key.startsWith("MANUAL_IMG")) {
                    manual.get(step - 1).setManualImgUrl(value.toString());
                } else {
                    manual.get(step - 1).setManualTxt(value.toString());
                }
            }
        }
    }
}

