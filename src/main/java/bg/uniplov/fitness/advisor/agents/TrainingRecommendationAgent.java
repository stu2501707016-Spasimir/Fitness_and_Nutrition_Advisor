package bg.uniplov.fitness.advisor.agents;

import bg.uniplov.fitness.advisor.model.*;
import bg.uniplov.fitness.advisor.ontology.OntologyService;

import java.util.*;
import java.util.stream.Collectors;

public final class TrainingRecommendationAgent implements Agent<RecommendationContext, TrainingPlanRecommendation> {
    private static final String NO_EQUIPMENT = "NoEquipment";
    private static final int MIN_EXERCISES_PER_SESSION = 4;
    private static final int MAX_EXERCISES_PER_SESSION = 6;

    private final OntologyService ontology;

    public TrainingRecommendationAgent(OntologyService ontology) {
        this.ontology = Objects.requireNonNull(ontology);
    }

    @Override
    public TrainingPlanRecommendation execute(RecommendationContext context) {
        List<ScoredPlan> scored = ontology.getIndividualsOfClass("TrainingPlan").stream()
                .map(plan -> scorePlan(plan, context.profile()))
                .sorted(Comparator.comparingInt(ScoredPlan::score).reversed().thenComparing(ScoredPlan::plan))
                .toList();

        if (scored.isEmpty()) {
            throw new IllegalStateException("Не е намерен подходящ тренировъчен план. Провери assertions в онтологията.");
        }

        ScoredPlan best = scored.get(0);
        List<WorkoutSessionRecommendation> sessions = sessionsForPlan(best.plan, context.profile());

        return new TrainingPlanRecommendation(
                best.plan,
                trainingPlanName(best.plan),
                ontology.firstDataValue(best.plan, "hasPlanDescription").orElse(""),
                best.score,
                ontology.dataInt(best.plan, "hasTrainingDaysPerWeek", 0),
                ontology.dataInt(best.plan, "hasEstimatedWorkoutDurationMin", 0),
                ontology.getObjectValues(best.plan, "trainingPlanSuitableForWorkoutEnvironment"),
                ontology.getObjectValues(best.plan, "usesTrainingStyle"),
                best.reasons,
                sessions
        );
    }

    private String trainingPlanName(String plan) {
        return ontology.firstDataValue(plan, "hasTrainingPlanName")
                .orElseGet(() -> ontology.displayName(plan));
    }

    private ScoredPlan scorePlan(String plan, UserProfile profile) {
        int score = 0;
        List<ScoreReason> reasons = new ArrayList<>();

        if (ontology.getObjectValues(plan, "trainingPlanSupportsGoal").contains(profile.goalIndividual())) {
            score += 40;
            reasons.add(new ScoreReason("Програмата подкрепя основната цел", 40));
        }
        if (ontology.getObjectValues(plan, "trainingPlanSuitableForTrainingExperience").contains(profile.trainingExperienceIndividual())) {
            score += 25;
            reasons.add(new ScoreReason("Подходяща е за тренировъчния опит", 25));
        }
        if (ontology.getObjectValues(plan, "trainingPlanSuitableForWorkoutEnvironment").contains(profile.workoutEnvironmentIndividual())) {
            score += 25;
            reasons.add(new ScoreReason("Съвпада с избраната среда", 25));
        }

        int planDays = ontology.dataInt(plan, "hasTrainingDaysPerWeek", 0);
        if (planDays > 0) {
            int diff = Math.abs(planDays - profile.trainingDaysPerWeek());
            int points = Math.max(0, 15 - diff * 5);
            score += points;
            reasons.add(new ScoreReason("Брой тренировки седмично: " + planDays, points));
        }

        int duration = ontology.dataInt(plan, "hasEstimatedWorkoutDurationMin", 0);
        if (duration > 0 && duration <= profile.preferredSessionMinutes()) {
            score += 10;
            reasons.add(new ScoreReason("Влиза в желаната продължителност", 10));
        } else if (duration > profile.preferredSessionMinutes()) {
            score -= 10;
            reasons.add(new ScoreReason("По-дълга е от желаното време", -10));
        }

        EquipmentCompatibility compatibility = equipmentCompatibility(plan, profile.availableEquipmentIndividuals());
        if (compatibility.totalExercises() == 0) {
            score -= 20;
            reasons.add(new ScoreReason("Планът няма въведени упражнения в онтологията", -20));
        } else if (compatibility.compatibleExercises() == compatibility.totalExercises()) {
            score += 14;
            reasons.add(new ScoreReason("Всички упражнения са съвместими с наличното оборудване", 14));
        } else if (compatibility.compatibleExercises() > 0) {
            int points = Math.max(2, (int) Math.round(14.0 * compatibility.compatibleExercises() / compatibility.totalExercises()));
            score += points;
            reasons.add(new ScoreReason("Програмата е адаптирана към наличното оборудване", points));
        } else {
            score -= 45;
            reasons.add(new ScoreReason("Оригиналната програма изисква липсващо оборудване", -45));
        }

        return new ScoredPlan(plan, score, reasons);
    }

