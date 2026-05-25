# FamilyHub Platform

[![Kotlin](https://img.shields.io/badge/Kotlin-2.1-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack_Compose-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Firebase](https://img.shields.io/badge/Backend-Firebase-FFCA28?logo=firebase&logoColor=black)](https://firebase.google.com)
[![License](https://img.shields.io/badge/License-MIT-green)](LICENSE)

> **A comprehensive family management platform for modern Android devices.**  
> Keep your family organized with tasks, chat, calendar, shopping lists, and more.

## 📱 Features

| Feature | Description | Status |
|---------|-------------|--------|
| 🔐 **Authentication** | Secure login, family registration, invite codes | ✅ |
| 👨‍👩‍👧‍👦 **Family Management** | Create/manage family groups, member roles | ✅ |
| ✅ **Task Management** | Assign, track, and complete family tasks | ✅ |
| 💬 **Real-time Chat** | Instant messaging within family groups | ✅ |
| 📅 **Shared Calendar** | Family events, reminders, scheduling | ✅ |
| 🛒 **Shopping Lists** | Collaborative lists with real-time sync | ✅ |
| 👤 **Profile & Settings** | User profiles, notifications, preferences | ✅ |

## 🏗️ Architecture

Built with **Clean Architecture**, **MVVM**, and **Test-Driven Development**.

```
FamilyHub/
├── android/              # Android app (Kotlin + Jetpack Compose)
│   ├── app/             # Application layer, navigation, DI
│   ├── data/            # Repository implementations, data sources
│   ├── domain/          # Use cases, business logic
│   └── presentation/    # ViewModels, Compose screens
├── functions/           # Firebase Cloud Functions (Node.js/TS)
│   ├── auth/            # Authentication triggers
│   ├── messaging/       # Push notifications
│   └── sync/            # Data synchronization
├── firestore/           # Firestore security rules & indexes
└── docs/                # Architecture documentation
```

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog+
- JDK 17
- Firebase project with Authentication, Firestore, and Cloud Functions

```bash
# Clone
git clone https://github.com/kluth/family-central.git
cd family-central

# Open in Android Studio
# File → Open → select android/ directory

# Deploy Cloud Functions
cd functions
npm install
npm run deploy

# Run app
# Select android/ run configuration → Run
```

## 🧪 Testing

```bash
# Run Android tests
cd android
./gradlew testDebugUnitTest

# Run Cloud Function tests
cd functions
npm test
```

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| **UI** | Jetpack Compose, Material 3 |
| **State** | StateFlow, Compose State |
| **DI** | Hilt |
| **Database** | Firestore (NoSQL) |
| **Auth** | Firebase Authentication |
| **Messaging** | Firebase Cloud Messaging |
| **Backend** | Firebase Cloud Functions (Node.js) |
| **Architecture** | Clean Architecture + MVVM |
| **Testing** | JUnit, MockK, Compose Testing |

## 📄 License

MIT