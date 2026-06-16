package bg.uniplov.fitness.advisor.agents;

import bg.uniplov.fitness.advisor.model.*;
import bg.uniplov.fitness.advisor.ontology.OntologyService;

public final class RecommendationCoordinator {
    private final UserIntakeAgent intakeAgent = new UserIntakeAgent();
    private final BodyProfileAgent bodyProfileAgent = new BodyProfileAgent();
    private final OntologyMappingAgent mappingAgent = new OntologyMappingAgent();
    private final OntologyReasoningAgent reasoningAgent;
    private final NutritionRecommendationAgent nutritionAgent;
    private final TrainingRecommendationAgent trainingAgent;
    private final ExplanationAgent explanationAgent = new ExplanationAgent();

    public RecommendationCoordinator(OntologyService ontologyService) {
        this.reasoningAgent = new OntologyReasoningAgent(ontologyService);
        this.nutritionAgent = new NutritionRecommendationAgent(ontologyService);
        this.trainingAgent = new TrainingRecommendationAgent(ontologyService);
    }

    public synchronized RecommendationResult recommend(UserProfile rawProfile) {
        UserProfile validated = intakeAgent.execute(rawProfile);
        UserProfile profiled = bodyProfileAgent.execute(validated);
        MappedProfile mapped = mappingAgent.execute(profiled);
        RecommendationContext context = reasoningAgent.execute(profiled, mapped);
        NutritionPlanRecommendation nutrition = nutritionAgent.execute(context);
        TrainingPlanRecommendation training = trainingAgent.execute(context);
        String explanation = explanationAgent.explain(context, nutrition, training);
        return new RecommendationResult(profiled, context.inferredUserTypes(), nutrition, training, explanation);
    }
}
