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
  - [PisanoActions](#pisanoactions)
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
- ✅ **Health Check**: Ability to check SDK status
- ✅ **User Information Support**: Ability to send user data
- ✅ **Multi-Language Support**: Ability to display surveys in different languages
- ✅ **Custom Title**: Customizable title support

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
      .setApiUrl("https://api.pisano.co")
      .setFeedbackUrl("https://web.pisano.co/web_feedback")
      .setEventUrl("YOUR_EVENT_URL") // optional
      .setCloseStatusCallback(object : ActionListener {
        override fun action(action: PisanoActions) {
          Log.d("Pisano", "action=$action")
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
            .setApiUrl("https://api.pisano.co")
            .setFeedbackUrl("https://web.pisano.co/web_feedback")
            .setEventUrl("YOUR_EVENT_URL") // optional
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
import co.pisano.feedback.data.helper.ViewMode
import co.pisano.feedback.data.model.PisanoCustomer
import co.pisano.feedback.data.model.Title
import co.pisano.feedback.managers.PisanoSDK

val customer = PisanoCustomer(
  name = "John Doe",
  email = "john@example.com",
  phoneNumber = "+1234567890",
  externalId = "CRM-12345",
  customAttributes = hashMapOf("source" to "app")
)

val title = Title(
  text = "We Value Your Feedback",
  textSize = 20f,
  textColor = Color.BLACK,
  textStyle = Typeface.BOLD,
  backgroundColor = Color.WHITE
)

PisanoSDK.show(
  viewMode = ViewMode.BOTTOM_SHEET,
  title = title,
  flowId = "specific-flow-id",
  language = "en",
  payload = hashMapOf("screen" to "home"),
  pisanoCustomer = customer
)
```

#### Java

```java
import co.pisano.feedback.data.helper.ViewMode;
import co.pisano.feedback.data.model.PisanoCustomer;
import co.pisano.feedback.managers.PisanoSDK;

public class Example {
    public void show() {
        PisanoCustomer customer = new PisanoCustomer(
            "John Doe",
            "john@example.com",
            null,
            null,
            "USER-123",
            null
        );

        PisanoSDK.INSTANCE.show(
            ViewMode.BOTTOM_SHEET,
            null,
            "flow-123",
            "en",
            null,
            customer
        );
    }
}
```

### 3) Health Check

```kotlin
import android.util.Log
import co.pisano.feedback.data.model.PisanoCustomer
import co.pisano.feedback.managers.PisanoSDK

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

### Sample app flow (this repo)

- Init once: `PisanoSampleApplication` → `PisanoSdkBootstrapper.ensureInitialized(...)`
- Show: `MainActivity` (XML UI) button → `PisanoSDK.show(...)`
- Deep link: `MainActivity` parses `flow_id` and forwards it to `show(...)`

### `PisanoSDK.track()`

Tracks a custom event.

**Parameters:**
- `event: String` (required)
- `payload: HashMap<String, String>?` (optional)
- `pisanoCustomer: PisanoCustomer?` (optional)
- `language: String?` (optional)

Kotlin:

```kotlin
import co.pisano.feedback.data.model.PisanoCustomer
import co.pisano.feedback.managers.PisanoSDK

PisanoSDK.track(
  event = "button_click",
  payload = hashMapOf("screen" to "home"),
  pisanoCustomer = PisanoCustomer(externalId = "USER-123"),
  language = "en"
)
```

Java:

```java
import java.util.HashMap;
import co.pisano.feedback.data.model.PisanoCustomer;
import co.pisano.feedback.managers.PisanoSDK;

HashMap<String, String> payload = new HashMap<>();
payload.put("screen", "home");

PisanoSDK.INSTANCE.track("button_click", payload, new PisanoCustomer(null, null, null, null, "USER-123", null), "en");
```

### `PisanoSDK.clearAction()`

Clears the last action/state held by the SDK.

```kotlin
import co.pisano.feedback.managers.PisanoSDK

PisanoSDK.clearAction()
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

When using Java, use `PisanoSDK.INSTANCE`:

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

It runs:

- SDK init (boot once)
- `PisanoSDK.healthCheck(...)`

If credentials are missing, the test will **skip** (so CI won’t fail).

Run on an emulator/device:

```bash
./gradlew :app:connectedAndroidTest
```

