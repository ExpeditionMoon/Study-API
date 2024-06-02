package moon.recipeAndCart.service;

import lombok.RequiredArgsConstructor;
import moon.recipeAndCart.config.WebClientConfig;
import moon.recipeAndCart.dto.RecipeManualDto;
import moon.recipeAndCart.dto.RecipePartsDto;
import moon.recipeAndCart.dto.official.RecipeApiEntityDto;
import moon.recipeAndCart.dto.official.RecipeApiProcessingDto;
import moon.recipeAndCart.dto.official.RecipeApiResponseDto;
import moon.recipeAndCart.entity.Recipe;
import moon.recipeAndCart.entity.RecipeManual;
import moon.recipeAndCart.entity.RecipeParts;
import moon.recipeAndCart.repository.RecipeManualRepository;
import moon.recipeAndCart.repository.RecipePartsRepository;
import moon.recipeAndCart.repository.RecipeRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RecipeApiService {

    private static final int PAGE_SIZE = 100;
    private final WebClientConfig webClientConfig;
    private final RecipeRepository recipeRepository;
    private final RecipeManualRepository manualRepository;
    private final RecipePartsRepository partsRepository;

    private final Map<String, Integer> menuPageMap = new HashMap<>();
    private final Map<String, Integer> typePageMap = new HashMap<>();

    /**
     * 메뉴 이름으로 가져온 레시피를 저장
     * 현재 페이지 번호를 기준으로 레시피를 가져온 후, 해당 메뉴의 페이지 번호 업데이트
     *
     * @param menu 가져올 메뉴 이름
     * @return 레시피 API를 호출하고 저장된 레시피 Entity를 DTO 객체 목록으로 반환
     */
    public Mono<List<RecipeApiEntityDto>> fetchAndSaveRecipesByMenu(String menu) {
        int currentPage = menuPageMap.getOrDefault(menu, 1);
        return fetchMenu("RCP_NM", menu, currentPage, PAGE_SIZE)
                .flatMap(response -> saveRecipesFromApiResponse(response)
                        .doOnNext(savedRecipes -> menuPageMap.put(menu, currentPage + 1))
                        .map(this::convertToRecipeResponseDtos));
    }

    /**
     * 메뉴 종류로 가져온 레시피를 저장
     * 현재 페이지 번호를 기준으로 레시피를 가져온 후, 해당 종류의 페이지 번호 업데이트
     *
     * @param type 가져올 메뉴 종류
     * @return 레시피 API를 호출하고 저장된 레시피 Entity를 DTO 객체 목록으로 반환
     */
    public Mono<List<RecipeApiEntityDto>> fetchAndSaveRecipesByType(String type) {
        int currentPage = typePageMap.getOrDefault(type, 1);
        return fetchMenu("RCP_PAT2", type, currentPage, PAGE_SIZE)
                .flatMap(response -> saveRecipesFromApiResponse(response)
                        .doOnNext(savedRecipes -> typePageMap.put(type, currentPage + 1))
                        .map(this::convertToRecipeResponseDtos));
    }

    /**
     * 주어진 키와 값으로 레시피 가져옴
     */
    private Mono<RecipeApiResponseDto> fetchMenu(String key, String value, int page, int size) {
        int start = (page - 1) * size + 1;
        int end = page * size;

        return webClientConfig.recipeWebClient().get()
                .uri(uriBuilder -> uriBuilder
                        .path(start + "/" + end + "/" + key + "=" + value)
                        .build())
                .retrieve()
                .bodyToMono(RecipeApiResponseDto.class);
    }

    /**
     * 레시피 API 응답을 DB에 저장
     */
    private Mono<List<Recipe>> saveRecipesFromApiResponse(RecipeApiResponseDto response) {
        List<RecipeApiProcessingDto> apiDtos = response.getCookrcp01().getRow();
        Set<Long> existingRecipeApiNos = new HashSet<>(recipeRepository.findAllRecipeApiNos());
        List<Recipe> savedRecipes = new ArrayList<>();

        apiDtos.stream()
                .filter(apiDto -> !existingRecipeApiNos.contains(apiDto.getRecipeApiNo()))
                .forEach(apiDto -> {
                    Recipe recipe = convertToRecipeEntity(apiDto);
                    recipeRepository.save(recipe);
                    saveApiManuals(apiDto.getManual(), recipe);
                    saveParts(apiDto.extractRecipeParts(), recipe);
                    savedRecipes.add(recipe);
                });
        return Mono.just(savedRecipes);
    }

    /**
     * 레시피 API 매뉴얼 목록 저장
     */
    private void saveApiManuals(List<RecipeManualDto> manuals, Recipe savedRecipe) {
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

    /**
     * 레시피 API 재료 목록 저장
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

    /**
     * API 에서 가져온 값을 Entity로 변환
     */
    private Recipe convertToRecipeEntity(RecipeApiProcessingDto apiDto) {
        return Recipe.builder()
                .recipeApiNo(apiDto.getRecipeApiNo())
                .recipeName(apiDto.getRecipeName())
                .recipeType(apiDto.getRecipeType())
                .recipeTip(apiDto.getRecipeTip())
                .build();
    }

    /**
     * 레시피 Entity를 응답 DTO로 변환
     */
    private List<RecipeApiEntityDto> convertToRecipeResponseDtos(List<Recipe> recipes) {
        return recipes.stream()
                .map(recipe -> RecipeApiEntityDto.toEntityDto(
                        recipe,
                        findManualsByRecipe(recipe),
                        findPartsByRecipe(recipe)
                ))
                .toList();
    }

    /**
     * 해당 레시피의 메뉴얼 목록을 찾음
     */
    private List<RecipeManualDto> findManualsByRecipe(Recipe recipe) {
        List<RecipeManual> manuals = manualRepository.findByRecipeRecipeId(recipe.getRecipeId());
        return manuals.stream()
                .map(RecipeManualDto::manualDto)
                .toList();
    }

    /**
     * 해당 레시피의 재료 목록을 찾음
     */
    private List<RecipePartsDto> findPartsByRecipe(Recipe recipe) {
        List<RecipeParts> parts = partsRepository.findByRecipeRecipeId(recipe.getRecipeId());
        return parts.stream()
                .map(RecipePartsDto::partsDto)
                .toList();
    }
}