<div align="center">

# 📚 EduGeeks Quiz App
### Android Developer Intern Assignment Submission

![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-Material3-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-Auth_+_Firestore-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)
![Room](https://img.shields.io/badge/Room-Offline_Support-003B57?style=for-the-badge&logo=sqlite&logoColor=white)

**Submitted by:** Sanskar  
**Role:** Android Developer Intern  
**Company:** EduGeeks

</div>

---

## 📱 Demo

| Screen | Screen | Screen |
|--------|--------|--------|
| 🔐 Login / Sign Up | 🏠 Category Home | ❓ Quiz Screen |
| ⏱️ Timer + MCQ | ✅ Answer Reveal | 🏆 Result Screen |

> 📺 **[Watch Demo Video](https://www.loom.com/share/3c5667f308a0492abadca214494f7379)**  
> 📦 **[Download APK](https://drive.google.com/file/d/1XS7FPSWR6CefLf2W_XgwxhYXgEqpIceH/view?usp=sharing)**

---

## ✅ Features Implemented

| Feature | Status | Detail |
|---|---|---|
| Firebase Auth | ✅ | Login, Sign Up |
| MCQ Quiz | ✅ | 50 handcrafted questions across 5 categories |
| Per-question Timer | ✅ | Countdown with auto-skip on expiry |
| Next / Previous / Skip | ✅ | Full bidirectional question navigation |
| Score Calculation | ✅ | Correct / Wrong / Skipped tracked in real time |
| Result Screen | ✅ | Animated score %, grade (A+ to F), all stats |
| Offline Support | ✅ | Room DB caching |
| Animations | ✅ | Spring, tween, slide, scale, AnimatedContent |
| Clean Architecture | ✅ | Separated data / domain / presentation layers |
| NavHost Navigation | ✅ | Typed routes with slide transitions |

---

## 🏗️ Architecture

### Pattern: MVVM + Clean Architecture

```
┌─────────────────────────────────────────────────┐
│              Presentation Layer                 │
│   Compose UI  ←→  ViewModel  ←→  StateFlow      │
└────────────────────┬────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────┐
│               Domain Layer                      │
│        Models: Question, QuizResult,            │
│                QuizSession, User                │
└────────────────────┬────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────┐
│                Data Layer                       │
│  ┌─────────────┐    ┌────────────┐              │
│  │  Room DB    │    │MockQuestion│              │
│  │ (offline)   │    │  Source    │              │
│  └─────────────┘    └────────────┘              │
└─────────────────────────────────────────────────┘
```

### Navigation Flow (Jetpack NavHost)

```
AUTH ──► HOME ──► QUIZ ──► RESULT
          ▲                  │
          │    (retake)      │
          └──────────────────┘
```

Each screen is a `composable(route)` in `NavGraph.kt` with typed string routes and animated slide transitions.

### State Management

- `StateFlow` + `collectAsState()` — single source of truth per ViewModel
- No shared mutable state between screens
- `QuizViewModel` owns the entire quiz session lifecycle including the timer coroutine

### Dependency Graph (No DI Framework)

```kotlin
// QuizApplication.kt — manual singletons via by lazy
val authRepository: AuthRepository by lazy { AuthRepository(firebaseAuth) }
val quizRepository: QuizRepository by lazy { QuizRepository(...) }
```

`by lazy` ensures thread-safe single instantiation without Hilt or Koin.

---

## 📁 Project Structure

```
app/src/main/java/com/example/edugeeksquiz/
├── data/
│   ├── local/
│   │   ├── QuizDatabase.kt          # Room database singleton
│   │   ├── dao/Daos.kt              # QuizResultDao, CachedQuestionDao
│   │   └── entities/Entities.kt     # Room table entities
│   ├── remote/
│   │   └── MockQuestionSource.kt    # 50 MCQs across 5 topics
│   └── repository/
│       └── Repositories.kt          # AuthRepository, QuizRepository
├── domain/
│   └── model/Models.kt              # Question, QuizSession, QuizResult, User
├── presentation/
│   ├── NavGraph.kt                  # NavHost + all routes + transitions
│   ├── auth/
│   │   ├── AuthViewModel.kt         # Login/Signup state management
│   │   └── AuthScreen.kt            # Login + Signup + validation UI
│   ├── quiz/
│   │   ├── QuizViewModel.kt         # Timer, answers, navigation logic
│   │   ├── CategorySelectScreen.kt  # Home / topic picker
│   │   └── QuizScreen.kt            # MCQ + timer ring + prev/next
│   ├── result/
│   │   └── ResultScreen.kt          # Animated score, grade, stats
│   └── common/
│       ├── Theme.kt                 # Material3 dark/light color schemes
│       └── Components.kt            # GradientButton, TimerRing, ScoreCircle
├── MainActivity.kt                  # Entry point — delegates to NavGraph
└── QuizApplication.kt               # Manual DI / singleton container
```

---

## 📚 Quiz Categories

| Category | Questions | Topics Covered |
|---|---|---|
| 📱 Android Dev | 10 | Jetpack Compose, Room, ViewModel, Navigation |
| 🎯 Kotlin | 10 | Coroutines, Sealed classes, Flow, Scope functions |
| 🧩 Data Structures | 10 | Arrays, Trees, Graphs, Sorting, Complexity |
| 🏗️ System Design | 10 | REST, CAP theorem, Caching, Scaling, DBs |
| 🤖 AI / ML | 10 | Neural networks, Transformers, Overfitting, GPT |

---

## 🚀 Setup & Run

### Prerequisites
- Android Studio Hedgehog or newer
- JDK 17
- Android device or emulator (API 26+)

### Step 1 — Firebase Setup
1. Go to [Firebase Console](https://console.firebase.google.com)
2. Create a new project → Add Android app
3. Package name: `com.example.edugeeksquiz`
4. Download `google-services.json` → place it in the `app/` folder
5. Enable **Email/Password** sign-in under Authentication
6. Create a **Firestore** database (start in test mode)

### Step 2 — Build & Run
```bash
# Clone the repo
git clone https://github.com/SanskarP819/Edugeeks_QuizApp.git

# Open in Android Studio → let Gradle sync

# Run on device/emulator
./gradlew assembleDebug
```

APK output: `app/build/outputs/apk/debug/app-debug.apk`

---

## 🔑 Key Design Decisions


**Offline First**
Questions are fetched and cached in Room on first load. Firestore's built-in offline persistence ensures results sync automatically when connectivity is restored.

**NavHost over State-Driven Navigation**
Each screen is a proper `composable(route)` — back stack is managed by NavController, transitions are slide animations, and each screen is independently navigable and testable.



---



---

<div align="center">

Made with ❤️ by **Sanskar** for EduGeeks Android Developer Intern Assignment

</div>
