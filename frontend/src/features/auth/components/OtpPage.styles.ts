import type { SxProps, Theme } from '@mui/material/styles'

export const containerStyles: SxProps<Theme> = {
  mt: 8,
  display: 'flex',
  flexDirection: 'column',
  alignItems: 'center',
}

export const paperStyles: SxProps<Theme> = {
  p: 4,
  width: '100%',
  borderRadius: 2,
}

export const titleStyles: SxProps<Theme> = {
  fontWeight: 'bold',
}

export const subtitleStyles: SxProps<Theme> = {
  mt: 1,
  mb: 3,
}

export const formBoxStyles: SxProps<Theme> = {
  mt: 1,
}

export const otpInputContainerStyles: SxProps<Theme> = {
  display: 'flex',
  justifyContent: 'space-between',
  gap: 1,
  mb: 2,
}

export const otpInputStyles: React.CSSProperties = {
  textAlign: 'center',
  fontSize: '1.5rem',
  padding: '10px',
}

export const errorTextStyles: SxProps<Theme> = {
  mt: 1,
}

export const verifyButtonStyles: SxProps<Theme> = {
  mt: 3,
  mb: 2,
  bgcolor: '#4F46E5',
  '&:hover': {
    bgcolor: '#4338ca',
  },
  textTransform: 'none',
  fontWeight: 'bold',
  py: 1.5,
}

export const backButtonStyles: SxProps<Theme> = {
  textTransform: 'none',
  color: 'text.secondary',
}

export const resendButtonStyles: SxProps<Theme> = {
  textTransform: 'none',
  color: '#4F46E5',
  mt: 1,
}
