# FamilyHub Platform 👨‍👩‍👧‍👦

> A comprehensive family management platform built with Android Native (Kotlin + Jetpack Compose) and Firebase, following strict TDD methodology and clean architecture principles.

[![CI - Lint](https://github.com/yourusername/family-central/workflows/CI%20-%20Lint/badge.svg)](https://github.com/yourusername/family-central/actions)
[![CI - Tests](https://github.com/yourusername/family-central/workflows/CI%20-%20Tests/badge.svg)](https://github.com/yourusername/family-central/actions)
[![codecov](https://codecov.io/gh/yourusername/family-central/branch/main/graph/badge.svg)](https://codecov.io/gh/yourusername/family-central)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

---

## 📋 Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
- [Development Workflow](#development-workflow)
- [Testing](#testing)
- [CI/CD Pipeline](#cicd-pipeline)
- [Documentation](#documentation)
- [Roadmap](#roadmap)
- [Contributing](#contributing)

---

## ✨ Features

### Core Features (v1.0)
- **🔐 Multi-Family Authentication**: Email, Google SSO, Phone authentication with secure family isolation
- **✅ Task Management**: Shared tasks with assignments, priorities, due dates, subtasks, and recurring tasks
- **💬 Real-Time Chat**: Group and direct messaging with read receipts, typing indicators, and media sharing
- **📅 Shared Calendar**: Family events with RSVPs, reminders, and sync
- **🛒 Shopping Lists**: Real-time collaborative shopping lists with categories
- **📱 Push Notifications**: FCM-powered notifications for tasks, messages, and events
- **🤖 AI Integration**: NLP-based task extraction from chat messages
- **📊 Weekly Summaries**: AI-generated insights and family activity reports

### Future Enhancements (v2.0+)
- **⌚ Wear OS Support**: Google Pixel Watch integration with custom watchfaces
- **👨‍⚕️ Shared Contacts**: Family contact directory with emergency contacts
- **📄 Document Vault**: Secure document storage with OCR and AI summaries
- **🎯 Goals Tracking**: Family goals and achievement system
- **💰 Budget Management**: Shared expense tracking and budgets

---

## 🛠️ Tech Stack

### Android Client
- **Language**: Kotlin 1.9.20
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM + Clean Architecture + UDF
- **DI**: Hilt
- **Async**: Coroutines + Flow
- **Navigation**: Jetpack Navigation Compose
- **Image Loading**: Coil

### Backend
- **Platform**: Firebase Suite
- **Database**: Cloud Firestore (real-time mode)
- **Authentication**: Firebase Auth
- **Storage**: Firebase Storage
- **Messaging**: Firebase Cloud Messaging (FCM)
- **Functions**: Cloud Functions (TypeScript)
- **Hosting**: Firebase Hosting (web dashboard - future)

### DevOps & Testing
- **CI/CD**: GitHub Actions
- **Testing**: JUnit 5, MockK, Turbine, Espresso, Jest
- **Code Quality**: ktlint, ESLint, Spotless
- **Coverage**: JaCoCo, Codecov
- **Security**: Dependabot, CodeQL

---

## 🏗️ Architecture

### Android Architecture Layers

```
┌─────────────────────────────────────────┐
│          UI Layer (Compose)             │
│  • Screens                              │
│  • Composables                          │
│  • Navigation                           │
└───────────────┬─────────────────────────┘
                │
                ▼
┌─────────────────────────────────────────┐
│        Presentation Layer               │
│  • ViewModels                           │
│  • UI State                             │
│  • UI Events                            │
└───────────────┬─────────────────────────┘
                │
                ▼
┌─────────────────────────────────────────┐
│          Domain Layer                   │
│  • Use Cases                            │
│  • Business Logic                       │
│  • Domain Models                        │
│  • Repository Interfaces                │
└───────────────┬─────────────────────────┘
                │
                ▼
┌─────────────────────────────────────────┐
│           Data Layer                    │
│  • Repository Implementations           │
│  • Data Sources (Firebase)              │
│  • DTOs & Mappers                       │
└─────────────────────────────────────────┘
```

### Modular Structure
```
android/
├── app/                    # Main application
├── core/
│   ├── auth/              # Authentication module
│   ├── data/              # Data layer abstractions
│   ├── domain/            # Domain models & use cases
│   ├── ui/                # Shared UI components
│   └── common/            # Utilities
└── feature/
    ├── chat/              # Chat feature
    ├── tasks/             # Task management
    ├── calendar/          # Calendar events
    ├── shared_data/       # Shopping lists, contacts
    └── profile/           # User profile & settings
```

See [Solution Architecture](docs/architecture/SOLUTION_ARCHITECTURE.md) for detailed diagrams.

---

## 🚀 Getting Started

### Prerequisites
- **JDK 17** or higher
- **Android Studio** Hedgehog (2023.1.1) or later
- **Node.js** 18 or higher
- **Firebase CLI**: `npm install -g firebase-tools`
- **Git**

### Setup Instructions

#### 1. Clone Repository
```bash
git clone https://github.com/yourusername/family-central.git
cd family-central
```

#### 2. Firebase Setup
```bash
# Login to Firebase
firebase login

# Create Firebase project (or use existing)
firebase projects:list

# Set project
firebase use --add

# Download google-services.json
# Place in android/app/google-services.json
```

#### 3. Install Dependencies

**Android:**
```bash
cd android
./gradlew build
```

**Functions:**
```bash
cd functions
npm install
npm run build
```

#### 4. Start Firebase Emulators
```bash
# From project root
firebase emulators:start
```

#### 5. Run Android App
```bash
cd android
./gradlew installDebug
```

Or open `android/` in Android Studio and click Run.

---

## 💻 Development Workflow

### TDD Cycle (MANDATORY)
We follow **strict Test-Driven Development**:

1. **🔴 RED**: Write failing test
2. **🟢 GREEN**: Write minimal code to pass
3. **🔵 REFACTOR**: Clean up code

**Example:**
```kotlin
// 1. RED: Write failing test
@Test
fun `createTask with valid data succeeds`() = runTest {
    val result = createTaskUseCase(validTaskData)
    assertTrue(result is Result.Success)
}

// 2. GREEN: Implement feature
class CreateTaskUseCase(private val repository: TaskRepository) {
    suspend operator fun invoke(task: Task): Result<Task> {
        return repository.createTask(task)
    }
}

// 3. REFACTOR: Clean up, add error handling, etc.
```

### Git Workflow

1. **Create feature branch**:
   ```bash
   git checkout -b feature/task-management
   ```

2. **Make changes following TDD**:
   - Write tests first
   - Implement features
   - Ensure all tests pass

3. **Commit with conventional commits**:
   ```bash
   git commit -m "feat(tasks): add task creation with due dates"
   ```

4. **Push and create PR**:
   ```bash
   git push origin feature/task-management
   ```

5. **CI checks must pass**:
   - Lint checks
   - Unit tests (100% coverage required)
   - Integration tests
   - Build succeeds

6. **Code review & merge**

### Code Quality Standards

- **Kotlin**: ktlint + Spotless
- **TypeScript**: ESLint (Google style)
- **Test Coverage**: Minimum 80%, target 100%
- **PR Requirements**: All checks green + 1 approval

---

## 🧪 Testing

### Test Pyramid

```
         /\
        /E2E\         Few E2E tests (critical flows)
       /------\
      /  Integ \      More integration tests
     /----------\
    /   Unit     \    Many unit tests (80%)
   /--------------\
```

### Running Tests

**Android Unit Tests:**
```bash
cd android
./gradlew testDebugUnitTest
```

**Android Integration Tests:**
```bash
# Start emulator
firebase emulators:start

# Run tests
./gradlew connectedDebugAndroidTest
```

**Functions Unit Tests:**
```bash
cd functions
npm run test:unit
```

**Functions Integration Tests:**
```bash
firebase emulators:start
npm run test:integration
```

**Coverage Report:**
```bash
# Android
./gradlew jacocoTestReport

# Functions
npm run test:coverage
```

---

## 🔄 CI/CD Pipeline

### GitHub Actions Workflows

#### 1. **Lint Workflow** (`lint.yml`)
Runs on every push and PR
- Kotlin ktlint
- TypeScript ESLint
- Format checks
- Security scans (CodeQL, Dependabot)

#### 2. **Test Workflow** (`test.yml`)
Runs on every PR
- Android unit tests
- Functions unit tests
- Integration tests (with Firebase Emulator)
- Coverage reporting

#### 3. **Build Workflow** (`build.yml`)
Runs on PR and merge to main
- Build debug APK
- Build release AAB
- Build Firebase Functions
- Size reports

#### 4. **Publish Workflow** (`publish.yml`)
Runs on version tags (e.g., `v1.0.0`)
- Deploy to Google Play Internal Track
- Deploy Firebase Functions
- Create GitHub Release

### Deployment

**Manual Deploy:**
```bash
# Deploy functions
firebase deploy --only functions

# Deploy rules
firebase deploy --only firestore:rules,storage

# Full deploy
firebase deploy
```

**Automated Deploy:**
```bash
# Tag release
git tag v1.0.0
git push origin v1.0.0

# GitHub Actions handles the rest
```

---

## 📚 Documentation

- [Solution Architecture](docs/architecture/SOLUTION_ARCHITECTURE.md) - System architecture with Mermaid diagrams
- [TDD Implementation Plan](docs/TDD_IMPLEMENTATION_PLAN.md) - Feature-by-feature test cases and steps
- [Cloud Functions API](docs/api/CLOUD_FUNCTIONS_API.md) - Backend API documentation
- [ADR 001: MVVM Choice](docs/adr/001-mvvm-architecture-choice.md) - Architecture decision record
- [ADR 002: Firebase Choice](docs/adr/002-firebase-backend-choice.md) - Backend platform decision
- [Project Structure](docs/PROJECT_STRUCTURE.md) - Directory layout and module dependencies

---

## 🗺️ Roadmap

### Phase 1: Foundation (Weeks 1-2) ✅
- [x] Project setup
- [x] Architecture design
- [x] CI/CD pipelines
- [x] Security rules

### Phase 2: Core Features (Weeks 3-5) 🚧
- [ ] Authentication & family management
- [ ] Task management
- [ ] Real-time chat

### Phase 3: Advanced Features (Weeks 6-7) 📅
- [ ] Calendar events
- [ ] Shopping lists
- [ ] AI task extraction

### Phase 4: Polish & Launch (Week 8) 🚀
- [ ] E2E testing
- [ ] Performance optimization
- [ ] Beta release

### Phase 5: Future Enhancements (v2.0+) 🔮
- [ ] Wear OS support (Pixel Watch)
- [ ] Custom watchfaces
- [ ] Document vault
- [ ] Budget management
- [ ] Health data integration

---

## 🤝 Contributing

We welcome contributions! Please follow these guidelines:

1. **Fork the repository**
2. **Create a feature branch** following Git workflow
3. **Follow TDD methodology** (tests first!)
4. **Ensure all tests pass** and coverage meets requirements
5. **Follow code style** (ktlint, ESLint)
6. **Write clear commit messages** (conventional commits)
7. **Submit PR** with detailed description

See [TDD Implementation Plan](docs/TDD_IMPLEMENTATION_PLAN.md) for development process.

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 👥 Authors

- **Principal Architect** - Architecture design and TDD strategy
- **DevOps Lead** - CI/CD pipeline and infrastructure
- **Android Team** - Client application development
- **Backend Team** - Firebase Functions and services

---

## 🙏 Acknowledgments

- Firebase team for excellent real-time platform
- Jetpack Compose team for modern UI toolkit
- Open source community for amazing libraries

---

## 📞 Support

- **Documentation**: [docs/](docs/)
- **Issues**: [GitHub Issues](https://github.com/yourusername/family-central/issues)
- **Discussions**: [GitHub Discussions](https://github.com/yourusername/family-central/discussions)

---

**Built with ❤️ following SOLID principles, Clean Architecture, and TDD methodology**
