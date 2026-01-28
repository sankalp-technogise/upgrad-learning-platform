import { Button, Container, Typography, Box } from '@mui/material'
import { useAuth } from '@/context/AuthContext'
import { useNavigate } from '@tanstack/react-router'

export const HomePage = () => {
  const { logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate({ to: '/' })
  }

  return (
    <Container maxWidth="md">
      <Box sx={{ mt: 8, textAlign: 'center' }}>
        <Typography variant="h3" component="h1" gutterBottom>
          Home Screen
        </Typography>
        <Typography variant="body1" sx={{ mb: 4 }}>
          Welcome back! You are successfully logged in.
        </Typography>
        <Button variant="outlined" color="primary" onClick={handleLogout}>
          Logout
        </Button>
      </Box>
    </Container>
  )
}
