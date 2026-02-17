import { useEffect, useCallback } from 'react'
import { Box, Typography, Button, Paper } from '@mui/material'

interface FeedbackModalProps {
  open: boolean
  onSubmit: (helpful: boolean) => void
  onDismiss?: () => void
}

export const FeedbackModal = ({ open, onSubmit, onDismiss }: FeedbackModalProps) => {
  const handleKeyDown = useCallback(
    (e: KeyboardEvent) => {
      if (e.key === 'Escape' && onDismiss) {
        onDismiss()
      }
    },
    [onDismiss]
  )

  useEffect(() => {
    if (open) {
      document.addEventListener('keydown', handleKeyDown)
      return () => document.removeEventListener('keydown', handleKeyDown)
    }
  }, [open, handleKeyDown])

  if (!open) return null

  return (
    <Box
      role="dialog"
      aria-labelledby="feedback-title"
      sx={{
        position: 'absolute',
        bottom: '15%',
        left: '50%',
        transform: 'translateX(-50%)',
        zIndex: 10,
      }}
    >
      <Paper
        elevation={6}
        sx={{
          px: 5,
          py: 3,
          borderRadius: 3,
          textAlign: 'center',
          minWidth: 280,
        }}
      >
        <Typography id="feedback-title" variant="subtitle1" sx={{ fontWeight: 600, mb: 2 }}>
          Did you like this video?
        </Typography>
        <Box sx={{ display: 'flex', gap: 2, justifyContent: 'center' }}>
          <Button
            variant="outlined"
            onClick={() => onSubmit(true)}
            sx={{
              minWidth: 100,
              borderColor: '#a5d6a7',
              color: '#2e7d32',
              bgcolor: '#e8f5e9',
              '&:hover': { bgcolor: '#c8e6c9', borderColor: '#81c784' },
              textTransform: 'none',
              fontWeight: 600,
              borderRadius: 2,
            }}
          >
            Yes
          </Button>
          <Button
            variant="outlined"
            onClick={() => onSubmit(false)}
            sx={{
              minWidth: 100,
              borderColor: '#ef9a9a',
              color: '#c62828',
              bgcolor: '#ffebee',
              '&:hover': { bgcolor: '#ffcdd2', borderColor: '#e57373' },
              textTransform: 'none',
              fontWeight: 600,
              borderRadius: 2,
            }}
          >
            No
          </Button>
        </Box>
      </Paper>
    </Box>
  )
}
