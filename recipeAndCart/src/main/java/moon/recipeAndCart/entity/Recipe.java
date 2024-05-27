package moon.recipeAndCart.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recipeId;

    @Column(nullable = true)
    private Long recipeApiNo;

    @Column(nullable = false)
    private String recipeName;

    @Column(nullable = false)
    private String recipeType;

    @Column(nullable = false)
    private String recipeParts;

    @Column(nullable = true)
    private String recipeNaTip;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<RecipeManual> recipeManual;

    @Builder
    public Recipe(Long recipeApiNo, String recipeName, String recipeType, String recipeParts, String recipeNaTip, List<RecipeManual> recipeManual) {
        this.recipeApiNo = recipeApiNo;
        this.recipeName = recipeName;
        this.recipeType = recipeType;
        this.recipeParts = recipeParts;
        this.recipeNaTip = recipeNaTip;
        this.recipeManual = recipeManual;
    }
}
