package bg.uniplov.fitness.advisor.model;

import java.util.List;
import java.util.Set;

public record TrainingPlanRecommendation(
        String individualName,
        String displayName,
        String description,
        int score,
        int trainingDaysPerWeek,
        int estimatedDurationMin,
        Set<String> workoutEnvironments,
        Set<String> trainingStyles,
        List<ScoreReason> reasons,
        List<WorkoutSessionRecommendation> sessions
) { }
