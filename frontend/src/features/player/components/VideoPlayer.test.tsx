import { fireEvent, render, screen } from '@testing-library/react'
import { VideoPlayer } from './VideoPlayer'
import { vi, describe, it, expect, beforeEach } from 'vitest'

describe('VideoPlayer', () => {
  const defaultProps = {
    src: 'http://example.com/video.mp4',
    poster: 'http://example.com/poster.jpg',
    title: 'Test Video',
    episodeNumber: 1,
    duration: 120,
  }

  beforeEach(() => {
    // Mock HTMLMediaElement properties and methods
    Object.defineProperty(window.HTMLMediaElement.prototype, 'play', {
      writable: true,
      value: vi.fn(),
    })
    Object.defineProperty(window.HTMLMediaElement.prototype, 'pause', {
      writable: true,
      value: vi.fn(),
    })
  })

  it('renders video player with basic controls', () => {
    render(<VideoPlayer {...defaultProps} />)

    expect(screen.getByRole('slider', { name: /seek/i })).toBeInTheDocument()
  })

  it('toggles play/pause on click', () => {
    render(<VideoPlayer {...defaultProps} />)
    const video = screen.getByTestId('video-element')

    // Simulate click on video
    fireEvent.click(video)
    expect(window.HTMLMediaElement.prototype.play).toHaveBeenCalled()

    fireEvent.click(video)
    expect(window.HTMLMediaElement.prototype.pause).toHaveBeenCalled()
  })

  // Add more tests for volume, fullscreen, etc.
})
