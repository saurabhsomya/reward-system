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

```json
[
    {
        "customerId": 1,
        "customerName": "Saurabh",
        "totalRewardPoints": 645,
        "monthlyRewards": {
            "JANUARY": 155,
            "DECEMBER": 330,
            "NOVEMBER": 160
        },
        "transactions": [
            {
                "transactionId": 1,
                "transactionAmount": 45.00,
                "transactionDate": "2024-11-03",
                "transactionRewardPoints": 0
            },
            {
                "transactionId": 2,
                "transactionAmount": 75.50,
                "transactionDate": "2024-11-10",
                "transactionRewardPoints": 25
            },
            {
                "transactionId": 3,
                "transactionAmount": 120.00,
                "transactionDate": "2024-11-17",
                "transactionRewardPoints": 90
            },
            {
                "transactionId": 4,
                "transactionAmount": 95.00,
                "transactionDate": "2024-11-24",
                "transactionRewardPoints": 45
            },
            {
                "transactionId": 5,
                "transactionAmount": 30.00,
                "transactionDate": "2024-12-01",
                "transactionRewardPoints": 0
            },
            {
                "transactionId": 6,
                "transactionAmount": 110.00,
                "transactionDate": "2024-12-08",
                "transactionRewardPoints": 70
            },
            {
                "transactionId": 7,
                "transactionAmount": 60.00,
                "transactionDate": "2024-12-15",
                "transactionRewardPoints": 10
            },
            {
                "transactionId": 8,
                "transactionAmount": 200.00,
                "transactionDate": "2024-12-22",
                "transactionRewardPoints": 250
            },
            {
                "transactionId": 9,
                "transactionAmount": 20.00,
                "transactionDate": "2025-01-05",
                "transactionRewardPoints": 0
            },
            {
                "transactionId": 10,
                "transactionAmount": 55.00,
                "transactionDate": "2025-01-12",
                "transactionRewardPoints": 5
            },
            {
                "transactionId": 11,
                "transactionAmount": 135.00,
                "transactionDate": "2025-01-19",
                "transactionRewardPoints": 120
            },
            {
                "transactionId": 12,
                "transactionAmount": 80.00,
                "transactionDate": "2025-01-26",
                "transactionRewardPoints": 30
            }
        ]
    },
    {
        "customerId": 2,
        "customerName": "Vrishali",
        "totalRewardPoints": 630,
        "monthlyRewards": {
            "JANUARY": 125,
            "DECEMBER": 315,
            "NOVEMBER": 190
        },
        "transactions": [
            {
                "transactionId": 13,
                "transactionAmount": 48.00,
                "transactionDate": "2024-11-04",
                "transactionRewardPoints": 0
            },
            {
                "transactionId": 14,
                "transactionAmount": 90.00,
                "transactionDate": "2024-11-11",
                "transactionRewardPoints": 40
            },
            {
                "transactionId": 15,
                "transactionAmount": 140.00,
                "transactionDate": "2024-11-18",
                "transactionRewardPoints": 130
            },
            {
                "transactionId": 16,
                "transactionAmount": 70.00,
                "transactionDate": "2024-11-25",
                "transactionRewardPoints": 20
            },
            {
                "transactionId": 17,
                "transactionAmount": 25.00,
                "transactionDate": "2024-12-02",
                "transactionRewardPoints": 0
            },
            {
                "transactionId": 18,
                "transactionAmount": 105.00,
                "transactionDate": "2024-12-09",
                "transactionRewardPoints": 60
            },
            {
                "transactionId": 19,
                "transactionAmount": 65.00,
                "transactionDate": "2024-12-16",
                "transactionRewardPoints": 15
            },
            {
                "transactionId": 20,
                "transactionAmount": 195.00,
                "transactionDate": "2024-12-23",
                "transactionRewardPoints": 240
            },
            {
                "transactionId": 21,
                "transactionAmount": 35.00,
                "transactionDate": "2025-01-06",
                "transactionRewardPoints": 0
            },
            {
                "transactionId": 22,
                "transactionAmount": 50.00,
                "transactionDate": "2025-01-13",
                "transactionRewardPoints": 0
            },
            {
                "transactionId": 23,
                "transactionAmount": 120.00,
                "transactionDate": "2025-01-20",
                "transactionRewardPoints": 90
            },
            {
                "transactionId": 24,
                "transactionAmount": 85.00,
                "transactionDate": "2025-01-27",
                "transactionRewardPoints": 35
            }
        ]
    },
    {
        "customerId": 3,
        "customerName": "Tamilarasan",
        "totalRewardPoints": 618,
        "monthlyRewards": {
            "JANUARY": 137,
            "DECEMBER": 338,
            "NOVEMBER": 143
        },
        "transactions": [
            {
                "transactionId": 25,
                "transactionAmount": 42.00,
                "transactionDate": "2024-11-05",
                "transactionRewardPoints": 0
            },
            {
                "transactionId": 26,
                "transactionAmount": 88.00,
                "transactionDate": "2024-11-12",
                "transactionRewardPoints": 38
            },
            {
                "transactionId": 27,
                "transactionAmount": 125.00,
                "transactionDate": "2024-11-19",
                "transactionRewardPoints": 100
            },
            {
                "transactionId": 28,
                "transactionAmount": 55.00,
                "transactionDate": "2024-11-26",
                "transactionRewardPoints": 5
            },
            {
                "transactionId": 29,
                "transactionAmount": 29.00,
                "transactionDate": "2024-12-03",
                "transactionRewardPoints": 0
            },
            {
                "transactionId": 30,
                "transactionAmount": 130.00,
                "transactionDate": "2024-12-10",
                "transactionRewardPoints": 110
            },
            {
                "transactionId": 31,
                "transactionAmount": 68.00,
                "transactionDate": "2024-12-17",
                "transactionRewardPoints": 18
            },
            {
                "transactionId": 32,
                "transactionAmount": 180.00,
                "transactionDate": "2024-12-24",
                "transactionRewardPoints": 210
            },
            {
                "transactionId": 33,
                "transactionAmount": 40.00,
                "transactionDate": "2025-01-07",
                "transactionRewardPoints": 0
            },
            {
                "transactionId": 34,
                "transactionAmount": 72.00,
                "transactionDate": "2025-01-14",
                "transactionRewardPoints": 22
            },
            {
                "transactionId": 35,
                "transactionAmount": 110.00,
                "transactionDate": "2025-01-21",
                "transactionRewardPoints": 70
            },
            {
                "transactionId": 36,
                "transactionAmount": 95.00,
                "transactionDate": "2025-01-28",
                "transactionRewardPoints": 45
            }
        ]
    }
]
```


