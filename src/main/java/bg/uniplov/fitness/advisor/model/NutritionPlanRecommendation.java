package bg.uniplov.fitness.advisor.model;

import java.util.List;

public record NutritionPlanRecommendation(
        String individualName,
        String displayName,
        int score,
        List<ScoreReason> reasons,
        List<RecipeRecommendation> recipes
) { }
