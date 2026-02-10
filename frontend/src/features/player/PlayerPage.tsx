import { useState, useEffect } from 'react'
import { useParams } from '@tanstack/react-router'
import { Box, Typography, CircularProgress } from '@mui/material'
import { contentApi, type ContentDetail } from './api/contentApi'
import { VideoPlayer } from './components/VideoPlayer'

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
      <Box
        sx={{
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
          height: '100vh',
          bgcolor: 'black',
        }}
      >
        <CircularProgress />
      </Box>
    )
  }

  if (error || !content) {
    return (
      <Box
        sx={{
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
          height: '100vh',
          bgcolor: 'black',
          color: 'white',
        }}
      >
        <Typography variant="h6">{error || 'Content not found'}</Typography>
      </Box>
    )
  }

  return (
    <Box sx={{ width: '100vw', height: '100vh', bgcolor: 'black' }}>
      <VideoPlayer
        src={content.videoUrl}
        poster={content.thumbnailUrl}
        title={content.title}
        episodeNumber={content.episodeNumber}
        duration={content.durationSeconds}
        muted
      />
    </Box>
  )
}
