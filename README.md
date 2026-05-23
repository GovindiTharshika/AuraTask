# 🌌 AuraTask
> **Aligning Your Cognitive Flow** — A premium, high-fidelity productivity dashboard and task manager styled with a futuristic cyberpunk aesthetic. Powered by Gemini API insights, offline-first Room database resilience, and Jetpack Compose.

---

## 🎨 Visual Preview & Identity
AuraTask is built with eye-safe dark themes, vibrant cyan & purple interactive components, and generous whitespace, creating a highly immersive and tactical work experience.

* **Cosmic Slate Theme:** A cohesive, custom dark theme featuring deep grey canvases bordered by neon accents.
* **Modern Illustrative Layouts:** Powered by high-contrast illustrations like **Aura Workspace** and **Active Capsule** to give life to empty states.
* **Flow Analytics:** Interactive circular completion indicators and custom canvas metrics that visualize task statistics dynamically.

---

## 🚀 Key Feature Set

### 1. Immersive Splash Entrance
* **Fluid Spring Transitions:** Smooth scale-in animations for the logo and layout groups.
* **Organic Glow Backdrop:** Seamless full-screen cyberpunk visual backdrop under interactive overlays.
* **Cognitive Loading Indicator:** Modern progress tracker keeping workflow loading clear and engaging.

### 2. High-Tech Workspace Dashboard
* **Dynamic Analytics Canvas:** Custom progress rings and horizontal metrics depicting task states.
* **Interactive Promotion Banner:** Quick-action shortcut workspace banner promoting scheduled flow states.
* **Daily Streaks Counter:** Motivating streak trackers showing operational frequency.

### 3. Smart Tasks & Filters
* **Adaptive Filtering:** Instantly sort tasks by priorities (High, Medium, Low) and standard categories.
* **Elegant Empty State:** A custom illustration of a stylized sleeping capsule robot that greets users when all tasks are complete, encouraging mindful downtime.
* **Seamless Action Management:** Modern Floating Action Buttons (FAB), custom checkboxes, and swipe-to-delete flows.

### 4. AI Productivity Coach
* **Cognitive Flow Analysis:** Integrates Google’s Gemini API to analyze current tasks and active schedules.
* **Coaching & Advice:** Interactive suggestions and conversational UI assisting in cognitive recovery, prioritization, and block clearing.
* **Offline-First Resilience:** Safe caching ensuring users can browse their work states without losing data or app performance.

### 5. Secure Credentials Portal
* **Futuristic Login Portal:** A polished login splash with premium sliding animations ensuring accessibility.
* **Seamless Access Control:** Intuitive simulated validation and sign-off profiles.

---

## 🛠️ High-Performance Architecture

The codebase leverages modern Android development best practices:

* **Jetpack Compose:** Declarative Material 3 layouts, edge-to-edge rendering, and interactive component states.
* **State Management:** Strict MVVM architecture leveraging `ViewModel`, `StateFlow` reactive streams, and lifecycle-aware composable collectors.
* **Local Caching:** Production-ready relational data layer via **Room Database** with Kotlin Symbol Processing (KSP) and custom DAO flows.
* **Cognitive AI Service:** Retrofit/Ktor networking endpoints supporting structured prompt injection and safety filtering with Google Gemini models.
* **Adaptive Loading:** Coil implementation for asynchronous and local image rendering (`AsyncImage`), minimizing visual stuttering.

---

## 📂 Codebase Repository Structure

```text
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/
│   │   │   │   ├── data/
│   │   │   │   │   ├── local/          # Room Entity definitions, AppDatabase, and DAO
│   │   │   │   │   ├── model/          # Custom task model definitions
│   │   │   │   │   └── network/        # Gemini Core API services and Retrofit interfaces
│   │   │   │   ├── ui/
│   │   │   │   │   ├── screens/        # Features (Dashboard, Tasks, Coach, Auth, Splash)
│   │   │   │   │   └── theme/          # Custom neon Cyberpunk dynamic color palettes
│   │   │   │   └── MainActivity.kt     # App lifecycle manager & navigation workspace selector
│   │   │   └── res/
│   │   │       ├── drawable/           # Modern custom icons, vectors, and background imagery
│   │   │       └── values/strings.xml  # Decentralized application name and text strings
│   │   └── test/                       # Roborazzi screenshot and Robolectric JVM test cases
│   └── build.gradle.kts                # Application build configurations & module dependencies
├── metadata.json                       # AI Studio Platform sync identity configuration
└── README.md                           # Modern architectural overview of AuraTask
```

---

## 🔧 Installation & Secrets Configuration

### 1. Standard Gradle Initialization
Clone the repository and import the root gradle file into your Android Studio workspace. Gradle will automatically parse correct dependencies using the central version catalog configurations.

### 2. Google Gemini API Injection
To activate smart cognitive tips and AI coaching inside the application, configure your official Gemini API Key through the **Secrets Panel in your AI Studio editor**:

No local property files or hardcoded key strings are configured within the source tree to preserve safety standards. Access is mapped cleanly at compile-time:
```kotlin
val apiKey = BuildConfig.GEMINI_API_KEY
```

---

## 🧪 Verification & Development Commands

AuraTask contains automated local unit and visual screenshot tests. Execute these tasks inside your environment to verify build status and styling correctness:

* **Verify Layout Compilation:**
  ```bash
  gradle compileDebugSources
  ```
* **Run Local Unit Tests (Robolectric):**
  ```bash
  gradle :app:testDebugUnitTest
  ```
* **Verify Layout Visual Integrity (Roborazzi Screenshots):**
  ```bash
  gradle :app:verifyRoborazziDebug
  ```
