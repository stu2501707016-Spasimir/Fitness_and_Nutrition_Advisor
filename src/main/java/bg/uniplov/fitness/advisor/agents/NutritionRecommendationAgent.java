package bg.uniplov.fitness.advisor.agents;

import bg.uniplov.fitness.advisor.model.*;
import bg.uniplov.fitness.advisor.ontology.OntologyService;

import java.util.*;
import java.util.stream.Collectors;

public final class NutritionRecommendationAgent implements Agent<RecommendationContext, NutritionPlanRecommendation> {
    private final OntologyService ontology;

    public NutritionRecommendationAgent(OntologyService ontology) {
        this.ontology = Objects.requireNonNull(ontology);
    }

    @Override
    public NutritionPlanRecommendation execute(RecommendationContext context) {
        List<ScoredPlan> scored = ontology.getIndividualsOfClass("NutritionPlan").stream()
                .map(plan -> scorePlan(plan, context.profile()))
                .filter(plan -> plan.score > Integer.MIN_VALUE / 2)
                .sorted(Comparator.comparingInt(ScoredPlan::score).reversed().thenComparing(ScoredPlan::plan))
                .toList();

        if (scored.isEmpty()) {
            throw new IllegalStateException("Не е намерен подходящ хранителен план. Провери assertions в онтологията.");
        }

        ScoredPlan best = scored.get(0);
        List<RecipeRecommendation> recipes = recipesForPlan(best.plan, context.profile()).stream()
                .limit(Math.max(1, context.profile().mealsPerDay()))
                .toList();

        return new NutritionPlanRecommendation(
                best.plan,
                ontology.displayName(best.plan),
                best.score,
                best.reasons,
                recipes
        );
    }

    private ScoredPlan scorePlan(String plan, UserProfile profile) {
        int score = 0;
        List<ScoreReason> reasons = new ArrayList<>();

        Set<String> goals = ontology.getObjectValues(plan, "nutritionPlanSupportsGoal");
        if (goals.contains(profile.goalIndividual())) {
            score += 45;
            reasons.add(new ScoreReason("Планът подкрепя основната цел", 45));
        }

        Set<String> bmiCategories = ontology.getObjectValues(plan, "nutritionPlanSuitableForBmiCategory");
        if (bmiCategories.contains(profile.bmiCategoryIndividual())) {
            score += 25;
            reasons.add(new ScoreReason("Съобразен е с BMI категорията", 25));
        }

        Set<String> experience = ontology.getObjectValues(plan, "nutritionPlanSuitableForTrainingExperience");
        if (experience.contains(profile.trainingExperienceIndividual())) {
            score += 20;
            reasons.add(new ScoreReason("Подходящ е за тренировъчния опит", 20));
        }

        Set<String> approaches = ontology.getObjectValues(plan, "usesDietaryApproach");
        Set<String> matchedApproaches = new LinkedHashSet<>(approaches);
        matchedApproaches.retainAll(profile.preferredDietaryApproachIndividuals());
        if (!matchedApproaches.isEmpty()) {
            score += 10;
            reasons.add(new ScoreReason("Съвпада с предпочитан хранителен подход", 10));
        }

        Set<String> experiencedApproaches = new LinkedHashSet<>(approaches);
        experiencedApproaches.retainAll(profile.dietExperienceIndividuals());
        if (!experiencedApproaches.isEmpty()) {
            score += 8;
            reasons.add(new ScoreReason("Потребителят има опит с този режим", 8));
        }

        List<RecipeRecommendation> recipes = recipesForPlan(plan, profile);
        long incompatibleRecipeCount = recipes.stream()
                .filter(recipe -> !Collections.disjoint(recipe.incompatibleConstraints(), profile.dietaryConstraintIndividuals()))
                .count();
        if (incompatibleRecipeCount > 0) {
            int penalty = (int) incompatibleRecipeCount * -25;
            score += penalty;
            reasons.add(new ScoreReason("Изключени са рецепти, несъвместими с ограниченията", penalty));
        } else if (!profile.dietaryConstraintIndividuals().isEmpty()) {
            score += 12;
            reasons.add(new ScoreReason("Няма конфликт с избраните ограничения", 12));
        }

        if (score <= 0) {
            score -= 30;
            reasons.add(new ScoreReason("Слабо съвпадение спрямо профила", -30));
        }

        return new ScoredPlan(plan, score, reasons);
    }

    private List<RecipeRecommendation> recipesForPlan(String plan, UserProfile profile) {
        Set<String> meals = ontology.getObjectValues(plan, "containsMeal");
        List<RecipeRecommendation> result = new ArrayList<>();
        for (String meal : meals) {
            for (String recipe : ontology.getObjectValues(meal, "hasRecipe")) {
                RecipeRecommendation recommendation = recipeRecommendation(recipe);
                if (Collections.disjoint(recommendation.incompatibleConstraints(), profile.dietaryConstraintIndividuals())) {
                    result.add(recommendation);
                }
            }
        }
        return result.stream()
                .sorted(Comparator.comparing(RecipeRecommendation::displayName))
                .collect(Collectors.toList());
    }

    private RecipeRecommendation recipeRecommendation(String recipe) {
        List<IngredientRecommendation> ingredients = ontology.getObjectValues(recipe, "hasIngredientPortion").stream()
                .map(portion -> new IngredientRecommendation(
                        ontology.getObjectValues(portion, "usesFoodItem").stream()
                                .findFirst()
                                .map(ontology::displayName)
                                .orElse(ontology.humanize(portion)),
                        ontology.dataDouble(portion, "hasAmountGrams", 0.0)
                ))
                .sorted(Comparator.comparing(IngredientRecommendation::foodItem))
                .toList();

        MacroProfile macros = new MacroProfile(
                ontology.dataDouble(recipe, "hasTotalCalories", 0),
                ontology.dataDouble(recipe, "hasTotalProteinG", 0),
                ontology.dataDouble(recipe, "hasTotalCarbsG", 0),
                ontology.dataDouble(recipe, "hasTotalSugarsG", 0),
                ontology.dataDouble(recipe, "hasTotalFatG", 0),
                ontology.dataDouble(recipe, "hasSaturatedFatG", 0),
                ontology.dataDouble(recipe, "hasTotalFiberG", 0),
                ontology.dataDouble(recipe, "hasSaltG", 0)
        );

        return new RecipeRecommendation(
                recipe,
                ontology.displayName(recipe),
                ontology.firstDataValue(recipe, "hasRecipeImagePath").orElse(""),
                ontology.firstDataValue(recipe, "hasPreparationInstructions").orElse(""),
                ontology.dataDouble(recipe, "hasServingSizeG", 0),
                ontology.dataInt(recipe, "hasPrepTimeMin", 0),
                ontology.dataInt(recipe, "hasCookingTimeMin", 0),
                ontology.firstDataValue(recipe, "hasDifficultyLabel").orElse("Средна"),
                macros,
                ingredients,
                ontology.getObjectValues(recipe, "recipeContainsAllergen"),
                ontology.getObjectValues(recipe, "recipeUnsuitableForDietaryConstraint"),
                ontology.getObjectValues(recipe, "recipeSupportsGoal"),
                ontology.getObjectValues(recipe, "recipeSuitableForDietaryApproach")
        );
    }

    private record ScoredPlan(String plan, int score, List<ScoreReason> reasons) { }
}
