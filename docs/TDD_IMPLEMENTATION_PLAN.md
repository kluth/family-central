# TDD Implementation Plan - FamilyHub Platform

## Test-Driven Development Methodology

### TDD Cycle (RED → GREEN → REFACTOR)

1. **RED**: Write a failing test that defines desired functionality
2. **GREEN**: Write minimal code to make the test pass
3. **REFACTOR**: Clean up code while keeping tests green

### Testing Strategy

- **100% Coverage Target**: Unit, Integration, and E2E tests
- **Test First**: No production code without failing test
- **Test Pyramid**: Many unit tests, fewer integration tests, minimal E2E tests

---

## Feature 1: User Authentication & Family Management

### User Story
> As a user, I want to sign up, create/join families, and manage family memberships so that I can organize my household.

### Acceptance Criteria
- [x] Users can sign up with email/password, Google SSO, or phone
- [x] Users can create new families
- [x] Users can join families via invite code
- [x] Family admins can manage members and settings
- [x] Users can switch between multiple families

### E2E Test Cases (Espresso/UI Automator)

```gherkin
Scenario: User signs up and creates first family
  Given I launch the app for the first time
  When I tap "Sign Up with Google"
  And I complete Google authentication
  Then I should see the "Create Your Family" screen

  When I enter family name "Smith Family"
  And I tap "Create Family"
  Then I should see the family dashboard
  And I should be assigned as family admin

Scenario: User joins existing family via invite
  Given I am signed in as a new user
  And I have a valid invite code "FAMILY123"
  When I tap "Join Family"
  And I enter invite code "FAMILY123"
  And I tap "Join"
  Then I should see "Smith Family" in my families list
  And I should be assigned as family member
```

### Integration Test Cases

```kotlin
// AuthRepositoryTest.kt
@Test
fun `signUpWithEmail creates user profile in Firestore`() = runTest {
    // Given
    val email = "test@example.com"
    val password = "password123"

    // When
    val result = authRepository.signUpWithEmail(email, password)

    // Then
    assertTrue(result is Result.Success)
    val userId = (result as Result.Success).data.uid

    // Verify user document exists
    val userDoc = firestore.collection("users").document(userId).get().await()
    assertTrue(userDoc.exists())
    assertEquals(email, userDoc.data?.get("email"))
}

@Test
fun `createFamily adds user as admin member`() = runTest {
    // Given
    val userId = "user123"
    val familyName = "Test Family"

    // When
    val result = familyRepository.createFamily(userId, familyName)

    // Then
    assertTrue(result is Result.Success)
    val familyId = (result as Result.Success).data.id

    // Verify family document
    val familyDoc = firestore.collection("families").document(familyId).get().await()
    val members = familyDoc.data?.get("members") as List<*>

    assertTrue(members.any { (it as Map<*, *>)["uid"] == userId && it["role"] == "admin" })
}
```

### Unit Test Cases

```kotlin
// SignUpViewModelTest.kt
class SignUpViewModelTest {
    @Test
    fun `signUp with valid email shows success state`() = runTest {
        // Given
        val signUpUseCase = mockk<SignUpUseCase>()
        coEvery { signUpUseCase(any()) } returns Result.Success(mockUser)

        val viewModel = SignUpViewModel(signUpUseCase)

        // When
        viewModel.onSignUp("test@example.com", "password123")

        // Then
        assertEquals(SignUpUiState.Success, viewModel.uiState.value)
        coVerify { signUpUseCase(SignUpRequest("test@example.com", "password123")) }
    }

    @Test
    fun `signUp with invalid email shows error`() = runTest {
        // Given
        val viewModel = SignUpViewModel(signUpUseCase)

        // When
        viewModel.onSignUp("invalid-email", "password")

        // Then
        assertTrue(viewModel.uiState.value is SignUpUiState.Error)
        assertEquals("Invalid email format", (viewModel.uiState.value as SignUpUiState.Error).message)
    }
}

// CreateFamilyUseCaseTest.kt
class CreateFamilyUseCaseTest {
    @Test
    fun `invoke creates family with current user as admin`() = runTest {
        // Given
        val repository = mockk<FamilyRepository>()
        val useCase = CreateFamilyUseCase(repository)

        coEvery { repository.createFamily(any(), any()) } returns Result.Success(mockFamily)

        // When
        val result = useCase(CreateFamilyRequest(userId = "user123", name = "Family"))

        // Then
        assertTrue(result is Result.Success)
        coVerify { repository.createFamily("user123", "Family") }
    }
}
```

