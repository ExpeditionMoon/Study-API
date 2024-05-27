package moon.recipeAndCart.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeApiResponse {
    @JsonProperty("COOKRCP01")
    private CookRcp01 cookrcp01;
}
