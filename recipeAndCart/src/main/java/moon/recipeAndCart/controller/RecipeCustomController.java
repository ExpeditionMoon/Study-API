package moon.recipeAndCart.controller;

import lombok.RequiredArgsConstructor;
import moon.recipeAndCart.dto.common.RecipeResponseDto;
import moon.recipeAndCart.service.RecipeCustomService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recipe")
@RequiredArgsConstructor
public class RecipeCustomController {

    private final RecipeCustomService customService;

    @PostMapping("/create")
    public RecipeResponseDto createRecipe(@RequestBody RecipeResponseDto recipeCustomDto) {
        return customService.saveRecipe(recipeCustomDto);
    }
}
