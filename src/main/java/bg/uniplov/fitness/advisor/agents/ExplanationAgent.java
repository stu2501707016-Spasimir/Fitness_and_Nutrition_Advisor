package bg.uniplov.fitness.advisor.agents;

import bg.uniplov.fitness.advisor.model.NutritionPlanRecommendation;
import bg.uniplov.fitness.advisor.model.RecommendationContext;
import bg.uniplov.fitness.advisor.model.ScoreReason;
import bg.uniplov.fitness.advisor.model.TrainingPlanRecommendation;

import java.util.Comparator;
import java.util.stream.Collectors;

public final class ExplanationAgent {
    public String explain(RecommendationContext context,
                          NutritionPlanRecommendation nutrition,
                          TrainingPlanRecommendation training) {
        String inferred = context.inferredUserTypes().stream()
                .sorted()
                .collect(Collectors.joining(", "));

        String nutritionReasons = nutrition.reasons().stream()
                .sorted(Comparator.comparingInt(ScoreReason::points).reversed())
                .map(ScoreReason::label)
                .distinct()
                .collect(Collectors.joining("; "));

        String trainingReasons = training.reasons().stream()
                .sorted(Comparator.comparingInt(ScoreReason::points).reversed())
                .map(ScoreReason::label)
                .distinct()
                .collect(Collectors.joining("; "));

        return String.join(System.lineSeparator() + System.lineSeparator(),
                "Reasoner-ът класифицира потребителя като: " + fallback(inferred, "няма допълнителни inferred класове") + ".",
                "Избраният хранителен план е " + nutrition.displayName() + ", защото: " + fallback(nutritionReasons, "няма записани scoring причини") + ".",
                "Избраната тренировъчна програма е " + training.displayName() + ", защото: " + fallback(trainingReasons, "няма записани scoring причини") + ".",
                "Важно: резултатите са учебна експертна препоръка, основана на онтология и правила за съвпадение. Те не заместват лекарска или диетологична консултация."
        );
    }

    private String fallback(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}
