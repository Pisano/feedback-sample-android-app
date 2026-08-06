# feedback-sample-android-app — Release Notes (SDK 1.3.33 / MT-38)

**Sample app release:** aligns with **Pisano Feedback Android SDK `1.3.33`** (Maven Central)  
**Branch:** `main`  
**Includes:** MT-38 `disable_zoom` + keyboard/viewport + bottom sheet scrim; MT-41 bottom sheet; 1.3.32 cleartext fix

---

## What changed in this sample repo

| Area | Change |
|------|--------|
| SDK dependency | `co.pisano:feedback:1.3.33` |
| Sample app version | `1.0.3` |
| Docs | README + this file |

No credentials are committed. Local keys: `local.properties` or `pisano-config.plist` (see README).

---

## SDK highlights (1.3.33 / MT-38)

### `disable_zoom` (channel config)

From SDK detail API field `disable_zoom`:

- `true` → pinch/content zoom disabled in survey WebView
- `false` / omitted → pinch zoom enabled
- Input focus auto-zoom → always disabled

Configure in Pisano panel per channel. No new `PisanoSDK.show()` parameter.

**Test channel (stage):** `PSN-uisqsxt`

### Keyboard + WebView scroll

- Bottom sheet: WebView resizes when keyboard opens
- Survey content scrolls inside web widget container

### Bottom sheet scrim

- Removed duplicate dimming; host app background stays visible behind sheet

### Included from 1.3.32 / 1.3.31

- Cleartext manifest security fix (1.3.32)
- Optional `dismissOnDrag` on `PisanoSDK.show()` — sample UI: Off / On (MT-41)

See also: [RELEASE_NOTES_1.3.32.md](./RELEASE_NOTES_1.3.32.md), [RELEASE_NOTES_1.3.31.md](./RELEASE_NOTES_1.3.31.md)

---

## Upgrade your app (integrators)

```gradle
dependencies {
    implementation 'co.pisano:feedback:1.3.33'
}
```

**Breaking changes:** None.

---

## References

- SDK source & tag: [feedback-android `1.3.33`](https://github.com/Pisano/feedback-android/releases/tag/1.3.33)
- SDK release notes: [RELEASE_NOTES_1.3.33.md](https://github.com/Pisano/feedback-android/blob/master/RELEASE_NOTES_1.3.33.md)
- Maven: `co.pisano:feedback:1.3.33`
