# 🏋️ AI Personal Trainer App

A **full-stack, AI-powered fitness application** that acts as your personalized trainer and nutritionist. It calculates your health stats, generates custom 7-day workout and diet plans, tracks your weekly progress with interactive charts, and features a conversational AI coach with history persistence.

---

## ✨ Features

- **AI Daily Food Log**: Type your meals in plain English (e.g., "3 eggs and avocado toast"), and the AI automatically calculates calories and macros for you.
- **ChatGPT-Style Chat Sessions**: Organize conversations into separate threads with persistent history. AI automatically generates short, descriptive titles for your chats.
- **Login Streak Tracker**: Gamified dashboard widget that tracks consecutive days logged in to encourage consistency.
- **Workout Rest Timer**: A global, floating countdown timer with audio/visual notifications to help you time your rest periods between sets.
- **Macro Calculator**: Standalone scientific calculator (Mifflin-St Jeor) that auto-syncs with your latest profile and progress data.
- **Secure Authentication**: Flexible login using either **Username or Email** with JWT-based stateless authentication.
- **Progress Tracking**: Log weekly weight and macro intake. Visualizes trends using **Recharts** with interactive tooltips.
- **Dynamic Health Profiling**: Instant calculation of BMI, Body Fat %, and Maintenance Calories.
- **Responsive Design**: Premium dark/light mode interface with glassmorphism aesthetics, built for both desktop and mobile.

---

## 🛠️ Tech Stack

### Backend
- **Framework**: Spring Boot 3.2 (Java 21)
- **Database**: PostgreSQL 16 (hosted on Railway)
- **ORM**: Spring Data JPA / Hibernate
- **Security**: Spring Security + JWT (Stateless)
- **AI Integration**: Google Gemini API / Groq (Llama 3.1)

### Frontend
- **Framework**: Next.js 14 (App Router)
- **State Management**: React Hooks (useState, useEffect, useRef)
- **Styling**: Vanilla CSS with modern variables (Glassmorphism & Adaptive Themes)
- **Charting**: Recharts
- **Markdown**: React Markdown (for formatted AI responses)
- **Notifications**: React Hot Toast

---

## 🚀 Quick Start (Local Development)

### Prerequisites
- JDK 21+
- Node.js 18+
- PostgreSQL running locally
- A free API key from [Google AI Studio](https://aistudio.google.com/)

### 1. Setup the Backend
```bash
cd ai-trainer
# Update application.properties with your credentials
mvn spring-boot:run
```

### 2. Setup the Frontend
```bash
cd ai-trainer-frontend
npm install
npm run dev
```

---

## 🏗️ Project Architecture

```
AI-Trainer/
├── ai-trainer/                     ← Spring Boot Backend
│   ├── controller/                 ← REST API endpoints (Chat, Auth, Profile, etc)
│   ├── model/                      ← JPA Entities (User, ChatSession, ChatMessage)
│   ├── repository/                 ← JPA Repositories
│   └── service/                    ← Business Logic (Gemini API, Stats Calculation)
│
├── ai-trainer-frontend/            ← Next.js Frontend
│   ├── app/                        ← Next.js Pages (App Router)
│   ├── components/                 ← Global Components (WorkoutTimer, Navbar)
│   └── lib/                        ← API Fetching Utilities
```

---

## 🌍 Deployment

- **Backend**: Deployed on **Railway** with CI/CD from GitHub.
- **Frontend**: Deployed on **Vercel** with automatic branch previews.

---

*Built with passion for fitness and engineering.*
