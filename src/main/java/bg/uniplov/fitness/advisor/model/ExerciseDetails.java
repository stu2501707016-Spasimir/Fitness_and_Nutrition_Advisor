package bg.uniplov.fitness.advisor.model;

import java.util.Set;

public record ExerciseDetails(
        String individualName,
        String displayName,
        String imagePath,
        String gifPath,
        String executionInstructions,
        String commonMistakes,
        String easierVariation,
        String harderVariation,
        Set<String> muscleGroups,
        Set<String> equipment
) { }
