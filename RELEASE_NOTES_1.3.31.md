# feedback-sample-android-app — Release Notes (SDK 1.3.31 / MT-41)

**Sample app release:** aligns with **Pisano Feedback Android SDK `1.3.31`** (Maven Central)  
**Branch:** `feat/MT-41-sample` → `main`  
**Ticket:** MT-41 — bottom sheet behaviour + optional `dismissOnDrag`

---

## What changed in this sample repo

| Area | Change |
|------|--------|
| SDK dependency | `co.pisano:feedback:1.3.31` |
| Form screen | **Dismiss on drag** radio group (Off / On) |
| `MainActivity` | Selected value passed to `PisanoSDK.show(..., dismissOnDrag = ...)` |
| Docs | README + this file |

No credentials are committed. Local keys: `local.properties` or `pisano-config.plist` (see README).

---

## SDK highlights (1.3.31)

Integrators bump the Maven artifact; the sample demonstrates the new optional flag.

### `dismissOnDrag` (optional, default `false`)

| `dismissOnDrag` | Bottom sheet behaviour |
|-----------------|------------------------|
| `false` (default) | Opens at **half height**. Drag on **header/toolbar** expands/collapses. WebView scrolls content only. No swipe-to-dismiss. No partial collapse below half. |
| `true` | Same header drag + WebView isolation; **swipe-down dismiss** from header enabled (`isHideable = true`). |

### Other fixes in 1.3.31

- **Samsung scrim:** Removed duplicate activity + dialog dimming (solid black overlay on some devices).

---

## Upgrade your app (integrators)

```gradle
dependencies {
    implementation 'co.pisano:feedback:1.3.31'
}
```

```kotlin
PisanoSDK.show(
    viewMode = ViewMode.BOTTOM_SHEET,
    title = "Title",
    code = null,           // optional override; init code used if null
    customer = customerMap,
    payload = payloadMap,
    dismissOnDrag = false, // optional — set true to allow swipe dismiss
)
```

**Breaking changes:** None. Existing `PisanoSDK.show(...)` calls work unchanged.

---

## Run this sample locally

1. Clone repo, checkout `feat/MT-41-sample` (or `main` after merge).
2. Copy credentials (do **not** commit):
   - `local.properties.example` → `local.properties`, **or**
   - `pisano-config.example.plist` → `pisano-config.plist`
3. Fill `PISANO_APP_ID`, `PISANO_ACCESS_KEY`, `PISANO_CODE`, `PISANO_API_URL`, `PISANO_FEEDBACK_URL`.
4. Open in Android Studio → Run on device/emulator.
5. Form screen → set **View mode** (Default / Bottom sheet) → **Dismiss on drag** Off/On → **Show**.

---

## Smoke test checklist (MT-41)

| # | Test | Expected |
|---|------|----------|
| 1 | Bottom sheet open | Sheet at ~half height |
| 2 | Scroll inside WebView | Sheet does not move |
| 3 | Drag header, Off | Resize expand/collapse; does not dismiss |
| 4 | Drag header down, On | Sheet dismisses |
| 5 | Samsung / dark scrim | No solid black full-screen overlay |

---

## References

- SDK source & tag: [feedback-android `1.3.31`](https://github.com/Pisano/feedback-android)
- Technical doc: [MT-41_BOTTOM_SHEET.md](https://github.com/Pisano/feedback-android/blob/main/docs/MT-41_BOTTOM_SHEET.md)
- Maven: `co.pisano:feedback:1.3.31`
