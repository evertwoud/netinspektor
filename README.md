# netinspektor

**netinspektor** is a Kotlin Multiplatform network inspection tool. It lets you monitor and inspect HTTP traffic from your Android, iOS, or Desktop app in real time — streamed directly to a native desktop application.

---

## How it works

```
┌─────────────────────────┐       WebSocket        ┌─────────────────────────────┐
│  Your App               │ ──────────────────────►│  netinspektor Desktop       │
│  (netinspektor-client)  │                        │  (macOS / Windows / Linux)  │
│                         │◄───────────────────────│                             │
└─────────────────────────┘       Discovery        └─────────────────────────────┘
```

1. Embed **netinspektor-client** in your KMP app and create a `NetInspektorSession`.
2. Call `session.logRequest(...)` and `session.logResponse(...)` whenever a network call is made.
3. The client automatically starts a local WebSocket server and announces itself to the discovery server.
4. Open the **netinspektor-desktop** app — your device/session appears automatically.
5. Inspect requests and responses in real time.

---

## Project structure

| Module | Description |
|---|---|
| [`netinspektor-core`](netinspektor-core/) | Shared data models and constants used by both the client and desktop app |
| [`netinspektor-client`](netinspektor-client/) | KMP library to embed in your app to capture and stream network events |
| [`netinspektor-desktop`](netinspektor-desktop/) | Native desktop application for real-time inspection |
| [`netinspektor-example`](netinspektor-example/) | Example KMP app demonstrating how to integrate the client library |

---

## Getting started

### 1. Add the client library

Add the dependency to your KMP module:

```kotlin
// build.gradle.kts
commonMain.dependencies {
    implementation("com.evertwoud.netinspektor:netinspektor-client:1.0.1")
}
```

### 2. Create a session

```kotlin
val session = NetInspektorSession(sessionName = "MyApp")
session.start()
```

### 3. Log your network calls

```kotlin
val request = NetInspektorEvent.Request(
    method = "GET",
    url = "https://api.example.com/data",
    headers = mapOf("Authorization" to "Bearer token"),
    body = null
)
session.logRequest(request)

// ... perform the actual HTTP call ...

session.logResponse(
    NetInspektorEvent.Response(
        requestUuid = request.uuid,
        statusCode = 200,
        headers = responseHeaders,
        body = NetInspektorEvent.Body(
            contentType = "application/json",
            data = responseBodyBytes
        )
    )
)
```

### 4. Open the desktop app

Download and run the **netinspektor desktop app**. Your session will appear in the pairing window automatically. Double-click it to connect, or enter the host and port manually.

---

## Platform support

| Platform | netinspektor-core | netinspektor-client |
|---|---|---|
| Android | ✅ | ✅ |
| iOS (arm64, x64, simulatorArm64) | ✅ | ✅ |
| Desktop (JVM) | ✅ | ✅ |

