package bg.uniplov.fitness.advisor.model;

import java.util.Set;

public record UserProfile(
        String displayName,
        int age,
        double heightCm,
        double weightKg,
        double bmi,
        String bmiCategoryIndividual,
        String goalIndividual,
        String trainingExperienceIndividual,
        String workoutEnvironmentIndividual,
        int trainingDaysPerWeek,
        int preferredSessionMinutes,
        int mealsPerDay,
        Set<String> dietExperienceIndividuals,
        Set<String> preferredDietaryApproachIndividuals,
        Set<String> dietaryConstraintIndividuals,
        Set<String> availableEquipmentIndividuals
) {
    public UserProfile withBodyMetrics(double newBmi, String newBmiCategoryIndividual) {
        return new UserProfile(
                displayName,
                age,
                heightCm,
                weightKg,
                newBmi,
                newBmiCategoryIndividual,
                goalIndividual,
                trainingExperienceIndividual,
                workoutEnvironmentIndividual,
                trainingDaysPerWeek,
                preferredSessionMinutes,
                mealsPerDay,
                Set.copyOf(dietExperienceIndividuals),
                Set.copyOf(preferredDietaryApproachIndividuals),
                Set.copyOf(dietaryConstraintIndividuals),
                Set.copyOf(availableEquipmentIndividuals)
        );
    }
}
