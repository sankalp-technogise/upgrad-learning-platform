import { useState, useRef, useEffect, useCallback } from 'react'
import { Box, Typography, IconButton, Slider, Stack } from '@mui/material'
import {
  PlayArrow,
  Pause,
  VolumeUp,
  VolumeOff,
  Fullscreen,
  FullscreenExit,
} from '@mui/icons-material'

interface VideoPlayerProps {
  src: string
  poster?: string
  title: string
  episodeNumber?: number
  duration?: number
  muted?: boolean
}

const formatTime = (time: number) => {
  const minutes = Math.floor(time / 60)
  const seconds = Math.floor(time % 60)
  return `${minutes}:${seconds.toString().padStart(2, '0')}`
}

export const VideoPlayer = ({
  src,
  poster,
  title,
  episodeNumber,
  duration: initialDuration,
  muted: initialMuted,
}: VideoPlayerProps) => {
  const videoRef = useRef<HTMLVideoElement>(null)
  const containerRef = useRef<HTMLDivElement>(null)
  const [playing, setPlaying] = useState(false)
  const [currentTime, setCurrentTime] = useState(0)
  const [duration, setDuration] = useState(initialDuration || 0)
  const [volume, setVolume] = useState(1)
  const [muted, setMuted] = useState(initialMuted || false)
  const [isFullscreen, setIsFullscreen] = useState(false)
  const [showControls, setShowControls] = useState(true)
  const controlsTimeoutRef = useRef<ReturnType<typeof setTimeout> | null>(null)

  const handlePlayPause = useCallback(() => {
    if (videoRef.current) {
      if (playing) {
        videoRef.current.pause()
      } else {
        videoRef.current.play()
      }
      setPlaying(!playing)
    }
  }, [playing])

  const handleTimeUpdate = useCallback(() => {
    if (videoRef.current) {
      setCurrentTime(videoRef.current.currentTime)
    }
  }, [])

  const handleLoadedMetadata = useCallback(() => {
    if (videoRef.current) {
      setDuration(videoRef.current.duration)
    }
  }, [])

  const handleSeek = (_: Event, value: number | number[]) => {
    if (videoRef.current && typeof value === 'number') {
      videoRef.current.currentTime = value
      setCurrentTime(value)
    }
  }

  const handleVolumeChange = (_: Event, value: number | number[]) => {
    if (videoRef.current && typeof value === 'number') {
      const newVolume = value
      videoRef.current.volume = newVolume
      setVolume(newVolume)
      setMuted(newVolume === 0)
    }
  }

  const toggleMute = useCallback(() => {
    if (videoRef.current) {
      const newMuted = !muted
      videoRef.current.muted = newMuted
      setMuted(newMuted)
      if (newMuted) {
        setVolume(0)
      } else {
        setVolume(1)
        videoRef.current.volume = 1
      }
    }
  }, [muted])

  const toggleFullscreen = useCallback(() => {
    if (!containerRef.current) return

    if (!document.fullscreenElement) {
      containerRef.current.requestFullscreen()
      setIsFullscreen(true)
    } else {
      document.exitFullscreen()
      setIsFullscreen(false)
    }
  }, [])

  const handleMouseMove = useCallback(() => {
    setShowControls(true)
    if (controlsTimeoutRef.current) {
      clearTimeout(controlsTimeoutRef.current)
    }
    controlsTimeoutRef.current = setTimeout(() => {
      if (playing) {
        setShowControls(false)
      }
    }, 3000)
  }, [playing])

  const onPlay = useCallback(() => setPlaying(true), [])
  const onPause = useCallback(() => setPlaying(false), [])

  useEffect(() => {
    const video = videoRef.current
    if (!video) return

    video.addEventListener('timeupdate', handleTimeUpdate)
    video.addEventListener('loadedmetadata', handleLoadedMetadata)
    video.addEventListener('play', onPlay)
    video.addEventListener('pause', onPause)

    // Cleanup
    return () => {
      video.removeEventListener('timeupdate', handleTimeUpdate)
      video.removeEventListener('loadedmetadata', handleLoadedMetadata)
      video.removeEventListener('play', onPlay)
      video.removeEventListener('pause', onPause)
      if (controlsTimeoutRef.current) {
        clearTimeout(controlsTimeoutRef.current)
      }
    }
  }, [handleTimeUpdate, handleLoadedMetadata, onPlay, onPause])

  // Handle fullscreen change events from browser
  useEffect(() => {
    const handleFullscreenChange = () => {
      setIsFullscreen(!!document.fullscreenElement)
    }
    document.addEventListener('fullscreenchange', handleFullscreenChange)
    return () => document.removeEventListener('fullscreenchange', handleFullscreenChange)
  }, [])

  return (
    <Box
      ref={containerRef}
      onMouseMove={handleMouseMove}
      onMouseLeave={() => playing && setShowControls(false)}
      sx={{
        position: 'relative',
        width: '100%',
        height: '100%',
        bgcolor: 'black',
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'center',
        overflow: 'hidden',
      }}
    >
      {/* Video Element */}
      <video
        data-testid="video-element"
        ref={videoRef}
        src={src}
        poster={poster}
        className="w-full h-full object-contain"
        autoPlay
        muted={muted}
        playsInline
        style={{ width: '100%', height: '100%', objectFit: 'contain' }}
        onClick={handlePlayPause}
      />

      {/* Controls Overlay */}
      <Box
        sx={{
          position: 'absolute',
          bottom: 0,
          left: 0,
          right: 0,
          background: 'linear-gradient(to top, rgba(0,0,0,0.7), transparent)',
          p: 2,
          opacity: showControls ? 1 : 0,
          transition: 'opacity 0.3s',
          display: 'flex',
          flexDirection: 'column',
          gap: 1,
        }}
        onClick={(e) => e.stopPropagation()} // Prevent playing when clicking controls
      >
        {/* Progress Bar */}
        <Slider
          aria-label="Seek"
          value={currentTime}
          min={0}
          max={duration || 100}
          onChange={handleSeek}
          size="small"
          sx={{
            color: 'primary.main',
            height: 4,
            '& .MuiSlider-thumb': {
              width: 12,
              height: 12,
              transition: '0.3s cubic-bezier(.47,1.64,.41,.8)',
              '&:before': {
                boxShadow: '0 2px 12px 0 rgba(0,0,0,0.4)',
              },
              '&:hover, &.Mui-focusVisible': {
                boxShadow: '0px 0px 0px 8px rgb(25 118 210 / 16%)',
              },
              '&.Mui-active': {
                width: 16,
                height: 16,
              },
            },
            '& .MuiSlider-rail': {
              opacity: 0.28,
            },
          }}
        />

        {/* Controls Row */}
        <Stack direction="row" alignItems="center" justifyContent="space-between" spacing={2}>
          <Stack direction="row" alignItems="center" spacing={1}>
            <IconButton onClick={handlePlayPause} sx={{ color: 'white' }}>
              {playing ? <Pause /> : <PlayArrow />}
            </IconButton>

            <Stack direction="row" alignItems="center" spacing={1} sx={{ width: 150 }}>
              <IconButton onClick={toggleMute} sx={{ color: 'white' }}>
                {muted || volume === 0 ? <VolumeOff /> : <VolumeUp />}
              </IconButton>
              <Slider
                aria-label="Volume"
                value={muted ? 0 : volume}
                min={0}
                max={1}
                step={0.1}
                onChange={handleVolumeChange}
                size="small"
                sx={{ color: 'white' }}
              />
            </Stack>

            <Typography variant="body2" color="white">
              {formatTime(currentTime)} / {formatTime(duration)}
            </Typography>
          </Stack>

          <Stack direction="row" alignItems="center" spacing={2}>
            {title && (
              <Typography variant="subtitle1" color="white" sx={{ fontWeight: 500 }}>
                {episodeNumber ? `Ep ${episodeNumber}: ` : ''}
                {title}
              </Typography>
            )}
            <IconButton onClick={toggleFullscreen} sx={{ color: 'white' }}>
              {isFullscreen ? <FullscreenExit /> : <Fullscreen />}
            </IconButton>
          </Stack>
        </Stack>
      </Box>
    </Box>
  )
}
