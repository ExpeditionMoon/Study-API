package moon.recipeAndCart.controller;

import lombok.RequiredArgsConstructor;
import moon.recipeAndCart.service.RecipeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/recipe")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    @GetMapping("/menu")
    public Mono<String> fetchAndSaveRecipes(@RequestParam("menu") String menu) {
        return recipeService.fetchAndSaveRecipesByMenu(menu);
    }

    @GetMapping("/type")
    public Mono<String> fetchAndSaveRecipesByType(@RequestParam("type") String type) {
        return recipeService.fetchAndSaveRecipesByType(type);
    }
}
