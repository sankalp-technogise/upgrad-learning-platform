import { useState, useEffect } from 'react'
import { useParams } from '@tanstack/react-router'
import { Box, Container, Typography, CircularProgress, IconButton } from '@mui/material'
import { ArrowBack } from '@mui/icons-material'
import { Link } from '@tanstack/react-router'
import { contentApi, type ContentDetail } from './api/contentApi'

const styles = {
  pageBackground: {
    minHeight: '100vh',
    backgroundColor: '#f8f9fa',
    pt: 4,
    pb: 8,
  },
  playerContainer: {
    maxWidth: 'md',
    mx: 'auto',
  },
  videoWrapper: {
    position: 'relative' as const,
    paddingTop: '56.25%', // 16:9 Aspect Ratio
    backgroundColor: '#000',
    borderRadius: 3,
    overflow: 'hidden',
    boxShadow: '0 4px 20px rgba(0,0,0,0.15)',
    mb: 3,
  },
  video: {
    position: 'absolute' as const,
    top: 0,
    left: 0,
    width: '100%',
    height: '100%',
  },
  header: {
    mb: 2,
    display: 'flex',
    alignItems: 'center',
    gap: 2,
  },
  backButton: {
    color: '#1a1a2e',
  },
  title: {
    fontWeight: 700,
    color: '#1a1a2e',
  },
  description: {
    color: '#555',
    lineHeight: 1.6,
  },
  errorContainer: {
    display: 'flex',
    justifyContent: 'center',
    pt: 10,
  },
  loadingContainer: {
    display: 'flex',
    justifyContent: 'center',
    pt: 10,
  },
} as const

export const PlayerPage = () => {
  const { contentId } = useParams({ from: '/watch/$contentId' })
  const [content, setContent] = useState<ContentDetail | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    const fetchContent = async () => {
      try {
        setLoading(true)
        const data = await contentApi.getContent(contentId)
        setContent(data)
      } catch (err) {
        console.error('Failed to load content:', err)
        setError('Failed to load video. Please try again.')
      } finally {
        setLoading(false)
      }
    }

    if (contentId) {
      fetchContent()
    }
  }, [contentId])

  if (loading) {
    return (
      <Box sx={styles.pageBackground}>
        <Container maxWidth="lg">
          <Box sx={styles.loadingContainer}>
            <CircularProgress />
          </Box>
        </Container>
      </Box>
    )
  }

  if (error || !content) {
    return (
      <Box sx={styles.pageBackground}>
        <Container maxWidth="lg">
          <Box sx={styles.errorContainer}>
            <Typography color="error">{error || 'Content not found'}</Typography>
          </Box>
        </Container>
      </Box>
    )
  }

  return (
    <Box sx={styles.pageBackground}>
      <Container maxWidth="lg">
        <Box sx={styles.header}>
          <Link to="/home">
            <IconButton sx={styles.backButton}>
              <ArrowBack />
            </IconButton>
          </Link>
          <Typography variant="h5" sx={styles.title}>
            {content.title}
          </Typography>
        </Box>

        <Box sx={styles.playerContainer}>
          <Box sx={styles.videoWrapper}>
            <video
              controls
              autoPlay
              src={content.videoUrl}
              style={styles.video}
              poster={content.thumbnailUrl}
            >
              Your browser does not support the video tag.
            </video>
          </Box>

          <Typography variant="body1" sx={styles.description}>
            {content.description}
          </Typography>
        </Box>
      </Container>
    </Box>
  )
}
