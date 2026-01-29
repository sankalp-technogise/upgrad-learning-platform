import { describe, it, expect, vi, beforeEach } from 'vitest'
import { screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { render } from '@/test/test-utils'
import { LoginPage } from './LoginPage'
import { authApi } from '@/features/auth/api/authApi'

// Mock the authApi
vi.mock('@/features/auth/api/authApi', () => ({
  authApi: {
    requestOtp: vi.fn(),
  },
}))

// Mock TanStack Router
const mockNavigate = vi.fn()
vi.mock('@tanstack/react-router', () => ({
  useNavigate: () => mockNavigate,
}))

// Mock sessionStorage
const sessionStorageMock = (() => {
  let store: Record<string, string> = {}
  return {
    getItem: (key: string) => store[key] || null,
    setItem: (key: string, value: string) => {
      store[key] = value
    },
    removeItem: (key: string) => {
      delete store[key]
    },
    clear: () => {
      store = {}
    },
  }
})()

Object.defineProperty(window, 'sessionStorage', {
  value: sessionStorageMock,
})

describe('LoginPage', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    sessionStorageMock.clear()
  })

  it('should render email input field and submit button', () => {
    render(<LoginPage />)

    expect(screen.getByRole('heading', { name: /login or sign up/i })).toBeInTheDocument()
    expect(screen.getByPlaceholderText(/you@example.com/i)).toBeInTheDocument()
    expect(screen.getByRole('button', { name: /send otp/i })).toBeInTheDocument()
  })

  it('should update email input value when user types', async () => {
    const user = userEvent.setup()
    render(<LoginPage />)

    const emailInput = screen.getByPlaceholderText(/you@example.com/i) as HTMLInputElement

    await user.type(emailInput, 'test@example.com')

    expect(emailInput.value).toBe('test@example.com')
  })

  it('should call authApi.requestOtp and navigate on successful submission', async () => {
    const user = userEvent.setup()
    const email = 'test@example.com'
    vi.mocked(authApi.requestOtp).mockResolvedValue(undefined)

    render(<LoginPage />)

    const emailInput = screen.getByPlaceholderText(/you@example.com/i)
    const submitButton = screen.getByRole('button', { name: /send otp/i })

    await user.type(emailInput, email)
    await user.click(submitButton)

    await waitFor(() => {
      expect(authApi.requestOtp).toHaveBeenCalledWith(email)
    })

    await waitFor(() => {
      expect(sessionStorageMock.getItem('auth_email')).toBe(email)
      expect(mockNavigate).toHaveBeenCalledWith({
        to: '/auth/otp',
        search: { email },
      })
    })
  })

  it('should display error message when API call fails', async () => {
    const user = userEvent.setup()
    vi.mocked(authApi.requestOtp).mockRejectedValue(new Error('Network error'))

    render(<LoginPage />)

    const emailInput = screen.getByPlaceholderText(/you@example.com/i)
    const submitButton = screen.getByRole('button', { name: /send otp/i })

    await user.type(emailInput, 'test@example.com')
    await user.click(submitButton)

    await waitFor(() => {
      expect(screen.getByText(/failed to send otp/i)).toBeInTheDocument()
    })
  })

  it('should disable button and show loading state during submission', async () => {
    const user = userEvent.setup()
    let resolveOtp: () => void
    const otpPromise = new Promise<void>((resolve) => {
      resolveOtp = resolve
    })
    vi.mocked(authApi.requestOtp).mockReturnValue(otpPromise)

    render(<LoginPage />)

    const emailInput = screen.getByPlaceholderText(/you@example.com/i)
    const submitButton = screen.getByRole('button', { name: /send otp/i })

    await user.type(emailInput, 'test@example.com')
    await user.click(submitButton)

    // Button should show loading state
    expect(screen.getByRole('button', { name: /sending/i })).toBeInTheDocument()
    expect(screen.getByRole('button', { name: /sending/i })).toBeDisabled()

    // Resolve the promise
    resolveOtp!()

    await waitFor(() => {
      expect(screen.getByRole('button', { name: /send otp/i })).toBeInTheDocument()
    })
  })

  it('should prevent form submission if email is empty', async () => {
    const user = userEvent.setup()
    render(<LoginPage />)

    const submitButton = screen.getByRole('button', { name: /send otp/i })

    await user.click(submitButton)

    // Browser's HTML5 validation should prevent submission
    expect(authApi.requestOtp).not.toHaveBeenCalled()
  })
})
