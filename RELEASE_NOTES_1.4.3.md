# Release Notes

```gradle
implementation 'co.pisano:feedback:1.4.3'
```

## 1.4.3

**October 13, 2026**

### Breaking change

- **`display_once_delay_period` is now in minutes:** The delay period was in hours. It is now in minutes, for example `1440` = once per day. Check the value in the Pisano panel before you upgrade.

### Features

- **Drag handle:** The bottom sheet shows a drag handle when the survey has no title. The handle sits in the correct position.

### Bug fixes

- **Display once timing:** The delay period for `display_once` now counts the correct time.
- **Bottom sheet crash:** Fixed a crash when the bottom sheet closes while it still settles or after a dialog is dismissed.
- **Bottom sheet scrim:** The dim background behind the bottom sheet shows correctly again.
- **`dismissOnDrag`:** When `dismissOnDrag` is `true`, a drag down closes the sheet directly, without a stop at the collapsed state.
