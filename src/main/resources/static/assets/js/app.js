const state = {
  options: null,
  labelMap: new Map()
};

const fallbackImages = {
  recipe: '/assets/images/placeholders/recipe-placeholder.svg',
  exercise: '/assets/images/placeholders/exercise-placeholder.svg'
};

const staticLabels = new Map(Object.entries({
  BeginnerUser: 'Начинаещ потребител',
  IntermediateUser: 'Средно напреднал потребител',
  AdvancedUser: 'Напреднал потребител',
  UnderweightBmi: 'Поднормено тегло',
  NormalWeightBmi: 'Нормално тегло',
  OverweightBmi: 'Наднормено тегло',
  ObeseBmi: 'Затлъстяване',
  WeightLoss: 'Отслабване',
  MuscleGain: 'Мускулна маса',
  StrengthImprovement: 'Сила',
  EnduranceImprovement: 'Издръжливост',
  HealthImprovement: 'Здравословни навици',
  BodyRecomposition: 'Рекомпозиция',
  BalancedDiet: 'Балансирано хранене',
  CalorieDeficitDiet: 'Калориен дефицит',
  HighProteinDiet: 'Високопротеинов режим',
  KetoDiet: 'Кето',
  LowCarbDiet: 'Low carb',
  IntermittentFasting: 'Intermittent fasting',
  MediterraneanDiet: 'Средиземноморски режим',
  BeginnerAfterLongBreak: 'Начинаещ / след дълга почивка',
  OccasionallyActive: 'Тренирам от време на време',
  ActivelyTraining: 'Тренирам активно',
  NoEquipment: 'Без оборудване',
  Dumbbell: 'Дъмбели',
  Barbell: 'Щанга',
  Machine: 'Машини',
  ResistanceBand: 'Ластици',
  ChestMuscle: 'Гърди',
  BackMuscle: 'Гръб',
  LegsMuscle: 'Крака',
  ArmsMuscle: 'Ръце',
  CoreMuscle: 'Корем',
  ShouldersMuscle: 'Рамене',
  MilkAllergen: 'Мляко',
  GlutenAllergen: 'Глутен',
  FishAllergen: 'Риба',
  EggAllergen: 'Яйца',
  NutAllergen: 'Ядки',
  SesameAllergen: 'Сусам',
  LactoseIntolerance: 'Лактозна непоносимост',
  GlutenSensitivity: 'Глутенова чувствителност',
  Diabetes: 'Диабет / контрол на захари',
  FishAllergy: 'Алергия към риба',
  EggAllergy: 'Алергия към яйца',
  NutAllergy: 'Алергия към ядки',
  SesameAllergy: 'Алергия към сусам',
  VegetarianPreference: 'Вегетариански предпочитания',
  NoPorkPreference: 'Не ям свинско',
  HomeWorkoutEnvironment: 'Вкъщи',
  GymWorkoutEnvironment: 'Във фитнес',
  PushPullLegsTrainingStyle: 'Push / Pull / Legs',
  HiitTrainingStyle: 'HIIT',
  FullBodyTrainingStyle: 'Full body'
}));



const ingredientLabels = new Map(Object.entries({
  'Chicken Breast': 'Пилешко филе',
  'Turkey Breast': 'Пуешко филе',
  'Beef': 'Телешко месо',
  'Rice': 'Ориз',
  'Quinoa': 'Киноа',
  'Potatoes': 'Картофи',
  'Broccoli': 'Броколи',
  'Tomato': 'Домат',
  'Cucumber': 'Краставица',
  'Spinach': 'Спанак',
  'Olive Oil': 'Зехтин',
  'Salmon': 'Сьомга',
  'Tuna': 'Риба тон',
  'Eggs': 'Яйца',
  'Avocado': 'Авокадо',
  'Greek Yogurt': 'Гръцко кисело мляко',
  'Oats': 'Овесени ядки',
  'Berries': 'Горски плодове',
  'Almonds': 'Бадеми',
  'Cottage Cheese': 'Извара',
  'Cauliflower Rice': 'Карфиолен ориз',
  'Lentils': 'Леща',
  'Chickpeas': 'Нахут',
  'Feta': 'Фета сирене',
  'Hummus': 'Хумус',
  'Tortilla': 'Тортиля',
  'Apple': 'Ябълка'
}));

