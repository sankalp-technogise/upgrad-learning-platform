import React, { useState } from 'react'
import { Box, Button, TextField, Typography, Paper, Container } from '@mui/material'
import { useNavigate, useSearch } from '@tanstack/react-router'
import { authApi } from '@/features/auth/api/authApi'
import { useAuth } from '@/context/useAuth'

export const OtpPage: React.FC = () => {
  const [otp, setOtp] = useState('')
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  // Use TanStack Router search params validation (assuming defined in route)
  // For now, loose typing or we define strict interface in route
  // We'll trust the route definition (step to come)
  const search: { email?: string } = useSearch({ strict: false })
  const email = search.email || ''

  const navigate = useNavigate()
  const { login } = useAuth()

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    if (otp.length !== 6) {
      setError('OTP must be exactly 6 digits')
      return
    }

    setIsLoading(true)
    setError(null)

    try {
      const response = await authApi.login(email, otp)
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
    return <Typography color="error">Email missing from navigation state.</Typography>
  }

  return (
    <Container maxWidth="xs">
      <Box sx={{ mt: 8, display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
        <Paper elevation={3} sx={{ p: 4, width: '100%', borderRadius: 2 }}>
          <Typography component="h1" variant="h5" align="center" gutterBottom>
            Verify OTP
          </Typography>
          <Typography variant="body2" color="text.secondary" align="center" sx={{ mb: 3 }}>
            Enter the 6-digit code sent to {email}
          </Typography>

          <Box component="form" onSubmit={handleSubmit} sx={{ mt: 1 }}>
            <TextField
              margin="normal"
              required
              fullWidth
              name="otp"
              label="OTP Code"
              type="text"
              id="otp"
              autoFocus
              value={otp}
              onChange={(e) => {
                const val = e.target.value.replace(/\D/g, '').slice(0, 6)
                setOtp(val)
              }}
              error={!!error}
              helperText={error}
              inputProps={{
                maxLength: 6,
                letterSpacing: 4,
                style: { textAlign: 'center', fontSize: '1.2rem' },
              }}
            />
            <Button
              type="submit"
              fullWidth
              variant="contained"
              sx={{ mt: 3, mb: 2 }}
              disabled={isLoading || otp.length !== 6}
            >
              {isLoading ? 'Verifying...' : 'Verify'}
            </Button>
            <Button fullWidth variant="text" onClick={() => navigate({ to: '/login' })}>
              Back to Login
            </Button>
          </Box>
        </Paper>
      </Box>
    </Container>
  )
}