### Implementation Steps (TDD Order)

1. **Write failing E2E test**: Sign up flow
2. **Write failing integration test**: `authRepository.signUpWithEmail()`
3. **Write failing unit tests**: `SignUpViewModel`, `SignUpUseCase`
4. **Implement**: UseCase → Repository → ViewModel → UI
5. **Write failing integration test**: `createFamily()`
6. **Write failing unit tests**: `CreateFamilyUseCase`, `CreateFamilyViewModel`
7. **Implement**: Family creation flow
8. **Write failing E2E test**: Join family via invite
9. **Write tests and implement**: Invite code generation and validation
10. **Refactor**: Clean up, extract common patterns

---

## Feature 2: Task Management

### User Story
> As a family member, I want to create, assign, and track tasks so that household responsibilities are organized and completed.

### Acceptance Criteria
- [x] Users can create tasks with title, description, due date, priority
- [x] Tasks can be assigned to multiple family members
- [x] Users can add subtasks and track progress
- [x] Users receive notifications for task assignments and due dates
- [x] Users can filter and sort tasks by status, assignee, priority

### E2E Test Cases

```gherkin
Scenario: Create and assign a task
  Given I am signed in and viewing family dashboard
  When I tap the "Add Task" button
  And I enter title "Buy groceries"
  And I set priority to "High"
  And I assign to "John" and "Mary"
  And I set due date to tomorrow
  And I tap "Create Task"
  Then I should see "Buy groceries" in the task list
  And John and Mary should receive notifications

Scenario: Complete a task with subtasks
  Given I have a task "Clean house" with subtasks:
    | Vacuum living room |
    | Wash dishes        |
  When I tap "Clean house"
  And I check "Vacuum living room"
  Then the task progress should show 50%

  When I check "Wash dishes"
  Then the task progress should show 100%

  When I tap "Mark Complete"
  Then the task status should be "Completed"
  And all family members should receive completion notification
```

### Integration Test Cases

```kotlin
// TaskRepositoryTest.kt
@Test
fun `createTask triggers notification to assigned users`() = runTest {
    // Given
    val task = Task(
        title = "Test Task",
        assignedTo = listOf("user1", "user2"),
        familyId = "family123"
    )

    // When
    val result = taskRepository.createTask(task)

    // Then
    assertTrue(result is Result.Success)
    val taskId = (result as Result.Success).data.id

    // Verify notifications were created
    advanceTimeBy(2000) // Wait for trigger

    val notifications = firestore.collection("notifications")
        .whereEqualTo("data.entityId", taskId)
        .get().await()

    assertEquals(2, notifications.size())
    assertTrue(notifications.documents.all { it.data?.get("type") == "task_assigned" })
}

@Test
fun `updateTaskStatus to completed triggers completion notification`() = runTest {
    // Given
    val taskId = "task123"
    setupExistingTask(taskId)

    // When
    taskRepository.updateTaskStatus(taskId, TaskStatus.COMPLETED)

    // Then
    advanceTimeBy(2000)

    val completionNotifications = firestore.collection("notifications")
        .whereEqualTo("type", "task_completed")
        .whereEqualTo("data.entityId", taskId)
        .get().await()

    assertTrue(completionNotifications.size() > 0)
}
```

### Unit Test Cases