const demos = {
  ivan: {
    displayName: 'Ivan', age: 25, heightCm: 183, weightKg: 90,
    goalIndividual: 'WeightLoss', trainingExperienceIndividual: 'BeginnerAfterLongBreak', workoutEnvironmentIndividual: 'HomeWorkoutEnvironment',
    trainingDaysPerWeek: 3, preferredSessionMinutes: 45, mealsPerDay: 3,
    dietExperienceIndividuals: [], preferredDietaryApproachIndividuals: ['BalancedDiet', 'CalorieDeficitDiet'],
    dietaryConstraintIndividuals: [], availableEquipmentIndividuals: ['NoEquipment', 'ResistanceBand']
  },
  maria: {
    displayName: 'Maria', age: 24, heightCm: 168, weightKg: 60,
    goalIndividual: 'MuscleGain', trainingExperienceIndividual: 'OccasionallyActive', workoutEnvironmentIndividual: 'GymWorkoutEnvironment',
    trainingDaysPerWeek: 4, preferredSessionMinutes: 60, mealsPerDay: 4,
    dietExperienceIndividuals: ['HighProteinDiet'], preferredDietaryApproachIndividuals: ['HighProteinDiet', 'BalancedDiet'],
    dietaryConstraintIndividuals: [], availableEquipmentIndividuals: ['Dumbbell', 'Barbell', 'Machine']
  },
  georgi: {
    displayName: 'Georgi', age: 29, heightCm: 178, weightKg: 82,
    goalIndividual: 'WeightLoss', trainingExperienceIndividual: 'ActivelyTraining', workoutEnvironmentIndividual: 'HomeWorkoutEnvironment',
    trainingDaysPerWeek: 5, preferredSessionMinutes: 40, mealsPerDay: 2,
    dietExperienceIndividuals: ['IntermittentFasting'], preferredDietaryApproachIndividuals: ['IntermittentFasting', 'LowCarbDiet'],
    dietaryConstraintIndividuals: [], availableEquipmentIndividuals: ['NoEquipment']
  }
};

window.addEventListener('DOMContentLoaded', init);

async function init() {
  bindSidebarToggle();
  bindNavigation();
  bindForm();
  bindDemoButtons();
  await loadOptions();
  await checkOntologyHealth();
  applyDemo('maria');
}

function bindSidebarToggle() {
  const toggle = document.getElementById('sidebarToggle');
  const collapsed = localStorage.getItem('sidebarCollapsed') === 'true';
  document.body.classList.toggle('sidebar-collapsed', collapsed);
  toggle.setAttribute('aria-expanded', String(!collapsed));
  toggle.addEventListener('click', () => {
    const nextCollapsed = !document.body.classList.contains('sidebar-collapsed');
    document.body.classList.toggle('sidebar-collapsed', nextCollapsed);
    localStorage.setItem('sidebarCollapsed', String(nextCollapsed));
    toggle.setAttribute('aria-expanded', String(!nextCollapsed));
  });
}

function bindNavigation() {
  const links = document.querySelectorAll('.side-nav a');
  const sections = [...links].map(link => document.querySelector(link.getAttribute('href'))).filter(Boolean);
  const setActive = () => {
    const current = sections.findLast(section => section.getBoundingClientRect().top < 190) || sections[0];
    links.forEach(link => link.classList.toggle('active', link.getAttribute('href') === `#${current.id}`));
  };
  document.addEventListener('scroll', setActive, { passive: true });
  setActive();
}

function bindForm() {
  document.getElementById('recommendationForm').addEventListener('submit', async (event) => {
    event.preventDefault();
    await generateRecommendation();
  });
}

function bindDemoButtons() {
  document.querySelectorAll('[data-demo]').forEach(button => {
    button.addEventListener('click', () => applyDemo(button.dataset.demo));
  });
}

