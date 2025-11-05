# FamilyHub Platform - Solution Architecture

## High-Level System Architecture

```mermaid
graph TB
    subgraph "Client Layer - Android Native"
        UI[Jetpack Compose UI]
        VM[ViewModels + Hilt DI]
        UC[Use Cases - Domain Layer]
        REPO[Repository Pattern]
        DS[Data Sources]
    end

    subgraph "Firebase Backend"
        AUTH[Firebase Auth]
        FIRESTORE[Cloud Firestore]
        STORAGE[Firebase Storage]
        FCM[Firebase Cloud Messaging]
        FUNCTIONS[Cloud Functions - TypeScript]
    end

    subgraph "External Services"
        AI[AI/NLP Service]
        CRON[Cloud Scheduler]
    end

    UI -->|User Events| VM
    VM -->|Execute| UC
    UC -->|Request Data| REPO
    REPO -->|Fetch/Store| DS

    DS -->|Authenticate| AUTH
    DS -->|Query/Listen| FIRESTORE
    DS -->|Upload/Download| STORAGE
    DS -->|Receive Push| FCM

    FUNCTIONS -->|Trigger| FIRESTORE
    FUNCTIONS -->|Send| FCM
    FUNCTIONS -->|Process| AI
    CRON -->|Schedule| FUNCTIONS

    FIRESTORE -->|Real-time Updates| DS
    FCM -->|Notifications| DS
```

## Detailed Architecture - MVVM with Clean Architecture

```mermaid
graph LR
    subgraph "Presentation Layer"
        SCREEN[Composable Screens]
        VM[ViewModel]
        UISTATE[UI State]
    end

    subgraph "Domain Layer"
        UC1[GetTasksUseCase]
        UC2[CreateTaskUseCase]
        UC3[SendMessageUseCase]
        MODEL[Domain Models]
    end

    subgraph "Data Layer"
        REPO_INT[Repository Interfaces]
        REPO_IMPL[Repository Implementation]
        REMOTE[Firebase Data Source]
        LOCAL[Local Cache - Room]
    end

    SCREEN -->|Observe State| VM
    SCREEN -->|Emit Events| VM
    VM -->|Updates| UISTATE
    VM -->|Invokes| UC1
    VM -->|Invokes| UC2
    VM -->|Invokes| UC3

    UC1 -->|Uses| REPO_INT
    UC2 -->|Uses| REPO_INT
    UC3 -->|Uses| REPO_INT

    REPO_INT -->|Implemented by| REPO_IMPL
    REPO_IMPL -->|Fetches| REMOTE
    REPO_IMPL -->|Caches| LOCAL
```

## Data Flow - Unidirectional Data Flow (UDF)

```mermaid
sequenceDiagram
    participant U as User
    participant UI as Compose UI
    participant VM as ViewModel
    participant UC as UseCase
    participant REPO as Repository
    participant FB as Firebase

    U->>UI: User Action (e.g., Create Task)
    UI->>VM: Event(CreateTask)
    VM->>VM: Set Loading State
    VM->>UC: execute(taskData)
    UC->>UC: Validate Business Rules
    UC->>REPO: createTask(task)
    REPO->>FB: Firestore.collection().add()
    FB-->>REPO: Success/Failure
    REPO-->>UC: Result<Task>
    UC-->>VM: Result<Task>
    VM->>VM: Update UI State
    VM-->>UI: State Update
    UI-->>U: UI Renders New State
```

## Firebase Backend Architecture

```mermaid
graph TB
    subgraph "Firestore Collections"
        FAMILIES[(families)]
        USERS[(users)]
        TASKS[(tasks)]
        CHATS[(chats)]
        MESSAGES[(messages)]
        EVENTS[(calendar_events)]
        SHARED[(shared_data)]
    end

    subgraph "Cloud Functions - TypeScript"
        FN_AI[AI Task Extraction]
        FN_NOTIFY[Notification Engine]
        FN_TASK[Task Management API]
        FN_CHAT[Chat Processing]
        FN_SUMMARY[Weekly Summary Cron]
    end

    subgraph "Security"
        RULES[Firestore Security Rules]
        STORAGE_RULES[Storage Rules]
    end

    TASKS -->|onCreate Trigger| FN_NOTIFY
    MESSAGES -->|onCreate Trigger| FN_CHAT
    FN_AI -->|HTTP Callable| TASKS
    FN_SUMMARY -->|Scheduled| USERS
    FN_NOTIFY -->|Sends| FCM[FCM Notifications]

    FAMILIES -.->|Protected by| RULES
    USERS -.->|Protected by| RULES
    TASKS -.->|Protected by| RULES
    CHATS -.->|Protected by| RULES
```

