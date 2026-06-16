package bg.uniplov.fitness.advisor.model;

import java.util.List;

public record WorkoutSessionRecommendation(
        int order,
        String dayLabel,
        String displayName,
        int durationMin,
        List<ExercisePrescriptionRecommendation> exercises
) { }
