# Java Cafe – App Description

Java Cafe is a customer-focused web application for browsing a curated menu, placing orders, and contacting the cafe. It comprises a Spring Boot backend and a React (Vite) frontend, with a PostgreSQL database for persistence.

## Purpose
- Provide an intuitive experience for customers to view menu items and categories.
- Support guest checkout and authenticated orders.
- Enable customers to submit general inquiries via Contact Us.

## Customer Pages
- **Home:** Branding, announcements, and featured items.
- **Menu:** Category-based browsing; product descriptions.
- **Cart:** Build an order; submit as guest or authenticated user.
- **Contact Us:** Submit inquiries; stored for staff follow-up.
- **Login:** Authenticate customers and obtain a JWT for protected actions.

## Key Features
- **Menu Browsing:** Products grouped by category (e.g., coffee, croissants, cookies).
- **Guest Checkout:** Place orders without creating an account.
- **Authenticated Orders:** Summary and detailed lookup for a customer's own orders.
- **Contact Submissions:** Persisted inquiries with timestamp.
- **Dev Proxy:** Vite dev server proxies `/api` to the backend, simplifying local development.

## Tech Stack
- **Backend:** Spring Boot (Java), JPA/Hibernate
- **Frontend:** React (Vite), Tailwind CSS
- **Database:** PostgreSQL
- **Auth:** JWT-based authentication for protected endpoints

## APIs (Customer Focus)
- **Auth:** `POST /api/auth/login`
- **Menu:** `GET /api/menu`, `GET /api/menu/description/{productId}`
- **Cart:** `GET /api/cart?categoryName={name}`, `POST /api/cart/guest/submit`, `POST /api/cart/new`, `GET /api/cart/{id}`, `GET /api/cart/{id}/items`
- **Contact:** `POST /api/contact/submit`

## Configuration Notes
- Database: `cafe_db` on `localhost:5432` (configure in `application.properties`).
- First run: `spring.jpa.hibernate.ddl-auto=create` (switch to `update` after tables are created).
- JWT: Set `JWT_SECRET` in environment for non-default secret.

## Intended Users
- Customers placing orders and contacting the cafe.
- Staff/admin use is out of scope for this description and current documentation.
