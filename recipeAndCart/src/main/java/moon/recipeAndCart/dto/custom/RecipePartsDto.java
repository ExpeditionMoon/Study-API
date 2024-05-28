package moon.recipeAndCart.dto.custom;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipePartsDto {
    private String partsName;
    private String partsQuantity;

    @Override
    public String toString() {
        return partsName;
    }
}
