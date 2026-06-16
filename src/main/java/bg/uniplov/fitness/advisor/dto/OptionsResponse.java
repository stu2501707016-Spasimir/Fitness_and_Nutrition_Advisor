package bg.uniplov.fitness.advisor.dto;

import bg.uniplov.fitness.advisor.model.OntologyOption;
import java.util.List;

public record OptionsResponse(
        List<OntologyOption> goals,
        List<OntologyOption> trainingExperience,
        List<OntologyOption> workoutEnvironment,
        List<OntologyOption> dietaryApproaches,
        List<OntologyOption> dietaryConstraints,
        List<OntologyOption> equipment
) { }
