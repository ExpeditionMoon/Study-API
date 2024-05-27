package moon.recipeAndCart.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RecipeManualDto {
    private int step;
    private String manualTxt;
    private String manualImgUrl;
}
