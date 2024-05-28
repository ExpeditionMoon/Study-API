package moon.recipeAndCart.service;

import lombok.RequiredArgsConstructor;
import moon.recipeAndCart.dto.common.RecipeManualDto;
import moon.recipeAndCart.dto.common.RecipePartsDto;
import moon.recipeAndCart.entity.Recipe;
import moon.recipeAndCart.entity.RecipeManual;
import moon.recipeAndCart.entity.RecipeParts;
import moon.recipeAndCart.repository.RecipeManualRepository;
import moon.recipeAndCart.repository.RecipePartsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecipeCommonService {

    private final RecipeManualRepository manualRepository;
    private final RecipePartsRepository partsRepository;

    public void saveManuals(List<RecipeManualDto> manuals, Recipe savedRecipe) {
        manuals.forEach(manualDto -> {
            RecipeManual manual = RecipeManual.builder()
                    .step(Long.valueOf(manualDto.getStep()))
                    .manualTxt(manualDto.getManualTxt())
                    .manualImgUrl(manualDto.getManualImgUrl())
                    .recipe(savedRecipe)
                    .build();
            manualRepository.save(manual);
        });
    }

    public void saveParts(List<RecipePartsDto> parts, Recipe savedRecipe) {
        parts.forEach(part -> {
            RecipeParts recipePart = RecipeParts.builder()
                    .partsName(String.valueOf(part))
                    .partsQuantity(part.getPartsQuantity())
                    .recipe(savedRecipe)
                    .build();
            partsRepository.save(recipePart);
        });
    }

    public List<RecipeManualDto> findManualsByRecipe(Recipe recipe) {
        List<RecipeManual> manuals = manualRepository.findByRecipeRecipeId(recipe.getRecipeId());
        return manuals.stream()
                .map(manual -> new RecipeManualDto(
                        manual.getStep().intValue(),
                        manual.getManualTxt(),
                        manual.getManualImgUrl()))
                .toList();
    }

    public List<RecipePartsDto> findPartsByRecipe(Recipe recipe) {
        List<RecipeParts> parts = partsRepository.findByRecipeRecipeId(recipe.getRecipeId());
        return parts.stream()
                .map(part -> new RecipePartsDto(
                        part.getPartsName(),
                        part.getPartsQuantity()))
                .toList();
    }
}