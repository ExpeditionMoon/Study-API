package moon.recipeAndCart.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import moon.recipeAndCart.dto.ApiResponse;
import moon.recipeAndCart.dto.RecipeRequestDto;
import moon.recipeAndCart.dto.RecipeResponseDto;
import moon.recipeAndCart.service.RecipeCustomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/recipe")
@RequiredArgsConstructor
public class RecipeCustomController {

    private final RecipeCustomService customService;

    /**
     * 레시피 관련 정보와 이미지 파일들 생성
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<RecipeResponseDto>> createRecipe(
            @RequestParam("recipeData") String recipeData,
            @RequestParam("files") List<MultipartFile> files
    ) throws JsonProcessingException {
        RecipeRequestDto requestDto = new ObjectMapper().readValue(recipeData, RecipeRequestDto.class);
        return ResponseEntity.ok(customService.createRecipe(requestDto, files));
    }

    /**
     * 해당 레시피의 작성자가 레시피 수정
     */
    @PatchMapping("/update")
    public ResponseEntity<ApiResponse<RecipeResponseDto>> updateRecipe(
            @RequestParam("recipeId") Long recipeId,
            @RequestParam("recipeData") String recipeData,
            @RequestParam("files") List<MultipartFile> files
    ) throws IOException {
        RecipeRequestDto requestDto = new ObjectMapper().readValue(recipeData, RecipeRequestDto.class);
        return ResponseEntity.ok(customService.updateRecipe(recipeId, requestDto, files));
    }

    /**
     * 해당 레시피의 작성자가 레시피 삭제
     */
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse> deleteRecipe(
            @RequestParam Long recipeId
    ) {
        return ResponseEntity.ok(customService.deleteRecipe(recipeId));
    }
}
