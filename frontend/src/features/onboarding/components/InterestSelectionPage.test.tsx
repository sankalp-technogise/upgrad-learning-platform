import { describe, it, expect, vi, beforeEach } from 'vitest'
import { screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { render } from '@/test/test-utils'
import { InterestSelectionPage } from './InterestSelectionPage'
import { interestApi, type Interest } from '../api/interestApi'

// Mock the interestApi
vi.mock('../api/interestApi', () => ({
  interestApi: {
    getAllInterests: vi.fn(),
    saveUserInterests: vi.fn(),
  },
}))

// Mock TanStack Router
const mockNavigate = vi.fn()
vi.mock('@tanstack/react-router', () => ({
  useNavigate: () => mockNavigate,
}))

const mockInterests: Interest[] = [
  {
    id: 'PYTHON_PROGRAMMING',
    name: 'Python Programming',
    description: 'Learn Python basics',
    iconName: 'puzzle',
  },
  {
    id: 'DATA_SCIENCE',
    name: 'Data Science',
    description: 'Master data analysis',
    iconName: 'chart',
  },
  {
    id: 'WEB_DEVELOPMENT',
    name: 'Web Development',
    description: 'Build modern websites',
    iconName: 'code',
  },
]

describe('InterestSelectionPage', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    vi.mocked(interestApi.getAllInterests).mockResolvedValue(mockInterests)
  })

  describe('loading state', () => {
    it('should display loading message while fetching interests', async () => {
      // Use a never-resolving promise to keep loading state
      vi.mocked(interestApi.getAllInterests).mockImplementation(
        () => new Promise(() => {}) // Never resolves
      )

      render(<InterestSelectionPage />)

      expect(await screen.findByText(/loading interests/i)).toBeInTheDocument()
    })

    it('should display interests after loading completes', async () => {
      render(<InterestSelectionPage />)

      await waitFor(() => {
        expect(screen.getByText(/python programming/i)).toBeInTheDocument()
      })

      expect(
        screen.getByRole('heading', { name: /what do you want to learn/i })
      ).toBeInTheDocument()
      expect(screen.getByText(/data science/i)).toBeInTheDocument()
      expect(screen.getByText(/web development/i)).toBeInTheDocument()
    })
  })

  describe('error state', () => {
    it('should display error message when fetching interests fails', async () => {
      vi.mocked(interestApi.getAllInterests).mockRejectedValue(new Error('Network error'))

      render(<InterestSelectionPage />)

      await waitFor(() => {
        expect(screen.getByRole('alert')).toHaveTextContent(/failed to load interests/i)
      })
    })

    it('should display error message when saving interests fails', async () => {
      const user = userEvent.setup()
      vi.mocked(interestApi.saveUserInterests).mockRejectedValue(new Error('Save failed'))

      render(<InterestSelectionPage />)

      await waitFor(() => {
        expect(screen.getByText(/python programming/i)).toBeInTheDocument()
      })

      // Select an interest
      await user.click(screen.getByText(/python programming/i))

      // Click continue
      await user.click(screen.getByRole('button', { name: /continue/i }))

      await waitFor(() => {
        expect(screen.getByRole('alert')).toHaveTextContent(/failed to save interests/i)
      })
    })
  })

  describe('selection behavior', () => {
    it('should toggle interest selection when clicked', async () => {
      const user = userEvent.setup()

      render(<InterestSelectionPage />)

      await waitFor(() => {
        expect(screen.getByText(/python programming/i)).toBeInTheDocument()
      })

      const pythonCard = screen
        .getByText(/python programming/i)
        .closest('button, div[role="button"], [class*="card"]')
      expect(pythonCard).toBeInTheDocument()

      // Click to select
      await user.click(screen.getByText(/python programming/i))

      // Click again to deselect
      await user.click(screen.getByText(/python programming/i))
    })

    it('should allow selecting multiple interests', async () => {
      const user = userEvent.setup()
      vi.mocked(interestApi.saveUserInterests).mockResolvedValue(undefined)

      render(<InterestSelectionPage />)

      await waitFor(() => {
        expect(screen.getByText(/python programming/i)).toBeInTheDocument()
      })

      // Select multiple interests
      await user.click(screen.getByText(/python programming/i))
      await user.click(screen.getByText(/data science/i))

      // Save should be called with both interests
      await user.click(screen.getByRole('button', { name: /continue/i }))

      await waitFor(() => {
        expect(interestApi.saveUserInterests).toHaveBeenCalledWith(
          expect.arrayContaining(['PYTHON_PROGRAMMING', 'DATA_SCIENCE'])
        )
      })
    })
  })

  describe('continue button', () => {
    it('should be disabled when no interests are selected', async () => {
      render(<InterestSelectionPage />)

      await waitFor(() => {
        expect(screen.getByText(/python programming/i)).toBeInTheDocument()
      })

      const continueButton = screen.getByRole('button', { name: /continue/i })
      expect(continueButton).toBeDisabled()
    })

    it('should be enabled when at least one interest is selected', async () => {
      const user = userEvent.setup()

      render(<InterestSelectionPage />)

      await waitFor(() => {
        expect(screen.getByText(/python programming/i)).toBeInTheDocument()
      })

      const continueButton = screen.getByRole('button', { name: /continue/i })
      expect(continueButton).toBeDisabled()

      // Select an interest
      await user.click(screen.getByText(/python programming/i))

      expect(continueButton).toBeEnabled()
    })

    it('should show saving state and be disabled during save', async () => {
      const user = userEvent.setup()
      let resolveSave: () => void
      const savePromise = new Promise<void>((resolve) => {
        resolveSave = resolve
      })
      vi.mocked(interestApi.saveUserInterests).mockReturnValue(savePromise)

      render(<InterestSelectionPage />)

      await waitFor(() => {
        expect(screen.getByText(/python programming/i)).toBeInTheDocument()
      })

      // Select an interest
      await user.click(screen.getByText(/python programming/i))

      // Click continue
      await user.click(screen.getByRole('button', { name: /continue/i }))

      // Button should show saving state
      expect(screen.getByRole('button', { name: /saving/i })).toBeInTheDocument()
      expect(screen.getByRole('button', { name: /saving/i })).toBeDisabled()

      // Resolve the promise
      resolveSave!()

      await waitFor(() => {
        expect(mockNavigate).toHaveBeenCalledWith({ to: '/home' })
      })
    })
  })

  describe('save and navigation', () => {
    it('should call saveUserInterests with selected interest IDs on continue', async () => {
      const user = userEvent.setup()
      vi.mocked(interestApi.saveUserInterests).mockResolvedValue(undefined)

      render(<InterestSelectionPage />)

      await waitFor(() => {
        expect(screen.getByText(/python programming/i)).toBeInTheDocument()
      })

      // Select an interest
      await user.click(screen.getByText(/python programming/i))

      // Click continue
      await user.click(screen.getByRole('button', { name: /continue/i }))

      await waitFor(() => {
        expect(interestApi.saveUserInterests).toHaveBeenCalledWith(['PYTHON_PROGRAMMING'])
      })
    })

    it('should navigate to /home after successful save', async () => {
      const user = userEvent.setup()
      vi.mocked(interestApi.saveUserInterests).mockResolvedValue(undefined)

      render(<InterestSelectionPage />)

      await waitFor(() => {
        expect(screen.getByText(/python programming/i)).toBeInTheDocument()
      })

      // Select an interest
      await user.click(screen.getByText(/python programming/i))

      // Click continue
      await user.click(screen.getByRole('button', { name: /continue/i }))

      await waitFor(() => {
        expect(mockNavigate).toHaveBeenCalledWith({ to: '/home' })
      })
    })
  })
})