async function loadOptions() {
  const response = await fetch('/api/options');
  if (!response.ok) throw new Error('Не могат да се заредят опциите.');
  state.options = await response.json();
  buildLabelMap();
  fillSelect('goalSelect', state.options.goals);
  fillSelect('experienceSelect', state.options.trainingExperience);
  fillSelect('environmentSelect', state.options.workoutEnvironment);
  renderDietMatrix(state.options.dietaryApproaches);
  renderChecks('constraintChecks', state.options.dietaryConstraints, 'dietaryConstraintIndividuals');
  renderChecks('equipmentChecks', state.options.equipment, 'availableEquipmentIndividuals');
}

async function checkOntologyHealth() {
  const box = document.getElementById('ontologyStatus');
  try {
    const response = await fetch('/api/ontology/health');
    const health = await response.json();
    box.classList.add('ok');
    box.innerHTML = `
      <span class="status-line">Онтология: <strong>${escapeHtml(health.status)}</strong></span>
      <span class="status-line">${health.nutritionPlans} хранителни плана</span>
      <span class="status-line">${health.trainingPlans} тренировъчни плана</span>
      <span class="status-line">${health.recipes} рецепти</span>
      <span class="status-line">${health.exercises} упражнения</span>
    `;
  } catch (error) {
    box.classList.add('error');
    box.textContent = 'Проблем при проверка на онтологията.';
  }
}

function buildLabelMap() {
  state.labelMap = new Map(staticLabels);
  Object.values(state.options).flat().forEach(option => state.labelMap.set(option.individualShortName, option.label));
}

function fillSelect(id, options) {
  const select = document.getElementById(id);
  select.innerHTML = options.map(option => `<option value="${escapeHtml(option.individualShortName)}">${escapeHtml(option.label)}</option>`).join('');
}

function renderChecks(containerId, options, name) {
  const container = document.getElementById(containerId);
  container.innerHTML = options.map(option => checkItem(name, option.individualShortName, option.label)).join('');
}

function renderDietMatrix(options) {
  const container = document.getElementById('dietMatrix');
  container.innerHTML = `
    <div class="diet-matrix-head">
      <span>Режим</span>
      <span>Имам опит</span>
      <span>Предпочитам</span>
    </div>
    ${options.map(option => {
      const value = escapeHtml(option.individualShortName);
      const text = escapeHtml(option.label);
      return `
        <div class="diet-row">
          <div class="diet-name">${text}<small>${humanize(option.individualShortName)}</small></div>
          ${dietToggle('dietExperienceIndividuals', value, 'Опит')}
          ${dietToggle('preferredDietaryApproachIndividuals', value, 'Предпочитам')}
        </div>
      `;
    }).join('')}
  `;
}

function checkItem(name, value, text) {
  return `
    <label class="check-item">
      <input type="checkbox" name="${escapeHtml(name)}" value="${escapeHtml(value)}">
      <span class="custom-check" aria-hidden="true"></span>
      <span class="check-text">${escapeHtml(text)}</span>
    </label>
  `;
}

function dietToggle(name, value, text) {
  return `
    <label class="diet-toggle">
      <input type="checkbox" name="${escapeHtml(name)}" value="${escapeHtml(value)}">
      <span class="custom-check" aria-hidden="true"></span>
      <span class="check-text">${escapeHtml(text)}</span>
    </label>
  `;
}

function applyDemo(name) {
  const demo = demos[name];
  if (!demo) return;
  const form = document.getElementById('recommendationForm');
  Object.entries(demo).forEach(([key, value]) => {
    if (Array.isArray(value)) return;
    const input = form.elements[key];
    if (input) input.value = value;
  });
  ['dietExperienceIndividuals', 'preferredDietaryApproachIndividuals', 'dietaryConstraintIndividuals', 'availableEquipmentIndividuals'].forEach(group => {
    document.querySelectorAll(`input[name="${group}"]`).forEach(input => {
      input.checked = (demo[group] || []).includes(input.value);
    });
  });
}

