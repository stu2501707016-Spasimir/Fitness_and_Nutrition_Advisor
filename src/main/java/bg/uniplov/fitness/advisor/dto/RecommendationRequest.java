package bg.uniplov.fitness.advisor.dto;

import jakarta.validation.constraints.*;
import java.util.LinkedHashSet;
import java.util.Set;

public record RecommendationRequest(
        @NotBlank(message = "Въведи име.")
        String displayName,

        @Min(value = 14, message = "Възрастта трябва да бъде поне 14 години.")
        @Max(value = 90, message = "Възрастта трябва да бъде до 90 години.")
        int age,

        @DecimalMin(value = "120.0", message = "Ръстът трябва да бъде поне 120 см.")
        @DecimalMax(value = "230.0", message = "Ръстът трябва да бъде до 230 см.")
        double heightCm,

        @DecimalMin(value = "35.0", message = "Теглото трябва да бъде поне 35 кг.")
        @DecimalMax(value = "250.0", message = "Теглото трябва да бъде до 250 кг.")
        double weightKg,

        @NotBlank(message = "Избери основна цел.")
        String goalIndividual,

        @NotBlank(message = "Избери тренировъчен опит.")
        String trainingExperienceIndividual,

        @NotBlank(message = "Избери тренировъчна среда.")
        String workoutEnvironmentIndividual,

        @Min(value = 2, message = "Тренировъчните дни трябва да бъдат поне 2.")
        @Max(value = 6, message = "Тренировъчните дни трябва да бъдат до 6.")
        int trainingDaysPerWeek,

        @Min(value = 20, message = "Тренировката трябва да е поне 20 минути.")
        @Max(value = 90, message = "Тренировката трябва да е до 90 минути.")
        int preferredSessionMinutes,

        @Min(value = 2, message = "Храненията трябва да са поне 2.")
        @Max(value = 6, message = "Храненията трябва да са до 6.")
        int mealsPerDay,

        Set<String> dietExperienceIndividuals,
        Set<String> preferredDietaryApproachIndividuals,
        Set<String> dietaryConstraintIndividuals,
        Set<String> availableEquipmentIndividuals
) {
    public Set<String> safeDietExperienceIndividuals() {
        return dietExperienceIndividuals == null ? new LinkedHashSet<>() : new LinkedHashSet<>(dietExperienceIndividuals);
    }

    public Set<String> safePreferredDietaryApproachIndividuals() {
        return preferredDietaryApproachIndividuals == null ? new LinkedHashSet<>() : new LinkedHashSet<>(preferredDietaryApproachIndividuals);
    }

    public Set<String> safeDietaryConstraintIndividuals() {
        return dietaryConstraintIndividuals == null ? new LinkedHashSet<>() : new LinkedHashSet<>(dietaryConstraintIndividuals);
    }

    public Set<String> safeAvailableEquipmentIndividuals() {
        return availableEquipmentIndividuals == null ? new LinkedHashSet<>() : new LinkedHashSet<>(availableEquipmentIndividuals);
    }
}
