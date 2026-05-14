# 高校竞赛队友匹配平台 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a complete Vue 3 + Spring Boot 3.0 full-stack teammate matching platform for university competitions.

**Architecture:** Two independent projects — `backend/` (Spring Boot 3.0 + Maven + MyBatis Plus + Redis + Spring Security + JWT) and `frontend/` (Vue 3 + Vite + Element Plus + Vue Router 4 + Axios). Backend exposes RESTful APIs consumed by frontend. Redis caches tag lists and recommendation results.

**Tech Stack:** Java 17, Spring Boot 3.0, MyBatis Plus 3.5, Redis, MySQL 8.0, Spring Security + JWT, Vue 3 (Composition API), Element Plus, Vite, Axios

---

## Workstream A: Backend Foundation

### Task A1: Maven Project Setup
**Files:**
- Create: `backend/pom.xml`
- Create: `backend/src/main/java/com/matchteam/MatchApplication.java`
- Create: `backend/src/main/resources/application.yml`

### Task A2: Database Schema & Initial Data
**Files:**
- Create: `backend/src/main/resources/db/schema.sql` (all DDL + indexes + seed data)

### Task A3: Entity Classes
**Files:**
- Create: `backend/src/main/java/com/matchteam/entity/User.java`
- Create: `backend/src/main/java/com/matchteam/entity/Tag.java`
- Create: `backend/src/main/java/com/matchteam/entity/UserTag.java`
- Create: `backend/src/main/java/com/matchteam/entity/CompetitionCategory.java`
- Create: `backend/src/main/java/com/matchteam/entity/Recruitment.java`
- Create: `backend/src/main/java/com/matchteam/entity/RecruitmentRequiredTag.java`
- Create: `backend/src/main/java/com/matchteam/entity/Application.java`

### Task A4: Common Package (Result, JWT, Exception)
**Files:**
- Create: `backend/src/main/java/com/matchteam/common/Result.java`
- Create: `backend/src/main/java/com/matchteam/common/JwtUtils.java`
- Create: `backend/src/main/java/com/matchteam/common/GlobalExceptionHandler.java`
- Create: `backend/src/main/java/com/matchteam/common/BusinessException.java`

### Task A5: Configuration Classes
**Files:**
- Create: `backend/src/main/java/com/matchteam/config/SecurityConfig.java`
- Create: `backend/src/main/java/com/matchteam/config/RedisConfig.java`
- Create: `backend/src/main/java/com/matchteam/config/MyBatisPlusConfig.java`
- Create: `backend/src/main/java/com/matchteam/config/CorsConfig.java`
- Create: `backend/src/main/java/com/matchteam/config/JwtAuthenticationFilter.java`

### Task A6: DTO Classes
**Files:**
- Create: All DTOs under `backend/src/main/java/com/matchteam/dto/`

### Task A7: Mapper Interfaces
**Files:**
- Create: All mapper interfaces under `backend/src/main/java/com/matchteam/mapper/`

### Task A8: Service Layer (including MatchingAlgorithm)
**Files:**
- Create: All service classes under `backend/src/main/java/com/matchteam/service/`
- Includes: MatchingService (core recommendation algorithm with Redis caching)

### Task A9: Controller Layer
**Files:**
- Create: All controllers under `backend/src/main/java/com/matchteam/controller/`

---

## Workstream B: Frontend

### Task B1: Vite + Vue 3 Project Setup
**Files:**
- Create: `frontend/package.json`
- Create: `frontend/vite.config.js`
- Create: `frontend/index.html`
- Create: `frontend/src/main.js`
- Create: `frontend/src/App.vue`

### Task B2: Axios Configuration & API Modules
**Files:**
- Create: `frontend/src/api/request.js` (axios instance with interceptors)
- Create: `frontend/src/api/auth.js`
- Create: `frontend/src/api/user.js`
- Create: `frontend/src/api/tag.js`
- Create: `frontend/src/api/recruitment.js`
- Create: `frontend/src/api/admin.js`

### Task B3: Vue Router Setup
**Files:**
- Create: `frontend/src/router/index.js`

### Task B4: View Pages (Login, Register, Square, Detail, Profile, Admin)
**Files:**
- Create: `frontend/src/views/LoginView.vue`
- Create: `frontend/src/views/RegisterView.vue`
- Create: `frontend/src/views/SquareView.vue` (recruitment listing + search/filter)
- Create: `frontend/src/views/DetailView.vue` (recruitment detail + recommendations + apply)
- Create: `frontend/src/views/ProfileView.vue` (personal center with tabs)
- Create: `frontend/src/views/AdminView.vue`
- Create: `frontend/src/components/NavBar.vue`

---

## Workstream C: Documentation & Deliverables

### Task C1: Project Documentation
- Startup guide (backend + frontend steps)
- Test accounts
- Operation demo steps
- Algorithm description
- Tech stack description
- UML materials (use cases, class diagram, package diagram, deployment diagram)
