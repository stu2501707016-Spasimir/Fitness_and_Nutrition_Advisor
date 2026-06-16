package bg.uniplov.fitness.advisor.agents;

import bg.uniplov.fitness.advisor.model.UserProfile;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class BodyProfileAgent implements Agent<UserProfile, UserProfile> {
    @Override
    public UserProfile execute(UserProfile profile) {
        double heightM = profile.heightCm() / 100.0;
        double bmi = profile.weightKg() / (heightM * heightM);
        double rounded = BigDecimal.valueOf(bmi).setScale(1, RoundingMode.HALF_UP).doubleValue();
        return profile.withBodyMetrics(rounded, bmiCategory(rounded));
    }

    private String bmiCategory(double bmi) {
        if (bmi < 18.5) return "UnderweightBmi";
        if (bmi < 25.0) return "NormalWeightBmi";
        if (bmi < 30.0) return "OverweightBmi";
        return "ObeseBmi";
    }
}
