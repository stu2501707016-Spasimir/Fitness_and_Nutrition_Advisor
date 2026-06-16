package bg.uniplov.fitness.advisor.model;

import java.util.Set;

public record RecommendationContext(
        UserProfile profile,
        MappedProfile mappedProfile,
        Set<String> inferredUserTypes
) { }
