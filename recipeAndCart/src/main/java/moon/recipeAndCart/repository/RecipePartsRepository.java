package moon.recipeAndCart.repository;

import moon.recipeAndCart.entity.RecipeParts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipePartsRepository extends JpaRepository<RecipeParts, Long> {
}
