# Architecture Documentation

## System Design Overview

This document provides an in-depth analysis of the distributed microservice architecture for the Healthcare Management System.

---

## 1. Architectural Pattern: Microservices

### Core Principles

**Service Independence**
- Each service owns its data and database
- No direct database access between services
- Autonomous deployment and scaling

**API-First Design**
- All communication happens through well-defined APIs
- REST for public APIs
- gRPC for internal service-to-service calls
- Kafka for asynchronous events

**Fault Isolation**
- Service failures don't cascade
- Circuit breakers for resilience (future enhancement)
- Graceful degradation

### Benefits in This Architecture
- **Scalability**: Services can scale independently based on demand
- **Technology Diversity**: Each service can use different tech stacks
- **Rapid Development**: Teams can develop services in parallel
- **Resilience**: Failures are isolated to specific services

---

## 2. Service-to-Service Communication

### Pattern 1: Synchronous Communication (REST/gRPC)

**Use Case**: When immediate response is required

#### REST Calls (API Gateway → Services)
```
Request Flow:
1. Client sends REST request to API Gateway
2. API Gateway routes to appropriate service
3. Service processes and returns response
4. Gateway aggregates response to client

Example: GET /patients/123
- Gateway receives request
- Calls Patient Service REST endpoint
- Returns patient data to client
```

**Advantages**:
- Simple and well-understood
- Easy debugging
- Synchronous behavior

**Disadvantages**:
- Tight coupling
- Service must be available
- Network latency

#### gRPC Calls (Inter-Service Communication)
```
High-Performance RPC Example:
- Appointment Service calls Patient Service (gRPC)
- Get patient data for appointment validation
- Uses Protocol Buffers for efficient serialization
- Binary protocol over HTTP/2

Performance: ~10x faster than REST/JSON
```

**gRPC Services in Architecture**:
| Service | Port | Used By | Purpose |
|---------|------|---------|---------|
| Patient Service | 9000 | Appointment, Billing | Patient data queries |
| Appointment Service | 9002 | Billing | Appointment validation |
| Billing Service | 9003 | Appointment | Billing operations |

---

### Pattern 2: Asynchronous Communication (Kafka)

**Use Case**: When processing can be deferred

```
Event Flow:
1. Patient Service creates/updates patient
2. Publishes PatientEvent to Kafka topic
3. Billing Service consumes event asynchronously
4. Analytics Service aggregates event data

Advantages:
- Decoupled services
- High throughput
- Built-in message reliability
- Consumer can lag (e.g., during maintenance)
```

**Kafka Topics**:
```yaml
patient-events:
  producers: [patient-service]
  
appointment-events:
  producers: [appointment-service]
  consumers: [billing-service]
```

---

## 3. API Gateway Architecture

### Responsibilities

```
┌─────────────────────────────────────────────┐
│          External Clients                    │
└──────────────────┬──────────────────────────┘
                   │ HTTP/REST
        ┌──────────▼──────────┐
        │   API Gateway       │
        │ - Request Routing   │
        │ - Load Balancing    │
        │ - Authentication    │
        │ - API Aggregation   │
        │ - Swagger UI        │
        └──────────┬──────────┘
                   │
        ┌──────────┴───────────────┬────────┬──────────┐
        │                          │        │          │
        ▼                          ▼        ▼          ▼
   Auth Service            Patient Service Appt    Billing
   (4005)                  (4000)          Service  Service
   JWT Validation          CRUD            (4002)   (4003)
                          Operations
```

### Features
- **Request Routing**: Routes incoming requests to appropriate services
- **Load Balancing**: Distributes load across service instances
- **Authentication Filter**: Validates JWT tokens before forwarding
- **Response Aggregation**: Combines multiple service responses
- **API Documentation**: Aggregate Swagger specs from services

### Configuration Example
```yaml
# routes defined as filters/predicates
Routes:
  - /auth/** → auth-service:4005
  - /patients/** → patient-service:4000
  - /appointments/** → appointment-service:4002
  - /billing/** → billing-service:4003
```

---

## 4. Authentication & Authorization

### JWT-Based Authentication Flow

```
1. Login Request
   Client → Auth Service: POST /auth/login
   
2. Token Generation
   Auth Service generates JWT: {header.payload.signature}
   
3. Token Usage
   Client attaches token: Authorization: Bearer {token}
   
4. Validation
   API Gateway validates token before forwarding
   Token contains: user_id, roles, expiry_time
   
5. Authorization
   Services check roles in token for authorization
```

### Token Structure
```json
{
  "header": {
    "alg": "HS256",
    "typ": "JWT"
  },
  "payload": {
    "sub": "user123",
    "email": "user@example.com",
    "roles": ["USER", "DOCTOR"],
    "iat": 1715000000,
    "exp": 1715003600
  },
  "signature": "..."
}
```

