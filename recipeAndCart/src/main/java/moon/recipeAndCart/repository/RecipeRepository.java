package moon.recipeAndCart.repository;

import moon.recipeAndCart.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    @Query("""
           SELECT r.recipeApiNo FROM Recipe r
            """)
    List<Long> findAllRecipeApiNos();
}
