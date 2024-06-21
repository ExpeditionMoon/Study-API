package moon.recipeAndCart.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
        log.info("레시피 ={},{},{},{},{}", requestDto.getRecipeTip(), requestDto.getRecipeType(), requestDto.getRecipeName(), requestDto.getRecipeManualList(), requestDto.getRecipePartsList());
        Recipe recipe = saveRecipe(requestDto);

        if (requestDto.getRecipeManualList() != null) {
            saveAllManuals(recipe, requestDto.getRecipeManualList(), files);
        }
        if (requestDto.getRecipePartsList() != null) {
            saveAllParts(recipe, requestDto.getRecipePartsList());
        }

        return ApiResponse.success(
                RecipeResponseDto.toResponseDto(
                        recipe, requestDto.getRecipeManualList(), requestDto.getRecipePartsList()),
                RecipeMessage.LOADED_RECIPE.getMessage()
        );
    }

    /**
     * 레시피 수정
     */
    public ApiResponse<RecipeResponseDto> updateRecipe(Long recipeId, RecipeRequestDto requestDto, List<MultipartFile> files) throws IOException {
        log.info("레시피 ={},{},{},{},{}", requestDto.getRecipeTip(), requestDto.getRecipeType(), requestDto.getRecipeName(), requestDto.getRecipeManualList(), requestDto.getRecipePartsList());
        Recipe findRecipe = getRecipe(recipeId);
        log.info("find 레시피 ={}", findRecipe.getRecipeId());
        log.info("find 레시피 ={}", findRecipe.getRecipeType());

        Recipe.RecipeBuilder recipeBuilder = findRecipe.toBuilder();
        if (requestDto.getRecipeName() != null && !requestDto.getRecipeName().equals(findRecipe.getRecipeName())) {
            recipeBuilder.recipeName(requestDto.getRecipeName());
        }
        if (requestDto.getRecipeType() != null && !requestDto.getRecipeType().equals(findRecipe.getRecipeType())) {
            recipeBuilder.recipeType(requestDto.getRecipeType());
        }
        if (requestDto.getRecipeTip() != null && !requestDto.getRecipeTip().equals(findRecipe.getRecipeTip())) {
            recipeBuilder.recipeTip(requestDto.getRecipeTip());
        }

        Recipe updatedRecipe = recipeBuilder.build();
        log.info("레시피 업데이트 시작1");
        if (requestDto.getRecipeManualList() != null) {
            deleteManualImgs(findRecipe.getRecipeId());
            log.info("레시피 메뉴얼 시작3");
            saveAllManuals(updatedRecipe, requestDto.getRecipeManualList(), files);
        }

        if (requestDto.getRecipePartsList() != null) {
            log.info("레시피 재료 시작4");
            saveAllParts(updatedRecipe, requestDto.getRecipePartsList());
        }

        return ApiResponse.success(
                RecipeResponseDto.toResponseDto(
                        findRecipe, requestDto.getRecipeManualList(), requestDto.getRecipePartsList()),
                RecipeMessage.SUCCESS_UPDATE.getMessage()
        );
    }

    /**
     * 레시피 삭제
     *
     * @param recipeId 해당 레시피 고유번호
     * @return 성공했다는 메시지 반환
     * @throws RuntimeException 이미지 파일 처리 중 오류 발생
     */
    public ApiResponse deleteRecipe(Long recipeId) {
        Recipe findRecipe = getRecipe(recipeId);
        deleteManualImgs(recipeId);
        recipeRepository.delete(findRecipe);
        return ApiResponse.success(null, RecipeMessage.SUCCESS_DELETE.getMessage());
    }

    /**
     * 해당 레시피 메뉴얼의 이미지 삭제
     */
    private void deleteManualImgs(Long recipeId) {
        List<RecipeManual> findManuals = getManuals(recipeId);
        log.info("레시피 이미지 시작2");

        findManuals.forEach(manual -> {
            String fileName = manual.getManualImgUrl();
            if (fileName.equals(null)) {
                log.info("레시피 이미지 진입2-1");
                try {
                    fileService.deleteFile(fileName);
                } catch (IOException e) {
                    throw new RuntimeException(RecipeMessage.ERROR_RECIPE_IMG_PROCESSING.getMessage(), e);
                }
                manualRepository.delete(manual);
            }
        });
    }

    /**
     * 해당 레시피 찾아서 반환
     */
    private Recipe getRecipe(Long recipeId) {
        return recipeRepository.findById(recipeId)
                .orElseThrow(() -> new EntityNotFoundException(RecipeMessage.NOT_FOUND_RECIPE.getMessage()));
    }

    /**
     * 해당 레시피의 재료들을 찾아서 반환
     */
    private List<RecipeParts> getParts(Long recipeId) {
        return partsRepository.findAllByRecipeRecipeId(recipeId)
                .orElse(new ArrayList<>());
    }

    /**
     * 해당 레시피의 메뉴얼을 가져옴
     */
    private List<RecipeManual> getManuals(Long recipeId) {
        return manualRepository.findAllByRecipeRecipeId(recipeId)
                .orElse(null);
    }

    /**
     * 레시피를 저장
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
     * 레시피 메뉴얼과 이미지 파일을 목록으로 생성
     */
    private List<RecipeManualDto> saveAllManuals(Recipe recipe, List<RecipeManualDto> manualDtos, List<MultipartFile> files) {
        List<RecipeManualDto> updatedManuals = new ArrayList<>();
        for (int i = 0; i < manualDtos.size(); i++) {
            RecipeManualDto manualDto = manualDtos.get(i);
            MultipartFile file = (files != null && i < files.size()) ? files.get(i) : null;
            try {
                RecipeManualDto savedManualDto = saveManualWithFile(recipe, manualDto, file);
                updatedManuals.add(savedManualDto);
            } catch (IOException e) {
                throw new RuntimeException(
                        RecipeMessage.ERROR_RECIPE_IMG_PROCESSING.getMessage(), e);
            }
        }
        return updatedManuals;
    }

    /**
     * 레시피 메뉴얼과 이미지 파일을 저장
     */
    private RecipeManualDto saveManualWithFile(Recipe recipe, RecipeManualDto manualDto, MultipartFile file) throws IOException {
        Recipe findRecipe = getRecipe(recipe.getRecipeId());
        String fileUrl = fileService.saveFile(findRecipe.getRecipeId(), (long) manualDto.getStep(), file);

        RecipeManual manual = RecipeManual.builder()
                .step((long) manualDto.getStep())
                .manualTxt(manualDto.getManualTxt())
                .manualImgUrl(fileUrl)
                .recipe(recipe)
                .build();
        manualRepository.save(manual);

        return RecipeManualDto.manualDto(manual);
    }

    /**
     * 레시피 제료 목록을 저장
     */
    private void saveAllParts(Recipe savedRecipe, List<RecipePartsDto> parts) {
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
