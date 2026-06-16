package bg.uniplov.fitness.advisor.agents;

import bg.uniplov.fitness.advisor.model.MappedProfile;
import bg.uniplov.fitness.advisor.model.RecommendationContext;
import bg.uniplov.fitness.advisor.model.UserProfile;
import bg.uniplov.fitness.advisor.ontology.OntologyService;

import java.util.Set;

public final class OntologyReasoningAgent {
    private final OntologyService ontologyService;

    public OntologyReasoningAgent(OntologyService ontologyService) {
        this.ontologyService = ontologyService;
    }

    public RecommendationContext execute(UserProfile profile, MappedProfile mappedProfile) {
        String temporaryUserName = ontologyService.createTemporaryUser(profile);
        Set<String> inferredTypes = ontologyService.getInferredTypes(temporaryUserName);
        return new RecommendationContext(profile, mappedProfile, inferredTypes);
    }
}
