package bg.uniplov.fitness.advisor.agents;

import bg.uniplov.fitness.advisor.model.ProfileValidationException;
import bg.uniplov.fitness.advisor.model.UserProfile;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

class UserIntakeAgentTest {
    private final UserIntakeAgent agent = new UserIntakeAgent();

    @Test
    void rejectsInvalidAge() {
        UserProfile raw = new UserProfile(
                "Test", 8, 170, 70, 0, "UnknownBmi",
                "WeightLoss", "BeginnerAfterLongBreak", "HomeWorkoutEnvironment",
                3, 45, 3, Set.of(), Set.of(), Set.of(), Set.of("NoEquipment")
        );

        assertThrows(ProfileValidationException.class, () -> agent.execute(raw));
    }
}
