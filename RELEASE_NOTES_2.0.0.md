# Release Notes

```gradle
implementation 'co.pisano:feedback:2.0.0'
```

## 2.0.0

## Upgrading from 1.x

2.0.0 has breaking changes. Do these steps in order. Your app compiles and behaves as before when all are done.

### 1. Replace the global listener

Remove `setCloseStatusCallback` and `ActionListener`. Take the result of each call in its own completion. Take the events of the open survey from `PisanoSDK.subscribe`.

```kotlin
// 1.x
val manager = PisanoSDKManager.Builder(context)
    .setCloseStatusCallback(object : ActionListener {
        override fun action(action: PisanoActions) { /* ... */ }
    })
    .build()
PisanoSDK.init(manager)

// 2.0.0
val manager = PisanoSDKManager.Builder(context).build()
PisanoSDK.init(manager) { succeeded -> /* true when the SDK booted */ }

val subscription = PisanoSDK.subscribe { event -> /* OPENED, CLOSED, ... */ }
// Call subscription.cancel() when you no longer need the events.
```

### 2. Rename `PisanoActions` to `CloseStatus`

Change the import to `co.pisano.feedback.data.helper.CloseStatus`. The values keep their names, except the ones in step 3.

### 3. Replace the removed values

| 1.x value | 2.0.0 |
| --- | --- |
| `OPENED` | Event `PisanoEventType.OPENED` |
| `REDIRECT_TO_STORE` | Event `PisanoEventType.REDIRECTED_TO_STORE` |
| `OUTSIDE` | `show()` ends with `CLOSED`. The `CLOSED` event's `closeReason` is `PisanoCloseReason.OUTSIDE`. |
| `SHOW_SUCCESS` | Remove it. The SDK never sent it. |
| `INIT_SUCCESS`, `INIT_FAILED` | The `init` completion gets a `Boolean`: `true` when the SDK booted, `false` when it failed. |

### 4. Handle the new value `CloseStatus.NONE`

`NONE` means no survey was shown and no other value fits, for example the SDK is not initialized, a request failed or the survey page did not load. Every `show()` now ends with exactly one result. Before, the `completion` was not called in some of these cases.

### Behavior changes to check

These need no code change, but make sure your app does not rely on the old behavior.

- **One survey at a time:** If a survey is open, another `show()` does not open a second one and ends with `NONE`.
- **`healthCheck`:** It always reports on the main thread. When the SDK is not initialized it reports `false`. Before, it stayed silent.
- **`track`:** It has an optional `completion`: `true` when the server accepted the event, `false` when it was not sent.

## What else is new

You do not need any of this to upgrade.

### Features

- **`PisanoSDK.subscribe`:** Receive `OPENED`, `FEEDBACK_SUBMITTED`, `REDIRECTED_TO_STORE` and `CLOSED` for the surveys that `show()` displays. `CLOSED` has a `closeReason`, for example `CLOSE_BUTTON`, `BACK` or `DRAG`. Call `cancel()` on the returned subscription when you no longer need the events.
- **`PisanoTask`:** `cancel()` stops the call and reports once: `false` for `init`, `healthCheck` and `track`, `NONE` for `show`. It has no effect after the call finished or after `show` put the survey on screen.
- **`PisanoSDK.clear()`:** Closes the open survey, cancels the calls that are still running and clears the SDK's local data. Call it when a user logs out. The closed survey ends its `show()` with `NONE`. `clearAction()` is a deprecated alias.
- **`PisanoSDK.requestTimeout`:** Connect and read timeout of the network requests, in seconds. The default is `30`.
- **`PisanoSDK.automaticStoreReview`:** The default is `true`. Set it to `false` to stop the SDK from asking for the Google Play review prompt and run your own flow on the `REDIRECTED_TO_STORE` event.

### Examples

Know when a survey opens and how it closes:

```kotlin
val subscription = PisanoSDK.subscribe { event ->
    when (event.type) {
        PisanoEventType.OPENED -> pauseVideo()
        PisanoEventType.CLOSED -> {
            resumeVideo()
            if (event.closeReason == PisanoCloseReason.BACK) { /* ... */ }
        }
        else -> {}
    }
}

// In onDestroy:
subscription.cancel()
```

Run your own store review flow:

```kotlin
PisanoSDK.automaticStoreReview = false

PisanoSDK.subscribe { event ->
    if (event.type == PisanoEventType.REDIRECTED_TO_STORE && !event.handledBySDK) {
        // You can ask for the review yourself.
    }
}
```

### Bug fixes

- **Close right after submit:** If the customer closes the survey after submitting, `show()` reports `SEND_FEEDBACK`.
- **Back key:** The widget closes only once on the back key and reports `BACK`, in the bottom sheet and in full screen. The back key does not go back in the survey page anymore.
- **Survey page fails to load:** The widget closes by itself, the `CLOSED` event's `closeReason` is `LOAD_FAILED` and `show()` ends with `NONE`. A load error after the page is visible does not close the widget.
- **Display once:** A survey counts as shown when the widget closes, not when it starts to load. A survey that failed to load is shown again in a later session. A survey that was answered or closed is not shown again.
