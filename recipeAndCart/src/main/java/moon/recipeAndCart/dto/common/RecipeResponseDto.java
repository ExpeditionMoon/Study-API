package moon.recipeAndCart.dto.common;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import moon.recipeAndCart.entity.Recipe;

import java.util.List;

@Data
@NoArgsConstructor
public class RecipeResponseDto {
    private String recipeName;
    private String recipeType;
    private String recipeNaTip;
    private List<RecipeManualDto> recipeManualList;
    private List<RecipePartsDto> recipePartsList;

    @Builder
    public RecipeResponseDto(String recipeName, String recipeType, String recipeNaTip, List<RecipeManualDto> recipeManualList, List<RecipePartsDto> recipePartsList) {
        this.recipeName = recipeName;
        this.recipeType = recipeType;
        this.recipeNaTip = recipeNaTip;
        this.recipeManualList = recipeManualList;
        this.recipePartsList = recipePartsList;
    }

    public static RecipeResponseDto toResponseDto(Recipe recipe, List<RecipeManualDto> manualList, List<RecipePartsDto> partsList) {
        return RecipeResponseDto.builder()
                .recipeName(recipe.getRecipeName())
                .recipeType(recipe.getRecipeType())
                .recipeNaTip(recipe.getRecipeNaTip())
                .recipeManualList(manualList)
                .recipePartsList(partsList)
                .build();
    }
}
