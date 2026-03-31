# netinspektor-desktop

Native desktop application for real-time network inspection. Built with Compose Multiplatform and the [Jewel](https://github.com/JetBrains/jewel) UI toolkit, it receives and displays HTTP traffic streamed from apps that embed **netinspektor-client**.

---

## Running the app

```bash
./gradlew :netinspektor-desktop:run
```

---

## Features

### Session management
- **Multi-session tabs** — connect to multiple apps simultaneously, each in its own tab
- **Automatic discovery** — Android devices connected over USB are discovered automatically via ADB port reversing; other platforms (iOS, desktop) connect over the local network
- **Manual pairing** — enter a host and port manually from the pairing window if auto-discovery is unavailable
- **Session status indicator** — title bar gradient turns green when a session is active, red when idle

### Event list
- **Live event stream** — requests and responses appear in real time
- **Filtering** — type in the filter field to search by URL, method, or status
- **Keyboard navigation** — use ↑ / ↓ arrow keys to move between events
- **Auto-scroll** — toggle auto-scroll to follow new events as they arrive
- **Archive** — clear the current list without losing events; restore from archive at any time

### Event detail
- **Request & response linkage** — click *Go to request* on a response to jump to the matching request, and vice versa
- **Headers** — sorted key-value list of all headers
- **URL** — method and full URL for the originating request
- **Status code** — colour-coded badge (green for 2xx, red for 4xx/5xx, etc.)
- **Duration** — elapsed time between request and response
- **Body viewer** — content rendered based on content type:
  - **JSON** — Original / Pretty-printed / Minified / Structured tree view
  - **Images** — rendered inline
  - **Plain text / HTML / XML** — displayed as monospace text
  - **Binary / unsupported types** — shown with an unsupported content type notice
- **cURL export** — copy the selected request as a ready-to-run `curl` command
- **Copy to clipboard** — copy the full event summary (endpoint, status, headers, body) in one click

### Settings
- **Always on top** — keep the window floating above other applications
- **Format style** — choose the default JSON body format (Original, Pretty, Minified, Structured)