```kotlin
// CreateTaskViewModelTest.kt
class CreateTaskViewModelTest {
    @Test
    fun `createTask with valid data succeeds`() = runTest {
        // Given
        val createTaskUseCase = mockk<CreateTaskUseCase>()
        coEvery { createTaskUseCase(any()) } returns Result.Success(mockTask)

        val viewModel = CreateTaskViewModel(createTaskUseCase)

        // When
        viewModel.onTitleChanged("Buy groceries")
        viewModel.onPrioritySelected(TaskPriority.HIGH)
        viewModel.onAssigneeSelected(listOf("user1"))
        viewModel.onCreateTask()

        // Then
        assertTrue(viewModel.uiState.value is CreateTaskUiState.Success)
    }

    @Test
    fun `createTask with empty title shows error`() = runTest {
        // Given
        val viewModel = CreateTaskViewModel(createTaskUseCase)

        // When
        viewModel.onTitleChanged("")
        viewModel.onCreateTask()

        // Then
        assertTrue(viewModel.uiState.value is CreateTaskUiState.Error)
        assertEquals("Title cannot be empty", viewModel.titleError.value)
    }
}

// TaskListViewModelTest.kt
class TaskListViewModelTest {
    @Test
    fun `tasks are filtered by status correctly`() = runTest {
        // Given
        val getTasks = mockk<GetTasksUseCase>()
        val allTasks = listOf(
            mockTask(status = TaskStatus.TODO),
            mockTask(status = TaskStatus.IN_PROGRESS),
            mockTask(status = TaskStatus.COMPLETED)
        )
        coEvery { getTasks(any()) } returns Result.Success(allTasks)

        val viewModel = TaskListViewModel(getTasks)

        // When
        viewModel.filterByStatus(TaskStatus.TODO)

        // Then
        assertEquals(1, viewModel.filteredTasks.value.size)
        assertEquals(TaskStatus.TODO, viewModel.filteredTasks.value[0].status)
    }
}
```

### Implementation Steps

1. **Write failing E2E test**: Create task flow
2. **Write failing integration test**: `taskRepository.createTask()` with Firestore
3. **Write failing unit tests**: `CreateTaskUseCase`, `CreateTaskViewModel`
4. **Implement**: Domain → Data → Presentation layers
5. **Write failing integration test**: Task trigger → notification creation
6. **Implement**: Cloud Function trigger for task notifications
7. **Write failing unit tests**: Task filtering and sorting logic
8. **Implement**: Filter and sort functionality
9. **Write failing E2E test**: Complete task with subtasks
10. **Write tests and implement**: Subtask management
11. **Refactor**: Extract common patterns, optimize queries

---

## Feature 3: Real-Time Chat

### User Story
> As a family member, I want to send messages, share media, and see read receipts so that I can communicate effectively with my family.

### Acceptance Criteria
- [x] Users can create group chats and direct messages
- [x] Messages are delivered in real-time
- [x] Users can send text, images, and files
- [x] Users can see typing indicators
- [x] Users can see read receipts
- [x] Users can mention other members

### E2E Test Cases

```gherkin
Scenario: Send message and see real-time delivery
  Given I am in "Family Chat"
  And Another user "John" is also viewing the chat
  When I type "Hello everyone"
  Then John should see typing indicator "User is typing..."

  When I press Send
  Then my message should appear in the chat
  And John should see my message within 1 second
  And my message should show "Delivered" status

Scenario: Send image and receive notification
  Given I am in "Family Chat"
  And "Mary" is not currently viewing the chat
  When I attach an image
  And I press Send
  Then the image should upload to Firebase Storage
  And the message should show image thumbnail
  And Mary should receive a push notification
```

### Integration Test Cases

