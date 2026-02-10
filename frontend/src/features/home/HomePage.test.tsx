import { render, screen, waitFor } from '@/test/test-utils'
import { HomePage } from './HomePage'
import { homepageApi } from './api/homepageApi'
import { vi, describe, it, expect, beforeEach } from 'vitest'

// Mock the API
vi.mock('./api/homepageApi', () => ({
  homepageApi: {
    getSections: vi.fn(),
  },
}))

// Mock useAuth
vi.mock('@/context/useAuth', () => ({
  useAuth: () => ({
    user: { email: 'test@example.com' },
  }),
}))

// Mock Link from router
vi.mock('@tanstack/react-router', () => ({
  Link: ({ to, params, children }: any) => {
    const url = to.replace('$contentId', params?.contentId || '')
    return (
      <a href={url} data-testid="mock-link">
        {children}
      </a>
    )
  },
}))

describe('HomePage', () => {
  const mockData = {
    continueWatching: undefined,
    recommended: [],
    exploration: [
      {
        id: '123',
        title: 'Exploration Video 1',
        description: 'Desc 1',
        thumbnailUrl: 'url1',
        category: 'Physics',
      },
    ],
  }

  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('renders exploration section when data is available', async () => {
    vi.mocked(homepageApi.getSections).mockResolvedValue(mockData)

    render(<HomePage />)

    await waitFor(() => {
      expect(screen.getByText('Explore New Topics')).toBeInTheDocument()
    })

    expect(screen.getByText('Exploration Video 1')).toBeInTheDocument()
  })

  it('navigates to player when exploration content is clicked', async () => {
    vi.mocked(homepageApi.getSections).mockResolvedValue(mockData)

    render(<HomePage />)

    await waitFor(() => {
      expect(screen.getByText('Exploration Video 1')).toBeInTheDocument()
    })

    const link = screen.getByText('Exploration Video 1').closest('a')
    expect(link).toBeInTheDocument()
    expect(link).toHaveAttribute('href', '/watch/123')
  })
})
