# FamilyHub Platform - Project Directory Structure

```
family-central/
├── .github/
│   └── workflows/              # GitHub Actions CI/CD pipelines
│       ├── lint.yml           # Code quality checks
│       ├── test.yml           # Automated testing
│       ├── build.yml          # Build & compile
│       └── publish.yml        # Deployment automation
│
├── android/                    # Android Native Client (Kotlin + Jetpack Compose)
│   ├── app/                   # Main application module
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── kotlin/com/familyhub/
│   │   │   │   │   ├── MainActivity.kt
│   │   │   │   │   ├── FamilyHubApplication.kt
│   │   │   │   │   ├── di/           # Hilt dependency injection
│   │   │   │   │   └── navigation/   # Compose Navigation
│   │   │   │   └── res/
│   │   │   │       ├── values/
│   │   │   │       ├── drawable/
│   │   │   │       └── layout/
│   │   │   ├── test/              # Unit tests
│   │   │   └── androidTest/        # Instrumented tests
│   │   ├── build.gradle.kts
│   │   └── proguard-rules.pro
│   │
│   ├── core/                   # Core shared modules
│   │   ├── auth/              # Authentication module
│   │   │   └── src/main/kotlin/com/familyhub/core/auth/
│   │   │       ├── domain/    # Auth use cases
│   │   │       ├── data/      # Auth repository impl
│   │   │       └── di/        # Auth DI module
│   │   │
│   │   ├── data/              # Data layer abstractions
│   │   │   └── src/main/kotlin/com/familyhub/core/data/
│   │   │       ├── repository/
│   │   │       ├── model/
│   │   │       └── source/
│   │   │
│   │   ├── domain/            # Domain layer (business logic)
│   │   │   └── src/main/kotlin/com/familyhub/core/domain/
│   │   │       ├── model/
│   │   │       ├── usecase/
│   │   │       └── repository/
│   │   │
│   │   ├── ui/                # Shared UI components
│   │   │   └── src/main/kotlin/com/familyhub/core/ui/
│   │   │       ├── components/
│   │   │       ├── theme/
│   │   │       └── utils/
│   │   │
│   │   └── common/            # Common utilities
│   │       └── src/main/kotlin/com/familyhub/core/common/
│   │           ├── result/
│   │           ├── extensions/
│   │           └── utils/
│   │
│   ├── feature/               # Feature modules (Clean Architecture)
│   │   ├── chat/             # Chat feature
│   │   │   └── src/
│   │   │       ├── main/kotlin/com/familyhub/feature/chat/
│   │   │       │   ├── ui/           # Compose UI
│   │   │       │   ├── viewmodel/    # ViewModels
│   │   │       │   ├── domain/       # Feature use cases
│   │   │       │   ├── data/         # Feature repositories
│   │   │       │   └── di/           # Feature DI
│   │   │       └── test/             # Feature unit tests
│   │   │
│   │   ├── tasks/            # Task planner feature
│   │   │   └── src/main/kotlin/com/familyhub/feature/tasks/
│   │   │       ├── ui/
│   │   │       ├── viewmodel/
│   │   │       ├── domain/
│   │   │       └── data/
│   │   │
│   │   ├── calendar/         # Shared calendar feature
│   │   │   └── src/main/kotlin/com/familyhub/feature/calendar/
│   │   │
│   │   ├── shared_data/      # Shared data (shopping lists, contacts)
│   │   │   └── src/main/kotlin/com/familyhub/feature/shared_data/
│   │   │
│   │   └── profile/          # User profile & family management
│   │       └── src/main/kotlin/com/familyhub/feature/profile/
│   │
│   ├── build.gradle.kts      # Root build configuration
│   ├── settings.gradle.kts   # Module configuration
│   └── gradle.properties     # Gradle properties
│
├── functions/                 # Firebase Cloud Functions (TypeScript)
│   ├── src/
│   │   ├── api/              # HTTP callable functions
│   │   │   ├── task-api.ts
│   │   │   ├── chat-api.ts
│   │   │   └── ai-api.ts
│   │   │
│   │   ├── services/         # Business logic services
│   │   │   ├── ai-service.ts
│   │   │   ├── notification-service.ts
│   │   │   └── task-service.ts
│   │   │
│   │   ├── models/           # TypeScript interfaces
│   │   │   ├── family.model.ts
│   │   │   ├── user.model.ts
│   │   │   ├── task.model.ts
│   │   │   ├── chat.model.ts
│   │   │   └── shared-data.model.ts
│   │   │
│   │   ├── utils/            # Utility functions
│   │   │   ├── validators.ts
│   │   │   └── helpers.ts
│   │   │
│   │   ├── triggers/         # Firestore/FCM triggers
│   │   │   ├── task-triggers.ts
│   │   │   ├── chat-triggers.ts
│   │   │   └── notification-triggers.ts
│   │   │
│   │   └── index.ts          # Function exports
│   │
│   ├── test/
│   │   ├── unit/             # Jest unit tests
│   │   └── integration/      # Integration tests with emulator
│   │
│   ├── package.json
│   ├── tsconfig.json
│   ├── jest.config.js
│   └── .eslintrc.js
│
├── firestore/
│   ├── firestore.rules       # Security rules
│   ├── firestore.indexes.json
│   └── storage.rules
│
├── docs/                      # Documentation
│   ├── architecture/
│   │   ├── SOLUTION_ARCHITECTURE.md
│   │   ├── DATA_FLOW.md
│   │   └── SECURITY_MODEL.md
│   │
│   ├── adr/                  # Architecture Decision Records
│   │   ├── 001-mvvm-vs-mvi.md
│   │   ├── 002-firebase-choice.md
│   │   └── 003-modular-architecture.md
│   │
│   ├── api/                  # API documentation
│   │   └── cloud-functions-api.md
│   │
│   └── TDD_IMPLEMENTATION_PLAN.md
│
├── firebase.json             # Firebase configuration
├── .firebaserc              # Firebase project config
├── README.md
└── LICENSE
```

