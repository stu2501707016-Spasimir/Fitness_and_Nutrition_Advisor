package bg.uniplov.fitness.advisor.model;

import java.util.Set;

public record RecommendationResult(
        UserProfile profile,
        Set<String> inferredUserTypes,
        NutritionPlanRecommendation nutrition,
        TrainingPlanRecommendation training,
        String explanation
) { }
