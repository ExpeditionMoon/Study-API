package moon.recipeAndCart.controller;

import lombok.RequiredArgsConstructor;
import moon.recipeAndCart.service.RecipeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    @GetMapping("/recipe")
    public Mono<String> fetchAndSaveRecipes(@RequestParam("menu") String menu) {
        return recipeService.fetchAndSaveRecipes(menu);
    }
}
