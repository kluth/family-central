# FamilyHub Platform v1.0

**A comprehensive family management platform built with modern Android development practices**

## Features

- Authentication & Family Management
- Task Management System
- Real-time Family Chat
- Shared Calendar
- Shopping Lists
- Profile & Settings

## Architecture

Built with Clean Architecture, MVVM, and Test-Driven Development.

## Tech Stack

- Kotlin, Jetpack Compose, Hilt
- Firebase (Auth, Firestore, FCM)
- 50+ Unit Tests with TDD
- Material 3 Design

## Project Structure

- `android/core/` - Domain, Data, Common modules
- `android/feature/` - Feature modules (Tasks, Chat, Calendar, etc.)
- `android/app/` - Main app module with navigation

## Getting Started

1. Clone the repository
2. Open in Android Studio
3. Add `google-services.json` from Firebase
4. Build and run

## Testing

```bash
./gradlew test  # Run all tests
```

Made with TDD principles and Clean Architecture.
