import { describe, it, expect } from 'vitest'
import { renderHook } from '@testing-library/react'
import { useAuth } from './useAuth'
import { AuthProvider } from './AuthContext'
import type { ReactNode } from 'react'

// Mock authApi for AuthProvider
import { vi } from 'vitest'
vi.mock('@/features/auth/api/authApi', () => ({
  authApi: {
    getMe: vi.fn().mockRejectedValue(new Error('Not authenticated')),
    logout: vi.fn(),
  },
}))

describe('useAuth', () => {
  it('should return auth context when used within AuthProvider', async () => {
    const { result } = renderHook(() => useAuth(), {
      wrapper: ({ children }: { children: ReactNode }) => <AuthProvider>{children}</AuthProvider>,
    })

    // Wait for provider to initialize
    await new Promise((resolve) => setTimeout(resolve, 100))

    expect(result.current).toBeDefined()
    expect(result.current).toHaveProperty('user')
    expect(result.current).toHaveProperty('token')
    expect(result.current).toHaveProperty('login')
    expect(result.current).toHaveProperty('logout')
    expect(result.current).toHaveProperty('isAuthenticated')
  })

  it('should throw error when used outside AuthProvider', () => {
    // Suppress console.error for this test
    const consoleErrorSpy = vi.spyOn(console, 'error').mockImplementation(() => {})

    expect(() => {
      renderHook(() => useAuth())
    }).toThrow('useAuth must be used within an AuthProvider')

    consoleErrorSpy.mockRestore()
  })
})
