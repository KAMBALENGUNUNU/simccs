<div align="center">

# 🛡️ SimCCS — Secure Information Management & Crisis Communication System

**A production-grade, cloud-native REST API backend built with Spring Boot 3**

[![Live API](https://img.shields.io/badge/Live%20API-simccs.onrender.com-brightgreen?style=for-the-badge&logo=render)](https://simccs.onrender.com)
[![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=openjdk)](https://openjdk.java.net/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen?style=for-the-badge&logo=springboot)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue?style=for-the-badge&logo=docker)](https://www.docker.com/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-18-blue?style=for-the-badge&logo=postgresql)](https://www.postgresql.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)](LICENSE)

</div>

---

## 📋 Table of Contents

- [About The Project](#-about-the-project)
- [Architecture Overview](#-architecture-overview)
- [Tech Stack](#-tech-stack)
- [Features](#-features)
- [Prerequisites](#-prerequisites)
- [Local Development Setup](#-local-development-setup)
- [Running with Docker Compose](#-running-with-docker-compose)
- [Environment Variables Reference](#-environment-variables-reference)
- [API Documentation](#-api-documentation)
- [Project Structure](#-project-structure)
- [Production Deployment (Render)](#-production-deployment-render)
- [Security Architecture](#-security-architecture)
- [Database Strategy](#-database-strategy)
- [Contributing](#-contributing)

---

## 🎯 About The Project

**SimCCS** is a high-performance, enterprise-grade backend system designed to manage **crisis communications**, **misinformation detection**, and **secure team collaboration** in high-stakes operational environments.

The system provides a centralized platform for organizations to:
- Receive, classify, and process **crisis incident reports** through a structured workflow
- Leverage **Google Gemini AI** to automatically analyze reports for misinformation using LLM-powered assessments
- Cross-reference claims against the **Google Fact Check Tools API** for authoritative verification
- Facilitate **real-time communications** between field agents and administrators via WebSockets
- Manage **role-based access control** across a multi-tier user hierarchy (User → Evaluator → Admin → Super Admin)
- Secure sensitive data using field-level **AES-256 encryption**

> **🌐 Live Production API**: `https://simccs.onrender.com`  
> **📚 Interactive API Docs**: `https://simccs.onrender.com/swagger-ui/index.html`

---

## 🏛️ Architecture Overview

SimCCS adheres to industry-standard clean architecture principles with a strict separation of concerns. Each domain is encapsulated within its own self-contained module.

```
┌─────────────────────────────────────────────────────────────────┐
│                        CLIENT LAYER                             │
│     React Frontend / Mobile App / Third-Party Integration       │
└───────────────────────────┬─────────────────────────────────────┘
                            │  HTTPS / WSS
┌───────────────────────────▼─────────────────────────────────────┐
│                     SECURITY LAYER                              │
│   JWT Authentication Filter → Spring Security → Method-Level    │
│   Role-based Access Control (RBAC) + MFA (TOTP)                │
└───────────────────────────┬─────────────────────────────────────┘
                            │
┌───────────────────────────▼─────────────────────────────────────┐
│                    CONTROLLER LAYER                             │
│   REST Controllers → Request Validation → DTO Mapping           │
└───────────────────────────┬─────────────────────────────────────┘
                            │
┌───────────────────────────▼─────────────────────────────────────┐
│                     SERVICE LAYER                               │
│   Business Logic │ AI Integration │ Async Jobs │ Notifications  │
└───────┬───────────────────┬────────────────────┬────────────────┘
        │                   │                    │
┌───────▼───────┐ ┌─────────▼──────┐ ┌──────────▼──────────────┐
│  Persistence  │ │  External APIs │ │  Async / Event-Driven   │
│  JPA/Flyway   │ │  Gemini AI     │ │  Spring @Async          │
│  MySQL/PgSQL  │ │  Google Facts  │ │  WebSocket (STOMP)      │
└───────────────┘ └────────────────┘ └─────────────────────────┘
```

**Design Decisions:**
- **Profile-Based Configuration**: `dev` profile uses local MySQL; `prod` profile uses cloud PostgreSQL — zero code changes required between environments.
- **Multi-Stage Docker Build**: Keeps the production image minimal by separating the Maven build stage from the JRE-only runtime stage.
- **Database Migrations with Flyway**: All schema changes are versioned, reproducible, and tracked — no ad-hoc `CREATE TABLE` statements.

---

## 🛠️ Tech Stack

| Category | Technology | Version |
|---|---|---|
| **Language** | Java | 17 (LTS) |
| **Framework** | Spring Boot | 3.3.4 |
| **Security** | Spring Security + JWT (JJWT) | 0.11.5 |
| **2FA / MFA** | TOTP via GoogleAuth + ZXing QR | 1.5.0 |
| **ORM** | Spring Data JPA + Hibernate | 6.5.3 |
| **DB Migrations** | Flyway | 10.x |
| **Database (Dev)** | MySQL | 8.0 |
| **Database (Prod)** | PostgreSQL | 18.x |
| **Object Storage** | MinIO (S3-compatible) | 8.5.5 |
| **Real-time** | Spring WebSocket + STOMP | — |
| **AI Integration** | Google Gemini 2.5 Flash API | — |
| **AI Fact-Check** | Google Fact Check Tools API | — |
| **Email** | Spring Mail (SMTP/Gmail) | — |
| **API Docs** | SpringDoc OpenAPI (Swagger UI) | 2.5.0 |
| **Observability** | Spring Boot Actuator | — |
| **Containerization** | Docker + Docker Compose | — |
| **Build Tool** | Maven (Wrapper included) | 3.9.x |
| **Code Generation** | Lombok | — |
| **Deployment** | Render.com (Cloud PaaS) | — |

---

## ✨ Features

### 🔐 Identity & Access Management
- Full **JWT-based stateless authentication** with access + refresh token rotation
- **Multi-Factor Authentication (MFA/TOTP)** via QR-code-based authenticator apps (Google Authenticator, Authy)
- **Role-Based Access Control (RBAC)** with 4 hierarchical roles: `USER`, `EVALUATOR`, `ADMIN`, `SUPER_ADMIN`
- Secure **password reset** flow via time-limited email tokens
- Method-level security via Spring's `@PreAuthorize`

### 📋 Crisis Report Management
- Full **CRUD lifecycle** for incident reports with structured status transitions
- **Report classification** by category, severity, and geographic zone
- Field-level **AES-256 encryption** for sensitive report content
- **File attachment** support (images, documents) via MinIO object storage
- **AI-powered report editor** assistant using Google Gemini

### 🤖 AI-Powered Misinformation Detection
- Automatic **LLM analysis** of submitted reports using Google Gemini 2.5 Flash
- Claim cross-referencing against the **Google Fact Check Tools API**
- Structured AI response with confidence scores and source citations

### 💬 Real-Time Communication
- **WebSocket (STOMP)** based chat system for secure team messaging
- **In-app notification** system with database persistence
- **Email notification** delivery for critical workflow events

### 📊 Analytics & Reporting
- Comprehensive admin **analytics dashboard** API endpoints
- Report counts aggregated by status, category, zone, and time period
- Personnel activity audit trails

### ⚙️ Operational Workflow Engine
- Structured challenge/evaluation workflow for report verification
- Dispute and cancellation management
- Schedule-based automated data synchronization via Spring `@Scheduler`

---

## 📦 Prerequisites

Before you begin, ensure you have the following installed on your machine:

| Tool | Minimum Version | Notes |
|---|---|---|
| **JDK** | 17 | [Download OpenJDK 17](https://adoptium.net/) |
| **Maven** | 3.8+ | Optional — a Maven Wrapper (`mvnw`) is included |
| **MySQL** | 8.0+ | For local development only |
| **Docker Desktop** | Latest | Required for Docker Compose setup |
| **Git** | Latest | — |

---

## 🚀 Local Development Setup

This is the recommended approach for active development. The application uses a `.env` file to manage secrets locally.

### Step 1: Clone the Repository

```bash
git clone https://github.com/your-username/simccs.git
cd simccs/simccs
```

### Step 2: Create Your Environment File

Copy the provided example file and fill in your credentials:

```bash
cp .env.example .env
```

Open `.env` and populate **every variable** (see the [Environment Variables Reference](#-environment-variables-reference) table below for details).

### Step 3: Create the Local Database

Open your MySQL client (MySQL Workbench, DBeaver, or CLI) and run:

```sql
CREATE DATABASE simccs_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

> **Note:** Flyway will automatically create all tables on the first application startup. You do **not** need to run any SQL scripts manually.

### Step 4: Run the Application

Using the Maven Wrapper (no Maven installation required):

```bash
# On Linux/macOS
./mvnw spring-boot:run

# On Windows
mvnw.cmd spring-boot:run
```

The application will start on **http://localhost:8080**.

You should see this in your console logs:
```
Started SimccsApplication in X.XXX seconds
```

### Step 5: Verify the Application is Running

```bash
curl http://localhost:8080/actuator/health
```
**Expected response:**
```json
{ "status": "UP" }
```

---

## 🐳 Running with Docker Compose

This is the most reproducible way to run the **full stack** (App + MySQL + MinIO) locally, eliminating the need to install MySQL or MinIO separately.

### Step 1: Set Environment Variables

Ensure your `.env` file is created and populated (see Step 2 in Local Development Setup above).

### Step 2: Build & Start All Services

```bash
docker-compose up --build
```

This single command will:
1. Build the Spring Boot application image using the multi-stage `Dockerfile`
2. Pull and start a **MySQL 8.0** container with automatic health-checking
3. Pull and start a **MinIO** object storage container
4. Wire all three services together on a private Docker network
5. Start the Spring Boot app only after MySQL is confirmed healthy

### Step 3: Access the Services

| Service | URL | Credentials |
|---|---|---|
| **Spring Boot API** | `http://localhost:8080` | — |
| **Swagger UI** | `http://localhost:8080/swagger-ui/index.html` | — |
| **MinIO Console** | `http://localhost:9001` | `minioadmin` / `minioadmin` |
| **Health Check** | `http://localhost:8080/actuator/health` | — |

### Step 4: Stop All Services

```bash
docker-compose down
```

> To also **delete all data volumes** (wipes database and files):
> ```bash
> docker-compose down -v
> ```

---

## 🔑 Environment Variables Reference

Copy `.env.example` to `.env` and fill in the following values. **Never commit your `.env` file to Git.**

| Variable | Required | Description | Example Value |
|---|---|---|---|
| `DB_URL` | ✅ | Full JDBC connection URL for MySQL | `jdbc:mysql://localhost:3306/simccs_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true` |
| `DB_USERNAME` | ✅ | Database user | `root` |
| `DB_PASSWORD` | ✅ | Database password | `your_strong_password` |
| `JWT_SECRET_KEY` | ✅ | Hex-encoded HS256 secret (min. 256-bit / 64 chars) | `404E635266556A58...` |
| `ENCRYPTION_SECRET_KEY` | ✅ | Base64-encoded AES-256 key (32 bytes) | `5lWTLwqQBgr6LKfc...` |
| `GEMINI_API_URL` | ✅ | Google Gemini API endpoint | `https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent` |
| `GEMINI_API_KEY` | ✅ | Google AI Studio API key | `AIzaSy...` |
| `FACTCHECK_API_KEY` | ✅ | Google Fact Check Tools API key | `AIzaSy...` |
| `MAIL_USERNAME` | ✅ | Gmail address for sending emails | `your-email@gmail.com` |
| `MAIL_PASSWORD` | ✅ | Gmail **App Password** (not your main password) | `abcd efgh ijkl mnop` |
| `MINIO_URL` | ✅ | MinIO server URL | `http://localhost:9000` |
| `MINIO_ACCESS_KEY` | ✅ | MinIO access key (username) | `minioadmin` |
| `MINIO_SECRET_KEY` | ✅ | MinIO secret key (password) | `minioadmin` |
| `MINIO_BUCKET_NAME` | ✅ | MinIO bucket for file uploads | `simccs-reports` |
| `SPRING_PROFILES_ACTIVE` | ➖ | Active Spring profile (`dev` or `prod`) | `dev` |

> **💡 How to Generate a Gmail App Password:**
> 1. Enable **2-Step Verification** on your Google account.
> 2. Go to **Google Account → Security → App Passwords**.
> 3. Generate a new App Password, select "Other" and name it `SimCCS`.
> 4. Use the generated 16-character code as your `MAIL_PASSWORD`.

> **💡 How to Generate a Secure JWT Secret:**
> ```bash
> openssl rand -hex 32
> ```

> **💡 How to Generate a Secure Encryption Key:**
> ```bash
> openssl rand -base64 32
> ```

---

## 📚 API Documentation

The API is fully documented with **Swagger UI** via SpringDoc OpenAPI 3.

| Environment | URL |
|---|---|
| **Production (Live)** | [https://simccs.onrender.com/swagger-ui/index.html](https://simccs.onrender.com/swagger-ui/index.html) |
| **Local Development** | [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html) |

### Key API Modules

| Prefix | Module | Description |
|---|---|---|
| `/api/auth/**` | Identity | Register, Login, Token Refresh (Public) |
| `/api/auth/mfa/**` | MFA | TOTP Setup, QR Code, Verification |
| `/api/auth/password/**` | Password Reset | Request & Confirm Password Reset via Email |
| `/api/admin/**` | Administration | User management (Admin role required) |
| `/api/reports/**` | Crisis Reports | Full CRUD for incident reports |
| `/api/workflow/**` | Workflow Engine | Report evaluation, challenge, dispute management |
| `/api/analytics/**` | Analytics | Dashboard statistics and audit trail data |
| `/api/chat/**` | Communication | Message threads and inbox management |
| `/api/notifications/**` | Notifications | In-app notification retrieval |
| `/api/ai/**` | AI Editor | AI-assisted report content generation |
| `/ws/**` | WebSocket | Real-time STOMP WebSocket endpoint |
| `/actuator/**` | Observability | Health checks and application info (Prod) |

### Authentication

All protected endpoints require a **Bearer Token** in the `Authorization` header:

```http
Authorization: Bearer <your_jwt_access_token>
```

**To obtain a token:**

```bash
curl -X POST https://simccs.onrender.com/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "your@email.com", "password": "yourpassword"}'
```

---

## 📁 Project Structure

```
simccs/
├── src/
│   └── main/
│       ├── java/com/acp/simccs/
│       │   ├── SimccsApplication.java          # Entry point
│       │   ├── config/                         # Spring configuration beans
│       │   │   ├── SecurityConfig.java         # JWT + CORS + RBAC security chain
│       │   │   ├── AsyncConfig.java            # Thread pool for @Async tasks
│       │   │   ├── MinioConfig.java            # Object storage client bean
│       │   │   ├── SwaggerConfig.java          # OpenAPI documentation config
│       │   │   ├── DataInitializer.java        # Seed initial admin user on startup
│       │   │   └── SchedulerConfig.java        # Task scheduler configuration
│       │   ├── security/                       # JWT & authentication filter chain
│       │   │   ├── JwtService.java             # Token generation & validation
│       │   │   ├── JwtAuthenticationFilter.java# Request filter per-request
│       │   │   └── SecurityService.java        # Context-aware security helpers
│       │   ├── common/                         # Shared cross-cutting concerns
│       │   │   ├── service/                    # EmailService, NotificationService
│       │   │   ├── exception/                  # GlobalExceptionHandler
│       │   │   └── util/                       # Email templates, encryption util
│       │   └── modules/                        # Bounded contexts (DDD-inspired)
│       │       ├── identity/                   # Auth, Users, MFA, Password Reset
│       │       ├── crisis/                     # Reports, Media, AI Editor, Analytics
│       │       ├── workflow/                   # Evaluation, Challenges, Disputes
│       │       ├── misinformation/             # Gemini AI + Fact-Check integration
│       │       ├── communication/              # Chat, WebSocket, Notifications
│       │       └── system/                     # System-level events and listeners
│       └── resources/
│           ├── application.properties          # Base (profile-agnostic) config
│           ├── application-dev.properties      # Local MySQL development config
│           ├── application-prod.properties     # Cloud PostgreSQL production config
│           ├── logback-spring.xml              # Structured console logging config
│           └── db/migration/
│               └── V1__Baseline.sql            # Flyway: initial schema migration
├── Dockerfile                                  # Multi-stage production Docker build
├── docker-compose.yml                          # Full local stack orchestration
├── .env.example                                # Environment variable template
└── pom.xml                                     # Maven dependencies & build config
```

---

## ☁️ Production Deployment (Render)

The application is deployed on **Render.com** using a **Dockerfile-based Web Service**. The build-and-deploy pipeline is fully automated — every `git push` to the main branch triggers a new deployment.

### Infrastructure Summary

| Component | Service | Provider |
|---|---|---|
| **Web Service** | Spring Boot (Dockerized) | Render Web Service |
| **Database** | PostgreSQL 18 | Render Managed PostgreSQL |
| **Object Storage** | MinIO | *(Configured via env — upgrade to S3 for true production scale)* |
| **Email** | SMTP Relay | Gmail SMTP + App Password |

### Required Render Environment Variables

In your Render Web Service dashboard, set all the variables from the [Environment Variables Reference](#-environment-variables-reference) table above, **plus** these production-specific ones:

| Variable | Production Value |
|---|---|
| `SPRING_PROFILES_ACTIVE` | `prod` |
| `DB_URL` | `jdbc:postgresql://<your-render-db-internal-host>/simccs_db` |
| `DB_USERNAME` | Your Render DB user |
| `DB_PASSWORD` | Your Render DB password |
| `PROD_CORS_URL` | Your deployed frontend URL (e.g., `https://your-app.vercel.app`) |
| `PORT` | `8080` |

### Deployment Architecture

```
GitHub Push → Render Builder detects Dockerfile
    → Stage 1: Maven compiles 107 source files into .jar
    → Stage 2: Copies .jar into a minimal JRE-only Alpine image
    → Render pushes image to internal registry
    → Render deploys container with injected environment variables
    → Spring Boot starts → Flyway validates migrations → App is LIVE 🎉
```

### Health Check

Render automatically monitors the application health via:
```
GET https://simccs.onrender.com/actuator/health
```

---

## 🔒 Security Architecture

Security is treated as a first-class citizen, not an afterthought.

### Authentication Flow

```
Client → POST /api/auth/login
    → AuthController validates credentials via DaoAuthenticationProvider
    → BCrypt password verification
    → (If MFA enabled) → TOTP code verification required
    → JwtService generates Access Token (24h) + Refresh Token (7d)
    → Tokens returned to client
    
Client → Any Protected Request
    → JwtAuthenticationFilter intercepts request
    → Validates JWT signature + expiry
    → Loads UserDetails from database
    → Sets SecurityContext
    → Request proceeds to Controller
```

### Security Hardening Measures

| Measure | Implementation |
|---|---|
| **Password Hashing** | BCrypt (strength 10) |
| **Stateless Sessions** | JWT — no server-side session state |
| **CSRF Protection** | Disabled (stateless API — not applicable) |
| **CORS** | Strict allowlist of trusted origins via `app.cors.allowed-origins` |
| **MFA / 2FA** | TOTP (RFC 6238) with QR code provisioning via ZXing |
| **Field Encryption** | AES-256 encryption on sensitive report/message content |
| **Non-Root Container** | Docker runs as dedicated `spring` user (not root) |
| **Error Sanitization** | `GlobalExceptionHandler` returns generic messages; full stack traces are logged server-side only |
| **Input Validation** | Spring's `@Valid` + JSR-303 bean validation on all request bodies |
| **Secret Management** | All secrets injected via environment variables — zero hardcoded credentials |

---

## 🗄️ Database Strategy

### Environment Isolation

| Environment | Database | Driver | Profile |
|---|---|---|---|
| Local Development | MySQL 8.0 | `mysql-connector-j` | `dev` |
| Production (Cloud) | PostgreSQL 18 | `postgresql` | `prod` |

Spring Boot automatically selects the correct JDBC driver based on the `DB_URL` prefix — no code changes needed to switch databases.

### Schema Management with Flyway

All database schema changes are managed by **Flyway**, ensuring version-controlled, repeatable, and auditable migrations.

| File | Description |
|---|---|
| `V1__Baseline.sql` | Full initial schema — all tables, indexes, and constraints |
| `V2__*.sql` | *(Future migrations follow this naming convention)* |

**On startup, Flyway will:**
1. Check the `flyway_schema_history` table in the database
2. Compare it to migration files in `src/main/resources/db/migration/`
3. Execute any pending migrations in order
4. If the database is already up-to-date, it skips all migrations

> **Production Note**: `spring.jpa.hibernate.ddl-auto=none` is explicitly set in the `prod` profile. Hibernate **never** modifies the production schema — Flyway has sole authority over schema changes.

---

## 🚩 Known Limitations & Roadmap

| Item | Status | Notes |
|---|---|---|
| MinIO File Storage | ⚠️ Local Only | In production, MinIO is not accessible. Upgrade to AWS S3 for full file upload support in cloud. |
| Flyway PostgreSQL Compatibility | ✅ Resolved | Added `flyway-database-postgresql` dependency for PG 18 support. |
| Boot Time (Free Tier) | ⚠️ ~3 minutes | Render's free tier has limited CPU. Cold starts take ~3 minutes. |

---

## 🤝 Contributing

Contributions, issues, and feature requests are welcome.

1. Fork the project
2. Create your feature branch: `git checkout -b feature/AmazingFeature`
3. Run the test suite: `./mvnw test`
4. Commit your changes: `git commit -m 'feat: add AmazingFeature'`
5. Push to the branch: `git push origin feature/AmazingFeature`
6. Open a Pull Request

---

## 📜 License

Distributed under the MIT License. See `LICENSE` for more information.

---

<div align="center">

**Built with precision, deployed with confidence.**

*SimCCS Backend — Production-grade from day one.*

</div>
