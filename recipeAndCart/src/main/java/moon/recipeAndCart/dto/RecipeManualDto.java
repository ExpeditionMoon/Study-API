package moon.recipeAndCart.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import moon.recipeAndCart.entity.RecipeManual;

@Data
@NoArgsConstructor
public class RecipeManualDto {
    private int step;
    private String manualTxt;
    private String manualImgUrl;

    @Builder
    public RecipeManualDto(int step, String manualTxt, String manualImgUrl) {
        this.step = step;
        this.manualTxt = manualTxt;
        this.manualImgUrl = manualImgUrl;
    }

    public static RecipeManualDto manualDto(RecipeManual manual) {
        return RecipeManualDto.builder()
                .step(Math.toIntExact(manual.getStep()))
                .manualTxt(manual.getManualTxt())
                .manualImgUrl(manual.getManualImgUrl())
                .build();
    }
}