async function generateRecommendation() {
  const form = document.getElementById('recommendationForm');
  const resultContent = document.getElementById('resultContent');
  const hero = document.getElementById('resultHero');
  form.classList.add('loading');
  hero.innerHTML = '<p class="eyebrow">Reasoning</p><h2>Генериране на препоръка...</h2><p>Агентите валидират профила, изчисляват BMI и заявяват онтологията.</p>';
  resultContent.innerHTML = '';

  try {
    const payload = collectPayload(form);
    const response = await fetch('/api/recommendations', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });
    if (!response.ok) {
      const error = await response.json().catch(() => ({ error: 'Грешка', details: ['Невалиден отговор от сървъра.'] }));
      throw new Error([error.error, ...(error.details || [])].join('\n'));
    }
    const data = await response.json();
    renderResult(data);
    document.getElementById('results').scrollIntoView({ behavior: 'smooth', block: 'start' });
  } catch (error) {
    hero.innerHTML = '<p class="eyebrow">Грешка</p><h2>Не може да се генерира препоръка</h2><p>Провери входните данни или backend логовете.</p>';
    resultContent.innerHTML = `<div class="error-box">${escapeHtml(error.message).replaceAll('\n', '<br>')}</div>`;
  } finally {
    form.classList.remove('loading');
  }
}

function collectPayload(form) {
  return {
    displayName: form.elements.displayName.value.trim(),
    age: Number(form.elements.age.value),
    heightCm: Number(form.elements.heightCm.value),
    weightKg: Number(form.elements.weightKg.value),
    goalIndividual: form.elements.goalIndividual.value,
    trainingExperienceIndividual: form.elements.trainingExperienceIndividual.value,
    workoutEnvironmentIndividual: form.elements.workoutEnvironmentIndividual.value,
    trainingDaysPerWeek: Number(form.elements.trainingDaysPerWeek.value),
    preferredSessionMinutes: Number(form.elements.preferredSessionMinutes.value),
    mealsPerDay: Number(form.elements.mealsPerDay.value),
    dietExperienceIndividuals: checkedValues('dietExperienceIndividuals'),
    preferredDietaryApproachIndividuals: checkedValues('preferredDietaryApproachIndividuals'),
    dietaryConstraintIndividuals: checkedValues('dietaryConstraintIndividuals'),
    availableEquipmentIndividuals: checkedValues('availableEquipmentIndividuals')
  };
}

function checkedValues(name) {
  return [...document.querySelectorAll(`input[name="${name}"]:checked`)].map(input => input.value);
}

function renderResult(data) {
  const hero = document.getElementById('resultHero');
  const profile = data.profile;
  hero.innerHTML = `
    <p class="eyebrow">Готова препоръка</p>
    <h2>${escapeHtml(profile.displayName)} · ${label(profile.goalIndividual)}</h2>
    <p>${escapeHtml(data.explanation).replaceAll('\n', '<br>')}</p>
  `;

  document.getElementById('resultContent').innerHTML = `
    ${renderProfileSummary(data)}
    ${renderNutrition(data.nutrition)}
    ${renderTraining(data.training)}
  `;
}

function renderProfileSummary(data) {
  const p = data.profile;
  return `
    <section class="result-card">
      <p class="eyebrow">Профил и inferred knowledge</p>
      <h2>Reasoner профил</h2>
      <div class="summary-grid">
        <div class="metric-card"><span>BMI</span><strong>${formatNumber(p.bmi, 1)}</strong></div>
        <div class="metric-card"><span>BMI категория</span><strong>${label(p.bmiCategoryIndividual)}</strong></div>
        <div class="metric-card"><span>Опит</span><strong>${label(p.trainingExperienceIndividual)}</strong></div>
        <div class="metric-card"><span>Среда</span><strong>${label(p.workoutEnvironmentIndividual)}</strong></div>
      </div>
      <div class="card-meta">${data.inferredUserTypes.map(type => badge(label(type), 'green')).join('')}</div>
    </section>
  `;
}

function renderNutrition(nutrition) {
  return `
    <section class="result-card">
      <p class="eyebrow">Хранителна препоръка</p>
      <h2>${escapeHtml(nutrition.displayName)}</h2>
      <div class="reason-list">${nutrition.reasons.map(reasonChip).join('')}</div>
      <div class="recipe-grid">${nutrition.recipes.map(renderRecipe).join('')}</div>
    </section>
  `;
}

