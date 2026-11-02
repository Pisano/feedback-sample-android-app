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
4. Handle `CloseStatus.OUTSIDE` as `CLOSED`. Read the `closeReason` of the `CLOSED` event if you need the outside tap.
5. Take a `Boolean` in the `init` completion instead of `CloseStatus.INIT_SUCCESS` / `INIT_FAILED`.

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

### Breaking changes

- **`PisanoActions` is now `CloseStatus`:** Update the import to `co.pisano.feedback.data.helper.CloseStatus`. All other values keep their names, except the ones removed below.
- **Four values are removed:** `CloseStatus` now reports only how a call ended. What happens while the survey is open is an event that you receive with `PisanoSDK.subscribe`.

  | Removed value | Replacement |
  | --- | --- |
  | `OPENED` | `PisanoEventType.OPENED` |
  | `REDIRECT_TO_STORE` | `PisanoEventType.REDIRECTED_TO_STORE` |
  | `SHOW_SUCCESS` | None. The SDK never sent it, so remove it from your code. |
  | `OUTSIDE` | An outside tap ends `show()` with `CLOSED`. The `CLOSED` event's `closeReason` is `PisanoCloseReason.OUTSIDE`. |

- **`init` reports a `Boolean`:** The `completion` of `PisanoSDK.init` is `(Boolean) -> Unit`: `true` when the SDK booted, `false` when it failed. `CloseStatus.INIT_SUCCESS` and `INIT_FAILED` are removed, so `CloseStatus` now holds only `show()` results, like `PisanoSDK.healthCheck` already reported a `Boolean`.
- **`setCloseStatusCallback` and `ActionListener` are removed:** The global listener is gone. Use the `init` completion, the `show()` completion and `PisanoSDK.subscribe`.
- **Every `show()` ends with one result:** The `completion` is always called once. If no survey is shown and no other value fits, for example when the SDK is not initialized or a request fails, the result is the new value `CloseStatus.NONE`. Before, the `completion` was not called in some of these cases.
- **`init`, `healthCheck`, `show` and `track` return a `PisanoTask`:** They returned `Unit`. Kotlin and Java source code that ignores the result still compiles. A library compiled against 1.x must be recompiled.
- **`track` has a `completion`:** `PisanoSDK.track(..., completion: ((Boolean) -> Unit)? = null)` reports `true` when the server accepted the event and `false` when it was not sent or the task was cancelled. Before, `track` reported nothing.
- **`healthCheck` always reports on the main thread:** It was sometimes called back at once on the calling thread, for example when there was no network. When the SDK is not initialized it now reports `false` instead of staying silent.
- **`PisanoSDKHelper` public functions changed:** Most apps use `PisanoSDK` only, so this affects you only if you call the helper directly.

  | 1.x | 2.0.0 |
  | --- | --- |
  | `getMobileSdkDetail()` | `getMobileSdkDetail(completion: ((Boolean) -> Unit)?)` |
  | `fetchSdkDetail(codeOverride, triggerInitEvents, onComplete)` | `fetchSdkDetail(codeOverride, onComplete)`: the `triggerInitEvents` parameter is removed. |
  | `getAll(..., completion: ((PisanoActions) -> Unit)?)` and `getTrigger(..., completion: ((PisanoActions) -> Unit)?)` | The `completion` type is `((CloseStatus) -> Unit)?`. |
  | `notifySurveyEvent(action)` and `clearCompletion()` | Removed. Use `PisanoSDK.subscribe` and the `show()` completion. |

- **`BottomSheetWebView` is not public anymore:** It is an internal class of the SDK. Show surveys with `PisanoSDK.show`.
- **`PisanoSDK.init` from Java:** `init(manager)` still compiles. The `completion` parameter is optional (`@JvmOverloads`).
- **One survey at a time:** If a survey is open, another `show()` does not open a second one and ends with `NONE`.

### Features

- **`PisanoSDK.subscribe`:** Receive `OPENED`, `FEEDBACK_SUBMITTED`, `REDIRECTED_TO_STORE` and `CLOSED` for the surveys that `show()` displays. `CLOSED` has a `closeReason`, for example `CLOSE_BUTTON`, `BACK` or `DRAG`. Call `cancel()` on the returned subscription when you no longer need the events.
- **Init completion:** `PisanoSDK.init(manager) { succeeded -> }` reports a `Boolean`: `true` when the SDK booted, `false` when it failed.
- **`PisanoTask`:** `init`, `healthCheck`, `show` and `track` return a task. `cancel()` stops the call and reports once: `false` for `init`, `healthCheck` and `track`, `NONE` for `show`. It has no effect after the call finished or after `show` put the survey on screen. `PisanoSDK.clear()` (`clearAction()` is a deprecated alias) cancels the calls that are still running.
- **`PisanoSDK.clear()`:** Closes the open survey and clears the SDK's local data. Call it when a user logs out. The closed survey ends its `show()` with `NONE`. `clearAction()` is a deprecated alias.
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
