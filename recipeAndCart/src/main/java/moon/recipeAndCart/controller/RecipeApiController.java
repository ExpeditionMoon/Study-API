package moon.recipeAndCart.controller;

import lombok.RequiredArgsConstructor;
import moon.recipeAndCart.dto.official.RecipeApiEntityDto;
import moon.recipeAndCart.service.RecipeApiService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/recipe")
@RequiredArgsConstructor
public class RecipeApiController {

    private final RecipeApiService recipeService;

    /**
     * 메뉴 이름으로 가져온 레시피를 저장
     */
    @GetMapping("/menu")
    public Mono<List<RecipeApiEntityDto>> fetchAndSaveRecipes(@RequestParam("menu") String menu) {
        return recipeService.fetchAndSaveRecipesByMenu(menu);
    }

    /**
     * 메뉴 종류로 가져온 레시피를 저장
     */
    @GetMapping("/type")
    public Mono<List<RecipeApiEntityDto>> fetchAndSaveRecipesByType(@RequestParam("type") String type) {
        return recipeService.fetchAndSaveRecipesByType(type);
    }
}
