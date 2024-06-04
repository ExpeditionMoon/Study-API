package moon.recipeAndCart.repository;

import moon.recipeAndCart.entity.RecipeManual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeManualRepository extends JpaRepository<RecipeManual, Long> {
    List<RecipeManual> findByRecipeRecipeId(Long recipeId);

    @Query("""
            SELECT MAX(m.step) FROM RecipeManual m
            WHERE m.recipe.recipeId = :recipeId
            """)
    Optional<Long> findLastStepByRecipeId(Long recipeId);

    Optional<RecipeManual> findManualImgUrlByRecipeRecipeIdAndStep(Long recipeId, Long step);
}
