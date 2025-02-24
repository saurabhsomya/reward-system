# Reward System API

## Overview
The Reward System API calculates customer reward points based on their purchase transactions. Built with Spring Boot, this RESTful service aggregates transaction data, applies reward logic, and exposes endpoints to fetch rewards for all customers or a specific customer. Optional date filters allow you to narrow down the transactions considered for reward calculation.

## Design Details

#### Layered Architecture  
The API follows a well-structured layered architecture to ensure maintainability, separation of concerns, and scalability:  

- **Controller Layer:**  
  - Exposes REST endpoints and handles incoming HTTP requests.  
  - Validates inputs (e.g., date ranges) before delegating processing to the service layer.  
  - Converts responses into a RESTful format for clients.  

- **Service Layer:**  
  - Implements the core business logic for calculating reward points.  
  - Retrieves transaction data from the repository, applies reward logic, and aggregates monthly and total rewards.  
  - Constructs and returns structured DTOs (Data Transfer Objects) to the controller.  

- **Repository Layer:**  
  - Uses Spring Data JPA to interact with an in-memory H2 database.  
  - Provides efficient query methods to fetch transactions by customer and date range.  


#### Asynchronous Processing  
To improve response times and ensure scalability under high-load conditions, the API utilizes asynchronous processing:  

- Uses `@Async` (enabled via `@EnableAsync`) to execute methods in separate threads, preventing the main request-handling thread from blocking.  
- `CompletableFuture` is leveraged to handle asynchronous execution, allowing tasks to run in the background while returning results when available.  
- Enhances API performance, particularly when handling large datasets or computationally expensive operations, such as aggregating reward points over thousands of transactions.  
- Ensures that API consumers receive quick responses while long-running computations continue asynchronously.  


#### Exception Handling  
A centralized exception handling mechanism is implemented using `@ControllerAdvice` to ensure consistent error responses and avoid repetitive try-catch blocks across controllers:  

- Captures and processes exceptions at a global level, improving maintainability.  
- Handles common error cases, such as:  
  - **Invalid Input:** Incorrect date ranges (e.g., end date before start date).  
  - **Resource Not Found:** Requests for non-existent customer data.  
  - **Database Errors:** Failed queries or unexpected database failures.  
- Returns structured error responses with appropriate HTTP status codes:  
  - `400 Bad Request` for invalid input.  
  - `404 Not Found` if a requested resource doesn’t exist.  
  - `500 Internal Server Error` for unexpected failures.  
- Ensures API consumers receive clear, actionable error messages in a standardized JSON format.  


#### Modularity and Testability  
The project is designed with modularity in mind, allowing easy maintenance, scalability, and testing:  

- **Clear Separation of Concerns:** Each layer (Controller, Service, Repository) is independently testable and loosely coupled.  
- **Unit Testing:** Key business logic is covered with JUnit and Mockito-based tests.  
- **Integration Testing:** Ensures API endpoints function correctly with the database and other components.  
- **Mocking Dependencies:** Service and repository layers use Mockito to simulate real behavior without depending on an actual database.  

This structured approach ensures the API is robust, scalable, and easy to extend in future iterations.  


## Technical Details
- **Language & Frameworks:**  
  - **Java 8** with **Spring Boot 2.7.18**
  
- **Database:**  
  - **H2 Database:** Utilized for easy setup and testing. The schema is defined in `schema.sql` and seed data in `data.sql`.

- **Build & Dependency Management:**  
  - **Maven:** The `pom.xml` manages dependencies including Spring Boot starters, Lombok, and testing libraries.

- **Logging:**  
  - **SLF4J/Logback:** Logging is configured via `logback.xml`, supporting both console and file output.

- **Code Quality:**  
  - Adheres to Java 8 best practices, with standardized naming, clear exception handling, and modular code design.


## API Details

### 1. Get Rewards for All Customers  
**Endpoint:**  
`GET /api/rewards/`  

**Description:**  
Returns reward details for all customers. If date filters are provided (as query parameters), the response will only include transactions within that date range. When no dates are provided, all transactions are used.  

**Example Requests:**  
- `GET http://localhost:8080/api/rewards` (Fetch rewards for all customers without filtering by date)  
- `GET http://localhost:8080/api/rewards?startDate=2027-11-15&endDate=2024-12-15` (Fetch rewards for all customers within the specified date range)  

---

### 2. Get Rewards for a Specific Customer  
**Endpoint:**  
`GET /api/rewards/{customerId}`  

**Description:**  
Returns reward details for the specified customer. Date filters can be optionally applied to narrow down the transactions.  

**Example Requests:**  
- `GET http://localhost:8080/api/rewards/1` (Fetch rewards for customer with ID 1 without filtering by date)  
- `GET http://localhost:8080/api/rewards/1?startDate=2027-11-15&endDate=2024-12-15` (Fetch rewards for customer with ID 1 within the specified date range)  