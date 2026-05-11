# Distributed Payment Processing Platform

A production-grade distributed payment processing platform built using Java, Spring Boot, Apache Kafka, PostgreSQL, and JWT-based authentication following microservices architecture principles.

The platform supports secure wallet transactions, idempotent payment processing, concurrency-safe money transfers, event-driven notifications, and centralized API routing through an API Gateway.

---

# System Architecture

```text
                        +----------------+
                        |     Client     |
                        +----------------+
                                 |
                                 v
                      +---------------------+
                      |     API Gateway     |
                      |  JWT Authentication |
                      +---------------------+
                                 |
                -----------------------------------
                |                                 |
                v                                 v
      +-------------------+            +-----------------------+
      |   Payment Service |            |  Notification Service |
      +-------------------+            +-----------------------+
                |
                |
                v
         +--------------+
         |    Kafka     |
         +--------------+
                |
                v
       +-------------------+
       |  Payment Events   |
       +-------------------+
                |
                v
        +----------------+
        | PostgreSQL DB  |
        +----------------+
```

---

# Core Features

## Wallet Management
- Create wallet for users
- Fetch wallet balance
- Credit money into wallet

## Payment Processing
- Secure money transfer between wallets
- Transaction integrity using database transactions
- Atomic debit and credit operations

## Idempotent APIs
- Prevent duplicate payments using idempotency keys
- Handles retries safely
- Ensures payment requests are processed exactly once

## Concurrency Handling
- Pessimistic row-level locking
- Prevents race conditions and double spending
- Deadlock prevention using consistent lock ordering

## Event-Driven Architecture
- Apache Kafka based asynchronous communication
- Payment events published after successful transactions
- Decoupled producer-consumer workflow

## Notification Service
- Independent microservice consuming payment events
- Simulates asynchronous user notifications
- Scalable event-driven design

## API Gateway
- Centralized request routing
- JWT-based authentication
- Secure entry point for all microservices

## Transaction Logging
- Transaction history tracking
- Failure-safe logging using transaction propagation
- Tracks both successful and failed payment attempts

---

# Microservices

## 1. Payment Service
Responsible for:
- Wallet management
- Payment processing
- Idempotency handling
- Concurrency control
- Transaction logging

### Tech Used
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Kafka Producer

---

## 2. Notification Service
Responsible for:
- Consuming payment events from Kafka
- Sending asynchronous notifications
- Event-driven workflow handling

### Tech Used
- Spring Boot
- Apache Kafka Consumer

---

## 3. API Gateway
Responsible for:
- Request routing
- Authentication
- Gateway-level security
- Centralized API access

### Tech Used
- Spring Cloud Gateway
- Spring Security
- JWT

---

# Tech Stack

| Category | Technologies |
|---|---|
| Language | Java 17 |
| Backend Framework | Spring Boot |
| Database | PostgreSQL |
| Messaging Queue | Apache Kafka |
| API Gateway | Spring Cloud Gateway |
| Security | JWT Authentication |
| ORM | Spring Data JPA / Hibernate |
| Build Tool | Maven |
| Version Control | Git & GitHub |
| Monitoring (Planned) | Grafana, Prometheus |
| Containerization (Planned) | Docker, Docker Compose |

---

# Project Structure

```text
distributed-payment-system/
│
├── payment-service/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── entity/
│   ├── dto/
│   ├── config/
│   └── exception/
│
├── notification-service/
│
├── api-gateway/
│
│
└── README.md
```

---

# Payment Flow

```text
1. Client sends transfer request
2. API Gateway validates JWT token
3. Payment Service validates request
4. Wallet rows locked using pessimistic locking
5. Money debited from sender
6. Money credited to receiver
7. Transaction stored in database
8. Payment event published to Kafka
9. Notification Service consumes event
10. Notification sent asynchronously
```

---

# Database Design

## Wallet Table

| Column | Description |
|---|---|
| id | Primary key |
| user_id | User identifier |
| balance | Current wallet balance |

---

## Transaction Table

| Column | Description |
|---|---|
| id | Transaction ID |
| from_user | Sender user ID |
| to_user | Receiver user ID |
| amount | Transfer amount |
| status | SUCCESS / FAILED |
| idempotency_key | Unique request identifier |
| created_at | Timestamp |

---

# Important Engineering Concepts Implemented

## Transactional Integrity
Used `@Transactional` to ensure atomic money transfer operations.

---

## Idempotency
Implemented idempotency keys to prevent duplicate payment processing during retries.

---

## Pessimistic Locking
Used row-level database locking to prevent concurrent balance corruption and double spending.

---

## Transaction Propagation
Used `REQUIRES_NEW` propagation to persist failed transaction logs independently.

---

## Event-Driven Communication
Integrated Kafka producer-consumer workflow for asynchronous communication between services.

---

# API Endpoints

## Wallet APIs

### Create Wallet
```http
POST /wallet
```

### Get Wallet Balance
```http
GET /wallet/{userId}
```

### Add Money
```http
POST /wallet/add-money
```

### Transfer Money
```http
POST /wallet/transfer
```

### Transaction History
```http
GET /wallet/transactions/{userId}
```

---

# Sample Transfer Request

```json
{
  "fromUser": 1,
  "toUser": 2,
  "amount": 100,
  "idempotencyKey": "txn-123"
}
```

---

# Running the Project Locally

## Prerequisites
- Java 17
- Maven
- PostgreSQL
- Docker
- Apache Kafka

---

## Clone Repository

```bash
git clone https://github.com/<your-github-username>/payment-processing-platform.git
```

---

## Start Kafka

```bash
docker run -p 9092:9092 apache/kafka
```

---

## Run Services

### Start Payment Service

```bash
cd payment-service
mvn spring-boot:run
```

### Start Notification Service

```bash
cd notification-service
mvn spring-boot:run
```

### Start API Gateway

```bash
cd api-gateway
mvn spring-boot:run
```

---

# Security

The system uses JWT-based authentication at the API Gateway layer.

Every incoming request is validated before routing to backend services.

---

# Future Enhancements

## Infrastructure
- Docker Compose orchestration
- Kubernetes deployment
- CI/CD pipeline integration

## Scalability
- Redis distributed locking
- Rate limiting
- Distributed caching

## Reliability
- Dead Letter Queue (DLQ)
- Retry mechanisms
- Circuit breakers

## Observability
- Prometheus metrics
- Grafana dashboards
- Distributed tracing
- Centralized logging

## Advanced Payment Features
- Ledger service
- Fraud detection
- Webhook support
- Payment reconciliation

---

# Resume Highlights

This project demonstrates:
- Distributed systems engineering
- Event-driven architecture
- Production-grade backend development
- Concurrency handling
- Microservices architecture
- Kafka integration
- Transaction management
- API security

---

# Author

Prayash Gupta

- LinkedIn: https://www.linkedin.com/in/prayash-gupta/
- LeetCode: https://leetcode.com/u/PRAYASH_GUPTA/

---

# License

This project is intended for learning, backend engineering practice, and distributed systems exploration.

