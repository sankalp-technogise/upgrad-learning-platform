import React, { useState } from 'react'
import { Box, Button, TextField, Typography, Paper, Container } from '@mui/material'
import { useNavigate } from '@tanstack/react-router'
import { authApi } from '@/features/auth/api/authApi' // Assuming aliasing is setup or use relative

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
      // Navigate to OTP page
      navigate({ to: '/auth/otp', search: { email } })
    } catch (err) {
      console.error(err)
      setError('Failed to send OTP. Please try again.')
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <Container maxWidth="xs">
      <Box sx={{ mt: 8, display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
        <Paper elevation={3} sx={{ p: 4, width: '100%', borderRadius: 2 }}>
          <Typography component="h1" variant="h5" align="center" sx={{ fontWeight: 'bold', mb: 1 }}>
            Login or Sign Up
          </Typography>
          <Typography variant="body2" color="text.secondary" align="center" sx={{ mb: 4 }}>
            Enter your email to receive a login code.
          </Typography>

          <Box component="form" onSubmit={handleSubmit}>
            <Typography variant="subtitle2" sx={{ mb: 1, fontWeight: 'bold' }}>
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
                sx: { borderRadius: 1 },
              }}
            />
            <Button
              type="submit"
              fullWidth
              variant="contained"
              size="large"
              sx={{
                mt: 3,
                mb: 1,
                py: 1.5,
                textTransform: 'none',
                fontSize: '1rem',
                fontWeight: 600,
              }}
              disabled={isLoading}
            >
              {isLoading ? 'Sending...' : 'Send OTP'}
            </Button>
          </Box>
        </Paper>
      </Box>
    </Container>
  )
}