    private EquipmentCompatibility equipmentCompatibility(String plan, Set<String> selectedEquipment) {
        int total = 0;
        int compatible = 0;
        Set<String> allowed = allowedEquipment(selectedEquipment);

        for (WorkoutSessionRecommendation session : rawSessionsForPlan(plan)) {
            for (ExercisePrescriptionRecommendation prescription : session.exercises()) {
                total++;
                if (isCompatible(prescription.exercise(), allowed)) {
                    compatible++;
                }
            }
        }
        return new EquipmentCompatibility(total, compatible);
    }

    private Set<String> allowedEquipment(Set<String> selectedEquipment) {
        LinkedHashSet<String> allowed = new LinkedHashSet<>();
        allowed.add(NO_EQUIPMENT);
        if (selectedEquipment != null) {
            selectedEquipment.stream()
                    .filter(Objects::nonNull)
                    .filter(value -> !value.isBlank())
                    .forEach(allowed::add);
        }
        return Collections.unmodifiableSet(allowed);
    }

    private boolean isCompatible(ExerciseDetails exercise, Set<String> allowedEquipment) {
        Set<String> required = exercise.equipment();
        return required == null
                || required.isEmpty()
                || required.stream().allMatch(eq -> NO_EQUIPMENT.equals(eq) || allowedEquipment.contains(eq));
    }

    private List<WorkoutSessionRecommendation> sessionsForPlan(String plan, UserProfile profile) {
        List<WorkoutSessionRecommendation> rawSessions = rawSessionsForPlan(plan);
        Set<String> allowed = allowedEquipment(profile.availableEquipmentIndividuals());
        List<ExerciseDetails> compatiblePool = compatibleExercisePool(profile, allowed);
        boolean hiitPlan = ontology.getObjectValues(plan, "usesTrainingStyle").contains("HiitTrainingStyle")
                || trainingPlanName(plan).toLowerCase(Locale.ROOT).contains("hiit");

        if (rawSessions.isEmpty()) {
            return generatedFallbackSessions(profile, compatiblePool, hiitPlan);
        }

        List<WorkoutSessionRecommendation> adapted = new ArrayList<>();
        Set<String> globallyUsed = new LinkedHashSet<>();

        for (WorkoutSessionRecommendation session : rawSessions) {
            List<ExercisePrescriptionRecommendation> filtered = session.exercises().stream()
                    .filter(prescription -> isCompatible(prescription.exercise(), allowed))
                    .collect(Collectors.toCollection(ArrayList::new));

            filtered.forEach(p -> globallyUsed.add(p.exercise().individualName()));

            int originalCount = Math.max(session.exercises().size(), MIN_EXERCISES_PER_SESSION);
            int desiredCount = Math.min(MAX_EXERCISES_PER_SESSION, originalCount);
            fillSessionWithAlternatives(filtered, compatiblePool, globallyUsed, desiredCount, hiitPlan);

            if (filtered.isEmpty()) {
                fillSessionWithAlternatives(filtered, compatiblePool, new LinkedHashSet<>(), MIN_EXERCISES_PER_SESSION, hiitPlan);
            }

            adapted.add(new WorkoutSessionRecommendation(
                    session.order(),
                    session.dayLabel(),
                    session.displayName(),
                    session.durationMin(),
                    reindex(filtered)
            ));
        }

        return adapted;
    }

