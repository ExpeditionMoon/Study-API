package moon.recipeAndCart.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import moon.recipeAndCart.dto.RecipeRequestDto;
import moon.recipeAndCart.dto.RecipeResponseDto;
import moon.recipeAndCart.service.RecipeCustomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<RecipeResponseDto> createRecipe(
            @RequestParam("recipeData") String recipeData,
            @RequestParam("files") List<MultipartFile> files
    ) throws JsonProcessingException {
        RecipeRequestDto requestDto = new ObjectMapper().readValue(recipeData, RecipeRequestDto.class);
        RecipeResponseDto response = customService.createRecipe(requestDto, files);
        return ResponseEntity.ok(response);
    }
}