- `GET http://localhost:8080/api/rewards?startDate=2024-11-15&endDate=2024-12-15` (Fetch rewards for all customers within the specified date range)  

```json
[
    {
        "customerId": 1,
        "customerName": "Saurabh",
        "totalRewardPoints": 215,
        "monthlyRewards": {
            "DECEMBER": 80,
            "NOVEMBER": 135
        },
        "transactions": [
            {
                "transactionId": 3,
                "transactionAmount": 120.00,
                "transactionDate": "2024-11-17",
                "transactionRewardPoints": 90
            },
            {
                "transactionId": 4,
                "transactionAmount": 95.00,
                "transactionDate": "2024-11-24",
                "transactionRewardPoints": 45
            },
            {
                "transactionId": 5,
                "transactionAmount": 30.00,
                "transactionDate": "2024-12-01",
                "transactionRewardPoints": 0
            },
            {
                "transactionId": 6,
                "transactionAmount": 110.00,
                "transactionDate": "2024-12-08",
                "transactionRewardPoints": 70
            },
            {
                "transactionId": 7,
                "transactionAmount": 60.00,
                "transactionDate": "2024-12-15",
                "transactionRewardPoints": 10
            }
        ]
    },
    {
        "customerId": 2,
        "customerName": "Vrishali",
        "totalRewardPoints": 210,
        "monthlyRewards": {
            "DECEMBER": 60,
            "NOVEMBER": 150
        },
        "transactions": [
            {
                "transactionId": 15,
                "transactionAmount": 140.00,
                "transactionDate": "2024-11-18",
                "transactionRewardPoints": 130
            },
            {
                "transactionId": 16,
                "transactionAmount": 70.00,
                "transactionDate": "2024-11-25",
                "transactionRewardPoints": 20
            },
            {
                "transactionId": 17,
                "transactionAmount": 25.00,
                "transactionDate": "2024-12-02",
                "transactionRewardPoints": 0
            },
            {
                "transactionId": 18,
                "transactionAmount": 105.00,
                "transactionDate": "2024-12-09",
                "transactionRewardPoints": 60
            }
        ]
    },
    {
        "customerId": 3,
        "customerName": "Tamilarasan",
        "totalRewardPoints": 215,
        "monthlyRewards": {
            "DECEMBER": 110,
            "NOVEMBER": 105
        },
        "transactions": [
            {
                "transactionId": 27,
                "transactionAmount": 125.00,
                "transactionDate": "2024-11-19",
                "transactionRewardPoints": 100
            },
            {
                "transactionId": 28,
                "transactionAmount": 55.00,
                "transactionDate": "2024-11-26",
                "transactionRewardPoints": 5
            },
            {
                "transactionId": 29,
                "transactionAmount": 29.00,
                "transactionDate": "2024-12-03",
                "transactionRewardPoints": 0
            },
            {
                "transactionId": 30,
                "transactionAmount": 130.00,
                "transactionDate": "2024-12-10",
                "transactionRewardPoints": 110
            }
        ]
    }
]
```

