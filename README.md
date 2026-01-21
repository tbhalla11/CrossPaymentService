# CrossPaymentService

A production-ready Spring Boot microservice for processing cross-border payments with real-time currency conversion via external FX service integration.

---

## 📋 Overview

**CrossPaymentService** is a payment processing system that:
- Accepts payment requests in one currency
- Calls an external FX service to get real-time exchange rates
- Calculates payout amounts in the destination currency
- Stores payment records in PostgreSQL
- Handles failures gracefully with retry, circuit breaker, and timeout patterns

### Key Features

✅ **RESTful API** - Clean API for creating and retrieving payments  
✅ **FX Integration** - Real-time currency conversion via Twirp protocol  
✅ **Resilience Patterns** - Retry (3 attempts), Circuit Breaker, Timeout (5s)  
✅ **Database Persistence** - PostgreSQL with JPA/Hibernate  
✅ **Input Validation** - Comprehensive request validation  
✅ **Error Handling** - Graceful failure handling with detailed error messages  
✅ **Transaction Management** - ACID compliance for payment processing  

### Tech Stack

| Component | Technology |
|-----------|------------|
| **Framework** | Spring Boot 3.2.1 |
| **Language** | Java 17 |
| **Database** | PostgreSQL 15 |
| **Build Tool** | Maven 3.9+ |
| **Resilience** | Resilience4j |
| **Utilities** | Lombok |

---

## 🏗️ Architecture

```
┌─────────────┐
│   Client    │
└──────┬──────┘
       │ POST /api/payments
       ↓
┌─────────────────────────────┐
│   PaymentController         │
│   (REST API Layer)          │
└──────┬──────────────────────┘
       │
       ↓
┌─────────────────────────────┐
│   PaymentService            │
│   (Business Logic)          │
└──────┬──────────────────────┘
       │
       ├──→ PaymentRepository ──→ PostgreSQL
       │
       └──→ FXServiceClient ────→ External FX Service (Twirp)
              (Retry + Circuit Breaker + Timeout)
```

### Payment Flow

1. **Client sends payment request** → `POST /api/payments`
2. **PaymentService creates PENDING payment** → Saved to database
3. **FXServiceClient calls FX service** → Gets exchange rate (Twirp protocol)
4. **Calculate payout amount** → `amount × exchangeRate`
5. **Update payment to SUCCESS/FAILED** → Final state saved
6. **Return response to client** → Complete payment details

---

## 🚀 Quick Start

### Prerequisites

- **Java 17** or higher
- **Maven 3.9+**
- **PostgreSQL 15+** (or Docker)
- **FX Service** running on port 4000 (or use mock)

### 1. Clone Repository

```bash
git clone https://github.com/yourusername/CrossPaymentService.git
cd CrossPaymentService
```

### 2. Start PostgreSQL

**Option A: Docker (Recommended)**
```bash
docker run --name crosspayment-postgres \
  -e POSTGRES_DB=crosspaymentdb \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  -d postgres:15-alpine
```

**Option B: Local PostgreSQL**
```bash
# Install PostgreSQL, then:
createdb crosspaymentdb
```

### 3. Configure Application

Edit `src/main/resources/application.properties`:

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/crosspaymentdb
spring.datasource.username=postgres
spring.datasource.password=postgres

# FX Service
fx.service.url=http://localhost:4000  # Real FX service
# fx.service.url=http://localhost:8080  # Mock FX service
```

### 4. Build & Run

```bash
# Build
mvn clean install

# Run
mvn spring-boot:run
```

Application starts on: **http://localhost:8080**

---

## 📡 API Endpoints

### Create Payment

```bash
POST /api/payments
Content-Type: application/json

{
  "sender": "John Doe",
  "receiver": "Jane Smith",
  "amount": 100.00,
  "sourceCurrency": "USD",
  "destinationCurrency": "EUR"
}
```

**Success Response (201 Created):**
```json
{
  "id": 1,
  "sender": "John Doe",
  "receiver": "Jane Smith",
  "amount": 100.00,
  "sourceCurrency": "USD",
  "destinationCurrency": "EUR",
  "exchangeRate": 0.920000,
  "payoutAmount": 92.00,
  "status": "SUCCESS",
  "message": null,
  "createdAt": "2026-01-21T00:10:00",
  "updatedAt": "2026-01-21T00:10:05"
}
```

**Failure Response (200 OK):**
```json
{
  "id": 2,
  "status": "FAILED",
  "message": "FX service is unavailable after multiple attempts",
  "exchangeRate": null,
  "payoutAmount": null
}
```

### Get Payment by ID

```bash
GET /api/payments/{id}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "sender": "John Doe",
  "status": "SUCCESS",
  ...
}
```

### Get Supported Currencies

```bash
GET /api/payments/currencies
```

**Response (200 OK):**
```json
{
  "currencies": ["USD", "EUR", "GBP", "JPY", "CAD", "AUD", "CHF", "CNY", "INR", "MXN"]
}
```

---

## 🧪 Testing

### cURL Examples

**Create Payment:**
```bash
curl -X POST http://localhost:8080/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "sender": "Alice",
    "receiver": "Bob",
    "amount": 250.00,
    "sourceCurrency": "USD",
    "destinationCurrency": "EUR"
  }'
