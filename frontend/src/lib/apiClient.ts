import axios from 'axios'

export const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_URL || '/api',
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
  },
})

// CSRF token handling for Spring Security
// Manually extract and decode the XSRF-TOKEN cookie since
// axios's built-in handling doesn't decode URL-encoded tokens
apiClient.interceptors.request.use((config) => {
  const cookies = document.cookie.split('; ')
  const xsrfCookie = cookies.find((cookie) => cookie.startsWith('XSRF-TOKEN='))
  if (xsrfCookie) {
    const token = decodeURIComponent(xsrfCookie.split('=')[1])
    config.headers['X-XSRF-TOKEN'] = token
  }
  return config
})
