# Setup & Installation Guide

Complete step-by-step guide to set up and run the distributed microservice architecture.

---

## 📋 Table of Contents

1. [System Requirements](#system-requirements)
2. [Installation Steps](#installation-steps)
3. [Docker Compose Setup](#docker-compose-setup)
4. [Local Development Setup](#local-development-setup)
5. [Verification](#verification)
6. [Troubleshooting](#troubleshooting)

---

## 🔧 System Requirements

### Minimum Requirements

| Requirement | Version | Notes |
|-------------|---------|-------|
| **Docker** | 20.10+ | For containerization |
| **Docker Compose** | 1.29+ | For orchestration |
| **Java JDK** | 21 LTS | For local development |
| **Maven** | 3.8+ | For building services |
| **Git** | 2.0+ | For version control |
| **RAM** | 8GB | Minimum, 16GB recommended |
| **Disk Space** | 20GB | For containers and databases |

### Recommended OS Specifics

**Windows**:
- Windows 10/11 Pro or Enterprise (for Docker Desktop)
- WSL 2 (Windows Subsystem for Linux 2)
- PowerShell or Windows Terminal

**macOS**:
- macOS 11 or later
- Apple Silicon or Intel processor
- Docker Desktop for Mac

**Linux**:
- Ubuntu 20.04 LTS or later
- Docker CE
- Docker Compose

---

## 📥 Installation Steps

### Step 1: Clone the Repository

```bash
# Clone repository
git clone <your-repository-url>
cd distributed-microservice-architecture

# Verify structure
ls -la
```

**Expected Output**:
```
drwxr-xr-x  api-gateway/
drwxr-xr-x  auth-service/
drwxr-xr-x  patient-service/
drwxr-xr-x  appointment-service/
drwxr-xr-x  billing-service/
drwxr-xr-x  docker-package/
-rw-r--r--  README.md
-rw-r--r--  ARCHITECTURE.md
-rw-r--r--  SETUP.md
```

### Step 2: Install Docker & Docker Compose

**Windows (Docker Desktop)**:
1. Download from https://www.docker.com/products/docker-desktop
2. Run installer and follow prompts
3. Enable WSL 2 during installation
4. Restart computer

**macOS (Docker Desktop)**:
1. Download from https://www.docker.com/products/docker-desktop
2. Open .dmg file and drag to Applications
3. Open Docker from Applications
4. Grant permissions when prompted

**Linux (Ubuntu)**:
```bash
# Install Docker
sudo apt-get update
sudo apt-get install docker.io -y
sudo usermod -aG docker $USER

# Install Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/download/v2.20.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Verify installation
docker --version
docker-compose --version
```

### Step 3: Install Java 21

**Windows**:
1. Download JDK 21 from https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html
2. Run installer
3. Set JAVA_HOME environment variable:
   - System Properties → Environment Variables
   - Add: `JAVA_HOME=C:\Program Files\Java\jdk-21.x.x`

**macOS**:
```bash
brew install java@21
# Add to ~/.zprofile:
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
```

**Linux**:
```bash
sudo apt-get update
sudo apt-get install openjdk-21-jdk -y

# Verify
java -version
```

### Step 4: Install Maven

**Windows PowerShell**:
```powershell
choco install maven
# Verify
mvn -v
```

**macOS**:
```bash
brew install maven
# Verify
mvn -v
```

**Linux**:
```bash
sudo apt-get install maven -y
# Verify
mvn -v
```

### Step 5: Verify Installation

```bash
# Check all versions
docker --version          # Docker version 20.10.0+
docker-compose --version  # Docker Compose version 1.29.0+
java -version            # Java version "21.x.x"
mvn -v                   # Apache Maven 3.8.1+
git --version            # git version 2.40.0+
```

---

## 🐳 Docker Compose Setup

### Quick Start (Recommended for Development)

```bash
cd docker-package

# Build and start all services
docker-compose up --build

# Or run in background
docker-compose up -d --build

# Check status
docker-compose ps

# View logs
docker-compose logs -f
```

### Services Startup Order

Docker Compose automatically manages dependencies:

```
1. Database Containers (Start immediately)
   - auth-db
   - patient-db
   - appointment-db
   - billing-db

2. Infrastructure (Start after DBs)
   - zookeeper
   - kafka

3. Services (Start after dependencies ready)
   - auth-service
   - patient-service
   - appointment-service
   - billing-service

4. API Gateway (Starts last, after all services)
   - api-gateway
```

### Wait for Services to Be Ready

```bash
# Check individual service health
docker-compose logs auth-service | tail -20
docker-compose logs patient-service | tail -20

# All services ready when you see:
# "Started [ServiceName] in X.XXX seconds"
```

### Stopping Services

```bash
# Stop all services (containers remain)
docker-compose stop

# Stop and remove containers
docker-compose down

# Remove containers and volumes (reset databases)
docker-compose down -v
```

### Common Docker Compose Commands

```bash
# View running containers
docker-compose ps

# View logs for specific service
docker-compose logs patient-service

# View live logs for all services
docker-compose logs -f

# Execute command in running container
docker-compose exec patient-service bash

# Rebuild single service
docker-compose build patient-service

# Restart service
docker-compose restart patient-service

# Validate compose file
docker-compose config
```

---

## 💻 Local Development Setup

### Build All Services Locally

```bash
# From project root
./mvnw clean install -DskipTests

# Or per-service
cd auth-service
./mvnw clean package -DskipTests
```

### Run Individual Service Locally

**Option 1: Using Maven Spring Boot Plugin**

```bash
cd patient-service

# Start service on port 4000
./mvnw spring-boot:run

# With custom port
./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=8001"

# With specific profile
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"
```

**Option 2: Run Built JAR**

```bash
cd patient-service

# Move to common location
java -jar target/patient-service-0.0.1-SNAPSHOT.jar

# With custom port
java -jar target/patient-service-0.0.1-SNAPSHOT.jar --server.port=8001
```

### Local Development Configuration

**Local Profile** (application-local.yaml):

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5434/patient_db
    username: postgres
    password: postgres
  
  kafka:
    bootstrap-servers: localhost:9092

  jpa:
    hibernate:
      ddl-auto: update

server:
  port: 4000

grpc:
  server:
    port: 9000
```

### Development Tools Setup

**IDE Setup (IntelliJ IDEA)**:

1. Open Project Structure
2. Set Project SDK to Java 21
3. Set Language Level to 21
4. Set Compiler output to `target`
5. Enable annotation processing (Lombok)

**VS Code Setup**:

1. Install extensions:
   - Extension Pack for Java
   - Maven for Java
   - Spring Boot Dashboard

2. Create `.vscode/launch.json`:
```json
{
  "version": "0.2.0",
  "configurations": [
    {
      "type": "java",
      "name": "Spring Boot App",
      "request": "launch",
      "cwd": "${workspaceFolder}",
      "mainClass": "com.pm.PatientServiceApplication",
      "projectName": "patient-service",
      "preLaunchTask": "maven: compile",
      "args": ""
    }
  ]
}
```

---

## ✅ Verification

### Test Docker Compose Deployment

```bash
# 1. Navigate to docker-package
cd docker-package

# 2. Start services
docker-compose up -d

# Check all containers running
docker-compose ps
# Expected: All 5 services + 4 DBs + Kafka + Zookeeper running

# 4. Test API Gateway
curl -X GET http://localhost:4004/swagger-ui.html

# 5. Check service health
curl http://localhost:4005/actuator/health       # Auth Service
curl http://localhost:4000/actuator/health       # Patient Service
curl http://localhost:4002/actuator/health       # Appointment Service
curl http://localhost:4003/actuator/health       # Billing Service

# 6. View logs
docker-compose logs auth-service
```

### Test Individual Services

```bash
# 1. Auth Service Health
curl http://localhost:4005/swagger-ui.html

# 2. Patient Service Endpoints
curl http://localhost:4000/patients

# 3. Appointment Service Endpoints  
curl http://localhost:4002/appointments

# 4. Kafka Connectivity
docker exec kafka kafka-topics.sh --list --bootstrap-server localhost:9092

# 5. Database Connectivity
docker exec patient-db psql -U postgres -d patient_db -c "SELECT version();"
```

### API Testing via Swagger UI

```
Services Available at:
- Auth Service: http://localhost:4005/swagger-ui.html
- Patient Service: http://localhost:4000/swagger-ui.html
- Appointment Service: http://localhost:4002/swagger-ui.html
- Billing Service: http://localhost:4003/swagger-ui.html

Aggregated View:
- API Gateway: http://localhost:4004/swagger-ui.html
```

### Test Authentication Flow

```bash
# 1. Login
curl -X POST http://localhost:4005/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com", "password":"password"}'

# Response: {"token":"eyJhbGcioiJIUzI1NiIs..."}

# 2. Use token for subsequent requests
TOKEN="your-jwt-token-here"

curl -X GET http://localhost:4000/patients \
  -H "Authorization: Bearer $TOKEN"
```

---

## 🔧 Troubleshooting

### Issue 1: Containers Not Starting

**Problem**: Services exit immediately or fail to start

**Solutions**:
```bash
# Check logs
docker-compose logs <service-name>

# Check port conflicts
netstat -ano | findstr :4004    # Windows
lsof -i :4004                   # macOS/Linux

# Rebuild containers
docker-compose down -v
docker-compose up --build

# Check Docker resources
docker system df
docker system prune
```

### Issue 2: Database Connection Errors

**Problem**: "Could not acquire connection" or "Connection refused"

**Solutions**:
```bash
# Verify database is running
docker-compose ps | grep db

# Check database logs
docker-compose logs patient-db

# Test database connection
docker exec patient-db psql -U postgres -c "SELECT 1"

# Check connection string matches container name
# In docker-compose.yml:
# SPRING_DATASOURCE_URL: jdbc:postgresql://patient-db:5432/patient_db
```

### Issue 3: Kafka Connection Issues

**Problem**: "Cannot connect to Kafka" or "No brokers available"

**Solutions**:
```bash
# Check Kafka container
docker-compose logs kafka

# Test Kafka connectivity
docker exec kafka kafka-broker-api-versions.sh --bootstrap-server localhost:9092

# Check topics
docker exec kafka kafka-topics.sh --list --bootstrap-server kafka:9092

# Reset Kafka (WARNING: Deletes all data)
docker-compose down -v
docker-compose up -d kafka zookeeper
```

### Issue 4: Port Already in Use

**Problem**: "Port 4004 already in use" or "Address already in use"

**Windows Solution**:
```powershell
# Find process using port
Get-Process -Id (Get-NetTCPConnection -LocalPort 4004).OwningProcess

# Kill process
Stop-Process -Id <PID> -Force

# Alternative: Change port in docker-compose.yml
# "4004:4004" → "4005:4004"
```

**macOS/Linux Solution**:
```bash
# Find process
lsof -i :4004

# Kill process
kill -9 <PID>

# Alternative: Change docker-compose.yml ports
```

### Issue 5: Out of Memory

**Problem**: Docker containers killed or "OutOfMemoryError"

**Solutions**:
```bash
# Check Docker resource limits
docker system df

# Increase Docker memory
# Docker Desktop Settings → Resources → Memory
# Set to at least 8GB

# Or limit individual services in docker-compose.yml:
services:
  patient-service:
    mem_limit: 512m
    memswap_limit: 1g
```

### Issue 6: Service Discovery (gRPC) Issues

**Problem**: Services can't call each other via gRPC

**Solution**:
```bash
# Verify service hostname resolution
docker exec patient-service ping billing-service

# Check gRPC server startup logs
docker-compose logs billing-service | grep "gRPC"

# Test gRPC connection
docker exec patient-service grpcurl -plaintext billing-service:9003 list
```

### Issue 7: Maven Build Fails

**Problem**: "Cannot resolve dependency" or "Protocol Buffers compilation error"

**Solutions**:
```bash
# Clear Maven cache
mvn clean

# Rebuild dependencies
mvn dependency:resolve

# Force update snapshots
mvn -U clean install

# Check Java version
java -version  # Should be 21
```

---

## 🚀 Quick Reference Commands

```bash
# Start all services
cd docker-package && docker-compose up -d

# Stop all services
cd docker-package && docker-compose down

# View all logs
docker-compose logs -f

# Restart specific service
docker-compose restart patient-service

# Execute command in container
docker-compose exec patient-service bash

# View service IP addresses
docker-compose exec patient-service hostname -I

# Scale service
docker-compose up -d --scale patient-service=3

# Build single service
./mvnw -pl patient-service clean package

# Run tests
./mvnw test

# Access service shell
docker exec -it patient-service /bin/bash
```

---

## 📚 Next Steps

After successful setup:

1. **Review API Documentation**
   - Visit Swagger UI: http://localhost:4005/swagger-ui.html
   - Test endpoints with interactive UI

2. **Study Architecture**
   - Read `ARCHITECTURE.md`
   - Understand service interactions

3. **Explore Source Code**
   - Check `patient-service/src/main/java`
   - Review controllers, services, and models

4. **Run Integration Tests**
   - Execute test suites
   - Understand testing patterns

5. **Modify & Experiment**
   - Change port numbers
   - Add new endpoints
   - Modify database schemas

---

## 📞 Support Resources

- **Docker Documentation**: https://docs.docker.com
- **Spring Boot Docs**: https://spring.io/projects/spring-boot
- **Maven Guide**: https://maven.apache.org/guides
- **PostgreSQL Docs**: https://www.postgresql.org/docs
- **Project README**: See `README.md`

---

**Last Updated**: May 2026  
**Setup Version**: 1.0





