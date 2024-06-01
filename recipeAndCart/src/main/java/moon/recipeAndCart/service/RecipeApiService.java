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

    public Mono<List<RecipeApiEntityDto>> fetchAndSaveRecipesByMenu(String menu) {
        int currentPage = menuPageMap.getOrDefault(menu, 1);
        return fetchMenu("RCP_NM", menu, currentPage, PAGE_SIZE)
                .flatMap(response -> saveRecipesFromApiResponse(response)
                        .doOnNext(savedRecipes -> menuPageMap.put(menu, currentPage + 1))
                        .map(this::convertToRecipeResponseDtos));
    }

    public Mono<List<RecipeApiEntityDto>> fetchAndSaveRecipesByType(String type) {
        int currentPage = typePageMap.getOrDefault(type, 1);
        return fetchMenu("RCP_PAT2", type, currentPage, PAGE_SIZE)
                .flatMap(response -> saveRecipesFromApiResponse(response)
                        .doOnNext(savedRecipes -> typePageMap.put(type, currentPage + 1))
                        .map(this::convertToRecipeResponseDtos));
    }

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

    private Recipe convertToRecipeEntity(RecipeApiProcessingDto apiDto) {
        return Recipe.builder()
                .recipeApiNo(apiDto.getRecipeApiNo())
                .recipeName(apiDto.getRecipeName())
                .recipeType(apiDto.getRecipeType())
                .recipeTip(apiDto.getRecipeTip())
                .build();
    }

    private List<RecipeApiEntityDto> convertToRecipeResponseDtos(List<Recipe> recipes) {
        return recipes.stream()
                .map(recipe -> RecipeApiEntityDto.toEntityDto(
                        recipe,
                        findManualsByRecipe(recipe),
                        findPartsByRecipe(recipe)
                ))
                .toList();
    }

    private List<RecipeManualDto> findManualsByRecipe(Recipe recipe) {
        List<RecipeManual> manuals = manualRepository.findByRecipeRecipeId(recipe.getRecipeId());
        return manuals.stream()
                .map(RecipeManualDto::manualDto)
                .toList();
    }

    private List<RecipePartsDto> findPartsByRecipe(Recipe recipe) {
        List<RecipeParts> parts = partsRepository.findByRecipeRecipeId(recipe.getRecipeId());
        return parts.stream()
                .map(RecipePartsDto::partsDto)
                .toList();
    }
}