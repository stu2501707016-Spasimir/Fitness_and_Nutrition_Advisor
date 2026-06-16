package bg.uniplov.fitness.advisor.model;

import java.util.List;

public final class Options {
    private Options() {}

    public static final List<OntologyOption> GOALS = List.of(
            new OntologyOption("Отслабване", "WeightLoss"),
            new OntologyOption("Покачване на мускулна маса", "MuscleGain"),
            new OntologyOption("Подобряване на сила", "StrengthImprovement"),
            new OntologyOption("Подобряване на издръжливост", "EnduranceImprovement"),
            new OntologyOption("Здравословни навици", "HealthImprovement"),
            new OntologyOption("Рекомпозиция", "BodyRecomposition")
    );

    public static final List<OntologyOption> TRAINING_EXPERIENCE = List.of(
            new OntologyOption("Начинаещ / след дълга почивка", "BeginnerAfterLongBreak"),
            new OntologyOption("Тренирам от време на време", "OccasionallyActive"),
            new OntologyOption("Тренирам активно", "ActivelyTraining")
    );

    public static final List<OntologyOption> WORKOUT_ENVIRONMENT = List.of(
            new OntologyOption("Вкъщи", "HomeWorkoutEnvironment"),
            new OntologyOption("Във фитнес", "GymWorkoutEnvironment")
    );

    public static final List<OntologyOption> DIETARY_APPROACHES = List.of(
            new OntologyOption("Балансирано хранене", "BalancedDiet"),
            new OntologyOption("Калориен дефицит", "CalorieDeficitDiet"),
            new OntologyOption("Високопротеинов режим", "HighProteinDiet"),
            new OntologyOption("Кето", "KetoDiet"),
            new OntologyOption("Low carb", "LowCarbDiet"),
            new OntologyOption("Intermittent fasting", "IntermittentFasting"),
            new OntologyOption("Средиземноморски режим", "MediterraneanDiet")
    );

    public static final List<OntologyOption> DIETARY_CONSTRAINTS = List.of(
            new OntologyOption("Лактозна непоносимост", "LactoseIntolerance"),
            new OntologyOption("Глутенова чувствителност", "GlutenSensitivity"),
            new OntologyOption("Диабет / нужда от контрол на захари", "Diabetes"),
            new OntologyOption("Алергия към риба", "FishAllergy"),
            new OntologyOption("Алергия към яйца", "EggAllergy"),
            new OntologyOption("Алергия към ядки", "NutAllergy"),
            new OntologyOption("Алергия към сусам", "SesameAllergy"),
            new OntologyOption("Вегетариански предпочитания", "VegetarianPreference"),
            new OntologyOption("Не ям свинско", "NoPorkPreference")
    );

    public static final List<OntologyOption> EQUIPMENT = List.of(
            new OntologyOption("Без оборудване", "NoEquipment"),
            new OntologyOption("Дъмбели", "Dumbbell"),
            new OntologyOption("Щанга", "Barbell"),
            new OntologyOption("Машини", "Machine"),
            new OntologyOption("Ластици", "ResistanceBand")
    );
}
