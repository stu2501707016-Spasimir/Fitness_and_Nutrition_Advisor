package bg.uniplov.fitness.advisor.agents;

import bg.uniplov.fitness.advisor.model.ProfileValidationException;
import bg.uniplov.fitness.advisor.model.UserProfile;

public final class UserIntakeAgent implements Agent<UserProfile, UserProfile> {
    @Override
    public UserProfile execute(UserProfile profile) {
        if (profile == null) {
            throw new ProfileValidationException("Липсва потребителски профил.");
        }
        if (profile.age() < 14 || profile.age() > 90) {
            throw new ProfileValidationException("Възрастта трябва да бъде между 14 и 90 години.");
        }
        if (profile.heightCm() < 120 || profile.heightCm() > 230) {
            throw new ProfileValidationException("Ръстът трябва да бъде между 120 и 230 см.");
        }
        if (profile.weightKg() < 35 || profile.weightKg() > 250) {
            throw new ProfileValidationException("Теглото трябва да бъде между 35 и 250 кг.");
        }
        if (profile.trainingDaysPerWeek() < 2 || profile.trainingDaysPerWeek() > 6) {
            throw new ProfileValidationException("Тренировъчните дни трябва да бъдат между 2 и 6.");
        }
        if (profile.preferredSessionMinutes() < 20 || profile.preferredSessionMinutes() > 90) {
            throw new ProfileValidationException("Продължителността на тренировка трябва да бъде между 20 и 90 минути.");
        }
        if (profile.mealsPerDay() < 2 || profile.mealsPerDay() > 6) {
            throw new ProfileValidationException("Храненията на ден трябва да бъдат между 2 и 6.");
        }
        return profile;
    }
}
