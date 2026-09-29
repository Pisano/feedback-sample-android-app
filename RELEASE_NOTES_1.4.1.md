# Release Notes

```gradle
implementation 'co.pisano:feedback:1.4.1'
```

## 1.4.1

**September 29, 2026**

### Breaking change

- **Minimum SDK 21:** `minSdkVersion` is now 21. Apps with a lower `minSdkVersion` must raise it to upgrade.

### Features

- **Per-call `show()` callback:** `PisanoSDK.show()` takes an optional `completion` parameter. It receives the result of that call only, for example `OPENED`, `CLOSED`, `SEND_FEEDBACK` or `DISPLAY_RATE_LIMITED`. The global `setCloseStatusCallback` listener still receives the same events.
- **Loading indicator:** In full screen mode, a loading indicator shows while the survey page loads.

### Bug fixes

- **Survey URL encoding:** Customer IDs and response payloads that contain `+`, `&` or spaces now reach the survey without changes.
- **Bottom sheet outside taps:** Taps inside the bottom sheet no longer send `OUTSIDE`. A tap outside the sheet closes it only when `close_on_outside_click` is enabled for the channel.
- **WebView debugging:** The SDK enables WebView remote debugging only when `setDebug(true)` is set and the app is debuggable. This setting applies to the whole app.
- **`allowBackup`:** The SDK no longer sets `android:allowBackup="true"` in your app's manifest.

### Improvement

- **Dependencies:** The Maven POM now lists the SDK's dependencies (Retrofit, Gson, AndroidX, Material). You no longer need to add them to your app.
