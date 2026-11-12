# QR to Clipboard

A minimal Android app: it opens a QR code scanner and copies every scanned value to the clipboard.
It runs on Android 16 or newer. The button in the bottom corner lists the last 100 scans; tap one to copy it again.

## Build

Needs a JDK (17 or newer) and the Android SDK with platform 37.2 (`ANDROID_HOME` or `sdk.dir` in `local.properties`).

```bash
./gradlew assembleDebug
```

The APK ends up in `app/build/outputs/apk/debug/app-debug.apk`.

## Release build

```bash
./gradlew assembleRelease
```

This builds a shrunk APK at `app/build/outputs/apk/release/app-release.apk`.

By default it is signed with the debug key of the machine it is built on, which is fine for installing it on
your own phone. To sign with your own key, create a `keystore.properties` file in the project root (it is not
checked in):

```properties
storeFile=my-release-key.jks
storePassword=...
keyAlias=...
keyPassword=...
```

`storeFile` is relative to the project root.
