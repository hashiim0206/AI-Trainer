# 🏋️ AI Personal Trainer App

A **full-stack, AI-powered fitness application** that acts as your personalized trainer and nutritionist. It calculates your health stats, generates custom 7-day workout and diet plans, tracks your weekly progress with interactive charts, and features a conversational AI coach that remembers your context.

---

## ✨ Features

- **Secure Authentication**: User registration and login using JWT tokens and BCrypt password hashing.
- **Dynamic Health Profiling**: Calculates your BMI, estimated Body Fat %, and Daily Maintenance Calories (TDEE).
- **AI Plan Generation**: Uses LLMs (Google Gemini / Groq) to instantly generate personalized diet and workout plans based on your exact body stats and goals.
- **Progress Tracking**: Log your weekly weight, macros (protein, carbs, fat, calories), and workout completion. Visualizes your weight trend using Recharts.
- **Interactive AI Coach**: A real-time chat interface where the AI knows your profile, your latest goal, and your conversation history to give you highly personalized fitness and nutrition advice.
- **Responsive UI**: Beautiful, glassmorphism-inspired dark mode interface built with Next.js and custom CSS.

---

## 🛠️ Tech Stack

### Backend
- **Framework**: Spring Boot 3.2 (Java 21)
- **Database**: PostgreSQL 16
- **ORM**: Hibernate / Spring Data JPA
- **Security**: Spring Security + JWT
- **AI Integration**: Groq API / Google Gemini API

### Frontend
- **Framework**: Next.js 14 (App Router)
- **UI Library**: React 19
- **Charting**: Recharts
- **Markdown Rendering**: React Markdown
- **Notifications**: React Hot Toast

---

## 🚀 Quick Start (Local Development)

### Prerequisites
- JDK 21+
- Node.js 18+
- PostgreSQL running locally (or via Docker)
- A free API key from [Groq](https://console.groq.com/) or [Google Gemini](https://aistudio.google.com/)

### 1. Setup the Backend
```bash
# Navigate to the backend directory
cd ai-trainer

# Update application.properties with your API Key
# (Set groq.api.key or GROQ_API_KEY environment variable)

# Run the Spring Boot application
mvn spring-boot:run
```
*The backend will start on `http://localhost:8082`*

### 2. Setup the Frontend
```bash
# Navigate to the frontend directory
cd ai-trainer-frontend

# Install dependencies
npm install

# Run the Next.js development server
npm run dev
```
*The frontend will start on `http://localhost:3000`*

---

## 🏗️ Project Architecture

```
AI-Trainer/
├── ai-trainer/                     ← Spring Boot Backend
│   ├── src/main/java/.../aitrainer/
│   │   ├── config/                 ← Security & CORS configurations
│   │   ├── controller/             ← REST API endpoints
│   │   ├── model/                  ← JPA Entities (User, Profile, Plan, Progress)
│   │   ├── repository/             ← Database Access
│   │   ├── security/               ← JWT Filtering and Auth
│   │   └── service/                ← Business Logic & AI Integration
│   └── src/main/resources/         ← application.properties
│
├── ai-trainer-frontend/            ← Next.js Frontend
│   ├── app/                        ← Next.js App Router pages
│   │   ├── auth/                   ← Login & Register pages
│   │   ├── chat/                   ← AI Chatbot interface
│   │   ├── dashboard/              ← User Dashboard
│   │   ├── plan/                   ← Generated Plans & PDF Export
│   │   ├── profile/                ← Health Profiling
│   │   └── progress/               ← Check-ins & Charts
│   ├── components/                 ← Reusable UI (Navbar, etc)
│   ├── lib/                        ← API utilities & JWT handling
│   └── public/                     ← Static assets
```

---

## 🌍 Deployment

This project is configured for easy deployment:
1. **Backend**: Deploy the `ai-trainer` directory to **Railway** (alongside a Railway PostgreSQL database). Configure the `SPRING_DATASOURCE_URL`, `JWT_SECRET`, and `GROQ_API_KEY` environment variables.
2. **Frontend**: Deploy the `ai-trainer-frontend` directory to **Vercel**. Set the `NEXT_PUBLIC_API_URL` to point to your live Railway backend URL.

---

*Built as part of a comprehensive full-stack learning journey.*
