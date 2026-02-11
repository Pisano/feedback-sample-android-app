# Feedback Android SDK

Pisano Feedback Android SDK is an SDK that allows you to easily integrate user feedback collection into your Android applications.

> This repository is a **sample app repo**. The **SDK source code is not in this repo**.

This repository contains a **public sample app**:

- The sample app UI (screens, inputs, buttons) is **native** (XML Views + Kotlin).
- The survey/question UI is **rendered by the SDK** after calling `PisanoSDK.show(...)` (the app does not build survey screens itself).

## 📋 Table of Contents

- [Features](#features)
- [Requirements](#requirements)
- [Installation](#installation)
- [Run the sample app](#run-the-sample-app)
- [Local credentials (do not commit)](#local-credentials-do-not-commit)
- [Quick Start](#quick-start)
- [API Reference](#api-reference)
- [Usage Examples](#usage-examples)
- [Configuration](#configuration)
- [Build / Run](#build--run)
- [Tests](#tests)
- [Frequently Asked Questions](#frequently-asked-questions)
- [Troubleshooting](#troubleshooting)
- [Smoke tests](#smoke-tests)

## ✨ Features

- ✅ **SDK-hosted Survey UI**: Surveys/questions are rendered by the SDK (typically via embedded web content)
- ✅ **Native sample app UI**: Sample screens implemented with XML Views + Kotlin
- ✅ **Kotlin & Java SDK API**: The SDK can be used from Kotlin and Java
- ✅ **Flexible View Modes**: Full-screen and bottom sheet view options
- ✅ **Event Tracking**: Ability to track user activities
- ✅ **Health Check**: Ability to check SDK status
- ✅ **User Information Support**: Ability to send user data
- ✅ **Multi-Language Support**: Ability to display surveys in different languages
- ✅ **Custom Title**: Customizable title support
- ✅ **Code-based configuration**: Single source in `init()` (applicationId, accessKey, code, URLs); all API calls use this state
- ✅ **Display rate throttling**: Backend can send `display_rate` (0–100); SDK shows or skips deterministically and reports `DISPLAY_RATE_LIMITED` when skipped

## 📱 Requirements

From this sample project:

- **minSdk**: 21 (`app/build.gradle`)
- **targetSdk/compileSdk**: 31 (`app/build.gradle`)
- **Android Gradle Plugin**: 7.2.1 (root `build.gradle`)
- **Kotlin**: 1.6.10 (root `build.gradle`)

## 📦 Installation

### Installation with Gradle

#### 1) Project repositories

```gradle
allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
```

#### 2) SDK dependency (latest stable used by this sample)

```gradle
dependencies {
    implementation 'co.pisano:feedback:1.3.27'
}
```

#### 3) Permissions

Add the following permissions to your `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

## ▶️ Run the sample app

### Open in Android Studio (recommended)

- Open the repo root in Android Studio
- Select the `app` configuration
- Run on an emulator or a device

### Build from CLI (optional)

Debug APK:

```bash
./gradlew :app:assembleDebug
```

## 🔑 Local credentials (do not commit)

This repository **does not include any API keys**.

To run locally, you must provide your own `PISANO_*` credentials. This sample reads secrets at build-time and injects them into `BuildConfig`.

**Where values come from (priority order)**:

- Gradle `-P` properties
- Environment variables
- `pisano-config.plist` (optional, local-only; gitignored)
- `local.properties` (gitignored)

**Recommended local setup**:

1. Copy `local.properties.example` → `local.properties`
2. Fill the keys (**do not commit** `local.properties`)

**Optional (for quick local testing)**:

- Create `pisano-config.plist` in the repo root (gitignored) and fill the same keys.

If credentials are missing, the sample app will **not initialize the SDK** (it skips init) and shows a user-facing warning, so opening the widget may not work until you add your own credentials.

## 🚀 Quick Start

### 1) Configure credentials

See: [Local credentials (do not commit)](#local-credentials-do-not-commit)

**Required keys**:

- `PISANO_APP_ID`
- `PISANO_ACCESS_KEY`
- `PISANO_API_URL`
- `PISANO_FEEDBACK_URL`
- `PISANO_EVENT_URL` (optional)

### 2) Initialize the SDK (boot once)

In this sample, SDK init is done **once** at app startup:

- `PisanoSampleApplication` → `PisanoSdkBootstrapper.ensureInitialized(...)`

If config is missing, init is skipped and the sample shows a user-facing warning (and logs a safe message without secrets).

### 3) Show the survey

In this sample, `MainActivity` calls `PisanoSDK.show(...)` when you press **Get Feedback**.

The sample also supports a deep link to pass `flowId`:

- `pisano://show?flow_id=...`

## 📚 API Reference

### `PisanoSDK.init()`

Initializes the SDK. Call this once at app startup (recommended) or before calling `show()`.

**Parameters (via `PisanoSDKManager.Builder`)**:
- `setApplicationId(String)` (required)
- `setAccessKey(String)` (required)
- `setApiUrl(String)` (required)
- `setFeedbackUrl(String)` (required)
- `setEventUrl(String)` (optional)
- `setCloseStatusCallback(ActionListener)` (optional)

### `PisanoSDK.show()`

Shows the feedback/survey UI provided by the SDK.

**Parameters:**
- `viewMode: ViewMode` (default: `ViewMode.DEFAULT`)
- `title: Title?` (optional)
- `flowId: String?` (optional)
- `language: String?` (optional)
- `payload: HashMap<String, String>?` (optional)
- `pisanoCustomer: PisanoCustomer?` (optional)

### `PisanoSDK.healthCheck()`

Checks SDK status. This sample includes an instrumented smoke test that calls `healthCheck` when config is present.

**Parameters:**
- `flowId: String?` (optional)
- `language: String?` (optional)
- `payload: HashMap<String, String>?` (optional)
- `pisanoCustomer: PisanoCustomer?` (optional)
- `isHealthCheckSuccessful: (Boolean) -> Unit`

### `PisanoActions`

The `PisanoActions` enum is returned by SDK callbacks to indicate the result of an operation.

Common values:
- `INIT_SUCCESS`, `INIT_FAILED`
- `OPENED`, `CLOSED`, `OUTSIDE`
- `SEND_FEEDBACK`
- `DISPLAY_ONCE`, `PREVENT_MULTIPLE_FEEDBACK`
- `CHANNEL_QUOTA_EXCEEDED`

## 💡 Usage Examples

### 1) Initializing the SDK

You can initialize at app startup (recommended):

```kotlin
import android.app.Application
import android.util.Log
import co.pisano.feedback.data.helper.ActionListener
import co.pisano.feedback.data.helper.PisanoActions
import co.pisano.feedback.managers.PisanoSDK
import co.pisano.feedback.managers.PisanoSDKManager

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        val manager = PisanoSDKManager.Builder(this)
            .setApplicationId("YOUR_APP_ID")
            .setAccessKey("YOUR_ACCESS_KEY")
            .setCode("YOUR_CODE")
            .setApiUrl("https://api.pisano.co")
            .setFeedbackUrl("https://web.pisano.co/web_feedback")
            .setEventUrl("https://track.pisano.co/track")
            .setCloseStatusCallback(object : ActionListener {
                override fun action(action: PisanoActions) {
                    when (action) {
                        PisanoActions.INIT_SUCCESS -> {
                            Log.d("Pisano", "SDK initialized successfully")
                        }
                        PisanoActions.INIT_FAILED -> {
                            Log.e("Pisano", "SDK initialization failed")
                        }
                        PisanoActions.SEND_FEEDBACK -> {
                            Log.d("Pisano", "Feedback sent")
                        }
                        PisanoActions.CLOSED -> {
                            Log.d("Pisano", "Survey closed")
                        }
                        else -> {}
                    }
                }
            })
            .build()
        
        PisanoSDK.init(manager)
    }
}
```

#### Java

```java
import android.app.Application;
import android.util.Log;

import co.pisano.feedback.data.helper.ActionListener;
import co.pisano.feedback.data.helper.PisanoActions;
import co.pisano.feedback.managers.PisanoSDK;
import co.pisano.feedback.managers.PisanoSDKManager;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        PisanoSDKManager manager = new PisanoSDKManager.Builder(this)
            .setApplicationId("YOUR_APP_ID")
            .setAccessKey("YOUR_ACCESS_KEY")
            .setCode("YOUR_CODE")
            .setApiUrl("https://api.pisano.co")
            .setFeedbackUrl("https://web.pisano.co/web_feedback")
            .setEventUrl("https://track.pisano.co/track")
            .setCloseStatusCallback(new ActionListener() {
                @Override
                public void action(PisanoActions action) {
                    Log.d("Pisano", "action=" + action);
                }
            })
            .build();

        PisanoSDK.INSTANCE.init(manager);
    }
}
```

### 2) Showing the Feedback Widget

Basic usage:

```kotlin
import co.pisano.feedback.managers.PisanoSDK

PisanoSDK.show()
```

Advanced usage:

```kotlin
import android.graphics.Color
import android.graphics.Typeface

val customer = PisanoCustomer(
    name = "John Doe",
    email = "john@example.com",
    phoneNumber = "+1234567890",
    externalId = "CRM-12345",
    customAttributes = hashMapOf(
        "language" to "en",
        "city" to "New York"
    )
)

val title = Title(
    text = "We Value Your Feedback",
    textSize = 20f,
    textColor = Color.BLACK,
    textStyle = Typeface.BOLD,
    backgroundColor = Color.WHITE
)

val payload = hashMapOf(
    "source" to "app",
    "screen" to "home"
)

PisanoSDK.show(
    viewMode = ViewMode.BOTTOM_SHEET,
    title = title,
    language = "en",
    payload = payload,
    pisanoCustomer = customer
)
```

## 📚 API Reference

### `PisanoSDK.init()`

Initializes the SDK. This method must be called either at application startup (usually in the `Application` class) or before calling the `show()` method.

**Parameters:**
- `pisanoSDKManager: PisanoSDKManager` - SDK configuration manager (required)
  - `applicationId: String` - Your application's unique ID
  - `accessKey: String` - Your API access key
  - `code: String` - Your widget code (required)
  - `apiUrl: String` - API endpoint URL
  - `feedbackUrl: String` - Feedback widget URL
  - `eventUrl: String?` - Event tracking URL (optional; required if you use `PisanoSDK.track`)
  - `isDebug: Boolean` - When `true`, SDK logs (e.g. display_rate skip, fetch success/fail) are printed to Logcat with tag `PISANO_SDK` (optional, default `false`)
  - `closeStatusActionListener: ActionListener?` - Initialization result callback (optional)

**Example:**

```kotlin
val manager = PisanoSDKManager.Builder(context)
    .setApplicationId("app-123")
    .setAccessKey("key-456")
    .setCode("code-789")
    .setApiUrl("https://api.pisano.co")
    .setFeedbackUrl("https://web.pisano.co/web_feedback")
    .setEventUrl("https://track.pisano.co/track")
    .setDebug(true)
    .setCloseStatusCallback(object : ActionListener {
        override fun action(action: PisanoActions) {
            when (action) {
                PisanoActions.INIT_SUCCESS -> {
                    println("SDK initialized successfully")
                }
                PisanoActions.INIT_FAILED -> {
                    println("SDK initialization failed")
                }
                else -> {}
            }
        }
    })
    .build()

PisanoSDK.init(manager)
```

### `PisanoSDK.show()`

Displays the feedback widget.

**Parameters:**
- `viewMode: ViewMode` - View mode (default: `ViewMode.DEFAULT`)
  - `ViewMode.DEFAULT`: Full-screen overlay
  - `ViewMode.BOTTOM_SHEET`: Bottom sheet mode
- `title: Title?` - Custom title (optional)
- `language: String?` - Language code (e.g., "en", "tr") (optional)
- `payload: HashMap<String, String>?` - Extra data (optional)
- `pisanoCustomer: PisanoCustomer?` - User information (optional)
- `code: String?` - **Optional.** Widget code for this call. If **not** provided, the SDK uses the **code from `init()` (boot)**. If provided, this code is used for this request (e.g. to show a different widget than the one configured at init).

**User Information Fields:**
- `name: String?` - User name
- `email: String?` - Email address
- `phoneNumber: String?` - Phone number
- `externalId: String?` - External system ID (CRM, etc.)
- `customAttributes: HashMap<String, Any>?` - Custom attributes

**Example:**

```kotlin
val customer = PisanoCustomer(
    name = "John Doe",
    email = "john@example.com",
    externalId = "USER-123"
)

val title = Title(
    text = "We Value Your Feedback",
    textSize = 20f,
    textColor = Color.BLACK
)

PisanoSDK.show(
    viewMode = ViewMode.BOTTOM_SHEET,
    title = title,
    language = "en",
    payload = null,
    pisanoCustomer = customer
)
```

**Using init (boot) code (default):**  
If you do not pass `code`, the SDK uses the code set in `PisanoSDK.init(...)`.

```kotlin
// Uses the code from init (boot)
PisanoSDK.show(viewMode = ViewMode.BOTTOM_SHEET, language = "en")
```

**Overriding code for a single call:**  
You can pass a different `code` for this call only; the SDK will use it for this request (e.g. to show another widget).

```kotlin
// Uses "PSN-other-widget" for this call only; init code is unchanged
PisanoSDK.show(
    viewMode = ViewMode.BOTTOM_SHEET,
    language = "tr",
    code = "PSN-other-widget"
)
```

### `PisanoSDK.track()`

Tracks user activities and automatically triggers the feedback widget if needed.

**Parameters:**
- `event: String` - Event name (required)
- `payload: HashMap<String, String>?` - Event data (optional)
- `pisanoCustomer: PisanoCustomer?` - User information (optional)
- `languageCode: String?` - Language code (optional)

**Example:**

```kotlin
val payload = hashMapOf(
    "product_id" to "PROD-123",
    "price" to "99.99",
    "currency" to "USD"
)

val customer = PisanoCustomer(
    externalId = "USER-456",
    email = "user@example.com"
)

PisanoSDK.track(
    event = "purchase_completed",
    payload = payload,
    pisanoCustomer = customer,
    languageCode = "en"
)
```

### `PisanoSDK.clearAction()`

Clears the SDK's local data (Shared Preferences). This is useful when logging out a user to ensure fresh state for the next user.

**Example:**

```kotlin
PisanoSDK.clearAction()
```

### `PisanoSDK.healthCheck()`

Checks the SDK status. It is recommended to use this before displaying the widget.

**Parameters:**
- `language: String?` - Language code (optional)
- `pisanoCustomer: PisanoCustomer?` - User information (optional)
- `payload: HashMap<String, String>?` - Extra data (optional)
- `code: String?` - **Optional.** Widget code for this check. If **not** provided, the SDK uses the **code from `init()` (boot)**. If provided, this code is used for this request.
- `isHealthCheckSuccessful: (Boolean) -> Unit` - Health check result (true: successful, false: failed)

**Example (uses init code):**

```kotlin
PisanoSDK.healthCheck(
    pisanoCustomer = PisanoCustomer(externalId = "USER-789")
) { isHealthy ->
    if (isHealthy) {
        PisanoSDK.show()
    } else {
        Log.e("Pisano", "SDK health check failed")
    }
}
```

**With code override (check a different widget):**

```kotlin
PisanoSDK.healthCheck(
    pisanoCustomer = PisanoCustomer(externalId = "USER-789"),
    code = "PSN-other-widget"
) { isHealthy ->
    if (isHealthy) {
        PisanoSDK.show(code = "PSN-other-widget")
    }
}
```

## 💡 Usage Examples

### Kotlin Usage

#### Usage in Activity

```kotlin
import androidx.appcompat.app.AppCompatActivity
import co.pisano.feedback.managers.PisanoSDK
import co.pisano.feedback.data.helper.ViewMode
import co.pisano.feedback.data.model.PisanoCustomer
import co.pisano.feedback.data.model.Title
import co.pisano.feedback.managers.PisanoSDK

class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        findViewById<Button>(R.id.showFeedbackButton).setOnClickListener {
            PisanoSDK.show(
                viewMode = ViewMode.BOTTOM_SHEET,
                language = "en",
                pisanoCustomer = PisanoCustomer(externalId = "USER-123")
            )
        }
        
        findViewById<Button>(R.id.trackEventButton).setOnClickListener {
            PisanoSDK.track(
                event = "button_clicked",
                payload = hashMapOf("button_name" to "feedback_button")
            )
        }
    }
}
```

#### Java

```java
import co.pisano.feedback.data.helper.ViewMode;
import co.pisano.feedback.data.model.PisanoCustomer;
import co.pisano.feedback.managers.PisanoSDK;

public class MainActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Button showFeedbackButton = findViewById(R.id.showFeedbackButton);
        showFeedbackButton.setOnClickListener(v -> {
            PisanoCustomer customer = new PisanoCustomer(
                null,           // name
                "USER-123",     // externalId
                null,           // email
                null,           // phoneNumber
                null            // customAttributes
            );
            
            PisanoSDK.INSTANCE.show(
                ViewMode.BOTTOM_SHEET,
                null,
                null,
                "en",
                null,
                customer
            );
        });
        
        Button trackEventButton = findViewById(R.id.trackEventButton);
        trackEventButton.setOnClickListener(v -> {
            HashMap<String, String> payload = new HashMap<>();
            payload.put("button_name", "feedback_button");
            
            PisanoSDK.INSTANCE.track(
                "button_clicked",
                payload,
                null,
                null
            );
        });
    }
}
```

### Listening to Events with ActionListener

The SDK also notifies about widget close status through the `ActionListener` callback.

```kotlin
val manager = PisanoSDKManager.Builder(context)
    .setApplicationId("YOUR_APP_ID")
    .setAccessKey("YOUR_ACCESS_KEY")
    .setApiUrl("https://api.pisano.co")
    .setFeedbackUrl("https://web.pisano.co/web_feedback")
    .setCloseStatusCallback(object : ActionListener {
        override fun action(action: PisanoActions) {
            when (action) {
                PisanoActions.CLOSED -> {
                    Log.d("Pisano", "Widget closed")
                }
                PisanoActions.SEND_FEEDBACK -> {
                    Log.d("Pisano", "Feedback sent")
                }
                PisanoActions.OUTSIDE -> {
                    Log.d("Pisano", "Closed by clicking outside")
                }
                PisanoActions.DISPLAY_ONCE -> {
                    Log.d("Pisano", "Already shown before")
                }
                PisanoActions.DISPLAY_RATE_LIMITED -> {
                    Log.d("Pisano", "Skipped due to display_rate throttling")
                }
                PisanoActions.CHANNEL_PASSIVE -> {
                    Log.d("Pisano", "Survey is in passive state")
                }
                else -> {}
            }
        }
    })
    .build()

PisanoSDK.init(manager)
```

## ⚙️ Configuration

### ViewMode

```kotlin
import co.pisano.feedback.data.helper.ViewMode
import co.pisano.feedback.managers.PisanoSDK

// Full-screen overlay (default)
PisanoSDK.show(viewMode = ViewMode.DEFAULT)

// Bottom sheet
PisanoSDK.show(viewMode = ViewMode.BOTTOM_SHEET)
```

### Display Rate (`display_rate`)

The backend can return `display_rate` (0–100) in `/detail` and/or `/trigger`. The SDK uses this value to **deterministically** throttle `show()` attempts:

- `100`: always show
- `0`: never show
- Example `50`: **show, skip, show, skip, ...** (1st call shows, 2nd skips, 3rd shows, ...)

This decision is **code-based** (state is stored per widget `code`). If the `code` changes, throttling starts fresh for the new `code`. If `display_rate` changes for the same `code`, the internal counter is reset.

If a `show()` attempt is skipped due to `display_rate`, the SDK will not open UI and will emit `PisanoActions.DISPLAY_RATE_LIMITED` via the callback.

### Custom Title

```kotlin
import android.graphics.Color
import android.graphics.Typeface
import co.pisano.feedback.data.model.Title
import co.pisano.feedback.managers.PisanoSDK

val title = Title(
  text = "WE VALUE YOUR FEEDBACK",
  textSize = 18f,
  textColor = Color.BLUE,
  textStyle = Typeface.BOLD,
  backgroundColor = Color.WHITE
)

PisanoSDK.show(title = title)
```

### User Information

```kotlin
import co.pisano.feedback.data.model.PisanoCustomer
import co.pisano.feedback.managers.PisanoSDK

val customer = PisanoCustomer(
  name = "John Doe",
  email = "john@example.com",
  phoneNumber = "+1234567890",
  externalId = "CRM-12345",
  customAttributes = hashMapOf(
    "language" to "en",
    "city" to "New York"
  )
)

PisanoSDK.show(pisanoCustomer = customer)
```

**Valid user keys (customer attributes):**
- `customAttributes`: `HashMap<String, Any>` (your custom keys and values)

## Build / Run

```bash
./gradlew :app:assembleDebug
```

## Tests

Unit tests:

```bash
./gradlew test
```

Instrumented tests (requires emulator/device):

```bash
./gradlew connectedAndroidTest
```

`PisanoSdkSmokeTest` auto-skips if `PISANO_*` config is missing (so CI doesn’t fail).

### Manual smoke checklist

- Launch app (SDK init logs)
- Tap **Get Feedback** → survey opens
- Fill → submit
- Close → verify callback/log status

## ❓ Frequently Asked Questions

### Does this sample build the survey/question UI?

No. The sample app UI is native, but the survey/question UI is rendered by the SDK after `PisanoSDK.show(...)`.

### Where is SDK initialization done in this repo?

In `PisanoSampleApplication` via `PisanoSdkBootstrapper.ensureInitialized(...)` (boot once).

## 🔧 Troubleshooting

### Build fails: “SDK location not found”

Set `sdk.dir=...` in `local.properties`, or export `ANDROID_HOME` / `ANDROID_SDK_ROOT`.

### Survey doesn’t open

- Verify `PISANO_*` config values
- Ensure network access

### Runtime crash: `NoClassDefFoundError` (Retrofit / Gson)

If you integrate the SDK into your own app and see errors like:

- `retrofit2/Retrofit$Builder`
- `com/google/gson/GsonBuilder`

Ensure your app includes the required runtime deps (this sample does):

```gradle
implementation 'com.google.code.gson:gson:2.10.1'
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
implementation 'com.squareup.okhttp3:okhttp:4.9.3'
```

### Java usage error

This feature ensures that the widget is shown to the user only once (or once per delay period). The backend sends `display_once` (boolean) and optionally `display_once_delay_period`. The SDK stores "shown" locally and, if a delay period is set, will not show again until that period has passed. **`display_once_delay_period` is in hours** (e.g. `24` = once per day). This matches iOS and fixes "once per day" behaviour on Android.

### Why is `code` required in init?

The SDK uses `code` (with `applicationId`, `accessKey`, `platform_id`, `bundle_id`) in every `/detail` and `/trigger` request. It is stored once at `PisanoSDK.init(...)` (boot) and reused for all subsequent calls. **On Android, `bundle_id` is the application package name** (`context.packageName`).  
**In `show()` and `healthCheck()`:** you can optionally pass `code`. If you **do not** pass it, the **init (boot) code** is used. If you pass it, that code is used for that call only (e.g. to show or check another widget).

### Why does `show()` sometimes not open the survey?

The backend can return `display_rate` (0–100). The SDK then decides deterministically whether to show or skip (e.g. 50% → show, skip, show, skip…). When skipped, the UI does not open and the callback receives `PisanoActions.DISPLAY_RATE_LIMITED`. Other cases: `DISPLAY_ONCE`, `CHANNEL_PASSIVE`, or quota/trigger rules.

```java
PisanoSDK.INSTANCE.show(...);
```

### ProGuard / R8 rules

If you need custom rules:

```proguard
# Pisano SDK
-keep class co.pisano.feedback.** { *; }
-dontwarn co.pisano.feedback.**
```

## ✅ Smoke tests

This sample includes an instrumented smoke test: `PisanoSdkSmokeTest`.

1. Make sure `applicationId`, `accessKey`, `code`, `apiUrl`, and `feedbackUrl` are set in `PisanoSDKManager.Builder(...)` (required); set `eventUrl` if you use `track`
2. Check that API URLs are correct and accessible
3. Check that internet permissions are in the `AndroidManifest.xml` file

- SDK init (boot once)
- `PisanoSDK.healthCheck(...)`

If credentials are missing, the test will **skip** (so CI won’t fail).

Run on an emulator/device:

```bash
./gradlew :app:connectedAndroidTest
```

### Widget won't display

1. Make sure the `PisanoSDK.init()` method completed successfully (with valid `applicationId`, `accessKey`, `code`)
2. Perform a health check to verify SDK status
3. Check internet connection
4. If the callback receives `DISPLAY_RATE_LIMITED`, the backend's `display_rate` caused this call to be skipped (deterministic throttling)

### Java usage error

When using Java, use `PisanoSDK.INSTANCE`:

```java
PisanoSDK.INSTANCE.show(...)
```

### Bottom sheet not working

Bottom sheet feature depends on Material Design Components. If the bottom sheet is not working, you can use full-screen mode (`ViewMode.DEFAULT`).

### MinifyEnabled error

Check your ProGuard rules. The `consumer-rules.pro` file is automatically included, but you may need to add custom rules.
