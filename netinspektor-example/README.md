# netinspektor-example

A Kotlin Multiplatform example application demonstrating how to integrate **netinspektor-client** into an Android, iOS, and Desktop app.

---

## Targets

| Platform | Entry point |
|---|---|
| Android | `composeApp/src/androidMain/` — `MainActivity` |
| iOS | `iosApp/` — `MainViewController` |
| Desktop (JVM) | `composeApp/src/desktopMain/` — `main.kt` |

---

## Running the example

```bash
# Desktop
./gradlew :netinspektor-example:composeApp:run
# Android — Open in Android Studio and run on a device or emulator
# iOS — Open `iosApp/iosApp.xcodeproj` in Xcode and run on a simulator or device.
```

---

## What the example shows

The app demonstrates the full netinspektor-client integration lifecycle:

1. **Create a session** — tap *Add session* to create a new `NetInspektorSession`. Each session gets a unique name based on the platform and index (e.g. `example-android (1)`).
2. **Start the session** — the session starts automatically when added, launching the local WebSocket event server and announcing itself to the discovery server.
3. **Make HTTP requests** — tap any of the preset request buttons to fire a real HTTP call and log both the request and response through the session:
   - **Products** — Json response
   - **Carts** — Json response
   - **Comments** — Json response
   - **Error** — (triggers a 4xx response)
   - **HTML** — (HTML content type)
   - **Image** — fetches am image (image content type)
4. **Inspect in the desktop app** — open netinspektor-desktop and pair with the session to see all traffic in real time.
5. **Stop / remove** — use the *Stop* button to pause the session server, or *Delete* to remove the session entirely.
