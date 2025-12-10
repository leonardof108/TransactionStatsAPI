# 🚀 Transaction Statistics - Itau Challenge

A high-performance, real-time financial monitoring system. This project consists of a **Spring Boot** backend API optimized for O(1) time complexity and a modern **React** frontend dashboard.

![Project Status](https://img.shields.io/badge/status-complete-green)
![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3-brightgreen)
![React](https://img.shields.io/badge/React-18-blue)
![License](https://img.shields.io/badge/license-MIT-grey)

## ✨ Key Features

- **O(1) Performance:** The statistics calculation uses a **Circular Buffer (Ring Buffer)** pattern instead of iterating over lists. This ensures constant time complexity for generating stats, regardless of transaction volume.
- **Thread Safety:** Fully synchronized backend handling concurrent requests without race conditions.
- **Real-Time Dashboard:** A React-based UI that polls the API to visualize the 60-second sliding window in real-time.
- **Memory Efficient:** Fixed memory footprint (60 buckets) preventing memory leaks over time.

---

## 🛠️ Tech Stack

### Backend
- **Java 17**
- **Spring Boot 3** (Web, Validation)
- **Maven**
- **JUnit 5** (Testing)

### Frontend
- **React** (Vite)
- **TypeScript**
- **Tailwind CSS**
- **Shadcn/UI** (Component Library)

---

## 🚀 How to Run

### Prerequisites
- Java 17+
- Node.js & npm (for the frontend)

### 1️⃣ Start the Backend

The backend runs on port `8080`.

```bash
# Clone the repository
git clone https://github.com/leonardof108/transactionstatsapi.git
cd transactionstatsapi

# Build and Run
./mvnw spring-boot:run
```

### 2️⃣ Start the Frontend

The frontend runs on port `3000` (or `5173` depending on Vite config).

Open a new terminal window:

```bash
cd frontend

# Install dependencies
npm install

# Start development server
npm run dev
```

Visit the URL shown in your terminal (e.g., `http://localhost:3000`) to access the dashboard.

---

## 🔌 API Endpoints

The API follows a strict contract for the coding challenge.

### 1. Create Transaction

**POST** `/transacao`

Receives a transaction. Returns `201` if successful, `422` if the date is in the future or invalid.

**Body:**
```json
{
  "valor": 123.45,
  "dataHora": "2024-10-05T14:30:00.000Z"
}
```

---

### 2. Get Statistics

**GET** `/estatistica`

Returns stats for the last 60 seconds.

**Response:**
```json
{
  "count": 10,
  "sum": 1234.56,
  "avg": 123.45,
  "min": 10.00,
  "max": 500.00
}
```

---

### 3. Clear Transactions

**DELETE** `/transacao`

Wipes the memory buffer completely.

---

## 🧠 Architecture Note: The "Bucket" Logic

To meet the requirement of high performance without a database, this application does not store a list of transactions.

Instead, it allocates **60 Buckets** (one for each second of the minute).

- **Input:** When a transaction arrives for second `:45`, it is added to `Bucket[45]`.
- **Output:** When stats are requested, we sum the valid buckets.
- **Cleanup:** If a bucket holds data older than 60 seconds, it is automatically overwritten by new incoming data (**Lazy Reset**).

This guarantees that memory usage never grows and calculation speed never degrades, achieving **O(1)** complexity.
