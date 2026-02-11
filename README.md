# Feedback Android SDK

Pisano Feedback Android SDK is an SDK that allows you to easily integrate user feedback collection into your Android applications. With this SDK, you can collect surveys and feedback from your users and improve the user experience.

## 📋 Table of Contents

- [Features](#features)
- [Requirements](#requirements)
- [Installation](#installation)
- [Quick Start](#quick-start)
- [API Reference](#api-reference)
- [Usage Examples](#usage-examples)
- [Configuration](#configuration)
- [Frequently Asked Questions](#frequently-asked-questions)
- [Troubleshooting](#troubleshooting)

## ✨ Features

- ✅ **Web-Based Feedback Forms**: Modern and flexible web-based form support
- ✅ **Native Android Integration**: Fully native Android SDK
- ✅ **Kotlin & Java Compatibility**: Can be used in both Kotlin and Java projects
- ✅ **Flexible View Modes**: Full-screen and bottom sheet view options
- ✅ **Event Tracking**: Ability to track user activities
- ✅ **Health Check**: Ability to check SDK status
- ✅ **User Information Support**: Ability to send user data
- ✅ **Multi-Language Support**: Ability to display surveys in different languages
- ✅ **Custom Title**: Customizable title support
- ✅ **Code-based configuration**: Single source in `init()` (applicationId, accessKey, code, URLs); all API calls use this state
- ✅ **Display rate throttling**: Backend can send `display_rate` (0–100); SDK shows or skips deterministically and reports `DISPLAY_RATE_LIMITED` when skipped

## 📱 Requirements

- Android API Level 16 (Android 4.1) or higher
- Gradle 4.1.3 or higher
- Kotlin 1.4.32 or higher
- AndroidX libraries

## 📦 Installation

### Installation with Gradle

#### 1. Project Level `build.gradle`

```gradle
allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
```

#### 2. Module Level `build.gradle`

```gradle
dependencies {
    implementation 'co.pisano:feedback:[VERSION]'
}
```

#### 3. Internet Permissions

Add the following permissions to your `AndroidManifest.xml` file:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

## 🚀 Quick Start

### 1. Initializing the SDK

You must initialize the SDK before using it. The SDK initialization should be done either at application startup (usually in the `Application` class) or before calling the `show()` method.

#### Kotlin

```kotlin
import co.pisano.feedback.managers.PisanoSDK
import co.pisano.feedback.managers.PisanoSDKManager
import co.pisano.feedback.data.helper.ActionListener
import co.pisano.feedback.data.helper.PisanoActions

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
import co.pisano.feedback.managers.PisanoSDK;
import co.pisano.feedback.managers.PisanoSDKManager;
import co.pisano.feedback.data.helper.ActionListener;
import co.pisano.feedback.data.helper.PisanoActions;

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
                    switch (action) {
                        case INIT_SUCCESS:
                            Log.d("Pisano", "SDK initialized successfully");
                            break;
                        case INIT_FAILED:
                            Log.e("Pisano", "SDK initialization failed");
                            break;
                    }
                }
            })
            .build();
        
        PisanoSDK.INSTANCE.init(manager);
    }
}
```

### 2. Showing the Feedback Widget

#### Basic Usage

```kotlin
PisanoSDK.show()
```

#### Advanced Usage

```kotlin
import co.pisano.feedback.data.helper.ViewMode
import co.pisano.feedback.data.model.Title
import co.pisano.feedback.data.model.PisanoCustomer
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

#### Usage in Fragment

```kotlin
class HomeFragment : Fragment() {
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        view.findViewById<Button>(R.id.feedbackButton).setOnClickListener {
            PisanoSDK.show(
                viewMode = ViewMode.DEFAULT,
                language = "tr"
            )
        }
    }
}
```

### Java Usage

```java
import co.pisano.feedback.managers.PisanoSDK;
import co.pisano.feedback.data.helper.ViewMode;
import co.pisano.feedback.data.model.PisanoCustomer;
import java.util.HashMap;

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

You can select the view mode:

```kotlin
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

You can customize the widget title:

```kotlin
import android.graphics.Color
import android.graphics.Typeface

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

You can provide a personalized experience by sending user information:

```kotlin
val customer = PisanoCustomer(
    name = "John Doe",
    email = "john@example.com",
    phoneNumber = "+1234567890",
    externalId = "CRM-12345",
    customAttributes = hashMapOf(
        "language" to "en",
        "city" to "New York",
        "gender" to "male",
        "birthday" to "1990-01-01"
    )
)

PisanoSDK.show(pisanoCustomer = customer)
```

**Valid user keys:**
- `name`: User name
- `email`: Email address
- `phoneNumber`: Phone number
- `externalId`: External system ID
- `customAttributes`: Custom attributes (HashMap)

## ❓ Frequently Asked Questions

### When should I initialize the SDK?

You must initialize the SDK either at application startup (in the `Application` class) or before calling the `show()` method.

### Should I use health check?

Health check allows you to check the SDK status before displaying the widget. It is recommended to use it before showing the widget on important screens.

### How can I display the widget in different languages?

You can display the widget in different languages using the `language` parameter:

```kotlin
PisanoSDK.show(language = "en") // English
PisanoSDK.show(language = "tr") // Turkish
```

### What is the display once feature?

This feature ensures that the widget is shown to the user only once (or once per delay period). The backend sends `display_once` (boolean) and optionally `display_once_delay_period`. The SDK stores "shown" locally and, if a delay period is set, will not show again until that period has passed. **`display_once_delay_period` is in hours** (e.g. `24` = once per day). This matches iOS and fixes "once per day" behaviour on Android.

### Why is `code` required in init?

The SDK uses `code` (with `applicationId`, `accessKey`, `platform_id`, `bundle_id`) in every `/detail` and `/trigger` request. It is stored once at `PisanoSDK.init(...)` (boot) and reused for all subsequent calls. **On Android, `bundle_id` is the application package name** (`context.packageName`).  
**In `show()` and `healthCheck()`:** you can optionally pass `code`. If you **do not** pass it, the **init (boot) code** is used. If you pass it, that code is used for that call only (e.g. to show or check another widget).

### Why does `show()` sometimes not open the survey?

The backend can return `display_rate` (0–100). The SDK then decides deterministically whether to show or skip (e.g. 50% → show, skip, show, skip…). When skipped, the UI does not open and the callback receives `PisanoActions.DISPLAY_RATE_LIMITED`. Other cases: `DISPLAY_ONCE`, `CHANNEL_PASSIVE`, or quota/trigger rules.

### What are the ProGuard/R8 rules?

ProGuard rules for release builds are located in the `consumer-rules.pro` file. If you need to add custom rules:

```proguard
# Pisano SDK
-keep class co.pisano.feedback.** { *; }
-dontwarn co.pisano.feedback.**
```

## 🔧 Troubleshooting

### SDK won't initialize

1. Make sure `applicationId`, `accessKey`, `code`, `apiUrl`, and `feedbackUrl` are set in `PisanoSDKManager.Builder(...)` (required); set `eventUrl` if you use `track`
2. Check that API URLs are correct and accessible
3. Check that internet permissions are in the `AndroidManifest.xml` file

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
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
