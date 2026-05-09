import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:48124',
        changeOrigin: true,
        // Strip the /api prefix when forwarding to the backend
        // (backend already has context-path /api)
        rewrite: (path) => path,
      },
    },
  },
})
