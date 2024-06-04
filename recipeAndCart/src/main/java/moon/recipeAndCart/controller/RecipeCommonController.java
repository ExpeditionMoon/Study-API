package moon.recipeAndCart.controller;

import lombok.RequiredArgsConstructor;
import moon.recipeAndCart.dto.ApiResponse;
import moon.recipeAndCart.dto.RecipeListDto;
import moon.recipeAndCart.dto.RecipeResponseDto;
import moon.recipeAndCart.service.RecipeCommonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recipe")
@RequiredArgsConstructor
public class RecipeCommonController {

    private final RecipeCommonService commonService;

    /**
     * 레시피 페이징 처리해서 가져오기
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<RecipeListDto>>> getAllRecipe(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        ApiResponse<List<RecipeListDto>> recipeList = commonService.getAllRecipe(page, size);
        return ResponseEntity.ok(recipeList);
    }

    /**
     * 해당 레시피 정보 가져오기
     */
    @GetMapping("/{recipeId}")
    public ResponseEntity<ApiResponse<RecipeResponseDto>> getRecipeId(
            @PathVariable("recipeId") Long recipeId
    ) {
        ApiResponse<RecipeResponseDto> recipe = commonService.getRecipeId(recipeId);
        return ResponseEntity.ok(recipe);
    }
}
