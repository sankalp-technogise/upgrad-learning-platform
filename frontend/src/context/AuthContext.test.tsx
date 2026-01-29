import { describe, it, expect, vi, beforeEach } from 'vitest'
import { waitFor, renderHook } from '@testing-library/react'
import { AuthProvider } from './AuthContext'
import { authApi } from '@/features/auth/api/authApi'
import type { ReactNode } from 'react'
import { useContext } from 'react'
import { AuthContext } from './authContextDef'

// Mock the authApi
vi.mock('@/features/auth/api/authApi', () => ({
  authApi: {
    getMe: vi.fn(),
    logout: vi.fn(),
  },
}))

describe('AuthContext', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should initialize with loading state and then set user to null when not authenticated', async () => {
    vi.mocked(authApi.getMe).mockRejectedValue(new Error('Not authenticated'))

    const { result } = renderHook(() => useContext(AuthContext), {
      wrapper: ({ children }: { children: ReactNode }) => <AuthProvider>{children}</AuthProvider>,
    })

    // Wait for the provider to finish loading and set authentication state
    await waitFor(() => {
      expect(result.current).toBeDefined()
      expect(result.current?.user).toBeNull()
      expect(result.current?.token).toBeNull()
      expect(result.current?.isAuthenticated).toBe(false)
    })
  })

  it('should call authApi.getMe on mount to restore session', async () => {
    vi.mocked(authApi.getMe).mockRejectedValue(new Error('Not authenticated'))

    renderHook(() => useContext(AuthContext), {
      wrapper: ({ children }: { children: ReactNode }) => <AuthProvider>{children}</AuthProvider>,
    })

    await waitFor(() => {
      expect(authApi.getMe).toHaveBeenCalledOnce()
    })
  })

  it('should set user and token when session is valid', async () => {
    const mockUser = { id: '1', email: 'test@example.com' }
    vi.mocked(authApi.getMe).mockResolvedValue(mockUser)

    const { result } = renderHook(() => useContext(AuthContext), {
      wrapper: ({ children }: { children: ReactNode }) => <AuthProvider>{children}</AuthProvider>,
    })

    await waitFor(() => {
      expect(result.current?.user).toEqual(mockUser)
      expect(result.current?.token).toBe('in-cookie')
      expect(result.current?.isAuthenticated).toBe(true)
    })
  })

  it('should update user and token when login is called', async () => {
    vi.mocked(authApi.getMe).mockRejectedValue(new Error('Not authenticated'))

    const { result } = renderHook(() => useContext(AuthContext), {
      wrapper: ({ children }: { children: ReactNode }) => <AuthProvider>{children}</AuthProvider>,
    })

    // Wait for provider to finish loading
    await waitFor(() => {
      expect(result.current).toBeDefined()
      expect(result.current?.user).toBeNull()
    })

    const newUser = { id: '2', email: 'new@example.com' }
    const newToken = 'new-token'

    // Call login
    result.current!.login(newToken, newUser)

    await waitFor(() => {
      expect(result.current?.user).toEqual(newUser)
      expect(result.current?.token).toBe(newToken)
      expect(result.current?.isAuthenticated).toBe(true)
    })
  })

  it('should clear user and token when logout is called', async () => {
    const mockUser = { id: '1', email: 'test@example.com' }
    vi.mocked(authApi.getMe).mockResolvedValue(mockUser)
    vi.mocked(authApi.logout).mockResolvedValue(undefined)

    const { result } = renderHook(() => useContext(AuthContext), {
      wrapper: ({ children }: { children: ReactNode }) => <AuthProvider>{children}</AuthProvider>,
    })

    // Wait for user to be set
    await waitFor(() => {
      expect(result.current?.user).toEqual(mockUser)
    })

    // Call logout
    await result.current!.logout()

    await waitFor(() => {
      expect(authApi.logout).toHaveBeenCalledOnce()
      expect(result.current?.user).toBeNull()
      expect(result.current?.token).toBeNull()
      expect(result.current?.isAuthenticated).toBe(false)
    })
  })

  it('should handle logout API failure gracefully', async () => {
    const mockUser = { id: '1', email: 'test@example.com' }
    vi.mocked(authApi.getMe).mockResolvedValue(mockUser)
    vi.mocked(authApi.logout).mockRejectedValue(new Error('Logout failed'))

    const consoleErrorSpy = vi.spyOn(console, 'error').mockImplementation(() => {})

    const { result } = renderHook(() => useContext(AuthContext), {
      wrapper: ({ children }: { children: ReactNode }) => <AuthProvider>{children}</AuthProvider>,
    })

    // Wait for user to be set
    await waitFor(() => {
      expect(result.current?.user).toEqual(mockUser)
    })

    // Call logout - should still clear state even if API fails
    await result.current!.logout()

    await waitFor(() => {
      expect(consoleErrorSpy).toHaveBeenCalledWith('Logout failed', expect.any(Error))
      expect(result.current?.user).toBeNull()
      expect(result.current?.token).toBeNull()
    })

    consoleErrorSpy.mockRestore()
  })

  it('should set isAuthenticated to true when user exists', async () => {
    const mockUser = { id: '1', email: 'test@example.com' }
    vi.mocked(authApi.getMe).mockResolvedValue(mockUser)

    const { result } = renderHook(() => useContext(AuthContext), {
      wrapper: ({ children }: { children: ReactNode }) => <AuthProvider>{children}</AuthProvider>,
    })

    await waitFor(() => {
      expect(result.current?.isAuthenticated).toBe(true)
    })
  })
})
