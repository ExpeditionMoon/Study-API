package moon.recipeAndCart.service;

import lombok.RequiredArgsConstructor;
import moon.recipeAndCart.dto.common.RecipeManualDto;
import moon.recipeAndCart.dto.custom.RecipePartsDto;
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
                    .step((long) manualDto.getStep())
                    .manualTxt(manualDto.getManualTxt())
                    .manualImgUrl(manualDto.getManualImgUrl())
                    .recipe(savedRecipe)
                    .build();
            manualRepository.save(manual);
        });
    }

    public void saveParts(List<String> parts, Recipe savedRecipe) {
        parts.forEach(part -> {
            RecipeParts recipePart = RecipeParts.builder()
                    .partsName(String.valueOf(part))
                    .recipe(savedRecipe)
                    .build();
            partsRepository.save(recipePart);
        });
    }

    public void saveCustomParts(List<RecipePartsDto> parts, Recipe savedRecipe) {
        parts.forEach(part -> {
            RecipeParts recipePart = RecipeParts.builder()
                    .partsName(String.valueOf(part))
                    .partsQuantity(part.getPartsQuantity())
                    .recipe(savedRecipe)
                    .build();
            partsRepository.save(recipePart);
        });
    }
}
