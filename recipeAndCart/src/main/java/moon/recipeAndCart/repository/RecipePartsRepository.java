package moon.recipeAndCart.repository;

import moon.recipeAndCart.entity.RecipeParts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipePartsRepository extends JpaRepository<RecipeParts, Long> {
    List<RecipeParts> findByRecipeRecipeId(Long recipeId);
}
