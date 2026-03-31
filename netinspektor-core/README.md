# netinspektor-core

Shared data models and constants for the netinspektor network inspection tool. This module is a dependency of both `netinspektor-client` (embedded in your app) and `netinspektor-desktop` (the desktop viewer).

---

## Installation

```kotlin
// build.gradle.kts
commonMain.dependencies {
    implementation("com.evertwoud.netinspektor:netinspektor-core:{version}")
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

## API reference

### `NetInspektorEvent`

Sealed interface representing a single network event. All events carry a UUID, timestamp (epoch milliseconds), headers, and an optional body.

```kotlin
sealed interface NetInspektorEvent {
    val uuid: String
    val timestamp: Long
    val headers: Map<String, String>
    val body: Body?
}
```

#### `NetInspektorEvent.Request`

Represents an outgoing HTTP request.

| Property | Type | Description |
|---|---|---|
| `uuid` | `String` | Unique identifier, auto-generated |
| `timestamp` | `Long` | Creation time in epoch milliseconds |
| `method` | `String` | HTTP method (e.g. `GET`, `POST`) |
| `url` | `String` | Full request URL |
| `headers` | `Map<String, String>` | Request headers |
| `body` | `Body?` | Optional request body |

#### `NetInspektorEvent.Response`

Represents an incoming HTTP response.

| Property | Type | Description |
|---|---|---|
| `uuid` | `String` | Unique identifier, auto-generated |
| `timestamp` | `Long` | Creation time in epoch milliseconds |
| `requestUuid` | `String?` | UUID of the matching request |
| `statusCode` | `Int` | HTTP status code (e.g. `200`, `404`) |
| `statusDescription` | `String?` | Optional status description |
| `headers` | `Map<String, String>` | Response headers |
| `body` | `Body?` | Optional response body |

#### `NetInspektorEvent.Body`

```kotlin
data class Body(
    val contentType: String?,
    val data: ByteArray?
)
```

---

### `NetInspektorConstants`

Shared constants for ports and WebSocket paths.

| Constant | Value | Description |
|---|---|---|
| `SERVICE_TYPE` | `_netinspektor._tcp` | mDNS/Bonjour service type |
| `DISCOVERY_SERVER_PATH` | `/netinspektor/discovery` | WebSocket path for discovery |
| `EVENT_SERVER_PATH` | `/netinspektor/events` | WebSocket path for event streaming |
| `DISCOVERY_SERVER_PORT` | `8129` | Port the discovery server listens on |
| `AUTO_ASSIGNED_PORT` | `0` | Tells the OS to auto-assign an available port |

---

### Socket message models

| Class | Description |
|---|---|
| `NetInspektorSocketMessage<T>` | Generic typed WebSocket message with a `type: String` and `data: T` |
| `NetInspektorDevice` | Describes a connected client device: `sessionName`, `platform`, `host`, `port` |
| `NetInspektorMetadata` | Session metadata sent on connection: `uuid`, `name`, `platform` |
| `NetInspektorSessionHistory` | Snapshot of all requests and responses for a session |
| `NetInspektorInitializeMessage` | Sent by the desktop client to initialise a session: `uuid` |