    private List<WorkoutSessionRecommendation> rawSessionsForPlan(String plan) {
        return ontology.getObjectValues(plan, "containsWorkoutSession").stream()
                .map(this::sessionRecommendation)
                .sorted(Comparator.comparingInt(WorkoutSessionRecommendation::order))
                .toList();
    }

    private void fillSessionWithAlternatives(List<ExercisePrescriptionRecommendation> sessionExercises,
                                             List<ExerciseDetails> pool,
                                             Set<String> used,
                                             int desiredCount,
                                             boolean hiitStyle) {
        Set<String> sessionNames = sessionExercises.stream()
                .map(prescription -> prescription.exercise().individualName())
                .collect(Collectors.toCollection(LinkedHashSet::new));

        for (ExerciseDetails exercise : pool) {
            if (sessionExercises.size() >= desiredCount) {
                break;
            }
            if (sessionNames.contains(exercise.individualName())) {
                continue;
            }
            boolean preferUnused = !used.contains(exercise.individualName()) || pool.size() <= desiredCount;
            if (!preferUnused) {
                continue;
            }
            sessionExercises.add(defaultPrescription(sessionExercises.size() + 1, exercise, hiitStyle));
            sessionNames.add(exercise.individualName());
            used.add(exercise.individualName());
        }

        if (sessionExercises.size() < desiredCount) {
            for (ExerciseDetails exercise : pool) {
                if (sessionExercises.size() >= desiredCount) {
                    break;
                }
                if (sessionNames.contains(exercise.individualName())) {
                    continue;
                }
                sessionExercises.add(defaultPrescription(sessionExercises.size() + 1, exercise, hiitStyle));
                sessionNames.add(exercise.individualName());
            }
        }
    }

    private List<WorkoutSessionRecommendation> generatedFallbackSessions(UserProfile profile,
                                                                         List<ExerciseDetails> pool,
                                                                         boolean hiitStyle) {
        int days = Math.max(2, Math.min(5, profile.trainingDaysPerWeek()));
        int duration = Math.max(25, Math.min(60, profile.preferredSessionMinutes()));
        List<WorkoutSessionRecommendation> sessions = new ArrayList<>();
        int cursor = 0;
        for (int day = 1; day <= days; day++) {
            List<ExercisePrescriptionRecommendation> exercises = new ArrayList<>();
            for (int i = 0; i < Math.min(MAX_EXERCISES_PER_SESSION, pool.size()); i++) {
                ExerciseDetails exercise = pool.get((cursor + i) % pool.size());
                exercises.add(defaultPrescription(i + 1, exercise, hiitStyle));
            }
            cursor += Math.max(1, exercises.size());
            sessions.add(new WorkoutSessionRecommendation(day, "Ден " + day, "Адаптирана тренировка", duration, exercises));
        }
        return sessions;
    }

    private List<ExercisePrescriptionRecommendation> reindex(List<ExercisePrescriptionRecommendation> exercises) {
        List<ExercisePrescriptionRecommendation> result = new ArrayList<>();
        for (int i = 0; i < exercises.size(); i++) {
            ExercisePrescriptionRecommendation old = exercises.get(i);
            result.add(new ExercisePrescriptionRecommendation(
                    i + 1,
                    old.exercise(),
                    old.sets(),
                    old.minReps(),
                    old.maxReps(),
                    old.workSeconds(),
                    old.restSeconds(),
                    old.note()
            ));
        }
        return result;
    }

    private ExercisePrescriptionRecommendation defaultPrescription(int order, ExerciseDetails exercise, boolean hiitStyle) {
        if (hiitStyle) {
            return new ExercisePrescriptionRecommendation(order, exercise, 3, 0, 0, 35, 25,
                    "Адаптирано упражнение според наличното оборудване.");
        }
        return new ExercisePrescriptionRecommendation(order, exercise, 3, 10, 12, 0, 90,
                "Адаптирано упражнение според наличното оборудване.");
    }

    private List<ExerciseDetails> compatibleExercisePool(UserProfile profile, Set<String> allowedEquipment) {
        return ontology.getIndividualsOfClass("Exercise").stream()
                .map(this::exerciseDetails)
                .filter(exercise -> isCompatible(exercise, allowedEquipment))
                .sorted(exerciseComparator(profile))
                .toList();
    }