```kotlin
// ChatRepositoryTest.kt
@Test
fun `sendMessage creates message document and updates chat lastMessage`() = runTest {
    // Given
    val chatId = "chat123"
    val messageContent = "Hello"

    // When
    val result = chatRepository.sendMessage(chatId, messageContent, MessageType.TEXT)

    // Then
    assertTrue(result is Result.Success)
    val messageId = (result as Result.Success).data.id

    // Verify message document
    val messageDoc = firestore
        .collection("chats/$chatId/messages")
        .document(messageId)
        .get().await()

    assertTrue(messageDoc.exists())
    assertEquals(messageContent, messageDoc.data?.get("content.text"))

    // Verify chat lastMessage updated
    val chatDoc = firestore.collection("chats").document(chatId).get().await()
    assertEquals(messageContent, chatDoc.data?.get("lastMessage.text"))
}

@Test
fun `messages are received in real-time via Flow`() = runTest {
    // Given
    val chatId = "chat123"
    val messages = mutableListOf<Message>()

    val job = launch {
        chatRepository.observeMessages(chatId).collect {
            messages.addAll(it)
        }
    }

    // When
    chatRepository.sendMessage(chatId, "Message 1", MessageType.TEXT)
    advanceTimeBy(500)
    chatRepository.sendMessage(chatId, "Message 2", MessageType.TEXT)
    advanceTimeBy(500)

    // Then
    assertEquals(2, messages.size)
    assertEquals("Message 1", messages[0].content.text)
    assertEquals("Message 2", messages[1].content.text)

    job.cancel()
}
```

### Unit Test Cases

```kotlin
// ChatViewModelTest.kt
class ChatViewModelTest {
    @Test
    fun `sendMessage updates UI state and clears input`() = runTest {
        // Given
        val sendMessage = mockk<SendMessageUseCase>()
        coEvery { sendMessage(any()) } returns Result.Success(mockMessage)

        val viewModel = ChatViewModel("chat123", sendMessage, observeMessages)

        // When
        viewModel.onMessageTextChanged("Hello")
        viewModel.onSendMessage()

        // Then
        assertEquals("", viewModel.messageText.value)
        coVerify { sendMessage(SendMessageRequest("chat123", "Hello")) }
    }

    @Test
    fun `typing indicator is shown when user types`() = runTest {
        // Given
        val viewModel = ChatViewModel("chat123", sendMessage, observeMessages, updateTyping)

        // When
        viewModel.onMessageTextChanged("H")
        advanceTimeBy(100)

        // Then
        coVerify { updateTyping("chat123", true) }
    }

    @Test
    fun `typing indicator is hidden after 3 seconds of inactivity`() = runTest {
        // Given
        val viewModel = ChatViewModel("chat123", sendMessage, observeMessages, updateTyping)

        // When
        viewModel.onMessageTextChanged("Hello")
        advanceTimeBy(3100)

        // Then
        coVerify { updateTyping("chat123", false) }
    }
}
```

### Implementation Steps

1. **Write failing E2E test**: Send text message
2. **Write failing integration test**: `sendMessage()` with real-time Flow
3. **Write failing unit tests**: `SendMessageUseCase`, `ChatViewModel`
4. **Implement**: Message sending flow
5. **Write failing integration test**: Real-time message updates
6. **Implement**: Firestore snapshot listeners with Flow
7. **Write failing unit tests**: Typing indicator logic
8. **Implement**: Typing indicator management
9. **Write failing E2E test**: Image upload and display
10. **Write tests and implement**: Media upload to Firebase Storage
11. **Write failing integration test**: Read receipts
12. **Implement**: Read receipt tracking
13. **Refactor**: Optimize real-time listeners, batch operations

---

## Feature 4: AI Task Extraction (NLP)

### User Story
> As a user, I want to create tasks by typing natural language so that I can quickly capture tasks without filling forms.

### Acceptance Criteria
- [x] System extracts task information from text
- [x] System identifies intent (create task, event, shopping item)
- [x] System suggests assignees based on context
- [x] System requires confirmation for low-confidence extractions
- [x] System learns from user corrections

### E2E Test Cases

```gherkin
Scenario: AI extracts task from chat message
  Given I am in "Family Chat"
  When I type "Remind John to buy milk tomorrow"
  And AI confidence is above 80%
  Then I should see a prompt "Create task: Buy milk?"
  And the suggested assignee should be "John"
  And the suggested due date should be tomorrow

  When I tap "Create Task"
  Then a new task "Buy milk" should be created
  And John should be assigned
  And due date should be tomorrow
```

### Integration Test Cases (Firebase Functions)

