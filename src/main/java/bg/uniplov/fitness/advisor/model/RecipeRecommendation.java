package bg.uniplov.fitness.advisor.model;

import java.util.List;
import java.util.Set;

public record RecipeRecommendation(
        String individualName,
        String displayName,
        String imagePath,
        String instructions,
        double servingSizeG,
        int prepTimeMin,
        int cookingTimeMin,
        String difficultyLabel,
        MacroProfile macros,
        List<IngredientRecommendation> ingredients,
        Set<String> allergens,
        Set<String> incompatibleConstraints,
        Set<String> goals,
        Set<String> dietaryApproaches
) { }