function renderRecipe(recipe) {
  const totalTime = (recipe.prepTimeMin || 0) + (recipe.cookingTimeMin || 0);
  const suitabilityBadges = [
    ...recipe.goals.map(goal => badge(label(goal), 'green')),
    ...recipe.dietaryApproaches.map(approach => badge(label(approach), 'gray')),
    badge(inferMealSlot(recipe), 'amber')
  ].join('');

  return `
    <article class="recipe-card">
      <div class="media"><img src="${assetUrl(recipe.imagePath)}" alt="${escapeHtml(recipe.displayName)}" onerror="this.src='${fallbackImages.recipe}'"></div>
      <div class="card-body">
        <div class="recipe-title">
          <h3>${escapeHtml(recipe.displayName)}</h3>
          <span class="serving-badge"><strong>${formatNumber(recipe.servingSizeG, 0)}</strong><small>гр.</small></span>
        </div>
        <div class="card-meta">
          ${badge(`${totalTime} мин`, 'amber')}
          ${badge(recipe.difficultyLabel || 'Средна трудност', 'gray')}
        </div>
        <div class="macro-grid">
          ${macro('calories', recipe.macros.calories, 'калории')}
          ${macro('protein', recipe.macros.proteinG, 'протеин')}
          ${macro('carbs', recipe.macros.carbsG, 'въглехидрати')}
          ${macro('fat', recipe.macros.fatG, 'мазнини')}
          ${macro('sugar', recipe.macros.sugarsG, 'захари')}
          ${macro('fiber', recipe.macros.fiberG, 'фибри')}
          ${macro('salt', recipe.macros.saltG, 'сол')}
          ${macro('saturated', recipe.macros.saturatedFatG, 'наситени мазнини')}
        </div>

        <div class="recipe-section">
          <strong>Продукти</strong>
          <div class="ingredients">${recipe.ingredients.map(i => badge(`${escapeHtml(foodLabel(i.foodItem))} ${formatNumber(i.amountGrams, 0)} гр.`, 'gray')).join('')}</div>
        </div>

        <div class="recipe-section">
          <strong>Алергени</strong>
          <div class="ingredients">${recipe.allergens.length ? recipe.allergens.map(a => badge(label(a), 'red')).join('') : badge('Няма отбелязани', 'green')}</div>
        </div>

        <div class="recipe-section">
          <strong>Начин на приготвяне</strong>
          ${renderPreparationSteps(recipe.instructions)}
        </div>

        <div class="recipe-section suitable-section">
          <strong>Подходящо за</strong>
          <div class="ingredients">${suitabilityBadges}</div>
        </div>
      </div>
    </article>
  `;
}

function renderTraining(training) {
  return `
    <section class="result-card">
      <p class="eyebrow">Тренировъчна програма</p>
      <h2>${escapeHtml(training.displayName)}</h2>
      <p class="instructions">${escapeHtml(training.description || '')}</p>
      <div class="card-meta">
        ${badge(`${training.trainingDaysPerWeek} дни седмично`, 'green')}
        ${badge(`${training.estimatedDurationMin} мин`, 'amber')}
        ${training.workoutEnvironments.map(e => badge(label(e), 'gray')).join('')}
        ${training.trainingStyles.map(s => badge(label(s), 'gray')).join('')}
      </div>
      <div class="reason-list">${training.reasons.map(reasonChip).join('')}</div>
      <div class="session-list">${training.sessions.map(renderSession).join('')}</div>
    </section>
  `;
}

function renderSession(session) {
  return `
    <article class="session-card">
      <div class="session-header">
        <div>
          <p class="eyebrow">${escapeHtml(session.dayLabel || `Ден ${session.order}`)}</p>
        </div>
        ${badge(`${session.durationMin} мин`, 'amber')}
      </div>
      <div class="exercise-grid">${session.exercises.map(renderExercise).join('')}</div>
    </article>
  `;
}

