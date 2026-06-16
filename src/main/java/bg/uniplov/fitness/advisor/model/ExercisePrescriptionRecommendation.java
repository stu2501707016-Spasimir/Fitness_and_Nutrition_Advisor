package bg.uniplov.fitness.advisor.model;

public record ExercisePrescriptionRecommendation(
        int order,
        ExerciseDetails exercise,
        int sets,
        int minReps,
        int maxReps,
        int workSeconds,
        int restSeconds,
        String note
) { }
