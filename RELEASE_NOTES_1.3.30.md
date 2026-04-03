# Pisano Feedback Android SDK - Release Notes v1.3.30

## Release Date
April 3, 2026

## What's New

### Bug Fixes

- **BottomSheet White Screen Fix**: Resolved an issue where a white background appeared behind the BottomSheet dialog on certain devices. A dedicated transparent theme (`Pisano_Theme_BottomSheet`) is now applied before `super.onCreate()`, ensuring consistent transparent rendering across all Android versions and devices.

- **BottomSheet Scroll Fix**: Fixed a scroll conflict between the BottomSheet and the embedded WebView. A custom `NestedScrollWebView` now prevents the BottomSheet from intercepting scroll gestures inside the WebView, allowing users to scroll survey content freely. The BottomSheet can still be dragged from the toolbar area.

- **Keyboard Focus Fix**: Improved keyboard behavior in BottomSheet mode. Added `focusableInTouchMode` to the WebView and `requestFocus()` after dialog display, ensuring input fields receive focus and the soft keyboard opens reliably when tapping on form elements.

### Technical Changes

- Added `Pisano_Theme_BottomSheet` style with transparent `windowBackground`
- Theme is now applied before `super.onCreate()` for reliable window attribute rendering
- Unused `full_screen_web_view` and `toolbar_default` are hidden (`View.GONE`) in BottomSheet mode
- `SOFT_INPUT_STATE_HIDDEN` flag added to keyboard behavior for predictable soft input handling
- New `NestedScrollWebView` class for proper scroll coordination

### Compatibility

- No breaking changes
- No API changes — drop-in replacement for 1.3.29
- Full-screen mode is completely unaffected
- minSdk: 16, targetSdk: 34

## Upgrade

```gradle
dependencies {
    implementation 'co.pisano:feedback:1.3.30'
}
```
