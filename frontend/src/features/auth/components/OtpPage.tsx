import React, { useState } from 'react'
import { Box, Button, TextField, Typography, Paper, Container } from '@mui/material'
import { useNavigate, useSearch } from '@tanstack/react-router'
import { authApi } from '@/features/auth/api/authApi'
import { useAuth } from '@/context/useAuth'

export const OtpPage: React.FC = () => {
  const [otp, setOtp] = useState<string[]>(new Array(6).fill(''))
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  // Use TanStack Router search params validation
  const { email } = useSearch({ from: '/auth/otp' })

  const navigate = useNavigate()

  // Removed useEffect for sessionStorage since we rely on URL params now
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
      setError('OTP must be exactly 6 digits')
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
      setError('Invalid OTP or expired.')
    } finally {
      setIsLoading(false)
    }
  }

  if (!email) {
    return null
  }

  return (
    <Container maxWidth="xs">
      <Box sx={{ mt: 8, display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
        <Paper elevation={3} sx={{ p: 4, width: '100%', borderRadius: 2 }}>
          <Typography
            component="h1"
            variant="h5"
            align="center"
            gutterBottom
            sx={{ fontWeight: 'bold' }}
          >
            Login or Sign Up
          </Typography>
          <Typography variant="body2" color="text.secondary" align="center" sx={{ mt: 1 }}>
            Enter your email to receive a login code.
          </Typography>
          <Typography variant="body2" color="text.secondary" align="center" sx={{ mb: 3 }}>
            Enter the 6-digit code sent to {email}.
          </Typography>

          <Box component="form" onSubmit={handleSubmit} sx={{ mt: 1 }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', gap: 1, mb: 2 }}>
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
                    style: { textAlign: 'center', fontSize: '1.5rem', padding: '10px' },
                  }}
                  autoFocus={index === 0}
                  error={!!error}
                />
              ))}
            </Box>
            {error && (
              <Typography color="error" variant="body2" align="center" sx={{ mt: 1 }}>
                {error}
              </Typography>
            )}

            <Button
              type="submit"
              fullWidth
              variant="contained"
              sx={{
                mt: 3,
                mb: 2,
                bgcolor: '#4F46E5', // approximate Purple from screenshot
                '&:hover': {
                  bgcolor: '#4338ca',
                },
                textTransform: 'none',
                fontWeight: 'bold',
                py: 1.5,
              }}
              disabled={isLoading || otp.some((digit) => digit === '')}
            >
              {isLoading ? 'Verifying...' : 'Verify & Continue'}
            </Button>
            <Button
              fullWidth
              variant="text"
              onClick={() => navigate({ to: '/login' })}
              sx={{ textTransform: 'none', color: 'text.secondary' }}
            >
              Back to email
            </Button>
          </Box>
        </Paper>
      </Box>
    </Container>
  )
}