## Feature Module Architecture

```mermaid
graph TB
    subgraph "Feature: Tasks"
        TASK_UI[Task UI Components]
        TASK_VM[TaskViewModel]
        TASK_UC[Task Use Cases]
        TASK_REPO[Task Repository]
    end

    subgraph "Feature: Chat"
        CHAT_UI[Chat UI Components]
        CHAT_VM[ChatViewModel]
        CHAT_UC[Chat Use Cases]
        CHAT_REPO[Chat Repository]
    end

    subgraph "Core Modules"
        CORE_AUTH[Core: Auth]
        CORE_DATA[Core: Data]
        CORE_UI[Core: UI Theme]
    end

    TASK_UI --> TASK_VM
    TASK_VM --> TASK_UC
    TASK_UC --> TASK_REPO

    CHAT_UI --> CHAT_VM
    CHAT_VM --> CHAT_UC
    CHAT_UC --> CHAT_REPO

    TASK_UI -.->|Uses| CORE_UI
    CHAT_UI -.->|Uses| CORE_UI
    TASK_REPO -.->|Uses| CORE_DATA
    CHAT_REPO -.->|Uses| CORE_DATA
    TASK_VM -.->|Protected| CORE_AUTH
    CHAT_VM -.->|Protected| CORE_AUTH
```

## Authentication & Authorization Flow

```mermaid
sequenceDiagram
    participant User
    participant App
    participant FirebaseAuth
    participant Firestore
    participant Functions

    User->>App: Launch App
    App->>FirebaseAuth: Check Auth State

    alt Not Authenticated
        App->>User: Show Login Screen
        User->>App: Login (Email/Google/Phone)
        App->>FirebaseAuth: signIn()
        FirebaseAuth-->>App: User Token
        App->>Functions: createUserProfile()
        Functions->>Firestore: Create user doc
        App->>User: Navigate to Family Selection
    end

    alt Authenticated
        App->>FirebaseAuth: Get Current User
        FirebaseAuth-->>App: User + Token
        App->>Firestore: Get User Families
        Firestore-->>App: Family List
        App->>User: Show Family Dashboard
    end

    User->>App: Access Protected Resource
    App->>App: Attach Auth Token to Request
    App->>Firestore: Query with Security Rules
    Firestore->>Firestore: Validate Token & Rules
    Firestore-->>App: Authorized Data Only
```

## Multi-Family Isolation Model

```mermaid
graph TB
    USER[User Account]

    subgraph "Family A"
        FA_TASKS[Tasks A]
        FA_CHATS[Chats A]
        FA_DATA[Shared Data A]
    end

    subgraph "Family B"
        FB_TASKS[Tasks B]
        FB_CHATS[Chats B]
        FB_DATA[Shared Data B]
    end

    USER -->|Member| Family_A_Membership
    USER -->|Admin| Family_B_Membership

    Family_A_Membership -.->|Read/Write| FA_TASKS
    Family_A_Membership -.->|Read/Write| FA_CHATS
    Family_A_Membership -.->|Read/Write| FA_DATA

    Family_B_Membership -.->|Full Control| FB_TASKS
    Family_B_Membership -.->|Full Control| FB_CHATS
    Family_B_Membership -.->|Full Control| FB_DATA

    style Family_A_Membership fill:#90EE90
    style Family_B_Membership fill:#FFB6C1
```

## Real-Time Data Synchronization

```mermaid
sequenceDiagram
    participant User_A as User A (Android)
    participant User_B as User B (Android)
    participant Firestore
    participant Functions
    participant FCM

    User_A->>Firestore: Create Task
    Firestore->>Functions: onCreate Trigger
    Functions->>Functions: Process New Task
    Functions->>FCM: Send Notification to Family

    Firestore-->>User_B: Real-time Snapshot Update
    FCM-->>User_B: Push Notification
    User_B->>User_B: Update UI with New Task

    User_B->>Firestore: Update Task Status
    Firestore-->>User_A: Real-time Snapshot Update
    User_A->>User_A: Update UI Automatically
```

