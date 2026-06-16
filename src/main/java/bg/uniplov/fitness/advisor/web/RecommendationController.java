package bg.uniplov.fitness.advisor.web;

import bg.uniplov.fitness.advisor.agents.BodyProfileAgent;
import bg.uniplov.fitness.advisor.agents.RecommendationCoordinator;
import bg.uniplov.fitness.advisor.dto.OptionsResponse;
import bg.uniplov.fitness.advisor.dto.RecommendationRequest;
import bg.uniplov.fitness.advisor.model.Options;
import bg.uniplov.fitness.advisor.model.RecommendationResult;
import bg.uniplov.fitness.advisor.model.UserProfile;
import bg.uniplov.fitness.advisor.ontology.OntologyService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class RecommendationController {
    private final RecommendationCoordinator coordinator;
    private final BodyProfileAgent bodyProfileAgent = new BodyProfileAgent();
    private final OntologyService ontologyService;

    public RecommendationController(RecommendationCoordinator coordinator, OntologyService ontologyService) {
        this.coordinator = coordinator;
        this.ontologyService = ontologyService;
    }

    @GetMapping("/options")
    public OptionsResponse options() {
        return new OptionsResponse(
                Options.GOALS,
                Options.TRAINING_EXPERIENCE,
                Options.WORKOUT_ENVIRONMENT,
                Options.DIETARY_APPROACHES,
                Options.DIETARY_CONSTRAINTS,
                Options.EQUIPMENT
        );
    }

    @GetMapping("/ontology/health")
    public Map<String, Object> ontologyHealth() {
        return Map.of(
                "status", "OK",
                "ontology", "Fitness_Nutrition_Recommendation_Ontology",
                "userClasses", ontologyService.getIndividualsOfClass("User").size(),
                "nutritionPlans", ontologyService.getIndividualsOfClass("NutritionPlan").size(),
                "trainingPlans", ontologyService.getIndividualsOfClass("TrainingPlan").size(),
                "recipes", ontologyService.getIndividualsOfClass("Recipe").size(),
                "exercises", ontologyService.getIndividualsOfClass("Exercise").size()
        );
    }

    @PostMapping("/recommendations")
    public RecommendationResult recommend(@Valid @RequestBody RecommendationRequest request) {
        UserProfile raw = new UserProfile(
                request.displayName(),
                request.age(),
                request.heightCm(),
                request.weightKg(),
                0.0,
                "UnknownBmi",
                request.goalIndividual(),
                request.trainingExperienceIndividual(),
                request.workoutEnvironmentIndividual(),
                request.trainingDaysPerWeek(),
                request.preferredSessionMinutes(),
                request.mealsPerDay(),
                request.safeDietExperienceIndividuals(),
                request.safePreferredDietaryApproachIndividuals(),
                request.safeDietaryConstraintIndividuals(),
                request.safeAvailableEquipmentIndividuals()
        );
        UserProfile profiled = bodyProfileAgent.execute(raw);
        return coordinator.recommend(profiled);
    }
}
