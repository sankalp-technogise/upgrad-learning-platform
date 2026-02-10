import type { SxProps, Theme } from '@mui/material'

export const styles: Record<string, SxProps<Theme>> = {
  pageBackground: {
    minHeight: '100vh',
    backgroundColor: '#f0f2f5',
    py: 4,
    px: 2,
  },
  contentPanel: {
    backgroundColor: '#ffffff',
    borderRadius: 4,
    boxShadow: '0 1px 4px rgba(0,0,0,0.06)',
    p: { xs: 3, md: 4 },
  },
  header: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    mb: 3,
  },
  headerTitle: {
    fontWeight: 700,
    fontSize: '1.5rem',
    color: '#1a1a2e',
  },
  headerActions: {
    display: 'flex',
    alignItems: 'center',
    gap: 1,
  },
  notificationIcon: {
    color: '#9e9e9e',
  },
  avatarButton: {
    p: 0,
  },
  grid: {
    display: 'grid',
    gridTemplateColumns: {
      xs: '1fr',
      sm: 'repeat(2, 1fr)',
      md: 'repeat(3, 1fr)',
    },
    gap: 3,
  },
  sectionBox: {
    mb: 3,
  },
  sectionTitle: {
    fontWeight: 700,
    fontSize: '1.3rem',
    mb: 2,
    color: '#1a1a2e',
  },
  loadingContainer: {
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    minHeight: '60vh',
  },
  errorContainer: {
    textAlign: 'center',
    mt: 8,
  },
}
