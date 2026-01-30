import { describe, it, expect, vi, beforeEach } from 'vitest'
import { screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { render } from '@/test/test-utils'
import { OtpPage } from './OtpPage'
import { authApi } from '@/features/auth/api/authApi'

// Mock the authApi
vi.mock('@/features/auth/api/authApi', () => ({
  authApi: {
    login: vi.fn(),
    requestOtp: vi.fn(),
  },
}))

// Mock TanStack Router
const mockNavigate = vi.fn()
const mockSearch = { email: 'test@example.com' }
vi.mock('@tanstack/react-router', () => ({
  useNavigate: () => mockNavigate,
  useSearch: () => mockSearch,
}))

// Mock useAuth hook
const mockLogin = vi.fn()
vi.mock('@/context/useAuth', () => ({
  useAuth: () => ({
    login: mockLogin,
    user: null,
    token: null,
    logout: vi.fn(),
    isAuthenticated: false,
  }),
}))

describe('OtpPage', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should render 6 OTP input fields', () => {
    render(<OtpPage />)

    const inputs = screen.getAllByRole('textbox')
    expect(inputs).toHaveLength(6)
  })

  it('should display email in the message', () => {
    render(<OtpPage />)

    expect(screen.getByText(/enter the 6-digit code sent to test@example.com/i)).toBeInTheDocument()
  })

  it('should handle single-digit input correctly', async () => {
    const user = userEvent.setup()
    render(<OtpPage />)

    const inputs = screen.getAllByRole('textbox') as HTMLInputElement[]

    // Type in first input
    await user.type(inputs[0], '1')

    expect(inputs[0].value).toBe('1')
    // Note: Focus management is handled by the component but is difficult to test in JSDOM
  })

  it('should reject non-numeric input', async () => {
    const user = userEvent.setup()
    render(<OtpPage />)

    const inputs = screen.getAllByRole('textbox') as HTMLInputElement[]

    await user.type(inputs[0], 'a')

    expect(inputs[0].value).toBe('')
  })

  it('should handle backspace to clear current field', async () => {
    const user = userEvent.setup()
    render(<OtpPage />)

    const inputs = screen.getAllByRole('textbox') as HTMLInputElement[]

    // Fill first input
    await user.type(inputs[0], '1')
    expect(inputs[0].value).toBe('1')

    // Clear it with backspace
    await user.type(inputs[0], '{Backspace}')
    expect(inputs[0].value).toBe('')
    // Note: Focus management is handled by the component but is difficult to test in JSDOM
  })

  it('should handle paste of 6-digit OTP', async () => {
    const user = userEvent.setup()
    render(<OtpPage />)

    const inputs = screen.getAllByRole('textbox') as HTMLInputElement[]

    // Paste 6-digit OTP
    await user.click(inputs[0])
    await user.paste('123456')

    expect(inputs[0].value).toBe('1')
    expect(inputs[1].value).toBe('2')
    expect(inputs[2].value).toBe('3')
    expect(inputs[3].value).toBe('4')
    expect(inputs[4].value).toBe('5')
    expect(inputs[5].value).toBe('6')
  })

  it('should disable submit button when OTP is incomplete', () => {
    render(<OtpPage />)

    const submitButton = screen.getByRole('button', { name: /verify & continue/i })

    expect(submitButton).toBeDisabled()
  })

  it('should enable submit button when all 6 digits are entered', async () => {
    const user = userEvent.setup()
    render(<OtpPage />)

    const inputs = screen.getAllByRole('textbox')

    // Fill all inputs
    for (let i = 0; i < 6; i++) {
      await user.type(inputs[i], String(i + 1))
    }

    const submitButton = screen.getByRole('button', { name: /verify & continue/i })
    expect(submitButton).not.toBeDisabled()
  })

  it('should call authApi.login with email and OTP on submit', async () => {
    const user = userEvent.setup()
    const mockResponse = {
      token: 'test-token',
      user: { id: '1', email: 'test@example.com' },
    }
    vi.mocked(authApi.login).mockResolvedValue(mockResponse)

    render(<OtpPage />)

    const inputs = screen.getAllByRole('textbox')

    // Fill all inputs
    await user.type(inputs[0], '1')
    await user.type(inputs[1], '2')
    await user.type(inputs[2], '3')
    await user.type(inputs[3], '4')
    await user.type(inputs[4], '5')
    await user.type(inputs[5], '6')

    const submitButton = screen.getByRole('button', { name: /verify & continue/i })
    await user.click(submitButton)

    await waitFor(() => {
      expect(authApi.login).toHaveBeenCalledWith('test@example.com', '123456')
    })
  })

  it('should call login from auth context and navigate to home on success', async () => {
    const user = userEvent.setup()
    const mockResponse = {
      token: 'test-token',
      user: { id: '1', email: 'test@example.com' },
    }
    vi.mocked(authApi.login).mockResolvedValue(mockResponse)

    render(<OtpPage />)

    const inputs = screen.getAllByRole('textbox')

    // Fill all inputs with paste
    await user.click(inputs[0])
    await user.paste('123456')

    const submitButton = screen.getByRole('button', { name: /verify & continue/i })
    await user.click(submitButton)

    await waitFor(() => {
      expect(mockLogin).toHaveBeenCalledWith('test-token', mockResponse.user)
      expect(mockNavigate).toHaveBeenCalledWith({ to: '/' })
    })
  })

  it('should display error message on invalid OTP', async () => {
    const user = userEvent.setup()
    vi.mocked(authApi.login).mockRejectedValue(new Error('Invalid OTP'))

    render(<OtpPage />)

    const inputs = screen.getAllByRole('textbox')

    // Fill all inputs
    await user.click(inputs[0])
    await user.paste('123456')

    const submitButton = screen.getByRole('button', { name: /verify & continue/i })
    await user.click(submitButton)

    await waitFor(() => {
      expect(screen.getByText(/invalid otp or expired/i)).toBeInTheDocument()
    })
  })

  it('should navigate back to login page when "Back to email" is clicked', async () => {
    const user = userEvent.setup()
    render(<OtpPage />)

    const backButton = screen.getByRole('button', { name: /back to email/i })
    await user.click(backButton)

    expect(mockNavigate).toHaveBeenCalledWith({ to: '/login' })
  })

  it('should show loading state during submission', async () => {
    const user = userEvent.setup()
    let resolveLogin: () => void
    const loginPromise = new Promise<{
      token: string
      user: { id: string; email: string }
    }>((resolve) => {
      resolveLogin = () =>
        resolve({ token: 'test-token', user: { id: '1', email: 'test@example.com' } })
    })
    vi.mocked(authApi.login).mockReturnValue(loginPromise)

    render(<OtpPage />)

    const inputs = screen.getAllByRole('textbox')

    // Fill all inputs
    await user.click(inputs[0])
    await user.paste('123456')

    const submitButton = screen.getByRole('button', { name: /verify & continue/i })
    await user.click(submitButton)

    // Button should show verifying state
    expect(screen.getByRole('button', { name: /verifying/i })).toBeInTheDocument()
    expect(screen.getByRole('button', { name: /verifying/i })).toBeDisabled()

    // Resolve the promise
    resolveLogin!()

    await waitFor(() => {
      expect(mockNavigate).toHaveBeenCalled()
    })
  })

  describe('Resend OTP', () => {
    it('should render Resend OTP button', () => {
      render(<OtpPage />)

      const resendButton = screen.getByRole('button', { name: /resend otp/i })
      expect(resendButton).toBeInTheDocument()
    })

    it('should call authApi.requestOtp when Resend OTP is clicked', async () => {
      const user = userEvent.setup()
      vi.mocked(authApi.requestOtp).mockResolvedValue(undefined)

      render(<OtpPage />)

      const resendButton = screen.getByRole('button', { name: /resend otp/i })
      await user.click(resendButton)

      await waitFor(() => {
        expect(authApi.requestOtp).toHaveBeenCalledWith('test@example.com')
      })
    })

    it('should clear OTP inputs after successful resend', async () => {
      const user = userEvent.setup()
      vi.mocked(authApi.requestOtp).mockResolvedValue(undefined)

      render(<OtpPage />)

      const inputs = screen.getAllByRole('textbox') as HTMLInputElement[]

      // Fill all inputs
      await user.click(inputs[0])
      await user.paste('123456')

      // Verify inputs are filled
      expect(inputs[0].value).toBe('1')
      expect(inputs[5].value).toBe('6')

      // Click resend
      const resendButton = screen.getByRole('button', { name: /resend otp/i })
      await user.click(resendButton)

      // Wait for inputs to be cleared
      await waitFor(() => {
        expect(inputs[0].value).toBe('')
        expect(inputs[5].value).toBe('')
      })
    })

    it('should show loading state during resend', async () => {
      const user = userEvent.setup()
      let resolveResend: () => void
      const resendPromise = new Promise<void>((resolve) => {
        resolveResend = resolve
      })
      vi.mocked(authApi.requestOtp).mockReturnValue(resendPromise)

      render(<OtpPage />)

      const resendButton = screen.getByRole('button', { name: /resend otp/i })
      await user.click(resendButton)

      // Button should show sending state
      expect(screen.getByRole('button', { name: /sending/i })).toBeInTheDocument()
      expect(screen.getByRole('button', { name: /sending/i })).toBeDisabled()

      // Resolve the promise
      resolveResend!()

      await waitFor(() => {
        expect(screen.getByRole('button', { name: /resend otp/i })).toBeInTheDocument()
      })
    })

    it('should display error message when resend fails', async () => {
      const user = userEvent.setup()
      vi.mocked(authApi.requestOtp).mockRejectedValue(new Error('Network error'))

      render(<OtpPage />)

      const resendButton = screen.getByRole('button', { name: /resend otp/i })
      await user.click(resendButton)

      await waitFor(() => {
        expect(screen.getByText(/failed to resend otp. please try again./i)).toBeInTheDocument()
      })
    })

    it('should display rate limit error message on 429 response', async () => {
      const user = userEvent.setup()
      const rateLimitError = {
        response: {
          status: 429,
          data: 'Too many OTP requests. Please try again in 45 seconds.',
        },
      }
      vi.mocked(authApi.requestOtp).mockRejectedValue(rateLimitError)

      render(<OtpPage />)

      const resendButton = screen.getByRole('button', { name: /resend otp/i })
      await user.click(resendButton)

      await waitFor(() => {
        expect(
          screen.getByText(/too many otp requests. please try again in 45 seconds./i)
        ).toBeInTheDocument()
      })
    })

    it('should clear error when resend is successful', async () => {
      const user = userEvent.setup()
      // First request fails
      vi.mocked(authApi.login).mockRejectedValue(new Error('Invalid OTP'))

      render(<OtpPage />)

      const inputs = screen.getAllByRole('textbox')

      // Fill and submit to trigger error
      await user.click(inputs[0])
      await user.paste('123456')
      const submitButton = screen.getByRole('button', { name: /verify \u0026 continue/i })
      await user.click(submitButton)

      // Wait for error
      await waitFor(() => {
        expect(screen.getByText(/invalid otp or expired/i)).toBeInTheDocument()
      })

      // Mock successful resend
      vi.mocked(authApi.requestOtp).mockResolvedValue(undefined)

      // Click resend
      const resendButton = screen.getByRole('button', { name: /resend otp/i })
      await user.click(resendButton)

      // Error should be cleared
      await waitFor(() => {
        expect(screen.queryByText(/invalid otp or expired/i)).not.toBeInTheDocument()
      })
    })
  })
})
