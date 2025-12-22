# Goal 2026 📱

A comprehensive Android application built with Jetpack Compose to help you track and achieve your goals for 2026. Manage your goals, tasks, notes, and calendar events all in one beautiful, modern interface.

## 📋 Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Dependencies](#dependencies)
- [Prerequisites](#prerequisites)
- [Build Instructions](#build-instructions)
  - [Debug Build](#debug-build)
  - [Release Build](#release-build)
- [Installation](#installation)
- [Usage](#usage)
- [Project Details](#project-details)

## ✨ Features

### 🎯 Goals Management
- **11 Pre-defined Goals** for 2026 covering:
  - Health & Fitness
  - Career (1 Crore Package)
  - Learning (Management & Human Behavior)
  - Communication & Confidence
  - Lifestyle (Early Rising)
  - Discipline & Consistency
  - Money Management & Investing
  - Long-term Life Planning
  - Startup Focus
  - Digital Detox
- Track progress with milestones
- Visual progress indicators
- Goal categories with custom colors and icons

### ✅ Tasks Management
- Create, update, and delete tasks
- Priority levels (Low, Medium, High, Urgent)
- Due dates and reminders
- Link tasks to goals
- Task completion tracking
- Recurring tasks (Daily, Weekly, Monthly, Yearly)

### 📝 Notes
- Create and manage notes
- Pin important notes
- Color-coded notes
- Link notes to goals
- Tag support

### 📅 Calendar
- View events by date
- Create calendar events
- Link events to goals and tasks
- All-day and timed events

### 📊 Dashboard
- Overview of all goals
- Progress statistics
- Today's tasks
- Upcoming tasks
- Streak tracking
- Overall progress metrics

### ⚙️ Settings
- Dark mode support
- Notifications settings
- Daily reminder configuration
- Weekly review day selection
- Data backup and restore
- Export/Import functionality

## 🛠 Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM (Model-View-ViewModel)
- **State Management**: StateFlow, MutableStateFlow
- **Navigation**: Navigation Compose
- **Data Persistence**: SharedPreferences with Gson
- **Dependency Injection**: Manual (ViewModel Factory)
- **Build System**: Gradle with Kotlin DSL
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 36
- **Compile SDK**: 36

## 📁 Project Structure

```
Goal2026/
├── app/
│   ├── build.gradle.kts          # App-level build configuration
│   ├── proguard-rules.pro         # ProGuard rules for release builds
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/lssgoo/goal2026/
│       │   │   ├── MainActivity.kt              # Main activity entry point
│       │   │   ├── data/
│       │   │   │   ├── local/
│       │   │   │   │   └── LocalStorageManager.kt  # Data persistence layer
│       │   │   │   └── model/
│       │   │   │       ├── AppData.kt           # App data models
│       │   │   │       ├── Goal.kt              # Goal data model
│       │   │   │       ├── Note.kt              # Note data model
│       │   │   │       └── Task.kt              # Task data model
│       │   │   ├── ui/
│       │   │   │   ├── components/
│       │   │   │   │   └── Components.kt        # Reusable UI components
│       │   │   │   ├── navigation/
│       │   │   │   │   └── Navigation.kt        # Navigation setup
│       │   │   │   ├── screens/
│       │   │   │   │   ├── calendar/
│       │   │   │   │   │   └── CalendarScreen.kt
│       │   │   │   │   ├── dashboard/
│       │   │   │   │   │   └── DashboardScreen.kt
│       │   │   │   │   ├── goals/
│       │   │   │   │   │   ├── GoalsScreen.kt
│       │   │   │   │   │   └── GoalDetailScreen.kt
│       │   │   │   │   ├── notes/
│       │   │   │   │   │   └── NotesScreen.kt
│       │   │   │   │   ├── settings/
│       │   │   │   │   │   └── SettingsScreen.kt
│       │   │   │   │   └── tasks/
│       │   │   │   │       └── TasksScreen.kt
│       │   │   │   ├── theme/
│       │   │   │   │   ├── Color.kt             # Color definitions
│       │   │   │   │   ├── Theme.kt             # Material 3 theme
│       │   │   │   │   └── Type.kt              # Typography
│       │   │   │   └── viewmodel/
│       │   │   │       └── Goal2026ViewModel.kt # Main ViewModel
│       │   │   └── res/                          # Resources (drawables, values, etc.)
│       │   ├── androidTest/                      # Android instrumentation tests
│       │   └── test/                             # Unit tests
│       └── build/                                 # Build outputs
├── build.gradle.kts              # Project-level build configuration
├── settings.gradle.kts           # Project settings
├── gradle/
│   ├── libs.versions.toml        # Dependency version catalog
│   └── wrapper/                  # Gradle wrapper files
├── gradle.properties             # Gradle properties
├── gradlew                       # Gradle wrapper (Unix)
└── gradlew.bat                   # Gradle wrapper (Windows)
```

## 📦 Dependencies

### Core Android Libraries
- `androidx.core:core-ktx` (1.17.0)
- `androidx.lifecycle:lifecycle-runtime-ktx` (2.6.1)
- `androidx.activity:activity-compose` (1.12.1)

### Jetpack Compose
- `androidx.compose.bom` (2024.09.00)
- `androidx.compose.ui:ui`
- `androidx.compose.ui:ui-graphics`
- `androidx.compose.ui:ui-tooling-preview`
- `androidx.compose.material3:material3`
- `androidx.compose.material3:material3-adaptive-navigation-suite`
- `androidx.compose.material:material-icons-extended`

### Navigation
- `androidx.navigation:navigation-compose` (2.7.7)

### ViewModel
- `androidx.lifecycle:lifecycle-viewmodel-compose` (2.6.1)

### Data Serialization
- `com.google.code.gson:gson` (2.10.1)

### Testing
- `junit:junit` (4.13.2)
- `androidx.test.ext:junit` (1.1.5)
- `androidx.test.espresso:espresso-core` (3.5.1)
- `androidx.compose.ui:ui-test-junit4`

### Build Tools
- Android Gradle Plugin: 8.13.2
- Kotlin: 2.0.21
- Kotlin Compose Compiler Plugin: 2.0.21

## 📋 Prerequisites

Before building the project, ensure 