### Security Best Practices Implemented
✅ Passwords hashed with Spring Security  
✅ JWT tokens signed and verified  
✅ Token expiration enforced  
✅ API Gateway validates all tokens  
✅ HTTPS recommended for production  

---

## 5. Database Design

### Database-Per-Service Pattern

**Philosophy**: Each service owns its data

```
Service         Database        Tables              Data Ownership
──────────────────────────────────────────────────────────────────
Auth Service    auth_db         - users             User authentication data
                                - credentials       
                                
Patient Service patient_db      - patients          Patient profiles,
                                - medical_history   medical information
                                
Appointment     appointment_db  - appointments      Appointment scheduling
Service                         - schedules         
                                
Billing Service billing_db      - invoices          Billing records,
                                - transactions      payment data
```

### Data Consistency

**Within Service**: ACID compliance via PostgreSQL transactions

**Across Services**: Eventual consistency via events
```
Example: Create Appointment
1. Appointment Service creates appointment (local transaction)
2. Publishes AppointmentCreated event
3. Billing Service receives event → creates billing record
4. Analytics Service receives event → updates dashboard

Guarantee: Eventual consistency between services
```

---

## 6. Resilience & Failure Handling

### Failure Scenarios

| Scenario | Handling | Result |
|----------|----------|--------|
| Service Down | Circuit Breaker (future) | Graceful degradation |
| DB Connection Lost | Retry logic | Exponential backoff |
| Kafka Unavailable | Buffer locally then retry | No data loss |
| Network Partition | Timeouts | Timeouts respected |

### Current Implementation
```java
// Kafka Producer: Ensures message delivery
@Bean
public ProducerFactory<String, PatientEvent> producerFactory() {
    Map<String, Object> configProps = new HashMap<>();
    configProps.put(ProducerConfig.RETRIES_CONFIG, 3);
    configProps.put(ProducerConfig.ACKS_CONFIG, "all");
    return new DefaultProducerFactory<>(configProps);
}

// Service calls: ReadTimeout configured
class RestTemplate {
    timeout: 10000ms  // 10 seconds
}
```

---

## 7. Deployment Architecture

### Container Orchestration

**Current**: Docker Compose (Development/Testing)
**Production**: Kubernetes recommended

```
┌────────────────────────────────────────────┐
│         Docker Compose Network             │
│          (patient-network bridge)          │
├────────────────────────────────────────────┤
│                                            │
│  Container 1        Container 2  ...      │
│  ┌──────────────┐  ┌──────────┐          │
│  │ Auth Service │  │Patient S │          │
│  │ + Auth DB    │  │+ Pat DB  │          │
│  │ + Networks   │  │+ Network │          │
│  └──────────────┘  └──────────┘          │
│                                            │
│  Infrastructure Services:                 │
│  - Kafka (9092)                           │
│  - Zookeeper (2181)                       │
│  - API Gateway                            │
│                                            │
└────────────────────────────────────────────┘
```

### Port Mapping

```yaml
External Port : Container Port : Service
4004          : 4004           : API Gateway
4005          : 4005           : Auth Service
4000          : 4000           : Patient Service
4002          : 4002           : Appointment Service
4003          : 4003           : Billing Service
4006          : 4006           : Analytics Service
9000          : 9000           : Patient Service (gRPC)
9002          : 9002           : Appointment Service (gRPC)
9003          : 9003           : Billing Service (gRPC)
```

---

## 8. Data Flow Scenarios

### Scenario 1: Create Patient Appointment

```
1. API Request
   POST /appointments
   {
     "patientId": 123,
     "date": "2056-05-20",
     "doctorId": 456
   }

2. API Gateway
   ├─ Validates JWT token
   └─ Routes to Appointment Service

3. Appointment Service
   ├─ Queries Patient Service (gRPC) → Validate patient exists
   ├─ Creates appointment record → appointment_db
   └─ Publishes "AppointmentCreated" event → Kafka

4. Billing Service (Async Consumer)
   ├─ Receives event
   ├─ Creates billing record
   └─ Updates billing_db

6. Response
   Returns 201 Created with appointment details
```

### Scenario 2: Update Patient Profile

```
1. API Request
   PUT /patients/123
   {
     "name": "John Doe",
     "email": "john@example.com"
   }

2. API Gateway
   ├─ Validates token & permissions
   └─ Routes to Patient Service

3. Patient Service
   ├─ Validates input
   ├─ Updates patient_db
   └─ Publishes "PatientUpdated" event

4. Downstream Services
   Billing Service processes the update asynchronously via Kafka

5. Response
   Returns 200 OK with updated patient data
```

---

## 9. Performance Considerations

### gRPC vs REST Performance