```typescript
// ai-service.integration.test.ts
describe('AI Task Extraction Integration', () => {
  test('extractTaskFromText creates AI task request in Firestore', async () => {
    // Given
    const request = {
      familyId: 'family123',
      text: 'Buy groceries tomorrow',
      sourceType: 'chat',
    };

    // When
    const result = await testEnv.call('extractTaskFromText', request, {
      auth: { uid: 'user123' }
    });

    // Then
    expect(result.success).toBe(true);
    expect(result.intent).toBe(AIIntent.CREATE_TASK);
    expect(result.extractedData?.title).toContain('groceries');

    // Verify AI task document created
    const aiTaskDoc = await testDb
      .collection('ai_tasks')
      .doc(result.aiTaskId)
      .get();

    expect(aiTaskDoc.exists).toBe(true);
  });
});
```

### Unit Test Cases (Functions)

```typescript
// ai-service.test.ts
describe('AIService', () => {
  test('parseTaskIntent identifies CREATE_TASK intent', () => {
    // Given
    const text = 'Remind me to call mom tomorrow';

    // When
    const intent = aiService.parseIntent(text);

    // Then
    expect(intent).toBe(AIIntent.CREATE_TASK);
  });

  test('extractTaskData extracts title and due date', () => {
    // Given
    const text = 'Buy groceries tomorrow at 5pm';

    // When
    const extracted = aiService.extractTaskData(text);

    // Then
    expect(extracted.title).toBe('Buy groceries');
    expect(extracted.dueDate).toBeDefined();
    expect(extracted.dueDate?.getHours()).toBe(17);
  });

  test('confidence score is low for ambiguous text', () => {
    // Given
    const text = 'Maybe sometime we should';

    // When
    const result = aiService.analyzeText(text);

    // Then
    expect(result.confidence).toBeLessThan(0.5);
    expect(result.requiresConfirmation).toBe(true);
  });
});
```

### Implementation Steps

1. **Write failing unit tests**: Intent classification
2. **Implement**: Regex-based intent parser
3. **Write failing unit tests**: Data extraction (title, date, assignee)
4. **Implement**: NLP extraction logic (chrono-node for dates)
5. **Write failing integration test**: Cloud Function with Firestore
6. **Implement**: `extractTaskFromText` callable function
7. **Write failing E2E test**: UI flow with AI suggestion
8. **Implement**: AI suggestion UI in Android
9. **Write failing unit tests**: Confidence scoring
10. **Implement**: Confidence calculation based on extraction completeness
11. **Write tests for future**: ML model integration placeholder
12. **Refactor**: Extract NLP service, add caching

---

## Feature 5: Weekly Summary & Notifications

### User Story
> As a family member, I want to receive weekly summaries and timely notifications so that I stay informed about family activities.

### Acceptance Criteria
- [x] Users receive FCM notifications for task assignments, messages, events
- [x] Users receive weekly summaries every Sunday
- [x] Weekly summaries include stats, highlights, and upcoming events
- [x] Users can customize notification preferences
- [x] System handles notification failures gracefully

### E2E Test Cases

```gherkin
Scenario: Receive task assignment notification
  Given I have notifications enabled for tasks
  And My FCM token is registered
  When "John" assigns me a task "Buy milk"
  Then I should receive a push notification
  And the notification title should be "New Task Assigned"
  And tapping it should open the task details

Scenario: View weekly summary
  Given Today is Sunday evening
  When The weekly summary cron job runs
  Then I should receive a notification "Your Family's Week in Review"

  When I tap the notification
  Then I should see family stats for the week
  And I should see completed tasks count
  And I should see upcoming events
```

### Integration Test Cases

