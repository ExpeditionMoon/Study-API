package moon.recipeAndCart.repository;

import moon.recipeAndCart.entity.RecipeManual;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeManualRepository extends JpaRepository<RecipeManual, Long> {
    List<RecipeManual> findByRecipeRecipeId(Long recipeId);
}
