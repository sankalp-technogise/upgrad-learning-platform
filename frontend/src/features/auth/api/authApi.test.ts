import { describe, it, expect, vi, beforeEach } from 'vitest'
import { authApi } from './authApi'
import { apiClient } from '@/lib/apiClient'

// Mock the apiClient
vi.mock('@/lib/apiClient', () => ({
  apiClient: {
    post: vi.fn(),
    get: vi.fn(),
  },
}))

describe('authApi', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  describe('requestOtp', () => {
    it('should send POST request to /auth/otp with email', async () => {
      const email = 'test@example.com'
      vi.mocked(apiClient.post).mockResolvedValue({ data: undefined })

      await authApi.requestOtp(email)

      expect(apiClient.post).toHaveBeenCalledWith('/auth/otp', { email })
    })

    it('should return void on success', async () => {
      vi.mocked(apiClient.post).mockResolvedValue({ data: undefined })

      const result = await authApi.requestOtp('test@example.com')

      expect(result).toBeUndefined()
    })
  })

  describe('login', () => {
    it('should send POST request to /auth/login with email and otp', async () => {
      const email = 'test@example.com'
      const otp = '123456'
      const mockResponse = {
        data: {
          token: 'test-token',
          user: { id: '1', email },
        },
      }
      vi.mocked(apiClient.post).mockResolvedValue(mockResponse)

      await authApi.login(email, otp)

      expect(apiClient.post).toHaveBeenCalledWith('/auth/login', { email, otp })
    })

    it('should return token and user data', async () => {
      const mockResponse = {
        data: {
          token: 'test-token',
          user: { id: '1', email: 'test@example.com' },
        },
      }
      vi.mocked(apiClient.post).mockResolvedValue(mockResponse)

      const result = await authApi.login('test@example.com', '123456')

      expect(result).toEqual(mockResponse.data)
      expect(result.token).toBe('test-token')
      expect(result.user.id).toBe('1')
      expect(result.user.email).toBe('test@example.com')
    })
  })

  describe('logout', () => {
    it('should send POST request to /auth/logout', async () => {
      vi.mocked(apiClient.post).mockResolvedValue({ data: undefined })

      await authApi.logout()

      expect(apiClient.post).toHaveBeenCalledWith('/auth/logout')
    })
  })

  describe('getMe', () => {
    it('should send GET request to /auth/me', async () => {
      const mockUser = { id: '1', email: 'test@example.com' }
      vi.mocked(apiClient.get).mockResolvedValue({ data: mockUser })

      await authApi.getMe()

      expect(apiClient.get).toHaveBeenCalledWith('/auth/me')
    })

    it('should return user data', async () => {
      const mockUser = { id: '1', email: 'test@example.com' }
      vi.mocked(apiClient.get).mockResolvedValue({ data: mockUser })

      const result = await authApi.getMe()

      expect(result).toEqual(mockUser)
      expect(result.id).toBe('1')
      expect(result.email).toBe('test@example.com')
    })
  })
})
