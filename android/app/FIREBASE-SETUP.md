# Firebase Setup for FamilyHub

## Current Status

The repository includes a **dummy `google-services.json`** file for CI/CD builds and demo purposes.

⚠️ **This dummy configuration allows the app to build but won't connect to a real Firebase backend.**

## For Demo/Testing

The dummy configuration is sufficient for:
- Building installable APKs
- Testing the UI and navigation
- Showcasing the app's features
- CI/CD pipeline builds

The app will build and install successfully, but Firebase features (auth, database, messaging) won't work.

## For Production Use

To connect to a real Firebase project:

### 1. Create a Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Add project"
3. Enter project name: "FamilyHub" (or your choice)
4. Follow the setup wizard

### 2. Add Android App to Firebase

1. In Firebase Console, click "Add app" → "Android"
2. Enter package name: `com.familyhub.app`
3. Download the `google-services.json` file
4. Replace the dummy file at `android/app/google-services.json`

### 3. Enable Firebase Services

In the Firebase Console, enable:

- **Authentication**
  - Enable Email/Password sign-in
  - Enable Google sign-in (optional)

- **Cloud Firestore**
  - Create database in production mode
  - Deploy security rules from `firestore/firestore.rules`
  - Deploy indexes from `firestore/firestore.indexes.json`

- **Cloud Messaging** (FCM)
  - Automatically enabled when you add the Android app

- **Cloud Storage**
  - Create a storage bucket
  - Deploy security rules from `storage/storage.rules`

### 4. Update App Configuration

If you changed the package name, also update:
- `android/app/build.gradle.kts` → `applicationId`
- `android/app/src/main/AndroidManifest.xml` → package attribute

### 5. Build and Test

```bash
cd android
./gradlew clean
./gradlew assembleDebug
```

Install and test Firebase features:
- User authentication
- Real-time database sync
- Push notifications

## Security

⚠️ **IMPORTANT**: Never commit your real `google-services.json` to public repositories!

Add to `.gitignore`:
```
# Real Firebase config (keep dummy for CI)
# android/app/google-services.json
```

Or use different configurations for different build variants.

## Multiple Environments

For production apps, consider:

1. **Build Flavors**
   ```kotlin
   android {
       flavorDimensions += "environment"
       productFlavors {
           create("dev") {
               dimension = "environment"
               applicationIdSuffix = ".dev"
           }
           create("prod") {
               dimension = "environment"
           }
       }
   }
   ```

2. **Different Config Files**
   - `app/src/dev/google-services.json` (dev Firebase)
   - `app/src/prod/google-services.json` (prod Firebase)

## Troubleshooting

### Build fails with "google-services.json missing"
- Ensure the file exists at `android/app/google-services.json`
- Check the JSON is valid

### App crashes on Firebase calls
- Check you're using a real Firebase configuration
- Verify Firebase services are enabled in console
- Check package name matches exactly

### Authentication fails
- Enable authentication methods in Firebase Console
- Add SHA-1 fingerprint for Google Sign-In
- Check API keys are valid

## Resources

- [Firebase Android Setup](https://firebase.google.com/docs/android/setup)
- [Firebase Authentication Docs](https://firebase.google.com/docs/auth)
- [Cloud Firestore Docs](https://firebase.google.com/docs/firestore)
- [Firebase Cloud Messaging](https://firebase.google.com/docs/cloud-messaging)