## AI Integration Architecture

```mermaid
graph LR
    USER[User Input]
    CHAT[Chat Message]

    CHAT -->|Send| FIRESTORE[Firestore: messages]
    FIRESTORE -->|Trigger| FN_AI[Cloud Function: AI Processor]

    FN_AI -->|Extract Intent| NLP[NLP Service]
    NLP -->|Task Detected| FN_AI

    FN_AI -->|Create| TASKS[(tasks collection)]
    FN_AI -->|Respond| CHAT_RESPONSE[AI Response Message]

    TASKS -->|Trigger| FN_NOTIFY[Notification Function]
    FN_NOTIFY -->|Send| FCM[FCM to Family Members]
```

## CI/CD Pipeline Architecture

```mermaid
graph LR
    DEV[Developer Push]

    DEV -->|Commit| GH[GitHub Repository]

    GH -->|Trigger| LINT[Lint Workflow]
    LINT -->|ktlint/eslint| CODE[Code Quality Check]

    CODE -->|Success| TEST[Test Workflow]
    TEST -->|Run| UNIT[Unit Tests]
    TEST -->|Run| INTEG[Integration Tests - Emulator]

    UNIT -->|All Pass| BUILD[Build Workflow]
    INTEG -->|All Pass| BUILD

    BUILD -->|Compile| APK[Debug APK]
    BUILD -->|Compile| AAB[Release AAB]

    AAB -->|Sign| SIGNED[Signed AAB]

    SIGNED -->|Tag Release| PUBLISH[Publish Workflow]
    PUBLISH -->|Deploy| PLAY[Google Play Internal Track]
    PUBLISH -->|Deploy| FUNCTIONS[Firebase Functions Prod]
```

## Security Model

```mermaid
graph TB
    subgraph "Authentication Layer"
        EMAIL[Email/Password]
        GOOGLE[Google SSO]
        PHONE[Phone Auth]
    end

    subgraph "Authorization Layer"
        TOKEN[Firebase Auth Token]
        CLAIMS[Custom Claims - Roles]
    end

    subgraph "Data Access Control"
        RULES[Firestore Security Rules]
        STORAGE_SEC[Storage Security Rules]
    end

    subgraph "Data Protection"
        ENCRYPT[Data Encryption at Rest]
        TLS[TLS in Transit]
        AUDIT[Audit Logging]
    end

    EMAIL --> TOKEN
    GOOGLE --> TOKEN
    PHONE --> TOKEN

    TOKEN --> CLAIMS
    CLAIMS --> RULES
    RULES --> ENCRYPT

    STORAGE_SEC --> TLS
    RULES --> AUDIT
```

## Key Architectural Decisions

### 1. MVVM over MVI
- **Rationale**: Better separation, less boilerplate, native support
- **Trade-off**: Slightly less predictable than MVI for complex state
- **Mitigation**: Strict UDF patterns + immutable state

### 2. Firebase over Custom Backend
- **Rationale**: Real-time sync, offline support, built-in auth, scalability
- **Trade-off**: Vendor lock-in, limited complex queries
- **Mitigation**: Repository pattern abstracts Firebase, easy migration path

### 3. Modular Architecture
- **Rationale**: Parallel development, faster builds, clear boundaries
- **Trade-off**: Initial setup complexity
- **Benefits**: Testability, reusability, maintainability

### 4. TypeScript for Cloud Functions
- **Rationale**: Type safety, better tooling, prevents runtime errors
- **Requirement**: MANDATORY per project spec

### 5. Hilt for DI
- **Rationale**: Official Android DI, compile-time validation, scoping
- **Alternative Considered**: Koin (runtime DI)
- **Winner**: Hilt (type safety + performance)

## Performance Considerations

1. **Firestore Query Optimization**
   - Composite indexes for complex queries
   - Pagination for large datasets
   - Denormalization where appropriate

2. **Image Loading**
   - Coil library for Compose
   - Firebase Storage URLs with caching
   - Thumbnail generation in Cloud Functions

3. **Offline Support**
   - Firestore offline persistence enabled
   - Local caching for critical data
   - Optimistic UI updates

4. **Build Performance**
   - Modular architecture enables parallel builds
   - Gradle build cache enabled
   - Configuration cache enabled