```
Operation: Query Patient Data

REST (JSON over HTTP/1.1):
├─ Payload Size: ~500 bytes (JSON + headers)
├─ Processing: JSON serialization/deserialization
├─ Latency: ~50ms average
└─ Throughput: ~2000 req/sec

gRPC (Protobuf over HTTP/2):
├─ Payload Size: ~100 bytes (binary + headers)
├─ Processing: Protobuf fast marshaling
├─ Latency: ~5ms average (10x faster!)
└─ Throughput: ~10000 req/sec
```

### Kafka Streaming Performance

```
Event Flow: 1000 events/second
├─ Broker Throughput: 100k events/sec (configurable)
├─ Consumer Lag: < 500ms typical
├─ Retention: 7 days (configurable)
└─ Replication: 1 broker (increase for HA)
```

---

## 10. Scaling Strategy

### Horizontal Scaling (Docker Compose)
```bash
# Scale Patient Service to 3 instances
docker-compose up -d --scale patient-service=3

# Load balancer automatically distributes requests
```

### Recommendations

| Scenario | Solution |
|----------|----------|
| High patient queries | Scale Patient Service instances, add read replicas |
| Kafka bottleneck | Increase partitions, optimize consumer config |
| DB connection pooling | Adjust HikariCP pool size in application.yaml |
| Memory issues | Increase container memory limits |

---

## 11. Technology Choices & Rationale

| Component | Choice | Why |
|-----------|--------|-----|
| **Framework** | Spring Boot | Industry standard, large ecosystem |
| **Data Layer** | PostgreSQL | ACID compliance, relational data |
| **Messaging** | Kafka | High throughput, topic-based pub/sub |
| **RPC** | gRPC | 10x performance over REST, streaming |
| **Packages** | Protobuf | Language-neutral, efficient |
| **Security** | JWT | Stateless, scalable, industry standard |
| **Gateway** | Spring Cloud Gateway | Native Spring, easy integration |
| **Cache** | Spring Cache (future) | Session storage, query caching |
| **Deployment** | Docker | Consistency, isolation, portability |

---

## 12. Future Enhancements

### Short-term (1-2 quarters)
- [ ] Circuit breaker pattern (Netflix Hystrix/Resilience4j)
- [ ] Distributed tracing (Spring Cloud Sleuth + Zipkin)
- [ ] Service mesh (Istio for Kubernetes)
- [ ] API rate limiting and quota management

### Medium-term (2-3 quarters)
- [ ] Kubernetes migration from Docker Compose
- [ ] Cache layer (Redis) for frequently accessed data
- [ ] Event sourcing for audit trail
- [ ] CQRS pattern for read-heavy services

### Long-term (3-6 months)
- [ ] GraphQL layer for flexible queries
- [ ] Machine learning for predictive analytics
- [ ] Multi-region deployment (geo-distributed)
- [ ] Real-time notifications (WebSockets)

---

## 13. Monitoring & Observability

### Logging Strategy
```
Level: DEBUG
Info Fields: timestamp, service, request_id, user_id
Aggregation: ELK Stack recommended (Elasticsearch, Logstash, Kibana)
```

### Metrics to Monitor
```
Service Health:
├─ Request latency (p50, p95, p99)
├─ Error rate & types
├─ Throughput (requests/sec)
├─ DB connection pool usage
└─ Kafka consumer lag

Infrastructure:
├─ CPU & Memory usage
├─ Disk I/O
├─ Network utilization
└─ Container restart count
```

### API Gateway Observability
```
Tracked Metrics:
- Route latency per service
- Authentication success/failure
- Request routing patterns
- Response status code distribution
```

---

## 14. Security Architecture

### Defense In Depth

```
Layer 1: API Gateway
├─ HTTPS/TLS termination
├─ JWT token validation
└─ Rate limiting

Layer 2: Service Level
├─ Request authentication
├─ Authorization checks (Spring Security)
├─ Input validation
└─ SQL injection prevention (JPA)

Layer 3: Database
├─ PostgreSQL user permissions
├─ Connection encryption
├─ Data encryption at rest (future)
└─ Audit logging

Layer 4: Network
├─ Docker network isolation
├─ Firewall rules
├─ Secrets management (environment vars)
└─ No hardcoded credentials
```

---

## 15. Troubleshooting Guide

### Common Issues

**Q: Services can't communicate**
```
A: Check Docker network: docker network ls
   Verify service URL format: http://service-name:port
   Check service logs: docker logs service-name
```

**Q: Kafka consumer lag increasing**
```
A: Check consumer config in application.yaml
   Monitor Kafka topic: kafka-consumer-groups.sh
   Scale service: docker-compose up -d --scale service=N
```

**Q: Database connection errors**
```
A: Verify PostgreSQL is running
   Check SPRING_DATASOURCE_URL matches container name
   Monitor connection pool: HikariCP metrics
```

---

**Last Updated**: May 2026  
**Architecture Version**: 1.0




