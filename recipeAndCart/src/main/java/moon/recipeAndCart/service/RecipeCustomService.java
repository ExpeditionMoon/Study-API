package moon.recipeAndCart.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import moon.recipeAndCart.dto.*;
import moon.recipeAndCart.entity.Recipe;
import moon.recipeAndCart.entity.RecipeManual;
import moon.recipeAndCart.entity.RecipeParts;
import moon.recipeAndCart.repository.RecipeManualRepository;
import moon.recipeAndCart.repository.RecipePartsRepository;
import moon.recipeAndCart.repository.RecipeRepository;
import moon.recipeAndCart.util.enums.RecipeMessage;
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

    /**
     * 레시피 관련 정보와 이미지 파일들 생성
     *
     * @param requestDto 저장할 정보가 담긴 DTO
     * @param files      첨부할 이미지 파일 목록
     * @return 저장된 레시피 정보 반환
     * @throws RuntimeException        이미지 파일 처리 중 오류 발생
     * @throws EntityNotFoundException 레시피를 찾을 수 없을 때 발생
     */
    @Transactional
    public ApiResponse<RecipeResponseDto> createRecipe(RecipeRequestDto requestDto, List<MultipartFile> files) {
        Recipe recipe = saveRecipe(requestDto);
        List<RecipeManualDto> updatedManuals = createManuals(recipe, requestDto.getRecipeManualList(), files);
        saveParts(requestDto.getRecipePartsList(), recipe);

        return ApiResponse.success(
                RecipeResponseDto.toResponseDto(
                        recipe, updatedManuals, requestDto.getRecipePartsList()),
                RecipeMessage.LOADED_RECIPE.getMessage()
        );
    }

    /**
     * 레시피 엔티티를 생성하고 저장
     */
    private Recipe saveRecipe(RecipeRequestDto requestDto) {
        Recipe recipe = Recipe.builder()
                .recipeName(requestDto.getRecipeName())
                .recipeType(requestDto.getRecipeType())
                .recipeTip(requestDto.getRecipeTip())
                .build();
        return recipeRepository.save(recipe);
    }

    /**
     * 레시피 메뉴얼과 이미지 파일을 목록으로 생성하고 저장
     */
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
                    throw new RuntimeException(
                            RecipeMessage.ERROR_RECIPE_IMG_PROCESSING.getMessage(), e);
                }
            }
        }
        return updatedManuals;
    }

    /**
     * 레시피 메뉴얼과 이미지 파일을 생성하고 저장
     */
    private RecipeManualDto saveManual(Long recipeId, RecipeManualDto manualDto, MultipartFile file) throws IOException {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new EntityNotFoundException(RecipeMessage.NOT_FOUND_RECIPE.getMessage()));

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

    /**
     * 레시피 메뉴얼의 다음 단계 번호를 반환
     */
    private Long getNextStepNumber(Long recipeId) {
        Long lastStep = manualRepository.findLastStepByRecipeId(recipeId)
                .orElse(0L);
        return lastStep + 1;
    }

    /**
     * 레시피 제료 목록을 생성하고 저장
     */
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
