package moon.recipeAndCart.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class RecipeManual {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long manualId;

    @Column(nullable = false)
    private Long step;

    @Column(nullable = false)
    private String manualTxt;

    @Column(nullable = true)
    private String manualImgUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    @Builder
    public RecipeManual(Long step, String manualTxt, String manualImgUrl, Recipe recipe) {
        this.step = step;
        this.manualTxt = manualTxt;
        this.manualImgUrl = manualImgUrl;
        this.recipe = recipe;
    }
}
