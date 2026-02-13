import { Box, Card, CardMedia, CardContent, Typography, LinearProgress } from '@mui/material'
import type { ContinueWatchingItem } from '../api/homepageApi'
import { Link } from '@tanstack/react-router'

interface ContinueWatchingSectionProps {
  item: ContinueWatchingItem
}

const styles = {
  section: {
    mb: 4,
  },
  sectionTitle: {
    fontWeight: 700,
    fontSize: '1.4rem',
    mb: 2,
    color: 'text.primary',
  },
  card: {
    display: 'flex',
    borderRadius: 3,
    boxShadow: '0 2px 12px rgba(0,0,0,0.08)',
    overflow: 'hidden',
    cursor: 'pointer',
    transition: 'box-shadow 0.2s ease',
    '&:hover': {
      boxShadow: '0 8px 24px rgba(0,0,0,0.12)',
    },
  },
  media: {
    width: 280,
    minHeight: 160,
    flexShrink: 0,
  },
  content: {
    display: 'flex',
    flexDirection: 'column',
    justifyContent: 'center',
    p: 3,
    flex: 1,
  },
  title: {
    fontWeight: 600,
    fontSize: '1.1rem',
    mb: 0.5,
  },
  description: {
    color: 'text.secondary',
    fontSize: '0.9rem',
    mb: 2,
  },
  progressContainer: {
    display: 'flex',
    alignItems: 'center',
    gap: 1.5,
  },
  progressBar: {
    flex: 1,
    height: 6,
    borderRadius: 3,
    backgroundColor: '#e0e0e0',
    '& .MuiLinearProgress-bar': {
      borderRadius: 3,
      background: 'linear-gradient(90deg, #646cff, #535bf2)',
    },
  },
  progressText: {
    fontSize: '0.8rem',
    color: 'text.secondary',
    fontWeight: 500,
    whiteSpace: 'nowrap',
  },
} as const

export function ContinueWatchingSection({ item }: ContinueWatchingSectionProps) {
  return (
    <Box sx={styles.section} id="continue-watching-section">
      <Typography variant="h5" sx={styles.sectionTitle}>
        Continue Watching
      </Typography>
      <Card sx={styles.card}>
        <Link
          to="/watch/$contentId"
          params={{ contentId: item.contentId }}
          search={{ resume: true }}
          style={{ textDecoration: 'none', color: 'inherit', display: 'flex', width: '100%' }}
        >
          <CardMedia
            component="img"
            sx={styles.media}
            image={item.thumbnailUrl ?? undefined}
            alt={item.title}
          />
          <CardContent sx={styles.content}>
            <Typography variant="h6" sx={styles.title}>
              {item.title}
            </Typography>
            {(item.category || item.episodeNumber) && (
              <Typography
                variant="body2"
                sx={{ color: 'text.secondary', fontSize: '0.85rem', mb: 0.5 }}
              >
                {[
                  item.category
                    ?.replace(/_/g, ' ')
                    .toLowerCase()
                    .replace(/\b\w/g, (c) => c.toUpperCase()),
                  item.episodeNumber ? `Episode ${item.episodeNumber}` : null,
                ]
                  .filter(Boolean)
                  .join(' · ')}
              </Typography>
            )}
            <Typography variant="body2" sx={styles.description}>
              {item.description}
            </Typography>
            <Box sx={styles.progressContainer}>
              <LinearProgress
                variant="determinate"
                value={item.progressPercent}
                sx={styles.progressBar}
              />
              <Typography sx={styles.progressText}>{item.progressPercent}% complete</Typography>
            </Box>
          </CardContent>
        </Link>
      </Card>
    </Box>
  )
}
