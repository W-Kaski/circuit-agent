# Frontend

Vue 3 client for the `circuit-agent` demos.

## Responsibilities

- render the landing page and chat surfaces
- connect to backend SSE endpoints
- separate the algorithm assistant and general agent flows

## Runtime

- Node.js `>= 18`
- npm `>= 9`

## Install

```bash
npm install
```

## Develop

```bash
npm run dev
```

## Build

```bash
npm run build
```

## Backend endpoints

- `/api/ai/algorithm_app/chat/sse`
- `/api/ai/manus/chat`

Default backend origin: `http://localhost:8123`

Production nginx config assumes the backend is reachable as `backend:8123` on the same network.
