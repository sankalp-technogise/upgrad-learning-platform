import React, { useState, type ReactNode } from 'react'
import { AuthContext, type User } from './authContextDef'
import { authApi } from '../features/auth/api/authApi'

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null)
  const [token, setToken] = useState<string | null>(null) // Token is now just an in-memory flag or can be removed if not needed for headers
  const [isLoading, setIsLoading] = useState(true)

  React.useEffect(() => {
    const restoreSession = async () => {
      try {
        const user = await authApi.getMe()
        setUser(user)
        setToken('in-cookie') // Placeholder to indicate auth state
      } catch {
        // Not authenticated
      } finally {
        setIsLoading(false)
      }
    }
    restoreSession()
  }, [])

  const login = (newToken: string, newUser: User) => {
    // Cookie is set by the server
    setToken(newToken || 'in-cookie')
    setUser(newUser)
  }

  const logout = async () => {
    try {
      await authApi.logout()
    } catch (error) {
      console.error('Logout failed', error)
    }
    setToken(null)
    setUser(null)
  }

  if (isLoading) {
    return null // or a loading spinner
  }

  return (
    <AuthContext.Provider value={{ user, token, login, logout, isAuthenticated: !!user }}>
      {children}
    </AuthContext.Provider>
  )
}
