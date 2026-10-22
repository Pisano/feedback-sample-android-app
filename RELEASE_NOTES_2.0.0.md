# Release Notes

```gradle
implementation 'co.pisano:feedback:2.0.0'
```

## 2.0.0

**Release date: to be set**

### Migrating from 1.x

### Breaking changes

- **`PisanoActions` is now `CloseStatus`:** The enum has a new name. Update the import to `co.pisano.feedback.data.helper.CloseStatus`. The values `CLOSED`, `SEND_FEEDBACK`, `OUTSIDE`, `DISPLAY_ONCE`, `PREVENT_MULTIPLE_FEEDBACK`, `CHANNEL_QUOTA_EXCEEDED`, `CHANNEL_PASSIVE`, `DISPLAY_RATE_LIMITED`, `INIT_FAILED` and `INIT_SUCCESS` keep their names.
- **Three values are removed from the enum:** `CloseStatus` reports how a call ended. What happens on the way is now an event that you receive with `PisanoSDK.subscribe`.

  | Removed value | Replacement |
  | --- | --- |
  | `OPENED` | `PisanoEventType.OPENED`. It is sent when the survey is visible. |
  | `REDIRECT_TO_STORE` | `PisanoEventType.REDIRECTED_TO_STORE`. The event has `handledBySDK`. |
  | `SHOW_SUCCESS` | None. Use `PisanoEventType.OPENED` to know that a survey is shown. |

  The store review flow of the SDK does not change.

### Features

### Bug fixes

### Improvement
