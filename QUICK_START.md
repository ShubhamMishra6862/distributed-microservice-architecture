# Quick Reference Guide

## 🚀 Quick Start (5 minutes)

### Start Everything
```bash
cd docker-package
docker-compose up -d --build
# Wait ~1 minute for all services to start
```

### Verify It's Working
```bash
# Open browser
http://localhost:4004/swagger-ui.html
# Or test with curl
curl http://localhost:4005/actuator/health
```

---

## 📁 Documentation Files

| File | Purpose | Read Time |
|------|---------|-----------|
| **README.md** | Project overview, architecture, tech stack | 5 min |
| **ARCHITECTURE.md** | System design, patterns, performance | 15 min |
| **SETUP.md** | Installation & configuration | 10 min |
| **API_GUIDE.md** | All endpoints & examples | 10 min |
| **QUICK_START.md** | This file - quick reference | 3 min |

---

## 🔗 All Service URLs

```
API Gateway:        http://localhost:4004
Auth Service:       http://localhost:4005
Patient Service:    http://localhost:4000
Appointment Service: http://localhost:4002
Billing Service:    http://localhost:4003
Kafka:             localhost:9092
Zookeeper:         localhost:2181
```

## 📊 Database Ports

```
Auth DB:       5433
Patient DB:    5434
Appointment DB: 5437
Billing DB:    5436
```

---

## 🛠 Common Commands

```bash
# Start all services
docker-compose up -d

# Stop all services
docker-compose down

# View logs
docker-compose logs -f

# Restart service
docker-compose restart patient-service

# Scale service
docker-compose up -d --scale patient-service=3

# Run local build
cd patient-service && ./mvnw clean package

# Run local service
java -jar target/patient-service-0.0.1-SNAPSHOT.jar
```

---

## 🔐 Authentication Flow

```
1. GET token:    POST /auth/login
2. Use token:    Authorization: Bearer {token}
3. Call API:     GET /patients (with token)
4. Refresh:      Token expires in 1 hour
```

---

## 📡 Communication Patterns

```
Client ──REST──> API Gateway
                    │
    ┌───────────────┼───────────────┐
    │               │               │
    ▼               ▼               ▼
Services      Services        Services
    │               │               │
    └───REST───────┘
    
Services ──gRPC──> Other Services (high performance)
Services ──Kafka─> Event Stream (async)
```

---

## ✅ Testing Checklist

- [ ] Docker Compose runs without errors
- [ ] All 6 services show healthy status
- [ ] Can login to auth service
- [ ] Can create a patient
- [ ] Can schedule appointment
- [ ] Can validate billing
- [ ] Analytics receives events
- [ ] Swagger UI loads for all services

---

## 🤔 Common Issues & Fixes

**Services won't start:**
```bash
docker-compose down -v
docker-compose up --build
```

**Port already in use:**
```bash
# Find process on port 4004
lsof -i :4004
kill -9 <PID>
```

**Database connection error:**
```bash
docker-compose logs patient-db
# Check if DB container is running
docker ps | grep db
```

**Kafka not responding:**
```bash
docker-compose logs kafka
docker exec kafka kafka-topics.sh --list --bootstrap-server kafka:9092
```

---

## 💡 Pro Tips

1. **Always start with**: `docker-compose up -d --build`
2. **Check status with**: `docker-compose ps`
3. **Debug with logs**: `docker-compose logs service-name`
4. **Test with curl**: `curl -X GET http://localhost:4000/patients`
5. **Use Swagger UI**: http://localhost:4005/swagger-ui.html

---

## 🎯 Learning Path

1. **Day 1**: Read README.md & understand architecture
2. **Day 2**: Run setup.md & get environment working
3. **Day 3**: Explore API_GUIDE.md & test endpoints
4. **Day 4**: Study ARCHITECTURE.md deeply
5. **Day 5**: Modify code & experiment with features

---

## 📞 When You Get Stuck

1. Check logs: `docker-compose logs`
2. Review SETUP.md troubleshooting section
3. Verify all containers running: `docker-compose ps`
4. Rebuild: `docker-compose down -v && docker-compose up --build`
5. Check API_GUIDE.md for endpoint examples

---

## 🚀 Next Steps

After successful setup:
- [ ] Review the README.md for full picture
- [ ] Study ARCHITECTURE.md for design decisions
- [ ] Test all endpoints via Swagger UI
- [ ] Modify and extend the services
- [ ] Deploy to Kubernetes (future)

---

**Last Updated**: May 2026  
**Status**: ✅ Ready for Production


