# Disease Prediction System (Spring Boot, Mini-1)

A lightweight, viva-friendly Disease Prediction System using Spring Boot, REST, JWT auth, JPA (MySQL), and a simple decision-tree-style symptom matcher. Includes a minimal Bootstrap-based static UI (`src/main/resources/static/index.html`).

## Tech Stack
- Java 17, Spring Boot 3 (Web, Security, Data JPA, Validation)
- JWT (jjwt 0.11.5)
- MySQL with Spring Data JPA (Hibernate)
- Maven
- Frontend: HTML + Bootstrap (static assets served by Spring Boot)

## Run Locally
1. Choose DB: default is in-memory H2 (quick demo). For MySQL, update `src/main/resources/application.properties` with your MySQL creds and JDBC URL.
2. Set JWT secret (required): provide a 32+ byte Base64 value via env/CLI or properties. Example CLI:
   ```bash
   ./mvnw spring-boot:run -Dapp.jwt.secret=UHJvZFNlY3JldC1LZXktMjAyNS1Mb25nLTMyLUJ5dGVzISE=
   ```
   Or export `APP_JWT_SECRET` and run normally. Tokens rotate when the secret changes.
3. Build & run:
   ```bash
   ./mvnw -DskipTests package
   ./mvnw spring-boot:run
   ```
4. Open http://localhost:8082/ to use the demo UI. Use Postman for APIs.

Health checks:
- GET http://localhost:8082/actuator/health
- GET http://localhost:8082/actuator/info

Smoke script (PowerShell):
- Run `./scripts/smoke.ps1` (defaults: baseUrl=http://localhost:8082, email=user@demo.com, password=password, symptoms=fever,cough)
- Override: `./scripts/smoke.ps1 -BaseUrl http://localhost:8082 -Email admin@demo.com -Password admin123 -Symptoms fever,cough,fatigue`

Default seeded accounts:
- Admin: `admin@demo.com` / `admin123`
- User: `user@demo.com` / `password`

## UI Pages
- **Home** (`/index.html`) — Welcome page with feature overview
- **Auth** (`/auth.html`) — Register/Login with JWT authentication
- **Predict** (`/predict.html`) — Select symptoms and get disease predictions
- **Admin** (`/admin.html`) — Admin dashboard (requires admin role)

## Key Endpoints
- `POST /auth/register` — register user, returns JWT
- `POST /auth/login` — login, returns JWT
- `POST /predict` — predict disease (auth required)
- `GET /predictions/user/{id}` — user prediction history
- `GET /admin/users` — list users (admin)
- `GET /admin/statistics` — summary stats (admin)
- `GET /admin/diseases` — list diseases (admin)
- `POST /admin/diseases` — create disease (admin)
- `PUT /admin/diseases/{id}` — update disease (admin)
- `DELETE /admin/diseases/{id}` — delete disease (admin)

## Prediction Logic (Decision Tree Style)
- Rules live in `DecisionTreeEngine` (symptom-to-disease mapping).
- Confidence = matched symptoms / rule symptoms (bounded between 5% and 99%).
- Precautions returned with each prediction; marked as assistance only (non-diagnostic).

## Database Entities
- User (id, name, email, password, role)
- Symptom (id, symptomName)
- Disease (id, diseaseName, description, precautions)
- Prediction (id, user, disease, confidence, date)

## Sample Data Seeding
`DataInitializer` seeds:
- Admin and demo user
- Core diseases (Flu, Common Cold, Migraine, Malaria, COVID-19)
- Common symptoms catalog

## Postman Quick Use
1. `POST /auth/login` with demo user to get token.
2. Call `POST /predict` with body:
   ```json
   { "symptoms": ["fever", "cough", "fatigue"] }
   ```
3. Pass header `Authorization: Bearer <token>` on protected routes.

## Notes / Viva Tips
- JWT security with stateless sessions; passwords hashed (BCrypt).
- Rule-based decision tree for explainability; easily extend `DecisionTreeEngine`.
- Controllers are thin; services hold business logic; repositories wrap persistence.
- Global validation & error handling via `GlobalExceptionHandler`.

## Next Steps (Mini-2+)
- Persist symptom-disease mappings in DB and train a real model.
- Add pagination, audit logs, and test coverage.
- Containerize with Docker Compose (API + MySQL).
