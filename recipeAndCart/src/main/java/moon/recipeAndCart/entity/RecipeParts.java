package moon.recipeAndCart.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class RecipeParts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long partsId;

    @Column(nullable = false)
    private String partsName;

    @Column(nullable = true)
    private String partsQuantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    @Builder
    public RecipeParts(String partsName, String partsQuantity, Recipe recipe) {
        this.partsName = partsName;
        this.partsQuantity = partsQuantity;
        this.recipe = recipe;
    }
}
