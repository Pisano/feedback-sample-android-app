# Feedback Android SDK (Sample App)

Pisano Feedback Android SDK helps you collect surveys and user feedback in your Android applications.

> This repository is a **sample app repo**. The **SDK source code is not in this repo**.

## ✅ Sample app in this repo

- **Module:** `app/` — single sample application (XML layouts + Kotlin).
- The sample UI (buttons, forms) is **native**; the survey UI is **rendered by the SDK** after `PisanoSDK.show(...)` (your app does not build survey screens itself).
- The form lets you try **View Mode** (Default / Bottom Sheet), **Dismiss on drag**, a custom title, customer fields and the **Automatic store review** switch. In debug builds it also shows the last result and event.

SDK artifact used by this sample: **`co.pisano:feedback`** (version **2.0.0**, Maven Central)

Details: [RELEASE_NOTES_2.0.0.md](./RELEASE_NOTES_2.0.0.md)

Migrating from 1.x? See the [migration steps](./RELEASE_NOTES_2.0.0.md#migrating-from-1x).

## ✨ Features

- ✅ **Survey UI hosted by the SDK**: Your app does not build survey screens
- ✅ **Kotlin & Java**: Use the SDK from both
- ✅ **View modes**: Full screen and bottom sheet
- ✅ **Event tracking and health check**: `PisanoSDK.track` and `PisanoSDK.healthCheck`
- ✅ **Customer information**: Send name, email, phone, external ID and custom attributes
- ✅ **Languages and custom title**: Show the survey in a language you choose, with your own title
- ✅ **Results and events**: The `show()` completion and `PisanoSDK.subscribe` tell you what happened
- ✅ **Store review**: When a survey asks for a store rating, the SDK shows the Google Play In-App Review prompt by default. To turn it off and run your own flow, set `PisanoSDK.automaticStoreReview = false`

## 📱 Requirements

From this sample project (`app/build.gradle` and root `build.gradle`):

- **minSdk**: 21 (SDK 2.0.0 requires `minSdkVersion` 21 or higher)
- **Java**: 17 (toolchain / `compileOptions`)
- **Android Gradle Plugin**: 8.7.3
- **Kotlin**: 1.9.24

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
    implementation 'co.pisano:feedback:2.0.0'
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

## 🔑 Credentials

The sample does not include API keys. Add your own `PISANO_*` values before you run it:

1. Copy `local.properties.example` to `local.properties`.
2. Fill in the keys. See [where to get them](#pisano-platform-where-to-get-credentials).

You can also pass the same keys as Gradle `-P` properties or environment variables.

If a key is missing, the sample skips the SDK init and shows a warning, so the survey does not open.

## 🚀 Quick Start

### 1) Configure credentials

Add these keys to `local.properties` (see Credentials above):

**Required keys**:

- `PISANO_APP_ID`
- `PISANO_ACCESS_KEY`
- `PISANO_CODE` (your survey/channel code from the Pisano panel)
- `PISANO_API_URL`
- `PISANO_FEEDBACK_URL`
- `PISANO_EVENT_URL` (optional)

### 2) Initialize the SDK (boot once)

In this sample, SDK init is done **once** at app startup:

- `PisanoSampleApplication` → `PisanoSdkBootstrapper.ensureInitialized(...)`

If config is missing, init is skipped and the sample shows a user-facing warning (and logs a safe message without secrets).

### 3) Show the survey

In this sample, `MainActivity` calls `PisanoSDK.show(...)` when you press **Get Feedback**.

The sample also supports a deep link to pass a code override:

- `pisano://show?code=...`

### About `code`

`.setCode(...)` on `PisanoSDKManager.Builder` sets the default survey code. `show()` and `healthCheck()` use it unless you pass a `code`. A `code` you pass applies to that call only. If your app has more than one survey, pass the `code` on each `show()` call.

## 💡 Code samples

### 1) Initializing the SDK

You can initialize at app startup (recommended). Get the values from the Pisano panel:

```kotlin
import android.app.Application
import android.util.Log
import com.example.app.BuildConfig   // replace with your application module’s BuildConfig
import co.pisano.feedback.data.helper.CloseStatus
import co.pisano.feedback.managers.PisanoSDK
import co.pisano.feedback.managers.PisanoSDKManager

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val manager = PisanoSDKManager.Builder(this)
            .setApplicationId("YOUR_APP_ID")
            .setAccessKey("YOUR_ACCESS_KEY")
            .setCode("YOUR_CODE")
            .setApiUrl("YOUR_API_URL")
            .setFeedbackUrl("YOUR_FEEDBACK_URL")
            .setEventUrl("YOUR_EVENT_URL")   // optional, only for PisanoSDK.track
            .setDebug(BuildConfig.DEBUG)
            .build()

        PisanoSDK.init(manager) { status ->
            when (status) {
                CloseStatus.INIT_SUCCESS -> Log.d("Pisano", "SDK initialized successfully")
                CloseStatus.INIT_FAILED -> Log.e("Pisano", "SDK initialization failed")
                else -> {}
            }
        }
    }
}
```

#### Java

```java
import android.app.Application;
import android.util.Log;

import co.pisano.feedback.data.helper.CloseStatus;
import co.pisano.feedback.managers.PisanoSDK;
import co.pisano.feedback.managers.PisanoSDKManager;
import kotlin.Unit;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        PisanoSDKManager manager = new PisanoSDKManager.Builder(this)
            .setApplicationId("YOUR_APP_ID")
            .setAccessKey("YOUR_ACCESS_KEY")
            .setCode("YOUR_CODE")                    // required
            .setApiUrl("YOUR_API_URL")
            .setFeedbackUrl("YOUR_FEEDBACK_URL")
            .setEventUrl("YOUR_EVENT_URL")   // optional, only for PisanoSDK.track
            .setDebug(BuildConfig.DEBUG)              // optional, recommended
            .build();

        PisanoSDK.INSTANCE.init(manager, status -> {
            Log.d("Pisano", "init status=" + status);
            return Unit.INSTANCE;
        });
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
    pisanoCustomer = customer,
    completion = { status ->
        Log.d("Pisano", "Result: $status")
    }
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
- `completion: ((CloseStatus) -> Unit)?` - **Optional.** Called once on the main thread with `CloseStatus.INIT_SUCCESS` or `CloseStatus.INIT_FAILED`.

**Example:**

```kotlin
val manager = PisanoSDKManager.Builder(context)
    .setApplicationId("app-123")
    .setAccessKey("key-456")
    .setCode("code-789")
    .setApiUrl("YOUR_API_URL")
    .setFeedbackUrl("YOUR_FEEDBACK_URL")
    .setEventUrl("YOUR_EVENT_URL")   // optional, only for PisanoSDK.track
    .setDebug(true)
    .build()

PisanoSDK.init(manager) { status ->
    when (status) {
        CloseStatus.INIT_SUCCESS -> println("SDK initialized successfully")
        CloseStatus.INIT_FAILED -> println("SDK initialization failed")
        else -> {}
    }
}
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
- `dismissOnDrag: Boolean` - Bottom sheet only. When `true`, a swipe down from the header dismisses the sheet (optional, default `false`)
- `completion: ((CloseStatus) -> Unit)?` - **Optional.** Called exactly once on the main thread with the result of this call. See [`CloseStatus`](#closestatus-the-result-of-show).

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

**Result of the call:**  
Pass `completion` to get the result of this `show()` call. It is called once per call.

```kotlin
PisanoSDK.show(
    viewMode = ViewMode.BOTTOM_SHEET,
    completion = { status ->
        when (status) {
            CloseStatus.SEND_FEEDBACK -> Log.d("Pisano", "Feedback sent")
            CloseStatus.CLOSED -> Log.d("Pisano", "Survey closed")
            CloseStatus.DISPLAY_RATE_LIMITED -> Log.d("Pisano", "Skipped by display_rate")
            CloseStatus.NONE -> Log.d("Pisano", "No survey was shown")
            else -> {}
        }
    }
)
```

**Another survey for one call:**  
Pass `code` to show a different survey for this call only, for example `PisanoSDK.show(code = "PSN-other-widget")`. Without `code`, the SDK uses the code from `init`.

### `CloseStatus` (the result of `show()`)

Every `show()` call ends with one `CloseStatus`. It says how the call ended. What happens while the survey is open comes through [`PisanoSDK.subscribe`](#survey-events-with-pisanosdksubscribe).

| Value | Meaning |
|---|---|
| `CLOSED` | The survey was closed without a submit. |
| `SEND_FEEDBACK` | The survey was submitted, however the widget closed afterwards. |
| `OUTSIDE` | A tap outside the bottom sheet closed it. |
| `DISPLAY_ONCE` | Not shown: the customer saw the survey before. |
| `PREVENT_MULTIPLE_FEEDBACK` | Not shown: the customer already submitted a feedback in the given time period. |
| `CHANNEL_QUOTA_EXCEEDED` | Not shown: the quota is exceeded. |
| `CHANNEL_PASSIVE` | Not shown: the survey is passive. |
| `DISPLAY_RATE_LIMITED` | Not shown: skipped by `display_rate`. |
| `NONE` | No survey was shown and no other value fits, for example the SDK is not initialized or a request failed. |

`INIT_SUCCESS` and `INIT_FAILED` are the results of `PisanoSDK.init`.

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

### Activity integration (Kotlin / Java)

```kotlin
import androidx.appcompat.app.AppCompatActivity
import co.pisano.feedback.data.helper.ViewMode
import co.pisano.feedback.data.model.PisanoCustomer
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
                null,           // title
                "en",           // language
                null,           // payload
                customer,
                null,           // code: null uses the code from init
                false,          // dismissOnDrag
                null            // completion
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

### Survey events with `PisanoSDK.subscribe`

The `show()` completion reports one result per call. `PisanoSDK.subscribe` reports what happened on the way: `OPENED`, `FEEDBACK_SUBMITTED`, `REDIRECTED_TO_STORE` and `CLOSED`. The listener receives the events of every survey shown by `show()` until you call `cancel()`. Each event carries the survey `code`. `CLOSED` also carries a `closeReason`, for example `CLOSE_BUTTON`, `BACK`, `DRAG` or `OUTSIDE`.

```kotlin
class MainActivity : AppCompatActivity() {
    private var subscription: PisanoSubscription? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        subscription = PisanoSDK.subscribe { event ->
            when (event.type) {
                PisanoEventType.OPENED -> Log.d("Pisano", "Survey opened")
                PisanoEventType.CLOSED -> Log.d("Pisano", "Survey closed: ${event.closeReason}")
                else -> {}
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        subscription?.cancel()   // stop receiving events when the owner goes away
    }
}
```

In this sample, `MainActivity` shows the last event (debug builds) and logs every event.

### Store review (`automaticStoreReview`)

When a survey redirects the customer to the store rating, the SDK asks Google Play for the In-App Review prompt. This does not change the result of `show()`: after a submit it stays `SEND_FEEDBACK`. The redirect is reported as a `REDIRECTED_TO_STORE` event.

| `PisanoSDK.automaticStoreReview` | In-App Review prompt | `REDIRECTED_TO_STORE.handledBySDK` |
|---|---|---|
| `true` (default) | Shown by the SDK | `true` |
| `false` | Not shown | `false` |

Set it to `false` to run your own review flow. Check `handledBySDK` so your flow runs only when the SDK did not ask for the review. The value is not stored: it is `true` again each time the app starts.

```kotlin
PisanoSDK.automaticStoreReview = false

PisanoSDK.subscribe { event ->
    if (event.type == PisanoEventType.REDIRECTED_TO_STORE && !event.handledBySDK) {
        // Run your own store review flow here.
    }
}
```

The sample has an **Automatic store review** switch for this setting.

## ⚙️ Configuration

### ViewMode

```kotlin
import co.pisano.feedback.data.helper.ViewMode
import co.pisano.feedback.managers.PisanoSDK

// Full-screen overlay (default) — uses init `code`
PisanoSDK.show(viewMode = ViewMode.DEFAULT)

// Bottom sheet — uses init `code`
PisanoSDK.show(viewMode = ViewMode.BOTTOM_SHEET)
```

### Display Rate (`display_rate`)

The backend can return `display_rate` (0–100) in `/detail` and/or `/trigger`. The SDK uses this value to **deterministically** throttle `show()` attempts:

- `100`: always show
- `0`: never show
- Example `50`: **show, skip, show, skip, ...** (1st call shows, 2nd skips, 3rd shows, ...)

This decision is **code-based** (state is stored per widget `code`). If the `code` changes, throttling starts fresh for the new `code`. If `display_rate` changes for the same `code`, the internal counter is reset.

If a `show()` attempt is skipped due to `display_rate`, the SDK will not open UI and will report `CloseStatus.DISPLAY_RATE_LIMITED` to the `show()` completion.

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
- Close → verify the `show()` result and the events in the log

## ❓ Frequently Asked Questions

### Does this sample build the survey/question UI?

No. The sample app UI is native, but the survey/question UI is rendered by the SDK after `PisanoSDK.show(...)`.

### Where is SDK initialization done in this repo?

In `PisanoSampleApplication` via `PisanoSdkBootstrapper.ensureInitialized(...)` (boot once).

### When should I initialize the SDK?

Call **`PisanoSDK.init(...)`** once at application startup (typically in your `Application` class), **before** the first `PisanoSDK.show(...)` / `healthCheck` / `track`.

### Should I use `healthCheck`?

Yes, when you want to verify reachability or configuration before opening the widget (same idea as the iOS sample: **health check → then `show`**).

## 🔧 Troubleshooting

### Build fails: “SDK location not found”

Set `sdk.dir=...` in `local.properties`, or export `ANDROID_HOME` / `ANDROID_SDK_ROOT`.

### Survey doesn’t open

- Verify `PISANO_*` config values
- Ensure network access

### Runtime crash: `NoClassDefFoundError` (Retrofit / Gson)

Since **1.4.1**, the Maven POM lists the SDK's dependencies (Retrofit, Gson, AndroidX, Material), so Gradle adds them to your app. You no longer need to declare them yourself.

If you use **1.3.x or earlier** and see errors like:

- `retrofit2/Retrofit$Builder`
- `com/google/gson/GsonBuilder`

Add the required runtime deps to your app:

```gradle
implementation 'com.google.code.gson:gson:2.10.1'
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
implementation 'com.squareup.okhttp3:okhttp:4.9.3'
```

### Display once (`display_once`)

The backend can send `display_once` (boolean) and optionally `display_once_delay_period`. The SDK stores "shown" state locally and, if a delay period is set, will not show again until that period has passed. **`display_once_delay_period` is in minutes** (e.g. `1440` = once per day; it was in hours before 1.4.3). When skipped, the `show()` completion receives `CloseStatus.DISPLAY_ONCE`.

### Why is `code` required in init?

`code` is your survey/channel code from the Pisano panel. The SDK sends it (along with `applicationId`, `accessKey`, and `bundle_id`) in every `/detail` and `/trigger` request. It is stored once at `PisanoSDK.init(...)` and reused for all subsequent calls. **On Android, `bundle_id` is the application package name** (`context.packageName`).

In `show()` and `healthCheck()`, `code` is **optional**. If you **omit it or pass `null`**, the SDK **always** uses the **exact `code` from `.setCode(...)` / `init`**—not another implicit value. If you pass a **non-null** string, that value is used **for that call only**; the next call without `code` uses the init default again.

### Why does `show()` sometimes not open the survey?

The backend can return `display_rate` (0–100). The SDK decides deterministically whether to show or skip (e.g. 50 → show, skip, show, skip…). When skipped, the UI does not open and the `show()` completion receives `CloseStatus.DISPLAY_RATE_LIMITED`. Other cases: `DISPLAY_ONCE`, `CHANNEL_PASSIVE`, or quota/trigger rules.

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
4. If the `show()` completion receives `DISPLAY_RATE_LIMITED`, the backend's `display_rate` caused this call to be skipped (deterministic throttling)

### Java usage error

When using Java, use `PisanoSDK.INSTANCE`:

```java
PisanoSDK.INSTANCE.show(...)
```

### Bottom sheet closes (or does not close) on outside tap

Since **1.4.1**, taps inside the bottom sheet no longer send `OUTSIDE`. A tap outside the sheet closes it only when `close_on_outside_click` is enabled for the channel in the Pisano panel.

### WebView remote debugging

Since **1.4.1**, the SDK enables WebView remote debugging (`chrome://inspect`) only when `.setDebug(true)` is set and the app is debuggable. This setting applies to the whole app.

### `android:allowBackup`

Since **1.4.1**, the SDK no longer sets `android:allowBackup="true"` in your app's manifest. Your app's own value applies.

### Bottom sheet not working

Bottom sheet feature depends on Material Design Components. If the bottom sheet is not working, you can use full-screen mode (`ViewMode.DEFAULT`).

### MinifyEnabled error

Check your ProGuard rules. The `consumer-rules.pro` file is automatically included, but you may need to add custom rules.

## Pisano platform: where to get credentials

The values you use in the SDK (`applicationId` / `accessKey` / `code` / `apiUrl` / `feedbackUrl`) come from the **Pisano panel**. The steps match the **iOS sample documentation**; screenshots are hosted in [`Pisano/feedback-sample-ios-app`](https://github.com/Pisano/feedback-sample-ios-app) so you can paste the same images into Confluence or view them on GitHub.

### 1. App ID and Access Key (Profile → Mobile applications)

1. In the Pisano panel, go to **Profile** → **Mobile applications** (or equivalent).
2. Click **Create Mobile Application**.
3. Enter an **Application name** and add **package name / bundle identifiers** for each platform (Android package name, e.g. `com.yourcompany.app`).
4. Save. The app card shows **App ID** and **Access Key** (copy buttons).

![Create Mobile Application](https://raw.githubusercontent.com/Pisano/feedback-sample-ios-app/main/docs/pisano-platform/create-mobile-app.png)

![Edit Mobile Application](https://raw.githubusercontent.com/Pisano/feedback-sample-ios-app/main/docs/pisano-platform/edit-mobile-app.png)

![App credentials — App ID and Access Key](https://raw.githubusercontent.com/Pisano/feedback-sample-ios-app/main/docs/pisano-platform/app-credentials.png)

Use **App ID** with `.setApplicationId(...)` and **Access Key** with `.setAccessKey(...)`.

### 2. Code, API URL, and Feedback URL (Mobile Channels → Deploy)

1. Go to **Mobile Channels**, create or open a channel, then click **Deploy**.
2. In **Publish Channel Parameters**, copy from the top section:
   - **Code** → `.setCode("...")` (required at init; optional override on `show` / `healthCheck`)
   - **Api Url** → `.setApiUrl("...")`
   - **Feedback Url** → `.setFeedbackUrl("...")`

Ignore **Legacy credentials** in that modal for the current SDK model; use **Code**, **Api Url**, and **Feedback Url** from the top, plus **App ID** and **Access Key** from step 1.

![Publish Channel Parameters](https://raw.githubusercontent.com/Pisano/feedback-sample-ios-app/main/docs/pisano-platform/publish-channel-params.png)
