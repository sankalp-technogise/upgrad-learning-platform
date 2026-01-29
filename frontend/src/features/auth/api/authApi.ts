import { apiClient } from '@/lib/apiClient'

export interface OtpRequest {
  email: string
}

export interface LoginRequest {
  email: string
  otp: string
}

export interface AuthResponse {
  token: string
  user: {
    id: string
    email: string
    onboardingCompleted: boolean
  }
}

export const authApi = {
  requestOtp: async (email: string) => {
    const { data } = await apiClient.post<void>('/auth/otp', { email } as OtpRequest)
    return data
  },

  login: async (email: string, otp: string) => {
    const { data } = await apiClient.post<AuthResponse>('/auth/login', {
      email,
      otp,
    } as LoginRequest)
    return data
  },

  logout: async () => {
    await apiClient.post('/auth/logout')
  },

  getMe: async () => {
    const { data } = await apiClient.get<AuthResponse['user']>('/auth/me')
    return data
  },
}
