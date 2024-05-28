package moon.recipeAndCart.controller;

import lombok.RequiredArgsConstructor;
import moon.recipeAndCart.dto.custom.RecipeCustomDto;
import moon.recipeAndCart.service.RecipeCustomService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recipe")
@RequiredArgsConstructor
public class RecioeCustomController {

    private final RecipeCustomService customService;

    @PostMapping("/create")
    public RecipeCustomDto createRecipe(@RequestBody RecipeCustomDto recipeCustomDto) {
        return customService.saveRecipe(recipeCustomDto);
    }
}
