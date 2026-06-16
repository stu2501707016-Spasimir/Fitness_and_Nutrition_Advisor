# Архитектура

Проектът използва трислойна структура:

1. Web UI — HTML/CSS/JavaScript, обслужван от Spring Boot static resources.
2. REST backend — Spring Boot controllers и DTO модели.
3. Ontology/Agent layer — OWLAPI, HermiT reasoner и специализирани агенти.

Тази структура отделя представянето от експертната логика и прави проекта подходящ за защита, защото ясно показва ролята на онтологията, reasoning-а и агентите.