## Architecture Patterns

### Android Client Architecture: **MVVM with UDF (Unidirectional Data Flow)**

**Justification:**
- **MVVM** chosen over MVI for:
  - Better separation of concerns with ViewModel as the single source of truth
  - Simpler state management for most use cases
  - Native Android architecture component support
  - Easier testing with clear boundaries
  - Less boilerplate than MVI for standard CRUD operations

- **UDF** ensures:
  - Predictable state management
  - Easy debugging and testing
  - Clear data flow: UI → ViewModel → UseCase → Repository → DataSource

### Layer Responsibilities

1. **UI Layer (Compose)**
   - Render UI based on ViewModel state
   - Emit user events to ViewModel
   - No business logic

2. **ViewModel Layer**
   - Manage UI state
   - Handle user events
   - Orchestrate use cases
   - Transform domain models to UI models

3. **Domain Layer (Use Cases)**
   - Business logic execution
   - Single responsibility per use case
   - Framework-independent

4. **Data Layer (Repository)**
   - Abstract data sources
   - Coordinate between remote and local data
   - Error handling and mapping

5. **Data Source Layer**
   - Firebase API integration
   - Local caching (Room if needed)
   - Network operations

## Module Dependencies

```
app
 ├─> feature:* (all feature modules)
 └─> core:*    (all core modules)

feature:*
 ├─> core:domain
 ├─> core:data
 ├─> core:ui
 └─> core:common

core:data
 ├─> core:domain
 └─> core:common

core:auth
 ├─> core:domain
 └─> core:common
```

## Testing Structure

- **Unit Tests**: `src/test/` in each module
- **Integration Tests**: Firebase Emulator Suite + HiltTestRunner
- **E2E Tests**: `android/app/src/androidTest/`
- **Function Tests**: `functions/test/`
