package bg.uniplov.fitness.advisor.agents;

import bg.uniplov.fitness.advisor.model.UserProfile;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BodyProfileAgentTest {
    private final BodyProfileAgent agent = new BodyProfileAgent();

    @Test
    void calculatesOverweightBmiCategory() {
        UserProfile raw = new UserProfile(
                "Ivan", 25, 183, 90, 0, "UnknownBmi",
                "WeightLoss", "BeginnerAfterLongBreak", "HomeWorkoutEnvironment",
                3, 45, 3, Set.of(), Set.of(), Set.of(), Set.of("NoEquipment")
        );

        UserProfile result = agent.execute(raw);

        assertEquals(26.9, result.bmi(), 0.01);
        assertEquals("OverweightBmi", result.bmiCategoryIndividual());
    }
}
