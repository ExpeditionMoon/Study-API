package moon.recipeAndCart.dto.official;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import moon.recipeAndCart.dto.common.RecipeManualDto;
import moon.recipeAndCart.dto.custom.RecipePartsDto;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public List<RecipePartsDto> extractRecipeParts() {
        List<RecipePartsDto> partsList = new ArrayList<>();
        if (this.recipeParts != null && !this.recipeParts.isEmpty()) {
            String[] partsArr = this.recipeParts.split("[\n,]");
            Pattern pattern = Pattern.compile("(.+?)\\s*(\\d+\\s*[gml개큰술작은술장]*)");
            for (String part : partsArr) {
                if (!part.isEmpty() && !part.startsWith("고명") &&
                        !part.startsWith("양념장") && !part.startsWith("양념") &&
                        !part.startsWith("●") && !part.startsWith("•")
                ) {
                    Matcher matcher = pattern.matcher(part);
                    if (matcher.find()) {
                        String partsName = matcher.group(1).trim();
                        String partsQuantity = matcher.group(2).trim();
                        partsList.add(new RecipePartsDto(partsName, partsQuantity));
                    } else {
                        partsList.add(new RecipePartsDto(part,null));
                    }
                }
            }
        }
        return partsList;
    }
}

