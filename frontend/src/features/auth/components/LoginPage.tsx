import React, { useState } from 'react'
import { Box, Button, TextField, Typography, Paper, Container } from '@mui/material'
import { useNavigate } from '@tanstack/react-router'
import { authApi } from '@/features/auth/api/authApi'
import {
  AUTH_EMAIL_STORAGE_KEY,
  BUTTON_SEND_OTP,
  BUTTON_SENDING,
  ERROR_SEND_OTP,
  OTP_ROUTE_PATH,
} from '../constants'
import { loginPaperStyles, pageContainerStyles, submitButtonStyles } from './LoginPage.styles'

export const LoginPage: React.FC = () => {
  const [email, setEmail] = useState('')
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const navigate = useNavigate()

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setIsLoading(true)
    setError(null)

    try {
      await authApi.requestOtp(email)
      sessionStorage.setItem(AUTH_EMAIL_STORAGE_KEY, email)
      navigate({ to: OTP_ROUTE_PATH, search: { email } })
    } catch (err) {
      console.error(err)
      setError(ERROR_SEND_OTP)
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <Box sx={pageContainerStyles}>
      <Container maxWidth="xs">
        <Paper elevation={0} sx={loginPaperStyles}>
          <Typography
            component="h1"
            variant="h5"
            align="center"
            sx={{ fontWeight: 700, mb: 1, color: '#263238' }}
          >
            Login or Sign Up
          </Typography>
          <Typography variant="body2" color="text.secondary" align="center" sx={{ mb: 5 }}>
            Enter your email to receive a login code.
          </Typography>

          <Box component="form" onSubmit={handleSubmit}>
            <Typography variant="subtitle2" sx={{ mb: 1, fontWeight: 600, color: '#37474f' }}>
              Email Address
            </Typography>
            <TextField
              required
              fullWidth
              id="email"
              placeholder="you@example.com"
              name="email"
              autoComplete="email"
              autoFocus
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              error={!!error}
              helperText={error}
              InputProps={{
                sx: { borderRadius: 1.5 },
              }}
            />
            <Button
              type="submit"
              fullWidth
              variant="contained"
              size="large"
              sx={submitButtonStyles}
              disabled={isLoading}
            >
              {isLoading ? BUTTON_SENDING : BUTTON_SEND_OTP}
            </Button>
          </Box>
        </Paper>
      </Container>
    </Box>
  )
}
