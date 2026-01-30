import type { SxProps, Theme } from '@mui/material/styles'

export const pageContainerStyles: SxProps<Theme> = {
  minHeight: '100vh',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  backgroundColor: '#f5f5f5',
}

export const loginPaperStyles: SxProps<Theme> = {
  p: 5,
  width: '100%',
  borderRadius: 3,
  boxShadow: '0 4px 20px rgba(0, 0, 0, 0.08)',
}

export const submitButtonStyles: SxProps<Theme> = {
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
}