```

**Get Payment:**
```bash
curl http://localhost:8080/api/payments/1
```

**Get Currencies:**
```bash
curl http://localhost:8080/api/payments/currencies
```

### Postman Collection

Import the Postman collection from `postman/CrossPaymentService.postman_collection.json`

---

## 🗄️ Database Schema

```sql
CREATE TABLE payments (
    id                    BIGSERIAL PRIMARY KEY,
    sender                VARCHAR(255) NOT NULL,
    receiver              VARCHAR(255) NOT NULL,
    amount                DECIMAL(19,2) NOT NULL,
    source_currency       VARCHAR(3) NOT NULL,
    destination_currency  VARCHAR(3) NOT NULL,
    exchange_rate         DECIMAL(19,6),
    payout_amount         DECIMAL(19,2),
    status                VARCHAR(20) NOT NULL,
    error_message         VARCHAR(500),
    created_at            TIMESTAMP NOT NULL,
    updated_at            TIMESTAMP NOT NULL
);
```

**View Payments:**
```bash
docker exec -it crosspayment-postgres psql -U postgres -d crosspaymentdb

SELECT id, sender, receiver, status, exchange_rate, payout_amount 
FROM payments 
ORDER BY created_at DESC;
```

---

## 🛡️ Resilience Patterns

### Retry Pattern
- **Attempts**: 3 (exponential backoff: 2s, 4s, 8s)
- **Triggers**: Network errors, HTTP 5xx errors
- **Fallback**: Mark payment as FAILED

### Circuit Breaker
- **Threshold**: 50% failure rate
- **Window**: Last 10 calls
- **Wait Duration**: 60 seconds
- **States**: CLOSED → OPEN → HALF_OPEN

### Timeout
- **Duration**: 5 seconds
- **Applies to**: FX service calls
- **Effect**: Prevents hanging on slow responses

---

## ⚙️ Configuration

### application.properties

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/crosspaymentdb
spring.datasource.username=postgres
spring.datasource.password=postgres

# FX Service
fx.service.url=http://localhost:4000
fx.service.timeout=5000

# Resilience4j - Retry
resilience4j.retry.instances.fxService.max-attempts=3
resilience4j.retry.instances.fxService.wait-duration=2s

# Resilience4j - Circuit Breaker
resilience4j.circuitbreaker.instances.fxService.failure-rate-threshold=50
resilience4j.circuitbreaker.instances.fxService.wait-duration-in-open-state=60s

# Server
server.port=8080
```

---

## 📦 Project Structure

```
CrossPaymentService/
├── src/main/java/com/example/crosspayment/
│   ├── CrossPaymentServiceApplication.java    # Main entry point
│   ├── controller/
│   │   └── PaymentController.java             # REST API endpoints
│   ├── service/
│   │   └── PaymentService.java                # Business logic
│   ├── repository/
│   │   └── PaymentRepository.java             # Database access
│   ├── client/
│   │   └── FXServiceClient.java               # FX service integration
│   ├── model/
│   │   ├── Payment.java                       # JPA entity
│   │   └── PaymentStatus.java                 # Enum
│   ├── dto/
│   │   ├── PaymentRequest.java                # API request
│   │   ├── PaymentResponse.java               # API response
│   │   ├── FXRateQuote.java                   # FX request
│   │   ├── FXRateResponse.java                # FX response
│   │   └── FXSupportedCurrency.java           # Currencies
│   ├── exception/
│   │   ├── FxServiceException.java
│   │   ├── PaymentNotFoundException.java
│   │   └── GlobalExceptionHandler.java
│   └── config/
│       └── RestTemplateConfig.java            # HTTP client
├── src/main/resources/
│   └── application.properties                 # Configuration
├── pom.xml                                    # Maven dependencies
└── README.md
```

---

## 🐳 Docker Deployment

### Build Docker Image

```bash
# Build JAR
mvn clean package -DskipTests

# Create Dockerfile
cat > Dockerfile << 'EOF'
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/CrossPaymentService-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
EOF

# Build image
docker build -t crosspayment-service:latest .
```

### Run with Docker Compose

Create `docker-compose.yml`:

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: crosspaymentdb
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
    volumes:
      - postgres-data:/var/lib/postgresql/data

  payment-service:
    image: crosspayment-service:latest
    depends_on:
      - postgres
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/crosspaymentdb
      FX_SERVICE_URL: http://fx-service:4000
    ports:
      - "8080:8080"

volumes:
  postgres-data:
```

**Start:**
```bash
docker-compose up -d
```

---

## 🚨 Troubleshooting

### Application won't start

**Error:** `Failed to configure a DataSource`

**Solution:** Make sure PostgreSQL is running
```bash
docker ps | grep postgres
```

---

### FX Service errors

**Error:** `404 Not Found` when calling FX service

**Solutions:**
1. Check FX service is running on port 4000
2. Or switch to mock: `fx.service.url=http://localhost:8080`

---

### Port 8080 already in use

```bash
# Find process
lsof -i :8080

# Kill it
kill -9 <PID>

# Or change port in application.properties
server.port=9090
```

---

## 📊 Monitoring

### Health Check

```bash
curl http://localhost:8080/actuator/health
```

### View Logs

```bash
# Application logs
tail -f logs/spring.log

# Docker logs
docker logs -f crosspayment-service
```

### Database Metrics

```bash
docker exec -it crosspayment-postgres psql -U postgres -d crosspaymentdb

-- Payment statistics
SELECT status, COUNT(*) as count, AVG(payout_amount) as avg_payout
FROM payments
GROUP BY status;
```

---

## 🔐 Security Notes

**For Production:**
- [ ] Enable HTTPS/TLS
- [ ] Add authentication (OAuth2/JWT)
- [ ] Encrypt database credentials
- [ ] Rate limiting
- [ ] Input sanitization
- [ ] SQL injection prevention (JPA handles this)
- [ ] CORS configuration

---

## 📝 License

This project is licensed under the MIT License.

---

## 👥 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

---


---

**Built with ❤️ using Spring Boot**
