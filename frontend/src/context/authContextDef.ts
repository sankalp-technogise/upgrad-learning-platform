import { createContext } from 'react'

export interface User {
  id: string
  email: string
  onboardingCompleted: boolean
}

export interface AuthContextType {
  user: User | null
  token: string | null
  login: (token: string, user: User) => void
  logout: () => void
  isAuthenticated: boolean
}

export const AuthContext = createContext<AuthContextType | undefined>(undefined)
