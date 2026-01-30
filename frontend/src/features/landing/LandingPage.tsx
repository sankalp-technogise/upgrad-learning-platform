import { Button, Container, Typography, Box } from '@mui/material'
import { Link } from '@tanstack/react-router'
import SchoolIcon from '@mui/icons-material/School'
import landingIllustration from '@/assets/landing_illustration.png'

export const LandingPage = () => {
  return (
    <Box sx={containerStyles}>
      <Container maxWidth="sm" sx={{ textAlign: 'center' }}>
        <SchoolIcon sx={{ fontSize: 60, color: '#5c6bc0', mb: 2 }} />

        <Typography variant="h3" component="h1" sx={{ fontWeight: 700, mb: 1, color: '#263238' }}>
          Welcome to LearnSphere
        </Typography>

        <Typography variant="body1" sx={{ color: '#607d8b', mb: 4 }}>
          Your personalized journey to knowledge starts here. Dive into curated video series and
          master new skills at your own pace.
        </Typography>

        <Box
          component="img"
          src={landingIllustration}
          alt="Students learning together"
          sx={imageStyles}
        />

        <Link to="/login" style={{ textDecoration: 'none' }}>
          <Button variant="contained" size="large" sx={buttonStyles}>
            Get Started
          </Button>
        </Link>
      </Container>
    </Box>
  )
}

const containerStyles = {
  minHeight: '100vh',
  display: 'flex',
  flexDirection: 'column',
  alignItems: 'center',
  justifyContent: 'center',
  backgroundColor: '#f5f5f5',
  px: 2,
}

const imageStyles = {
  width: '100%',
  maxWidth: 500,
  borderRadius: 2,
  mb: 4,
  boxShadow: 3,
}

const buttonStyles = {
  px: 5,
  py: 1.5,
  textTransform: 'none',
  fontSize: '1rem',
  backgroundColor: '#5c6bc0',
  '&:hover': {
    backgroundColor: '#3f51b5',
  },
}
