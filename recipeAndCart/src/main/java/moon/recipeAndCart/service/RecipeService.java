package moon.recipeAndCart.service;

import lombok.RequiredArgsConstructor;
import moon.recipeAndCart.config.WebClientConfig;
import moon.recipeAndCart.dto.RecipeApiDto;
import moon.recipeAndCart.dto.RecipeApiResponse;
import moon.recipeAndCart.dto.RecipeManualDto;
import moon.recipeAndCart.entity.Recipe;
import moon.recipeAndCart.entity.RecipeManual;
import moon.recipeAndCart.entity.RecipeParts;
import moon.recipeAndCart.repository.RecipeManualRepository;
import moon.recipeAndCart.repository.RecipePartsRepository;
import moon.recipeAndCart.repository.RecipeRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final WebClientConfig webClientConfig;
    private final RecipeRepository recipeRepository;
    private final RecipeManualRepository manualRepository;
    private final RecipePartsRepository partsRepository;

    public Mono<String> fetchAndSaveRecipesByMenu(String menu) {
        return fetchMenu("RCP_NM", menu)
                .doOnNext(this::saveRecipesFromApiResponse)
                .map(response -> "레시피를 성공적으로 저장했습니다.");
    }

    public Mono<String> fetchAndSaveRecipesByType(String type) {
        return fetchMenu("RCP_PAT2", type)
                .doOnNext(this::saveRecipesFromApiResponse)
                .map(response -> "레시피를 성공적으로 저장했습니다.");
    }

    private Mono<RecipeApiResponse> fetchMenu(String key, String value) {
        return webClientConfig.recipeWebClient().get()
                .uri(uriBuilder -> uriBuilder
                        .path("/" + key + "=" + value)
                        .build())
                .retrieve()
                .bodyToMono(RecipeApiResponse.class);
    }

    private void saveRecipesFromApiResponse(RecipeApiResponse response) {
        List<RecipeApiDto> apiDtos = response.getCookrcp01().getRow();
        apiDtos.forEach(apiDto -> {
            Recipe recipe = convertToRecipeEntity(apiDto);
            recipeRepository.save(recipe);
            saveManuals(apiDto.getManual(), recipe);
            saveParts(apiDto.extractRecipeParts(), recipe);
        });
    }

    private void saveManuals(List<RecipeManualDto> manuals, Recipe savedRecipe) {
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

    private void saveParts(List<String> parts, Recipe savedRecipe) {
        parts.forEach(part -> {
            RecipeParts recipePart = RecipeParts.builder()
                    .partsName(part)
                    .recipe(savedRecipe)
                    .build();
            partsRepository.save(recipePart);
        });
    }

    private Recipe convertToRecipeEntity(RecipeApiDto apiDto) {
        return Recipe.builder()
                .recipeApiNo(apiDto.getRecipeApiNo())
                .recipeName(apiDto.getRecipeName())
                .recipeType(apiDto.getRecipeType())
                .recipeParts(apiDto.getRecipeParts())
                .recipeNaTip(apiDto.getRecipeNaTip())
                .build();
    }
}
