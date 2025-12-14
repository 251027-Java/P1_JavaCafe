# Java Cafe – Full Stack Runbook

A full-stack cafe application built with Spring Boot (Java), React (Vite), PostgreSQL, and Tailwind CSS.

## Prerequisites
- Java 17+ and Maven (or use the included `mvnw.cmd` on Windows)
- Node.js 18+ and npm
- PostgreSQL 14+ running locally
- Optional: set environment variable `JWT_SECRET` for a custom JWT key

## Backend Setup (Spring Boot)
1. Create the database (default connection in `JavaCafe/src/main/resources/application.properties`):
	- DB name: `cafe_db`
	- User: `postgres`
	- Password: `mysecretpassword`

	Create via psql:
	```powershell
	psql -U postgres -c "CREATE DATABASE cafe_db;"
	```

2. Verify backend config:
	- `spring.datasource.url=jdbc:postgresql://localhost:5432/cafe_db`
	- `spring.jpa.hibernate.ddl-auto=create` for first run (switch to `update` after tables are created)
	- `jwt.secret` uses `JWT_SECRET` if provided, otherwise a local default.

3. Start the backend (port 8080):
	```powershell
	cd .\JavaCafe
	.\mvnw.cmd spring-boot:run
	```

## Frontend Setup (Vite React)
The dev server is configured on port 3000 and proxies `/api` to the backend at `http://localhost:8080` (see `javacafe-frontend/vite.config.js`).

1. Install dependencies:
	```powershell
	cd ..\javacafe-frontend
	npm install
	```

2. Start the frontend (port 3000):
	```powershell
	npm run dev
	```

3. Open the app:
	- Frontend: http://localhost:3000
	- Backend API: http://localhost:8080

## Customer Pages
- Home (static content)
- Menu (GET `/api/menu`, GET `/api/menu/description/{productId}`)
- Cart
  - Browse products: GET `/api/cart?categoryName={name}`
  - Guest checkout: POST `/api/cart/guest/submit`
  - Authenticated order: POST `/api/cart/new` (requires `Authorization: Bearer <token>`)
  - Order summary: GET `/api/cart/{id}` (requires auth)
  - Order details: GET `/api/cart/{id}/items` (requires auth)
- Contact Us: POST `/api/contact/submit`
- Login (customers): POST `/api/auth/login`

## Environment Notes
- After first successful backend run, change `spring.jpa.hibernate.ddl-auto` to `update` to preserve data.
- If PostgreSQL credentials differ, update `application.properties` accordingly.
- JWT: Set `JWT_SECRET` to a long Base64-like string in your environment for production-like runs.

## Troubleshooting
- Frontend fails to call API: ensure backend is running on 8080; proxy is defined in `vite.config.js`.
- DB errors: confirm `cafe_db` exists and credentials match `application.properties`.
- CORS: proxied `/api` calls avoid CORS issues during development.

