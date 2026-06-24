# Pisano Feedback Android SDK — Release Notes v1.3.31 (MT-41)

## What's new

- **`dismissOnDrag` on `PisanoSDK.show()`** — Optional parameter (default `false`). When `true` and `viewMode` is `BOTTOM_SHEET`, the user can dismiss the sheet by dragging down from the **header/toolbar**.
- **Default bottom sheet (MT-41):** Opens at half height. Header drag expands/collapses between half and full; WebView area scrolls content only. No drag-to-dismiss and no partial collapse below half when `dismissOnDrag` is not set.
- **Samsung scrim fix:** Removed double dimming (activity + dialog) that caused solid black overlay on some Samsung devices.

## Sample app

This repo includes **Dismiss on drag (Off/On)** on the form screen, passed to both Show buttons.

```kotlin
PisanoSDK.show(
    viewMode = ViewMode.BOTTOM_SHEET,
    dismissOnDrag = true, // optional, default false
)
```

## Migration from 1.3.30

```gradle
dependencies {
    implementation 'co.pisano:feedback:1.3.31'
}
```

No breaking API changes. Existing `PisanoSDK.show(...)` calls work unchanged; `dismissOnDrag` defaults to `false`.

See also: [feedback-android/docs/MT-41_BOTTOM_SHEET.md](https://github.com/Pisano/feedback-android/blob/main/docs/MT-41_BOTTOM_SHEET.md)
