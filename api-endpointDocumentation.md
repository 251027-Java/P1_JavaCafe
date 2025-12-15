# Java Café - Customer API Documentation

**Base URL:** http://localhost:8080

---

## Scope (Customer Pages)
- Home: no API calls
- Menu: browse products and descriptions
- Cart: browse products (by category) and place orders
- Contact Us: submit inquiries
- Login: authenticate customers

---

## Authentication

### Login (Customers)
**POST** /api/auth/login

Authenticate an existing customer and receive a JWT.

Request
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

Response (200 OK)
```json
{
  "token": "<jwt-token>"
}
```

---

## Menu

### Get All Products
**GET** /api/menu

Response (200 OK)
```json
[
  {
    "productId": 1,
    "category": "CROISSANTS",
    "name": "Classic Butter Croissant",
    "basePrice": 3.75,
    "availability": "IN_STOCK"
  }
]
```

### Get Product Description
**GET** /api/menu/description/{productId}

Path
- productId (Long)

Response (200 OK)
```json
{
  "productId": 7,
  "name": "Classic Butter Croissant",
  "description": "Flaky, buttery croissant...",
  "basePrice": 3.75,
  "category": "CROISSANTS",
  "availability": "IN_STOCK"
}
```

---

## Cart

### Browse Products (optional category filter)
**GET** /api/cart?categoryName={name}

Notes
- Returns products; if `categoryName` is omitted, returns all.

### Place Order (Guest)
**POST** /api/cart/guest/submit

Request
```json
{
  "email": "guest@example.com",
  "firstname": "Jane",
  "lastname": "Smith",
  "phone": "555-5678",
  "items": [
    { "productId": 7, "quantity": 2 }
  ]
}
```

Response (201 CREATED)
```json
{
  "orderid": 123,
  "status": "PENDING",
  "totalAmount": 7.50,
  "createdAt": "2025-12-13T10:30:00Z",
  "items": [
    { "orderItemId": 1, "productId": 7, "quantity": 2, "priceAtPurchase": 3.75 }
  ]
}
```

### Place Order (Authenticated)
**POST** /api/cart/new

Headers
- Authorization: Bearer <token>

Request
```json
{
  "items": [
    { "productId": 7, "quantity": 2 }
  ]
}
```

### Get Order Summary
**GET** /api/cart/{id}

Headers
- Authorization: Bearer <token>

Response (200 OK)
```json
{
  "orderid": 123,
  "status": "PENDING",
  "totalAmount": 7.50,
  "createdAt": "2025-12-13T10:30:00Z"
}
```

### Get Order with Items
**GET** /api/cart/{id}/items

Headers
- Authorization: Bearer <token>

Response (200 OK)
```json
{
  "orderid": 123,
  "status": "PENDING",
  "totalAmount": 7.50,
  "createdAt": "2025-12-13T10:30:00Z",
  "items": [
    { "orderItemId": 1, "productId": 7, "quantity": 2, "priceAtPurchase": 3.75 }
  ]
}
```

---

## Contact Us

### Submit Contact Form
**POST** /api/contact/submit

Request
```json
{
  "firstname": "John",
  "lastname": "Doe",
  "phone": "555-1234",
  "email": "john@example.com",
  "subject": "Catering Inquiry",
  "message": "I would like to inquire about..."
}
```

Response (201 CREATED)
```json
{
  "submissionid": 1,
  "submittedAt": "2025-12-13T10:30:00Z"
}
```

---

## Common Status Codes
- 200 OK – Request successful
- 201 CREATED – Resource created
- 204 NO_CONTENT – No content
- 400 BAD_REQUEST – Invalid request
- 401 UNAUTHORIZED – Missing/invalid token (for protected endpoints)
- 404 NOT_FOUND – Resource not found
- 500 INTERNAL_SERVER_ERROR – Server error

---

## Authentication Notes
Include JWT in requests to protected endpoints:

Authorization: Bearer <your-jwt-token>

Token fields: userId, email, userRole (CUSTOMER)

---

