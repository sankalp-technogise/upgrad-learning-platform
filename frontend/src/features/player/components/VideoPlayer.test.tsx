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
    // Mock HTMLMediaElement methods — play() must return a Promise
    Object.defineProperty(window.HTMLMediaElement.prototype, 'play', {
      writable: true,
      value: vi.fn().mockImplementation(function (this: HTMLMediaElement) {
        Object.defineProperty(this, 'paused', { value: false, writable: true, configurable: true })
        return Promise.resolve()
      }),
    })
    Object.defineProperty(window.HTMLMediaElement.prototype, 'pause', {
      writable: true,
      value: vi.fn().mockImplementation(function (this: HTMLMediaElement) {
        Object.defineProperty(this, 'paused', { value: true, writable: true, configurable: true })
      }),
    })
  })

  it('renders video player with basic controls', () => {
    render(<VideoPlayer {...defaultProps} />)

    expect(screen.getByRole('slider', { name: /seek/i })).toBeInTheDocument()
  })

  it('toggles play/pause on click', async () => {
    render(<VideoPlayer {...defaultProps} />)
    const video = screen.getByTestId('video-element')

    // Video starts paused in JSDOM, click should call play()
    fireEvent.click(video)
    expect(window.HTMLMediaElement.prototype.play).toHaveBeenCalled()

    // After play() mock sets paused=false, next click should call pause()
    await vi.waitFor(() => {
      fireEvent.click(video)
      expect(window.HTMLMediaElement.prototype.pause).toHaveBeenCalled()
    })
  })

  it('calls onEnded when video ended event fires', () => {
    const onEnded = vi.fn()
    render(<VideoPlayer {...defaultProps} onEnded={onEnded} />)
    const video = screen.getByTestId('video-element')

    fireEvent.ended(video)
    expect(onEnded).toHaveBeenCalledTimes(1)
  })
})
