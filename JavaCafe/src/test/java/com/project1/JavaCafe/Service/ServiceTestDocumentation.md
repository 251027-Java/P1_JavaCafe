# Service Test Documentation

This document provides an overview of the test classes in the Service folder of our test section. Each class is responsible for testing the functionality of its corresponding service class in the main application.

---

## 1. ProductsServiceTest

**Purpose:**  
Tests the ProductsService class, which manages product-related operations.

**Main Test Methods:**
- testInitializeTable(): Verifies that the product table is initialized correctly.
- testAddProduct(): Checks that a new product can be added successfully.
- testGetAllProducts(): Ensures all products are retrieved as expected.
- testDeleteProduct(): Confirms that a product can be deleted.

---

## 2. AppUserServiceTest

**Purpose:**  
Tests the AppUserService class, which handles user-related operations.

**Main Test Methods:**
- testRegisterUser(): Validates user registration logic.
- testFindUserByEmail(): Checks retrieval of users by email.
- testUpdateUserProfile(): Ensures user profile updates work correctly.
- testDeleteUser(): Verifies user deletion functionality.

---

## 3. OrderServiceTest

**Purpose:**  
Tests the OrderService class, which manages order processing.

**Main Test Methods:**
- testCreateOrder(): Checks order creation logic.
- testGetOrdersByUser(): Ensures orders are fetched for a specific user.
- testCancelOrder(): Verifies order cancellation.

---

---

## 4. ContactSubmissionServiceTest

**Purpose:**  
Tests the ContactSubmissionService class, which manages customer contact form submissions.

**Main Test Methods:**
- testSubmitContactForm(): Verifies that a contact form submission is processed and stored correctly.
- testGetAllSubmissions(): Ensures all contact submissions can be retrieved.
- testDeleteSubmission(): Checks that a contact submission can be deleted as expected.

---

---

> **Note:**  
> Each test class uses mock dependencies and assertions to verify the correctness of service logic. 