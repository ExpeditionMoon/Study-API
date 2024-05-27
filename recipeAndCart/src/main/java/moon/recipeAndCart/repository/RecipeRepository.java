package moon.recipeAndCart.repository;

import moon.recipeAndCart.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
}