function renderExercise(prescription) {
  const exercise = prescription.exercise;
  const reps = prescription.workSeconds > 0
    ? `${prescription.workSeconds} сек работа`
    : `${prescription.minReps}${prescription.maxReps && prescription.maxReps !== prescription.minReps ? '–' + prescription.maxReps : ''} повторения`;
  return `
    <article class="exercise-card">
      <div class="media"><img src="${assetUrl(exercise.imagePath || exercise.gifPath)}" alt="${escapeHtml(exercise.displayName)}" onerror="this.src='${fallbackImages.exercise}'"></div>
      <div class="card-body">
        <h4>${prescription.order}. ${escapeHtml(exercise.displayName)}</h4>
        <div class="card-meta">
          ${exercise.muscleGroups.map(m => badge(label(m), 'green')).join('')}
          ${exercise.equipment.map(e => badge(label(e), 'gray')).join('')}
        </div>
        <div class="prescription-line">${prescription.sets || 1} серии · ${reps} · почивка ${prescription.restSeconds || 0} сек</div>
        <div class="technique-grid">
          <div class="technique-box"><strong>Техника</strong><p>${escapeHtml(exercise.executionInstructions || 'Изпълнявай движението контролирано и без болка.')}</p></div>
          <div class="technique-box"><strong>Чести грешки</strong><p>${escapeHtml(exercise.commonMistakes || 'Избягвай прекалено бързо изпълнение и загуба на контрол.')}</p></div>
          <div class="technique-box"><strong>По-лесен вариант</strong><p>${escapeHtml(exercise.easierVariation || 'Намали тежестта, темпото или амплитудата.')}</p></div>
          <div class="technique-box"><strong>По-труден вариант</strong><p>${escapeHtml(exercise.harderVariation || 'Добави контролирано темпо, тежест или допълнителна серия.')}</p></div>
        </div>
      </div>
    </article>
  `;
}

function reasonChip(reason) {
  return `<span class="score-chip">${escapeHtml(reason.label)}</span>`;
}

function macro(type, value, subtitle) {
  const digits = type === 'calories' ? 0 : 1;
  const unit = type === 'calories' ? '' : ' гр.';
  return `<div class="macro-chip"><strong>${formatNumber(value, digits)}${unit}</strong><span>${escapeHtml(subtitle)}</span></div>`;
}

function foodLabel(value) {
  const normalized = String(value || '').trim();
  return ingredientLabels.get(normalized) || label(normalized.replaceAll(' ', '')) || normalized;
}

function renderPreparationSteps(instructions) {
  const steps = String(instructions || '')
    .split(/(?<=[.!?])\s+/)
    .map(step => step.trim())
    .filter(Boolean);

  if (steps.length <= 1) {
    return `<p class="instructions">${escapeHtml(instructions || 'Следвай стандартна хигиена на приготвяне и овкуси умерено.')}</p>`;
  }

  return `<ol class="preparation-list">${steps.map(step => `<li>${escapeHtml(step)}</li>`).join('')}</ol>`;
}

function inferMealSlot(recipe) {
  const value = `${recipe.individualName || ''} ${recipe.displayName || ''}`.toLowerCase();
  if (value.includes('breakfast') || value.includes('yogurt') || value.includes('oat') || value.includes('egg')) return 'Закуска';
  if (value.includes('postworkout') || value.includes('power')) return 'След тренировка';
  if (value.includes('dinner') || value.includes('salmon') || value.includes('keto')) return 'Вечеря';
  if (value.includes('fasting')) return 'Основно хранене';
  if (value.includes('soup')) return 'Обяд / супа';
  return 'Основно хранене';
}

function badge(text, type = 'gray') {
  return `<span class="badge ${type}">${escapeHtml(text)}</span>`;
}

function label(individual) {
  return state.labelMap.get(individual) || humanize(individual || '');
}

function humanize(value) {
  return String(value).replace(/([a-z])([A-Z])/g, '$1 $2');
}

function assetUrl(path) {
  if (!path) return fallbackImages.recipe;
  if (path.startsWith('http') || path.startsWith('/')) return path;
  return '/' + path;
}

function formatNumber(value, digits = 1) {
  const n = Number(value || 0);
  return n.toLocaleString('bg-BG', { maximumFractionDigits: digits, minimumFractionDigits: digits === 0 ? 0 : 0 });
}

function escapeHtml(value) {
  return String(value ?? '')
    .replaceAll('&', '&amp;')
    .replaceAll('<', '&lt;')
    .replaceAll('>', '&gt;')
    .replaceAll('"', '&quot;')
    .replaceAll("'", '&#039;');
}
