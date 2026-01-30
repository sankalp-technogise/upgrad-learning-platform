import React, { useState } from 'react'
import { Box, Button, TextField, Typography, Paper, Container } from '@mui/material'
import { useNavigate, useSearch } from '@tanstack/react-router'
import { authApi } from '@/features/auth/api/authApi'
import { useAuth } from '@/context/useAuth'
import {
  ERROR_OTP_LENGTH,
  ERROR_INVALID_OTP,
  ERROR_RATE_LIMIT_DEFAULT,
  ERROR_RESEND_FAILED,
  BUTTON_VERIFYING,
  BUTTON_VERIFY,
  BUTTON_BACK,
  BUTTON_RESEND,
  BUTTON_SENDING,
} from '@/features/auth/constants'
import {
  containerStyles,
  paperStyles,
  titleStyles,
  subtitleStyles,
  formBoxStyles,
  otpInputContainerStyles,
  otpInputStyles,
  errorTextStyles,
  verifyButtonStyles,
  backButtonStyles,
  resendButtonStyles,
} from './OtpPage.styles'

export const OtpPage: React.FC = () => {
  const [otp, setOtp] = useState<string[]>(new Array(6).fill(''))
  const [isLoading, setIsLoading] = useState(false)
  const [isResending, setIsResending] = useState(false)
  const [error, setError] = useState<string | null>(null)

  // Use TanStack Router search params validation
  const { email } = useSearch({ from: '/auth/otp' })

  const navigate = useNavigate()

  // Moved useEffect for sessionStorage since we rely on URL params now
  // If email is missing, the route validation would have caught it
  const { login } = useAuth()

  const handleChange = (element: HTMLInputElement, index: number) => {
    if (isNaN(Number(element.value))) return false

    const newOtp = [...otp]
    newOtp[index] = element.value
    setOtp(newOtp)

    // Focus next input
    if (element.value && element.nextSibling) {
      ;(element.nextSibling as HTMLInputElement).focus()
    }
  }

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>, index: number) => {
    if (e.key === 'Backspace') {
      if (!otp[index] && index > 0) {
        const newOtp = [...otp]
        newOtp[index - 1] = '' // Clear previous if current is empty and backspace hit
        setOtp(newOtp)

        // Focus previous input
        const prevParams = e.currentTarget.previousSibling as HTMLInputElement
        if (prevParams) {
          prevParams.focus()
        }
      } else {
        const newOtp = [...otp]
        newOtp[index] = ''
        setOtp(newOtp)
      }
    }
  }

  const handlePaste = (e: React.ClipboardEvent<HTMLInputElement>) => {
    e.preventDefault()
    const val = e.clipboardData.getData('text').replace(/\D/g, '').slice(0, 6)
    if (val) {
      const newOtp = [...otp]
      val.split('').forEach((char, i) => {
        if (i < 6) newOtp[i] = char
      })
      setOtp(newOtp)

      // Focus the last filled input or the first empty one
      // We can't easily set focus directly from here without refs, but the user can click.
      // Optionally we could add refs for each input.
    }
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    const otpValue = otp.join('')

    if (otpValue.length !== 6) {
      setError(ERROR_OTP_LENGTH)
      return
    }

    setIsLoading(true)
    setError(null)

    try {
      const response = await authApi.login(email, otpValue)
      login(response.token, response.user)
      navigate({ to: '/' })
    } catch (err) {
      console.error(err)
      setError(ERROR_INVALID_OTP)
    } finally {
      setIsLoading(false)
    }
  }

  const handleResendOtp = async () => {
    setIsResending(true)
    setError(null)

    try {
      await authApi.requestOtp(email)
      // Clear OTP inputs for new code
      setOtp(new Array(6).fill(''))
    } catch (err: unknown) {
      console.error(err)

      // Check if it's a 429 rate limit error
      if (err && typeof err === 'object' && 'response' in err) {
        const response = (err as { response?: { status?: number; data?: string } }).response
        if (response?.status === 429) {
          setError(response.data || ERROR_RATE_LIMIT_DEFAULT)
        } else {
          setError(ERROR_RESEND_FAILED)
        }
      } else {
        setError(ERROR_RESEND_FAILED)
      }
    } finally {
      setIsResending(false)
    }
  }

  if (!email) {
    return null
  }

  return (
    <Container maxWidth="xs">
      <Box sx={containerStyles}>
        <Paper elevation={3} sx={paperStyles}>
          <Typography component="h1" variant="h5" align="center" gutterBottom sx={titleStyles}>
            Login or Sign Up
          </Typography>
          <Typography variant="body2" color="text.secondary" align="center" sx={subtitleStyles}>
            Enter the 6-digit code sent to {email}.
          </Typography>

          <Box component="form" onSubmit={handleSubmit} sx={formBoxStyles}>
            <Box sx={otpInputContainerStyles}>
              {otp.map((data, index) => (
                <TextField
                  key={index}
                  value={data}
                  onChange={(e) => handleChange(e.target as HTMLInputElement, index)}
                  onKeyDown={(e) =>
                    handleKeyDown(e as React.KeyboardEvent<HTMLInputElement>, index)
                  }
                  onPaste={handlePaste}
                  inputProps={{
                    maxLength: 1,
                    style: otpInputStyles,
                  }}
                  autoFocus={index === 0}
                  error={!!error}
                />
              ))}
            </Box>
            {error && (
              <Typography color="error" variant="body2" align="center" sx={errorTextStyles}>
                {error}
              </Typography>
            )}

            <Button
              type="submit"
              fullWidth
              variant="contained"
              sx={verifyButtonStyles}
              disabled={isLoading || otp.some((digit) => digit === '')}
            >
              {isLoading ? BUTTON_VERIFYING : BUTTON_VERIFY}
            </Button>
            <Button
              fullWidth
              variant="text"
              onClick={() => navigate({ to: '/login' })}
              sx={backButtonStyles}
            >
              {BUTTON_BACK}
            </Button>
            <Button
              fullWidth
              variant="text"
              onClick={handleResendOtp}
              disabled={isResending}
              sx={resendButtonStyles}
            >
              {isResending ? BUTTON_SENDING : BUTTON_RESEND}
            </Button>
          </Box>
        </Paper>
      </Box>
    </Container>
  )
}
