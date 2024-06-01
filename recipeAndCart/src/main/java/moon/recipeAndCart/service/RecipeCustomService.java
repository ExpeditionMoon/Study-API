package moon.recipeAndCart.service;

import lombok.RequiredArgsConstructor;
import moon.recipeAndCart.dto.RecipeManualDto;
import moon.recipeAndCart.dto.RecipePartsDto;
import moon.recipeAndCart.dto.RecipeRequestDto;
import moon.recipeAndCart.dto.RecipeResponseDto;
import moon.recipeAndCart.entity.Recipe;
import moon.recipeAndCart.entity.RecipeManual;
import moon.recipeAndCart.entity.RecipeParts;
import moon.recipeAndCart.repository.RecipeManualRepository;
import moon.recipeAndCart.repository.RecipePartsRepository;
import moon.recipeAndCart.repository.RecipeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecipeCustomService {

    private final RecipeRepository recipeRepository;
    private final RecipeManualRepository manualRepository;
    private final RecipePartsRepository partsRepository;
    private final RecipeFileService fileService;

    @Transactional
    public RecipeResponseDto saveRecipe(RecipeRequestDto requestDto, List<MultipartFile> files) {
        Recipe recipe = createRecipe(requestDto);
        List<RecipeManualDto> updatedManuals = createManuals(recipe, requestDto.getRecipeManualList(), files);
        saveParts(requestDto.getRecipePartsList(), recipe);

        return RecipeResponseDto.toResponseDto(
                recipe,
                updatedManuals,
                requestDto.getRecipePartsList());
    }

    private Recipe createRecipe(RecipeRequestDto requestDto) {
        Recipe recipe = Recipe.builder()
                .recipeName(requestDto.getRecipeName())
                .recipeType(requestDto.getRecipeType())
                .recipeTip(requestDto.getRecipeTip())
                .build();
        return recipeRepository.save(recipe);
    }

    private List<RecipeManualDto> createManuals(Recipe recipe, List<RecipeManualDto> manualDtos, List<MultipartFile> files) {
        List<RecipeManualDto> updatedManuals = new ArrayList<>();
        for (int i = 0; i < manualDtos.size(); i++) {
            if (i < files.size()) {
                    RecipeManualDto manualDto = manualDtos.get(i);
                    MultipartFile file = files.get(i);
                try {
                    RecipeManualDto savedManualDto = saveManual(recipe.getRecipeId(), manualDto, file);
                    updatedManuals.add(savedManualDto);
                } catch (IOException e) {
                    throw new RuntimeException("이미지 파일 처리 중 오류가 발생했습니다.", e);
                }
            }
        }
        return updatedManuals;
    }

    private RecipeManualDto saveManual(Long recipeId, RecipeManualDto manualDto, MultipartFile file) throws IOException {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("레시피를 찾을 수 없습니다." + recipeId));

        Long step = getNextStepNumber(recipeId);
        String fileUrl = fileService.saveFile(recipeId, step, file);

        RecipeManual manual = RecipeManual.builder()
                .step(step)
                .manualTxt(manualDto.getManualTxt())
                .manualImgUrl(fileUrl)
                .recipe(recipe)
                .build();
        manualRepository.save(manual);

        return RecipeManualDto.manualDto(manual);
    }

    private Long getNextStepNumber(Long recipeId) {
        Long lastStep = manualRepository.findLastStepByRecipeId(recipeId)
                .orElse(0L);
        return lastStep + 1;
    }

    private void saveParts(List<RecipePartsDto> parts, Recipe savedRecipe) {
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
