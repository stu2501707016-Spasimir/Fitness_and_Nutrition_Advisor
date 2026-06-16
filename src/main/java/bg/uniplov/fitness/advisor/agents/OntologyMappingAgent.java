package bg.uniplov.fitness.advisor.agents;

import bg.uniplov.fitness.advisor.model.MappedProfile;
import bg.uniplov.fitness.advisor.model.UserProfile;

import java.util.Set;

public final class OntologyMappingAgent implements Agent<UserProfile, MappedProfile> {
    @Override
    public MappedProfile execute(UserProfile profile) {
        String safeName = profile.displayName() == null || profile.displayName().isBlank()
                ? "Candidate"
                : profile.displayName().replaceAll("[^A-Za-z0-9]", "");
        return new MappedProfile(
                safeName,
                profile.goalIndividual(),
                profile.trainingExperienceIndividual(),
                profile.bmiCategoryIndividual(),
                profile.workoutEnvironmentIndividual(),
                Set.copyOf(profile.dietExperienceIndividuals()),
                Set.copyOf(profile.preferredDietaryApproachIndividuals()),
                Set.copyOf(profile.dietaryConstraintIndividuals()),
                Set.copyOf(profile.availableEquipmentIndividuals())
        );
    }
}
