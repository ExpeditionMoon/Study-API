package moon.recipeAndCart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeManualDto {
    private int step;
    private String manualTxt;
    private String manualImgUrl;
}
