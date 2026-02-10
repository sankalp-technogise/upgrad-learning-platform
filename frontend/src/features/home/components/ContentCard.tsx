import { Card, CardMedia, CardContent, Typography } from '@mui/material'
import { Link } from '@tanstack/react-router'
import type { ContentItem } from '../api/homepageApi'

interface ContentCardProps {
  content: ContentItem
}

const styles = {
  card: {
    borderRadius: 3,
    border: '1px solid #e8e8e8',
    boxShadow: 'none',
    transition: 'transform 0.2s ease, box-shadow 0.2s ease',
    cursor: 'pointer',
    overflow: 'hidden',
    backgroundColor: '#ffffff',
    '&:hover': {
      transform: 'translateY(-3px)',
      boxShadow: '0 4px 16px rgba(0,0,0,0.10)',
    },
  },
  media: {
    height: 170,
    objectFit: 'cover' as const,
  },
  content: {
    p: 2,
    '&:last-child': {
      pb: 2,
    },
  },
  title: {
    fontWeight: 700,
    fontSize: '0.95rem',
    lineHeight: 1.4,
    mb: 0.5,
    color: '#1a1a2e',
  },
  description: {
    color: '#757575',
    fontSize: '0.82rem',
    lineHeight: 1.5,
    display: '-webkit-box',
    WebkitLineClamp: 3,
    WebkitBoxOrient: 'vertical' as const,
    overflow: 'hidden',
  },
} as const

export function ContentCard({ content }: ContentCardProps) {
  return (
    <Card sx={styles.card} id={`content-card-${content.id}`}>
      <Link
        to="/watch/$contentId"
        params={{ contentId: content.id }}
        style={{ textDecoration: 'none', color: 'inherit', display: 'block' }}
      >
        <CardMedia
          component="img"
          sx={styles.media}
          image={content.thumbnailUrl}
          alt={content.title}
        />
        <CardContent sx={styles.content}>
          <Typography variant="subtitle1" sx={styles.title}>
            {content.title}
          </Typography>
          <Typography variant="body2" sx={styles.description}>
            {content.description}
          </Typography>
        </CardContent>
      </Link>
    </Card>
  )
}