```typescript
// notification-triggers.integration.test.ts
describe('Notification Triggers', () => {
  test('onNotificationCreated sends FCM message', async () => {
    // Given
    const userId = 'user123';
    const fcmToken = 'test-fcm-token';

    await testDb.collection('users').doc(userId).set({
      notificationPreferences: { fcmToken }
    });

    // When
    await testDb.collection('notifications').add({
      userId,
      type: 'task_assigned',
      title: 'New Task',
      body: 'You have a new task',
      data: { entityType: 'task', entityId: 'task123' }
    });

    // Wait for trigger
    await new Promise(resolve => setTimeout(resolve, 2000));

    // Then
    // Verify FCM send was called (mock verification)
    expect(mockMessaging.send).toHaveBeenCalledWith(
      expect.objectContaining({
        token: fcmToken,
        notification: expect.objectContaining({
          title: 'New Task'
        })
      })
    );
  });
});
```

### Unit Test Cases

```kotlin
// NotificationManagerTest.kt
class NotificationManagerTest {
    @Test
    fun `showNotification displays notification with correct data`() {
        // Given
        val notification = Notification(
            id = "notif123",
            type = NotificationType.TASK_ASSIGNED,
            title = "New Task",
            body = "Buy milk",
            data = NotificationData(entityType = "task", entityId = "task123")
        )

        val manager = NotificationManager(context)

        // When
        manager.showNotification(notification)

        // Then
        val shownNotification = shadowOf(notificationManager).getNotification(notification.id.hashCode())
        assertEquals("New Task", shownNotification.extras.getString(Notification.EXTRA_TITLE))
    }
}
```

### Implementation Steps

1. **Write failing integration test**: FCM notification trigger
2. **Implement**: `onNotificationCreated` Cloud Function trigger
3. **Write failing unit tests**: Android notification display
4. **Implement**: NotificationManager for Android
5. **Write failing integration test**: Weekly summary generation
6. **Implement**: `generateWeeklySummary` scheduled function
7. **Write failing unit tests**: Stats calculation
8. **Implement**: Stats aggregation logic
9. **Write failing E2E test**: Notification preferences
10. **Implement**: Preferences UI and logic
11. **Refactor**: Batch notifications, optimize queries

---

## Testing Infrastructure

### Android Testing Setup

```kotlin
// HiltTestRunner.kt
class HiltTestRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader?, name: String?, context: Context?): Application {
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}

// TestAppModule.kt
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AppModule::class]
)
object TestAppModule {
    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore =
        Firebase.firestore.also {
            it.useEmulator("localhost", 8080)
        }
}
```

### Firebase Functions Testing Setup

```typescript
// test-helpers.ts
import { initializeTestEnvironment } from '@firebase/rules-unit-testing';

export async function setupTestEnv() {
  const testEnv = await initializeTestEnvironment({
    projectId: 'familyhub-test',
    firestore: {
      host: 'localhost',
      port: 8080,
    },
  });

  return testEnv;
}
```

### CI/CD Test Execution

```yaml
# .github/workflows/test.yml
- name: Run Android Unit Tests
  run: ./gradlew testDebugUnitTest

- name: Run Android Integration Tests with Emulator
  run: |
    firebase emulators:start --only firestore,auth &
    ./gradlew connectedDebugAndroidTest

- name: Run Functions Unit Tests
  run: cd functions && npm run test:unit

- name: Run Functions Integration Tests
  run: |
    firebase emulators:start --only firestore,functions &
    cd functions && npm run test:integration
```

---

## Implementation Timeline

### Phase 1: Foundation (Week 1-2)
- Auth & Family Management
- Basic CI/CD setup

### Phase 2: Core Features (Week 3-5)
- Task Management
- Chat functionality

### Phase 3: Advanced Features (Week 6-7)
- AI Integration
- Notifications

### Phase 4: Polish & Deploy (Week 8)
- E2E testing
- Performance optimization
- Production deployment

---

## Success Metrics

### Code Quality
- **Test Coverage**: 100% (enforced by CI)
- **Code Review**: All PRs require approval
- **Linting**: Zero warnings in production

### Performance
- **App Launch**: < 2 seconds
- **Real-time Updates**: < 500ms latency
- **Cloud Functions**: < 1 second cold start

### Reliability
- **Crash-free Rate**: > 99.9%
- **Test Pass Rate**: 100% on main branch
- **Deployment Success**: Zero-downtime releases
