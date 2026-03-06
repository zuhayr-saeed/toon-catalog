# Webtoon Catalog MVP
Production-ready monorepo for a MyAnimeList-style webtoon tracker.

## Stack
- Backend: Spring Boot 3, Java 21, PostgreSQL, Flyway, JWT, JPA, Meilisearch
- Frontend: Vite, React, TypeScript, MUI, TanStack React Query
- Infra: Docker Compose, Nginx, GitHub Actions CI

## Features
- Browse/search/filter series
- MAL-style personal list with `status`, `progress`, and `favorite`
- Ratings + written reviews with aggregate summary
- Episode detail view + “mark as read” progress update
- Public profiles + follow/unfollow

## Repository Layout
- `backend/` Spring Boot API
- `frontend/` React app
- `infra/docker-compose.dev.yml` legacy infra-only compose
- `docker-compose.yml` root full stack compose (db + meili + backend + frontend)

## Environment Variables
Copy `.env.example` in repo root and `frontend/.env.example` in frontend if needed.

### Backend
- `JWT_SECRET` required in production (stable signing key)
- `JWT_EXPIRATION_MS` token expiration in milliseconds
- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`
- `MEILI_HOST`, `MEILI_API_KEY`
- `APP_CORS_ALLOWED_ORIGINS` comma-separated origins

### Frontend
- `VITE_API_URL` default `/api/v1`

## Local Development

### Option A: One-command full stack (recommended)
```bash
docker compose up --build
```
Services:
- Frontend: http://localhost:3000
- Backend: http://localhost:8080
- Meilisearch: http://localhost:7700
- Postgres: localhost:5432

### Option B: Run app locally, infra in Docker
```bash
docker compose -f infra/docker-compose.dev.yml up -d
cd backend && ./gradlew bootRun
cd frontend && npm ci && npm run dev
```
Frontend dev server: http://localhost:5173

## Build & Test

### Frontend
```bash
cd frontend
npm ci
npm run lint
npm run build
```

### Backend
```bash
cd backend
./gradlew test
```

## Database & Migrations
- Flyway scripts are in `backend/src/main/resources/db/migration`.
- On startup, backend runs Flyway automatically.
- To start with a fresh DB in Docker:
```bash
docker compose down -v
docker compose up --build
```

## Seed / Demo Data
Create seed series via API after registering and logging in:
```bash
curl -X POST http://localhost:8080/api/v1/series \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN>" \
  -d '{
    "title": "Sample Webtoon",
    "type": "WEBTOON",
    "synopsis": "Demo synopsis",
    "coverImageUrl": "https://example.com/cover.jpg",
    "genres": ["Action"],
    "tags": ["Fantasy"],
    "authors": ["Demo Author"]
  }'
```

## API Notes
- Base path: `/api/v1`
- Health: `/api/v1/healthz` (also `/healthz`)
- OpenAPI (dev): `/swagger-ui/index.html`
- Controllers return DTOs (no direct JPA entity JSON for rating/follow/list/social APIs)

## Deployment (Docker Compose on a VM)
1. Copy repo to VM.
2. Create `.env` with production values (`JWT_SECRET`, db credentials, meili key).
3. Build and start:
```bash
docker compose up -d --build
```
4. Put reverse proxy/TLS (Caddy/Nginx/Traefik) in front of `frontend:3000`.

## CI
- Backend workflow: `.github/workflows/backend.yml` runs `./gradlew test`
- Frontend workflow: `.github/workflows/frontend.yml` runs `npm ci`, `npm run lint`, `npm run build`

## Manual QA Checklist
1. Register + login and confirm token-based auth works.
2. Open `/api/v1/healthz` and verify `ok`.
3. Browse `/series`, search, filter by genre/tag, and sort.
4. Open a series detail page, add to list, change status/progress, toggle favorite.
5. Confirm favorite toggle does not remove non-empty list entries.
6. Submit rating + review, verify summary updates and reviews appear.
7. Open an episode page, click “Mark this episode as read”, verify progress updates.
8. Open `/library`, filter by status/favorites, update inline controls.
9. Open `/users/{username}`, follow/unfollow from another account, verify counts.
10. Restart backend container and verify existing JWTs remain valid with stable `JWT_SECRET`.
