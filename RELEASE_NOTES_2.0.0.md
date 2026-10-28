# Release Notes

```gradle
implementation 'co.pisano:feedback:2.0.0'
```

## 2.0.0

**Release date: to be set**

### Migrating from 1.x

1. Rename `PisanoActions` to `CloseStatus`.
2. Remove `setCloseStatusCallback` and `ActionListener`. Use the `init` completion, the `show()` completion and `PisanoSDK.subscribe` instead.
3. Handle the new `CloseStatus.NONE` value.

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
PisanoSDK.init(manager) { status -> /* INIT_SUCCESS or INIT_FAILED */ }

val subscription = PisanoSDK.subscribe { event -> /* OPENED, CLOSED, ... */ }
// Call subscription.cancel() when you no longer need the events.
```

### Breaking changes

- **`PisanoActions` is now `CloseStatus`:** Update the import to `co.pisano.feedback.data.helper.CloseStatus`. All other values keep their names.
- **Three values are removed:** `CloseStatus` now reports only how a call ended. What happens while the survey is open is an event that you receive with `PisanoSDK.subscribe`.

  | Removed value | Replacement |
  | --- | --- |
  | `OPENED` | `PisanoEventType.OPENED` |
  | `REDIRECT_TO_STORE` | `PisanoEventType.REDIRECTED_TO_STORE` |
  | `SHOW_SUCCESS` | None. The SDK never sent it, so remove it from your code. |

- **`setCloseStatusCallback` and `ActionListener` are removed:** The global listener is gone. Use the `init` completion, the `show()` completion and `PisanoSDK.subscribe`.
- **Every `show()` ends with one result:** The `completion` is always called once. If no survey is shown and no other value fits, for example when the SDK is not initialized or a request fails, the result is the new value `CloseStatus.NONE`. Before, the `completion` was not called in some of these cases.
- **One survey at a time:** If a survey is open, another `show()` does not open a second one and ends with `NONE`.

### Features

- **`PisanoSDK.subscribe`:** Receive `OPENED`, `FEEDBACK_SUBMITTED`, `REDIRECTED_TO_STORE` and `CLOSED` for the surveys that `show()` displays. `CLOSED` has a `closeReason`, for example `CLOSE_BUTTON`, `BACK` or `DRAG`. Call `cancel()` on the returned subscription when you no longer need the events.
- **Init completion:** `PisanoSDK.init(manager) { status -> }` reports `INIT_SUCCESS` or `INIT_FAILED`.
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
- **Back key:** The bottom sheet closes only on the back key and reports `BACK`.
