# Feedback Android SDK

Pisano Feedback Android SDK is an SDK that allows you to easily integrate user feedback collection into your Android applications. With this SDK, you can collect surveys and feedback from your users and improve the user experience.

## 📋 Table of Contents

- [Features](#features)
- [Requirements](#requirements)
- [Installation](#installation)
- [Quick Start](#quick-start)
- [API Reference](#api-reference)
  - [PisanoActions](#pisanoactions)
- [Usage Examples](#usage-examples)
- [Configuration](#configuration)
- [Frequently Asked Questions](#frequently-asked-questions)
- [Troubleshooting](#troubleshooting)

## ✨ Features

- ✅ **Web-Based Feedback Forms**: Modern and flexible web-based form support
- ✅ **Native Android Integration**: Fully native Android SDK
- ✅ **Kotlin & Java Compatibility**: Can be used in both Kotlin and Java projects
- ✅ **Flexible View Modes**: Full-screen and bottom sheet view options
- ✅ **Health Check**: Ability to check SDK status
- ✅ **User Information Support**: Ability to send user data
- ✅ **Multi-Language Support**: Ability to display surveys in different languages
- ✅ **Custom Title**: Customizable title support

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
            .setApiUrl("https://api.pisano.co")
            .setFeedbackUrl("https://web.pisano.co/web_feedback")
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
            .setApiUrl("https://api.pisano.co")
            .setFeedbackUrl("https://web.pisano.co/web_feedback")
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
    flowId = "specific-flow-id",
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
  - `apiUrl: String` - API endpoint URL
  - `feedbackUrl: String` - Feedback widget URL
  - `closeStatusActionListener: ActionListener?` - Initialization result callback (optional)

**Example:**

```kotlin
val manager = PisanoSDKManager.Builder(context)
    .setApplicationId("app-123")
    .setAccessKey("key-456")
    .setApiUrl("https://api.pisano.co")
    .setFeedbackUrl("https://web.pisano.co/web_feedback")
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
- `flowId: String?` - Specific flow ID (optional)
- `language: String?` - Language code (e.g., "en", "tr") (optional)
- `payload: HashMap<String, String>?` - Extra data (optional)
- `pisanoCustomer: PisanoCustomer?` - User information (optional)

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
    flowId = null,
    language = "en",
    payload = null,
    pisanoCustomer = customer
)
```

### `PisanoSDK.healthCheck()`

Checks the SDK status. It is recommended to use this before displaying the widget.

**Parameters:**
- `flowId: String?` - Flow ID (optional)
- `language: String?` - Language code (optional)
- `pisanoCustomer: PisanoCustomer?` - User information (optional)
- `payload: HashMap<String, String>?` - Extra data (optional)
- `isHealthCheckSuccessful: (Boolean) -> Unit` - Health check result (true: successful, false: failed)

**Example:**

```kotlin
PisanoSDK.healthCheck(
    flowId = "flow-123",
    pisanoCustomer = PisanoCustomer(externalId = "USER-789")
) { isHealthy ->
    if (isHealthy) {
        PisanoSDK.show()
    } else {
        Log.e("Pisano", "SDK health check failed")
    }
}
```

### `PisanoActions`

The `PisanoActions` enum is returned by various SDK methods to indicate the result of an operation.

**PisanoActions Values:**

- `INIT_SUCCESS`: SDK initialized successfully
- `INIT_FAILED`: SDK initialization failed
- `OPENED`: Survey/widget opened
- `CLOSED`: User clicked the close button
- `SEND_FEEDBACK`: Feedback was sent
- `OUTSIDE`: Closed by clicking outside
- `DISPLAY_ONCE`: Already shown before
- `PREVENT_MULTIPLE_FEEDBACK`: Multiple feedback prevented
- `CHANNEL_PASSIVE`: Survey is in passive state
- `CHANNEL_QUOTA_EXCEEDED`: Quota exceeded

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
                null, null, null, null,
                "USER-123",
                null
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
    }
}
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

This feature ensures that the widget is shown to the user only once. This control is managed by the backend.

### What are the ProGuard/R8 rules?

ProGuard rules for release builds are located in the `consumer-rules.pro` file. If you need to add custom rules:

```proguard
# Pisano SDK
-keep class co.pisano.feedback.** { *; }
-dontwarn co.pisano.feedback.**
```

## 🔧 Troubleshooting

### SDK won't initialize

1. Make sure `appId` and `accessKey` values are correct
2. Check that API URLs are accessible
3. Check that internet permissions are in the `AndroidManifest.xml` file

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

### Widget won't display

1. Make sure the `PisanoSDK.init()` method completed successfully
2. Perform a health check to verify SDK status
3. Check internet connection

### Java usage error

When using Java, use `PisanoSDK.INSTANCE`:

```java
PisanoSDK.INSTANCE.show(...)
```

### Bottom sheet not working

Bottom sheet feature depends on Material Design Components. If the bottom sheet is not working, you can use full-screen mode (`ViewMode.DEFAULT`).

### MinifyEnabled error

Check your ProGuard rules. The `consumer-rules.pro` file is automatically included, but you may need to add custom rules.
