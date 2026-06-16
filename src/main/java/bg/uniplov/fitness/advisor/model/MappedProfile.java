package bg.uniplov.fitness.advisor.model;

import java.util.Set;

public record MappedProfile(
        String userIndividualShortName,
        String goalIndividual,
        String trainingExperienceIndividual,
        String bmiCategoryIndividual,
        String workoutEnvironmentIndividual,
        Set<String> dietaryApproachExperienceIndividuals,
        Set<String> preferredDietaryApproachIndividuals,
        Set<String> dietaryConstraintIndividuals,
        Set<String> availableEquipmentIndividuals
) { }
