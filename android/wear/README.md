# FamilyHub Wear OS Module

Comprehensive Wear OS companion app for FamilyHub, providing family organization features on your wrist.

## Features

### 1. Main Screens

#### Home Screen
- Quick access hub with feature chips
- Navigation to Tasks, Messages, Shopping, and Health

#### Tasks Screen
- View all family tasks
- Toggle task completion with ToggleChips
- Priority indicators (High/Medium/Low)
- Voice input for adding new tasks
- Interactive task list optimized for round screens

#### Messages Screen
- View recent family messages
- Sender and timestamp information
- Quick reply button
- Unread message indicators

#### Shopping Screen
- Interactive shopping list
- Check/uncheck items
- Quantity display
- Completion progress counter
- Optimized for quick grocery shopping

#### Health Screen
- Steps counter with goal progress
- Heart rate monitoring
- Quick activity tracking (Walk, Run, Workout)
- Activity cards with visual indicators

### 2. Tiles

#### Tasks Tile
- Shows top 3 urgent tasks
- Priority color coding
- Click to open full task list
- Updates every 5 minutes

#### Shopping Tile
- Shopping list preview
- Completion progress indicator
- Quick glance at items
- Direct launch to full list

### 3. Complications

#### Tasks Complication
- Display active task count
- Supports SHORT_TEXT, LONG_TEXT, and RANGED_VALUE
- Tap to open task list
- Real-time updates

#### Messages Complication
- Shows unread message count
- Multiple display formats
- Tap to open messages
- Badge notifications

### 4. Health Services

#### Passive Monitoring
- Background step counting
- Heart rate tracking
- Calorie tracking
- Low battery impact

#### Active Workouts
- Walk tracking
- Run tracking
- Generic workout tracking
- Auto pause/resume
- Real-time metrics

### 5. Phone-Watch Sync

#### Data Layer Integration
- Real-time task updates from phone
- Shopping list synchronization
- Message notifications
- Bi-directional communication

#### Sync Features
- Automatic sync when phone app updates
- Manual sync on demand
- Task completion sync to phone
- Shopping item check sync to phone

## Architecture

### Technology Stack
- **Language**: Kotlin 1.9.20
- **UI Framework**: Jetpack Compose for Wear OS 1.3.0
- **Navigation**: Wear Compose Navigation
- **Health**: Health Services Client 1.0.0-beta03
- **Sync**: Play Services Wearable 18.1.0
- **DI**: Hilt

### Project Structure
```
wear/
├── presentation/
│   ├── MainActivity.kt           # Main entry point
│   ├── FamilyHubWearApplication.kt
│   ├── screens/
│   │   ├── HomeScreen.kt
│   │   ├── TasksScreen.kt
│   │   ├── MessagesScreen.kt
│   │   ├── ShoppingListScreen.kt
│   │   └── HealthScreen.kt
│   └── theme/
│       ├── Theme.kt
│       ├── Color.kt
│       └── Typography.kt
├── tiles/
│   ├── TasksTileService.kt      # Tasks tile provider
│   └── ShoppingTileService.kt   # Shopping tile provider
├── complications/
│   ├── TasksComplicationService.kt
│   └── MessagesComplicationService.kt
├── health/
│   └── HealthTrackingService.kt # Health monitoring
└── sync/
    └── DataLayerListenerService.kt # Phone-watch sync
```

## Design Patterns

### UI Patterns
- **ScalingLazyColumn**: Optimized scrolling for round screens
- **SwipeDismissableNavHost**: Native Wear OS navigation
- **ToggleChip**: Interactive toggles for tasks and shopping
- **Card**: Information display containers
- **Chip**: Action buttons

### Architectural Patterns
- **MVVM**: Model-View-ViewModel (screens will use ViewModels in production)
- **Repository Pattern**: Data access abstraction
- **Service-Oriented**: Background services for health and sync

## Setup Requirements

### Permissions
The app requires the following permissions:
- `BODY_SENSORS` - Heart rate monitoring
- `ACTIVITY_RECOGNITION` - Step counting and activity tracking
- `RECORD_AUDIO` - Voice input for tasks and messages
- `WAKE_LOCK` - Keep device awake during workouts

### Dependencies
All dependencies are managed in `build.gradle.kts`:
- Wear Compose Material
- Wear Tiles
- Wear Complications
- Health Services
- Play Services Wearable

## Development

### Running the App
```bash
# Connect Wear OS device or emulator
adb devices

# Install and run
./gradlew :wear:installDebug
```

### Testing on Emulator
1. Create Wear OS emulator in Android Studio
2. Pair with phone emulator using Wear OS app
3. Install and test phone-watch sync

### Building Release
```bash
./gradlew :wear:assembleRelease
```

## Future Enhancements

### Planned Features
1. **Real Repository Integration**
   - Connect to Firebase Firestore
   - Real-time data updates
   - Offline support with local caching

2. **Voice Input Implementation**
   - Speech recognition for tasks
   - Voice replies to messages
   - Hands-free shopping list additions

3. **Ongoing Activities**
   - Persistent notifications for active tasks
   - Quick task completion from notification
   - Workout session notifications

4. **Advanced Health Features**
   - Sleep tracking
   - Stress monitoring
   - Family health challenges
   - Activity sharing with family

5. **Smart Notifications**
   - Contextual task reminders
   - Location-based shopping reminders
   - Time-based family event alerts

6. **Watch Face**
   - Custom FamilyHub watch face
   - Multiple complication slots
   - Themed designs

## Performance Optimizations

### Battery Efficiency
- Passive monitoring uses low-power APIs
- Foreground service only when needed
- Efficient data sync strategies
- Minimal network usage

### UI Performance
- Lazy loading with ScalingLazyColumn
- Efficient recomposition with remember
- Optimized image resources
- Minimal overdraw

## Accessibility

### Features
- Content descriptions for all interactive elements
- Support for screen readers
- High contrast mode compatible
- Large touch targets (min 48dp)

## Testing Strategy

### Unit Tests (Planned)
- ViewModel logic
- Data transformation
- Health calculation utils

### Integration Tests (Planned)
- Tile update flow
- Complication data flow
- Sync operations

### UI Tests (Planned)
- Screen navigation
- Task interaction
- Shopping list operations

## Documentation

### Code Documentation
- KDoc comments on all public APIs
- Inline comments for complex logic
- README files for each module

### User Documentation
- In-app tutorial (planned)
- Help screens (planned)
- Quick start guide (planned)

## Contributing

When contributing to the Wear OS module:
1. Follow Kotlin coding conventions
2. Use Jetpack Compose best practices
3. Test on both round and square watch faces
4. Ensure battery efficiency
5. Document all public APIs

## License

Part of the FamilyHub Platform project.

## Version History

### v1.0.0 (Current)
- Initial Wear OS implementation
- 5 main screens (Home, Tasks, Messages, Shopping, Health)
- 2 tiles (Tasks, Shopping)
- 2 complications (Tasks, Messages)
- Health tracking service
- Phone-watch data sync
- Comprehensive UI with Wear Compose Material

## Support

For issues or questions:
- GitHub Issues: [familyhub/issues](https://github.com/kluth/family-central/issues)
- Documentation: [docs/wear-os.md](../docs/wear-os.md)
