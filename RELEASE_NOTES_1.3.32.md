# feedback-sample-android-app — Release Notes (SDK 1.3.32)

**Sample app release:** aligns with **Pisano Feedback Android SDK `1.3.32`** (Maven Central)  
**Branch:** `fix/1.3.32-sample` → `main`  
**Includes:** MT-41 bottom sheet + cleartext manifest security fix

---

## What changed in this sample repo

| Area | Change |
|------|--------|
| SDK dependency | `co.pisano:feedback:1.3.32` |
| Sample app version | `1.0.2` |
| Docs | README + this file |

No credentials are committed. Local keys: `local.properties` or `pisano-config.plist` (see README).

---

## SDK highlights (1.3.32)

### Security — cleartext traffic

- Removed `android:usesCleartextTraffic="true"` from the SDK library manifest.
- Host apps are no longer forced to allow HTTP via manifest merge.
- **Production:** continue using **HTTPS** for `apiUrl` and `feedbackUrl` in `PisanoSDKManager.Builder`.

### Included from 1.3.31 (MT-41)

- Optional `dismissOnDrag` on `PisanoSDK.show()` (sample UI: Off / On).
- Bottom sheet half-height default, header drag, Samsung scrim fix.

See also: [RELEASE_NOTES_1.3.31.md](./RELEASE_NOTES_1.3.31.md)

---

## Upgrade your app (integrators)

```gradle
dependencies {
    implementation 'co.pisano:feedback:1.3.32'
}
```

**Breaking changes:** None.

---

## References

- SDK source & tag: [feedback-android `v1.3.32`](https://github.com/Pisano/feedback-android)
- SDK release notes: [RELEASE_NOTES_1.3.32.md](https://github.com/Pisano/feedback-android/blob/master/RELEASE_NOTES_1.3.32.md)
- Maven: `co.pisano:feedback:1.3.32`
