package moon.recipeAndCart.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import moon.recipeAndCart.entity.Recipe;
import moon.recipeAndCart.entity.RecipeManual;

import java.util.Optional;

@Data
@NoArgsConstructor
public class RecipeListDto {
    private Long recipeId;
    private String recipeName;
    private String recipeType;
    private String manualImgUrl;

    @Builder
    public RecipeListDto(Long recipeId, String recipeName, String recipeType, String manualImgUrl) {
        this.recipeId = recipeId;
        this.recipeName = recipeName;
        this.recipeType = recipeType;
        this.manualImgUrl = manualImgUrl;
    }

    public static RecipeListDto recipeResList(Recipe recipe, Optional<RecipeManual> manual) {
        return RecipeListDto.builder()
                .recipeId(recipe.getRecipeId())
                .recipeName(recipe.getRecipeName())
                .recipeType(recipe.getRecipeType())
                .manualImgUrl(manual.map(RecipeManual::getManualImgUrl).orElse(null))
                .build();
    }
}
