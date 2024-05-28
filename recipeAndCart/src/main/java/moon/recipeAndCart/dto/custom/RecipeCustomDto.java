package moon.recipeAndCart.dto.custom;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import moon.recipeAndCart.dto.common.RecipeManualDto;
import moon.recipeAndCart.entity.Recipe;

import java.util.List;

@Data
@NoArgsConstructor
public class RecipeCustomDto {
    private String recipeName;
    private String recipeType;
    private String recipeNaTip;
    private List<RecipeManualDto> recipeManualList;
    private List<RecipePartsDto> recipePartsList;

    @Builder
    public RecipeCustomDto(String recipeName, String recipeType, String recipeNaTip, List<RecipeManualDto> recipeManualList, List<RecipePartsDto> recipePartsList) {
        this.recipeName = recipeName;
        this.recipeType = recipeType;
        this.recipeNaTip = recipeNaTip;
        this.recipeManualList = recipeManualList;
        this.recipePartsList = recipePartsList;
    }

    public static RecipeCustomDto customDto(Recipe recipe, List<RecipeManualDto> manualList, List<RecipePartsDto> partsList) {
        return RecipeCustomDto.builder()
                .recipeName(recipe.getRecipeName())
                .recipeType(recipe.getRecipeType())
                .recipeNaTip(recipe.getRecipeNaTip())
                .recipeManualList(manualList)
                .recipePartsList(partsList)
                .build();
    }
}
