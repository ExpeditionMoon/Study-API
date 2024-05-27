package moon.recipeAndCart.repository;

import moon.recipeAndCart.entity.RecipeManual;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeManualRepository extends JpaRepository<RecipeManual, Long> {
}
