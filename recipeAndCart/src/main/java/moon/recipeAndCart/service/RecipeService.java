package moon.recipeAndCart.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moon.recipeAndCart.config.WebClientConfig;
import moon.recipeAndCart.dto.RecipeApiDto;
import moon.recipeAndCart.dto.RecipeApiResponse;
import moon.recipeAndCart.dto.RecipeManualDto;
import moon.recipeAndCart.entity.Recipe;
import moon.recipeAndCart.entity.RecipeManual;
import moon.recipeAndCart.repository.RecipeManualRepository;
import moon.recipeAndCart.repository.RecipeRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecipeService {

    private final WebClientConfig webClientConfig;
    private final RecipeRepository recipeRepository;
    private final RecipeManualRepository manualRepository;

    // TODO. 재료 저장 로직 구현
    public Mono<String> fetchAndSaveRecipes(String menu) {
        return fetchMenu(menu)
                .doOnNext(this::saveRecipesFromApiResponse)
                .map(response -> "레시피를 성공적으로 저장했습니다.");
    }

    private Mono<RecipeApiResponse> fetchMenu(String menu) {
        return webClientConfig.recipeWebClient().get()
                .uri(uriBuilder -> uriBuilder
                        .path("/RCP_NM=" + menu)
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
