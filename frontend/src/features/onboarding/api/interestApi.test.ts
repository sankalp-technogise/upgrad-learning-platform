import { describe, it, expect, vi, beforeEach } from 'vitest'
import { interestApi } from './interestApi'
import { apiClient } from '@/lib/apiClient'

// Mock the apiClient
vi.mock('@/lib/apiClient', () => ({
  apiClient: {
    get: vi.fn(),
    post: vi.fn(),
  },
}))

describe('interestApi', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  describe('getAllInterests', () => {
    it('should send GET request to /interests', async () => {
      const mockInterests = [
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
      ]
      vi.mocked(apiClient.get).mockResolvedValue({ data: mockInterests })

      await interestApi.getAllInterests()

      expect(apiClient.get).toHaveBeenCalledWith('/interests')
    })

    it('should return array of interests', async () => {
      const mockInterests = [
        {
          id: 'PYTHON_PROGRAMMING',
          name: 'Python Programming',
          description: 'Learn Python basics',
          iconName: 'puzzle',
        },
      ]
      vi.mocked(apiClient.get).mockResolvedValue({ data: mockInterests })

      const result = await interestApi.getAllInterests()

      expect(result).toEqual(mockInterests)
      expect(result).toHaveLength(1)
      expect(result[0].id).toBe('PYTHON_PROGRAMMING')
      expect(result[0].name).toBe('Python Programming')
      expect(result[0].iconName).toBe('puzzle')
    })
  })

  describe('saveUserInterests', () => {
    it('should send POST request to /user/interests with interestNames', async () => {
      const interestNames = ['PYTHON_PROGRAMMING', 'DATA_SCIENCE']
      vi.mocked(apiClient.post).mockResolvedValue({ data: undefined })

      await interestApi.saveUserInterests(interestNames)

      expect(apiClient.post).toHaveBeenCalledWith('/user/interests', {
        interestNames,
      })
    })

    it('should return void on success', async () => {
      vi.mocked(apiClient.post).mockResolvedValue({ data: undefined })

      const result = await interestApi.saveUserInterests(['PYTHON_PROGRAMMING'])

      expect(result).toBeUndefined()
    })

    it('should send empty array when no interests selected', async () => {
      vi.mocked(apiClient.post).mockResolvedValue({ data: undefined })

      await interestApi.saveUserInterests([])

      expect(apiClient.post).toHaveBeenCalledWith('/user/interests', {
        interestNames: [],
      })
    })
  })
})