    private Comparator<ExerciseDetails> exerciseComparator(UserProfile profile) {
        return Comparator
                .comparingInt((ExerciseDetails exercise) -> exercisePriority(exercise, profile)).reversed()
                .thenComparing(ExerciseDetails::displayName);
    }

    private int exercisePriority(ExerciseDetails exercise, UserProfile profile) {
        int points = 0;
        Set<String> goals = ontology.getObjectValues(exercise.individualName(), "exerciseSupportsGoal");
        if (goals.contains(profile.goalIndividual())) {
            points += 30;
        }
        Set<String> experience = ontology.getObjectValues(exercise.individualName(), "exerciseSuitableForTrainingExperience");
        if (experience.contains(profile.trainingExperienceIndividual())) {
            points += 20;
        }
        Set<String> environment = ontology.getObjectValues(exercise.individualName(), "exerciseSuitableForWorkoutEnvironment");
        if (environment.contains(profile.workoutEnvironmentIndividual())) {
            points += 15;
        }
        if (exercise.equipment().contains(NO_EQUIPMENT)) {
            points += 5;
        }
        return points;
    }

    private WorkoutSessionRecommendation sessionRecommendation(String session) {
        List<ExercisePrescriptionRecommendation> exercises = ontology.getObjectValues(session, "hasExercisePrescription").stream()
                .map(this::prescriptionRecommendation)
                .sorted(Comparator.comparingInt(ExercisePrescriptionRecommendation::order))
                .toList();

        return new WorkoutSessionRecommendation(
                ontology.dataInt(session, "hasSessionOrder", 0),
                ontology.firstDataValue(session, "hasSessionDayLabel").orElse(""),
                ontology.firstDataValue(session, "hasWorkoutSessionName").orElse(ontology.displayName(session)),
                ontology.dataInt(session, "hasSessionDurationMin", 0),
                exercises
        );
    }

    private ExercisePrescriptionRecommendation prescriptionRecommendation(String prescription) {
        String exercise = ontology.getObjectValues(prescription, "prescribesExercise").stream()
                .findFirst()
                .orElse("UnknownExercise");
        ExerciseDetails details = exerciseDetails(exercise);
        return new ExercisePrescriptionRecommendation(
                ontology.dataInt(prescription, "hasExerciseOrder", 0),
                details,
                ontology.dataInt(prescription, "hasPrescriptionSets", 0),
                ontology.dataInt(prescription, "hasMinReps", 0),
                ontology.dataInt(prescription, "hasMaxReps", 0),
                ontology.dataInt(prescription, "hasWorkSeconds", 0),
                ontology.dataInt(prescription, "hasPrescriptionRestSeconds", 0),
                ontology.firstDataValue(prescription, "hasExercisePrescriptionNote").orElse("")
        );
    }

    private ExerciseDetails exerciseDetails(String exercise) {
        return new ExerciseDetails(
                exercise,
                ontology.firstDataValue(exercise, "hasExerciseDisplayName").orElse(ontology.displayName(exercise)),
                ontology.firstDataValue(exercise, "hasExerciseImagePath").orElse(""),
                ontology.firstDataValue(exercise, "hasExerciseGifPath").orElse(""),
                ontology.firstDataValue(exercise, "hasExecutionInstructions").orElse(""),
                ontology.firstDataValue(exercise, "hasCommonMistakes").orElse(""),
                ontology.firstDataValue(exercise, "hasEasierVariation").orElse("Намали тежестта, темпото или амплитудата и изпълнявай движението контролирано."),
                ontology.firstDataValue(exercise, "hasHarderVariation").orElse("Добави контролирано темпо, допълнителна тежест или по-къса почивка само при стабилна техника."),
                ontology.getObjectValues(exercise, "targetsMuscleGroup"),
                ontology.getObjectValues(exercise, "requiresEquipment")
        );
    }

    private record ScoredPlan(String plan, int score, List<ScoreReason> reasons) { }

    private record EquipmentCompatibility(int totalExercises, int compatibleExercises) { }
}
