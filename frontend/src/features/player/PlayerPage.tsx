import { useState, useEffect, useRef, useCallback } from 'react'
import { useParams, useNavigate, useSearch } from '@tanstack/react-router'
import { Box, Typography, CircularProgress } from '@mui/material'
import { contentApi, type ContentDetail } from './api/contentApi'
import { watchProgressApi } from './api/watchProgressApi'
import { VideoPlayer, type ProgressUpdateEvent } from './components/VideoPlayer'

const SAVE_INTERVAL_MS = 5000
const API_BASE_URL = import.meta.env.VITE_API_URL || '/api'

const getCsrfToken = (): string | null => {
  if (typeof document === 'undefined') return null
  const cookies = document.cookie.split('; ')
  const xsrfCookie = cookies.find((c) => c.startsWith('XSRF-TOKEN='))
  if (!xsrfCookie) return null
  const eqIndex = xsrfCookie.indexOf('=')
  if (eqIndex === -1) return null
  return decodeURIComponent(xsrfCookie.slice(eqIndex + 1))
}

const sendKeepaliveProgress = (targetContentId: string, event: ProgressUpdateEvent): void => {
  const csrfToken = getCsrfToken()
  fetch(`${API_BASE_URL}/watch-progress`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      ...(csrfToken ? { 'X-XSRF-TOKEN': csrfToken } : {}),
    },
    body: JSON.stringify({
      contentId: targetContentId,
      progressPercent: event.progressPercent,
      lastWatchedPosition: Math.floor(event.currentTime),
    }),
    keepalive: true,
    credentials: 'include',
  })
}

export const PlayerPage = () => {
  const { contentId } = useParams({ from: '/watch/$contentId' })
  const { resume } = useSearch({ from: '/watch/$contentId' })
  const navigate = useNavigate()
  const [content, setContent] = useState<ContentDetail | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [initialTime, setInitialTime] = useState<number | undefined>(undefined)

  const lastSaveTimeRef = useRef(0)
  const latestProgressRef = useRef<ProgressUpdateEvent | null>(null)

  const saveProgress = useCallback(
    async (event: ProgressUpdateEvent) => {
      if (!contentId) return
      try {
        await watchProgressApi.saveProgress(
          contentId,
          event.progressPercent,
          Math.floor(event.currentTime)
        )
      } catch (err) {
        console.error('Failed to save progress:', err)
      }
    },
    [contentId]
  )

  const handleProgressUpdate = useCallback(
    (event: ProgressUpdateEvent) => {
      latestProgressRef.current = event
      const now = Date.now()
      if (now - lastSaveTimeRef.current >= SAVE_INTERVAL_MS) {
        lastSaveTimeRef.current = now
        saveProgress(event)
      }
    },
    [saveProgress]
  )

  useEffect(() => {
    const fetchContentAndProgress = async () => {
      try {
        setError(null)
        setLoading(true)

        const [contentData, progressData] = await Promise.all([
          contentApi.getContent(contentId),
          watchProgressApi.getProgress(contentId).catch(() => null),
        ])

        if (progressData && progressData.progressPercent >= 100 && resume) {
          const nextEpisode = await contentApi.getNextEpisode(contentId)
          if (nextEpisode) {
            navigate({
              to: '/watch/$contentId',
              params: { contentId: nextEpisode.id },
              search: { resume: true },
            })
            return
          }
        }

        setContent(contentData)

        if (progressData && progressData.lastWatchedPosition > 0) {
          setInitialTime(progressData.lastWatchedPosition)
        }
      } catch (err) {
        console.error('Failed to load content:', err)
        setError('Failed to load video. Please try again.')
      } finally {
        setLoading(false)
      }
    }

    if (contentId) {
      fetchContentAndProgress()
    }
  }, [contentId, navigate])

  useEffect(() => {
    const handleBeforeUnload = () => {
      if (latestProgressRef.current && contentId) {
        sendKeepaliveProgress(contentId, latestProgressRef.current)
      }
    }
    window.addEventListener('beforeunload', handleBeforeUnload)
    return () => window.removeEventListener('beforeunload', handleBeforeUnload)
  }, [contentId])

  useEffect(() => {
    const capturedContentId = contentId
    return () => {
      if (latestProgressRef.current && capturedContentId) {
        sendKeepaliveProgress(capturedContentId, latestProgressRef.current)
      }
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
        initialTime={initialTime}
        onProgressUpdate={handleProgressUpdate}
        muted
      />
    </Box>
  )
}
