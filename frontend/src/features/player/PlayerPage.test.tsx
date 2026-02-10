import { render, screen, waitFor } from '@testing-library/react'
import { PlayerPage } from './PlayerPage'
import { contentApi } from './api/contentApi'
import { vi, describe, it, expect, beforeEach, type Mock } from 'vitest'
import { useParams } from '@tanstack/react-router'
import '@testing-library/jest-dom'

vi.mock('./api/contentApi', () => ({
  contentApi: {
    getContent: vi.fn(),
  },
}))

vi.mock('./components/VideoPlayer', () => ({
  VideoPlayer: ({ title }: { title: string }) => <div data-testid="video-player">{title}</div>,
}))

vi.mock('@tanstack/react-router', () => ({
  useParams: vi.fn(),
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

  beforeEach(() => {
    vi.clearAllMocks()
    ;(useParams as Mock).mockReturnValue({ contentId: '123' })
  })

  it('shows loading state initially', () => {
    ;(contentApi.getContent as Mock).mockReturnValue(new Promise(() => {}))

    render(<PlayerPage />)
    expect(screen.getByRole('progressbar')).toBeInTheDocument()
  })

  it('renders video player when content loads successfully', async () => {
    ;(contentApi.getContent as Mock).mockResolvedValue(mockContent)

    render(<PlayerPage />)

    await waitFor(() => {
      expect(screen.getByTestId('video-player')).toBeInTheDocument()
      expect(screen.getByText('Test Video')).toBeInTheDocument()
    })
  })

  it('shows error message when fetch fails', async () => {
    ;(contentApi.getContent as Mock).mockRejectedValue(new Error('Network error'))

    render(<PlayerPage />)

    await waitFor(() => {
      expect(screen.getByText('Failed to load video. Please try again.')).toBeInTheDocument()
    })
  })
})
