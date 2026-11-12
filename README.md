# QR to Clipboard

A minimal Android app: it opens a QR code scanner and copies every scanned value to the clipboard.
The button in the bottom corner lists the last 100 scans; tap one to copy it again.

## Build

Needs a JDK (17 or newer) and the Android SDK (`ANDROID_HOME` or `sdk.dir` in `local.properties`).

```bash
./gradlew assembleDebug
```

The APK ends up in `app/build/outputs/apk/debug/app-debug.apk`.
