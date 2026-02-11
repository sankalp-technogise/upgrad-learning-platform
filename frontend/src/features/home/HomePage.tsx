import { useState, useEffect } from 'react'
import { Container, Typography, Box, CircularProgress, IconButton, Avatar } from '@mui/material'
import { Notifications as NotificationsIcon } from '@mui/icons-material'
import { useAuth } from '@/context/useAuth'
import { homepageApi, type HomepageSections } from './api/homepageApi'
import { ContentCard } from './components/ContentCard'
import { ContinueWatchingSection } from './components/ContinueWatchingSection'
import { styles } from './HomePage.styles'

export const HomePage = () => {
  const { user } = useAuth()
  const [sections, setSections] = useState<HomepageSections | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    const fetchHomepage = async () => {
      try {
        setLoading(true)
        const data = await homepageApi.getSections()
        setSections(data)
        setError(null)
      } catch (err) {
        setError('Failed to load homepage. Please refresh the page.')
        console.error('Failed to fetch homepage:', err)
      } finally {
        setLoading(false)
      }
    }
    fetchHomepage()
  }, [])

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

  if (error) {
    return (
      <Box sx={styles.pageBackground}>
        <Container maxWidth="lg">
          <Box sx={styles.errorContainer}>
            <Typography color="error" variant="h6">
              {error}
            </Typography>
          </Box>
        </Container>
      </Box>
    )
  }

  return (
    <Box sx={styles.pageBackground} id="homepage">
      <Container maxWidth="lg">
        <Box sx={styles.contentPanel}>
          {/* Header */}
          <Box sx={styles.header}>
            <Typography variant="h5" sx={styles.headerTitle}>
              Recommended For You
            </Typography>
            <Box sx={styles.headerActions}>
              <IconButton aria-label="notifications" sx={styles.notificationIcon}>
                <NotificationsIcon />
              </IconButton>
              <IconButton sx={styles.avatarButton} aria-label="User profile">
                <Avatar alt={user?.email} sx={{ width: 36, height: 36 }} />
              </IconButton>
            </Box>
          </Box>

          {/* Continue Watching (conditional) */}
          {sections?.continueWatching && (
            <ContinueWatchingSection item={sections.continueWatching} />
          )}

          {/* Interest-based Recommendations */}
          {sections?.recommended && sections.recommended.length > 0 && (
            <Box sx={styles.sectionBox} id="recommended-section">
              <Typography variant="h5" sx={styles.sectionTitle}>
                Recommended for you
              </Typography>
              <Box sx={styles.grid}>
                {sections.recommended.map((content) => (
                  <ContentCard key={content.id} content={content} />
                ))}
              </Box>
            </Box>
          )}

          {/* Exploration */}
          {sections?.exploration && sections.exploration.length > 0 && (
            <Box sx={styles.sectionBox} id="exploration-section">
              <Typography variant="h5" sx={styles.sectionTitle}>
                Explore New Topics
              </Typography>
              <Box sx={styles.grid}>
                {sections.exploration.map((content) => (
                  <ContentCard key={content.id} content={content} />
                ))}
              </Box>
            </Box>
          )}
        </Box>
      </Container>
    </Box>
  )
}
