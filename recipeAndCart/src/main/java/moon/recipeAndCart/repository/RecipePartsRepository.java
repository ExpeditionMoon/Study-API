package moon.recipeAndCart.repository;

import moon.recipeAndCart.entity.RecipeParts;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipePartsRepository extends JpaRepository<RecipeParts, Long> {
    List<RecipeParts> findByRecipeRecipeId(Long recipeId);
}
