package moon.recipeAndCart.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import moon.recipeAndCart.dto.common.RecipeResponseDto;
import moon.recipeAndCart.entity.Recipe;
import moon.recipeAndCart.repository.RecipeRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecipeCustomService {

    private final RecipeRepository recipeRepository;
    private final RecipeCommonService commonService;

    @Transactional
    public RecipeResponseDto saveRecipe(RecipeResponseDto dto) {
        Recipe recipe = Recipe.builder()
                .recipeName(dto.getRecipeName())
                .recipeType(dto.getRecipeType())
                .recipeNaTip(dto.getRecipeNaTip())
                .build();
        recipeRepository.save(recipe);
        commonService.saveManuals(dto.getRecipeManualList(), recipe);
        commonService.saveParts(dto.getRecipePartsList(), recipe);

        return RecipeResponseDto.toResponseDto(
                recipe,
                dto.getRecipeManualList(),
                dto.getRecipePartsList());
    }
}
