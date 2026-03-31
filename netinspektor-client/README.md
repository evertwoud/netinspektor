# netinspektor-client

KMP library to embed in your application to capture and stream network events to the **netinspektor desktop** app in real time.

---

## Installation

```kotlin
// build.gradle.kts
commonMain.dependencies {
    implementation("com.evertwoud.netinspektor:netinspektor-client:{version}")
}
```

---

## Platform support

| Platform | Supported |
|---|---|
| Android (minSdk 24) | ✅ |
| iOS (arm64, x64, simulatorArm64) | ✅ |
| Desktop (JVM) | ✅ |

---

## Quick start

### 1. Create and start a session

```kotlin
val session = NetInspektorSession(sessionName = "MyApp")
session.start()
```

Call `session.start()` once when your app initialises. The session will automatically start a local WebSocket server and connect to the netinspektor discovery server so the desktop app can find it.

### 2. Log a request

Before performing your HTTP call, construct a `NetInspektorEvent.Request` and log it:

```kotlin
val request = NetInspektorEvent.Request(
    method = "GET",
    url = "https://api.example.com/users",
    headers = mapOf("Authorization" to "Bearer $token"),
    body = null
)
session.logRequest(request)
```

### 3. Log the response

After receiving the response, log it with the matching `requestUuid`:

```kotlin
session.logResponse(
    NetInspektorEvent.Response(
        requestUuid = request.uuid,
        statusCode = 200,
        statusDescription = "OK",
        headers = responseHeaders,
        body = NetInspektorEvent.Body(
            contentType = "application/json",
            data = responseBodyBytes
        )
    )
)
```

### 4. Stop the session

```kotlin
session.stop()
```

`stop()` clears the session history and shuts down the local WebSocket server.

---

## API reference

### `NetInspektorSession`

The main entry point of the library.

```kotlin
class NetInspektorSession(
    val uuid: String,       // auto-generated
    val sessionName: String // human-readable name shown in the desktop app
)
```

| Method | Description |
|---|---|
| `start()` | Starts the local event server and announces the session to the discovery server |
| `stop()` | Clears history and stops the server |
| `logRequest(request)` | Records a request and streams it to all connected desktop clients |
| `logResponse(response)` | Records a response and streams it to all connected desktop clients |
| `clearHistory()` | Clears accumulated requests and responses without stopping the server |

### `SessionServer`

Embedded Ktor CIO WebSocket server (started automatically by `NetInspektorSession`). Exposes observable state:

| Property | Type | Description |
|---|---|---|
| `running` | `StateFlow<Boolean>` | Whether the server is currently running |
| `port` | `StateFlow<String>` | The port the server is listening on |
| `clientCount` | `StateFlow<Int>` | Number of connected desktop clients |

### `DiscoveryClient`

Connects to the discovery server running in the desktop app (port `8129`) and announces the session so it appears automatically in the pairing window. Reconnects automatically on failure.

---

## How discovery works

```
App (client)                              Desktop App
     │                                         │
     │── WebSocket connect to :8129 ──────────►│
     │── "announce" { sessionName, host, port }│
     │                                         │── device appears in pairing window
```

When the user pairs with the session, the desktop connects directly to the local event server port.
