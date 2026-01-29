import React, { useState } from 'react'
import { Box, Button, TextField, Typography, Paper, Container } from '@mui/material'
import { useNavigate } from '@tanstack/react-router'
import { authApi } from '@/features/auth/api/authApi'

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
      sessionStorage.setItem('auth_email', email)
      navigate({ to: '/auth/otp', search: { email } })
    } catch (err) {
      console.error(err)
      setError('Failed to send OTP. Please try again.')
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <Box
      sx={{
        minHeight: '100vh',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        backgroundColor: '#f5f5f5',
      }}
    >
      <Container maxWidth="xs">
        <Paper
          elevation={0}
          sx={{
            p: 5,
            width: '100%',
            borderRadius: 3,
            boxShadow: '0 4px 20px rgba(0, 0, 0, 0.08)',
          }}
        >
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
              sx={{
                mt: 4,
                mb: 1,
                py: 1.5,
                textTransform: 'none',
                fontSize: '1rem',
                fontWeight: 600,
                borderRadius: 2,
                backgroundColor: '#5c6bc0',
                '&:hover': {
                  backgroundColor: '#3f51b5',
                },
              }}
              disabled={isLoading}
            >
              {isLoading ? 'Sending...' : 'Send OTP'}
            </Button>
          </Box>
        </Paper>
      </Container>
    </Box>
  )
}