---

### 2. Get Rewards for a Specific Customer  
**Endpoint:**  
`GET /api/rewards/{customerId}`  

**Description:**  
Returns reward details for the specified customer. Date filters can be optionally applied to narrow down the transactions.  

**Example Requests:**  
- `GET http://localhost:8080/api/rewards/1` (Fetch rewards for customer with ID 1 without filtering by date)  

```json
{
    "customerId": 1,
    "customerName": "Saurabh",
    "totalRewardPoints": 645,
    "monthlyRewards": {
        "JANUARY": 155,
        "DECEMBER": 330,
        "NOVEMBER": 160
    },
    "transactions": [
        {
            "transactionId": 1,
            "transactionAmount": 45.00,
            "transactionDate": "2024-11-03",
            "transactionRewardPoints": 0
        },
        {
            "transactionId": 2,
            "transactionAmount": 75.50,
            "transactionDate": "2024-11-10",
            "transactionRewardPoints": 25
        },
        {
            "transactionId": 3,
            "transactionAmount": 120.00,
            "transactionDate": "2024-11-17",
            "transactionRewardPoints": 90
        },
        {
            "transactionId": 4,
            "transactionAmount": 95.00,
            "transactionDate": "2024-11-24",
            "transactionRewardPoints": 45
        },
        {
            "transactionId": 5,
            "transactionAmount": 30.00,
            "transactionDate": "2024-12-01",
            "transactionRewardPoints": 0
        },
        {
            "transactionId": 6,
            "transactionAmount": 110.00,
            "transactionDate": "2024-12-08",
            "transactionRewardPoints": 70
        },
        {
            "transactionId": 7,
            "transactionAmount": 60.00,
            "transactionDate": "2024-12-15",
            "transactionRewardPoints": 10
        },
        {
            "transactionId": 8,
            "transactionAmount": 200.00,
            "transactionDate": "2024-12-22",
            "transactionRewardPoints": 250
        },
        {
            "transactionId": 9,
            "transactionAmount": 20.00,
            "transactionDate": "2025-01-05",
            "transactionRewardPoints": 0
        },
        {
            "transactionId": 10,
            "transactionAmount": 55.00,
            "transactionDate": "2025-01-12",
            "transactionRewardPoints": 5
        },
        {
            "transactionId": 11,
            "transactionAmount": 135.00,
            "transactionDate": "2025-01-19",
            "transactionRewardPoints": 120
        },
        {
            "transactionId": 12,
            "transactionAmount": 80.00,
            "transactionDate": "2025-01-26",
            "transactionRewardPoints": 30
        }
    ]
}
```

- `GET http://localhost:8080/api/rewards/1?startDate=2024-11-15&endDate=2024-12-15` (Fetch rewards for customer with ID 1 within the specified date range)  

```json
{
    "customerId": 1,
    "customerName": "Saurabh",
    "totalRewardPoints": 215,
    "monthlyRewards": {
        "DECEMBER": 80,
        "NOVEMBER": 135
    },
    "transactions": [
        {
            "transactionId": 3,
            "transactionAmount": 120.00,
            "transactionDate": "2024-11-17",
            "transactionRewardPoints": 90
        },
        {
            "transactionId": 4,
            "transactionAmount": 95.00,
            "transactionDate": "2024-11-24",
            "transactionRewardPoints": 45
        },
        {
            "transactionId": 5,
            "transactionAmount": 30.00,
            "transactionDate": "2024-12-01",
            "transactionRewardPoints": 0
        },
        {
            "transactionId": 6,
            "transactionAmount": 110.00,
            "transactionDate": "2024-12-08",
            "transactionRewardPoints": 70
        },
        {
            "transactionId": 7,
            "transactionAmount": 60.00,
            "transactionDate": "2024-12-15",
            "transactionRewardPoints": 10
        }
    ]
}
```