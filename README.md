# Fitness & Nutrition Advisor Web

Локална web версия на дипломния проект: Java backend, Spring Boot REST API, OWLAPI + HermiT reasoning, агентна архитектура и модерен HTML/CSS/JavaScript интерфейс.

## Стартиране в IntelliJ

1. Отвори папката `fitness-nutrition-advisor-web`.
2. Изчакай Maven sync.
3. Стартирай класа:
   `bg.uniplov.fitness.advisor.FitnessAdvisorWebApplication`
4. Отвори браузър на:
   `http://localhost:8080`

## Стартиране с Maven

Ако Maven е инсталиран глобално:

```bash
mvn spring-boot:run
```

или:

```bash
mvn clean package
java -jar target/fitness-nutrition-advisor-web-2.0.0.jar
```

## Архитектура

- `UserIntakeAgent` валидира входните данни.
- `BodyProfileAgent` изчислява BMI и BMI категория.
- `OntologyMappingAgent` мапва входа към ontology individuals.
- `OntologyReasoningAgent` създава runtime user и извлича inferred класове.
- `NutritionRecommendationAgent` избира хранителен план и рецепти.
- `TrainingRecommendationAgent` избира тренировъчна програма и упражнения.
- `ExplanationAgent` генерира обяснение за препоръката.

## API

- `GET /api/options` — списъци за UI селектори и чекбоксове.
- `GET /api/ontology/health` — кратка проверка на заредената онтология.
- `POST /api/recommendations` — генерира препоръка.

## Assets

Изображенията се зареждат от `src/main/resources/static/assets/`.
Ако изображение липсва, frontend-ът показва fallback placeholder.
