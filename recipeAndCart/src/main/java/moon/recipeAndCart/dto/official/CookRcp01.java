package moon.recipeAndCart.dto.official;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CookRcp01 {
    private List<RecipeApiProcessingDto> row;
}
