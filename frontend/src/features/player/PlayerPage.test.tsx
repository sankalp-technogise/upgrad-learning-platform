import { render, screen, waitFor } from '@testing-library/react'
import { PlayerPage } from './PlayerPage'
import { contentApi } from './api/contentApi'
import { watchProgressApi } from './api/watchProgressApi'
import { vi, describe, it, expect, beforeEach, type Mock } from 'vitest'
import { useParams, useNavigate, useSearch } from '@tanstack/react-router'
import '@testing-library/jest-dom'

vi.mock('./api/contentApi', () => ({
  contentApi: {
    getContent: vi.fn(),
    getNextEpisode: vi.fn(),
  },
}))

vi.mock('./api/watchProgressApi', () => ({
  watchProgressApi: {
    getProgress: vi.fn(),
    saveProgress: vi.fn(),
  },
}))

vi.mock('./components/VideoPlayer', () => ({
  VideoPlayer: ({ title, initialTime }: { title: string; initialTime?: number }) => (
    <div data-testid="video-player" data-initial-time={initialTime}>
      {title}
    </div>
  ),
}))

vi.mock('@tanstack/react-router', () => ({
  useParams: vi.fn(),
  useNavigate: vi.fn(),
  useSearch: vi.fn(),
}))

describe('PlayerPage', () => {
  const mockContent = {
    id: '123',
    title: 'Test Video',
    description: 'Desc',
    thumbnailUrl: 'thumb.jpg',
    videoUrl: 'video.mp4',
    category: 'TECH',
    episodeNumber: 1,
    durationSeconds: 60,
    createdAt: '2023-01-01',
  }

  const mockNavigate = vi.fn()

  beforeEach(() => {
    vi.clearAllMocks()
    ;(useParams as Mock).mockReturnValue({ contentId: '123' })
    ;(useNavigate as Mock).mockReturnValue(mockNavigate)
    ;(useSearch as Mock).mockReturnValue({ resume: undefined })
  })

  it('shows loading state initially', () => {
    ;(contentApi.getContent as Mock).mockReturnValue(new Promise(() => {}))
    ;(watchProgressApi.getProgress as Mock).mockReturnValue(new Promise(() => {}))

    render(<PlayerPage />)
    expect(screen.getByRole('progressbar')).toBeInTheDocument()
  })

  it('renders video player when content loads successfully', async () => {
    ;(contentApi.getContent as Mock).mockResolvedValue(mockContent)
    ;(watchProgressApi.getProgress as Mock).mockResolvedValue(null)

    render(<PlayerPage />)

    await waitFor(() => {
      expect(screen.getByTestId('video-player')).toBeInTheDocument()
      expect(screen.getByText('Test Video')).toBeInTheDocument()
    })
  })

  it('shows error message when fetch fails', async () => {
    ;(contentApi.getContent as Mock).mockRejectedValue(new Error('Network error'))
    ;(watchProgressApi.getProgress as Mock).mockRejectedValue(new Error('fail'))

    render(<PlayerPage />)

    await waitFor(() => {
      expect(screen.getByText('Failed to load video. Please try again.')).toBeInTheDocument()
    })
  })

  it('resumes from saved position when progress exists', async () => {
    ;(contentApi.getContent as Mock).mockResolvedValue(mockContent)
    ;(watchProgressApi.getProgress as Mock).mockResolvedValue({
      contentId: '123',
      progressPercent: 50,
      lastWatchedPosition: 30,
    })

    render(<PlayerPage />)

    await waitFor(() => {
      const player = screen.getByTestId('video-player')
      expect(player).toBeInTheDocument()
      expect(player).toHaveAttribute('data-initial-time', '30')
    })
  })

  it('redirects to next episode when resume is true and episode is completed', async () => {
    ;(useSearch as Mock).mockReturnValue({ resume: true })
    ;(contentApi.getContent as Mock).mockResolvedValue(mockContent)
    ;(watchProgressApi.getProgress as Mock).mockResolvedValue({
      contentId: '123',
      progressPercent: 100,
      lastWatchedPosition: 60,
    })
    ;(contentApi.getNextEpisode as Mock).mockResolvedValue({
      ...mockContent,
      id: '456',
      title: 'Next Episode',
      episodeNumber: 2,
    })

    render(<PlayerPage />)

    await waitFor(() => {
      expect(mockNavigate).toHaveBeenCalledWith({
        to: '/watch/$contentId',
        params: { contentId: '456' },
        search: { resume: true },
      })
    })
  })

  it('renders completed episode player when resume is not set', async () => {
    ;(useSearch as Mock).mockReturnValue({ resume: undefined })
    ;(contentApi.getContent as Mock).mockResolvedValue(mockContent)
    ;(watchProgressApi.getProgress as Mock).mockResolvedValue({
      contentId: '123',
      progressPercent: 100,
      lastWatchedPosition: 60,
    })

    render(<PlayerPage />)

    await waitFor(() => {
      expect(screen.getByTestId('video-player')).toBeInTheDocument()
    })
    expect(contentApi.getNextEpisode).not.toHaveBeenCalled()
    expect(mockNavigate).not.toHaveBeenCalled()
  })

  it('shows current content when resume is true, completed, but no next episode exists', async () => {
    ;(useSearch as Mock).mockReturnValue({ resume: true })
    ;(contentApi.getContent as Mock).mockResolvedValue(mockContent)
    ;(watchProgressApi.getProgress as Mock).mockResolvedValue({
      contentId: '123',
      progressPercent: 100,
      lastWatchedPosition: 60,
    })
    ;(contentApi.getNextEpisode as Mock).mockResolvedValue(null)

    render(<PlayerPage />)

    await waitFor(() => {
      expect(screen.getByTestId('video-player')).toBeInTheDocument()
    })
    expect(mockNavigate).not.toHaveBeenCalled()
  })
})
