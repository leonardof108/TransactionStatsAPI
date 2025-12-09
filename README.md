# Transaction Statistics API

This is a RESTful API developed as a programming challenge. The API allows clients to post financial transactions and retrieve real-time statistics for transactions made within the last 60 seconds.

The entire application is designed to be lightweight and performant, storing all data in-memory without relying on any external databases or caches.

## Technologies Used

- **Java 17**: The core programming language.
- **Spring Boot**: For creating the RESTful API and managing dependencies.
- **Maven**: For project build and dependency management.

## How to Build and Run

### Prerequisites

- Java 17 (or a newer version)
- Apache Maven

### Steps

1.  **Clone the repository:**
    ```bash
    git clone <your-repository-url>
    cd TransactionStatsAPI
    ```

2.  **Build the project using Maven:**
    ```bash
    mvn clean install
    ```

3.  **Run the application:**
    ```bash
    java -jar target/TransactionStatsAPI-0.0.1-SNAPSHOT.jar
    ```

The API will start on `http://localhost:8080`.

## API Endpoints

### 1. Create a Transaction

This endpoint registers a new transaction. The transaction time is crucial, as statistics are calculated based on it.

- **URL:** `/transacao`
- **Method:** `POST`
- **Headers:**
  - `Content-Type: application/json`
- **Body:**

  ```json
  {
      "valor": "123.45",
      "dataHora": "2024-07-29T14:20:50.123-03:00"
  }
  ```

  - `valor`: The transaction amount (must be non-negative).
  - `dataHora`: The exact timestamp of the transaction in ISO 8601 format (must not be in the future).

- **Responses:**
  - `201 Created`: The transaction was successfully created.
  - `400 Bad Request`: The request body is malformed or contains invalid JSON.
  - `422 Unprocessable Entity`: The transaction fails validation (e.g., negative amount, future date).

### 2. Get Statistics

This endpoint returns statistics for all transactions that occurred in the last 60 seconds.

- **URL:** `/estatistica`
- **Method:** `GET`
- **Responses:**
  - `200 OK`: Returns a JSON object with the statistics.

    ```json
    {
        "count": 10,
        "sum": 1234.56,
        "avg": 123.46,
        "min": 12.34,
        "max": 500.00
    }
    ```
    If no transactions occurred in the last 60 seconds, all values will be zero.
    - **Note:** When providing `dataHora` in the `POST /transacao` endpoint, ensure the `OffsetDateTime` includes the correct timezone offset. For example, if the current date/time in Brazil is `2025-07-29T12:54:30Z` (UTC-03:00), the `dataHora` in the payload should reflect this offset (e.g., `2025-07-29T15:54:00Z`). The statistics calculation considers transactions within 60 seconds of the server's current time.

### 3. Delete All Transactions

This endpoint clears all transaction data from memory.

- **URL:** `/transacao`
- **Method:** `DELETE`
- **Responses:**
  - `200 OK`: All transactions were successfully deleted.
