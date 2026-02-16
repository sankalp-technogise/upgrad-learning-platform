import { apiClient } from '@/lib/apiClient'

export interface WatchProgress {
  contentId: string
  progressPercent: number
  lastWatchedPosition: number
}

export const watchProgressApi = {
  saveProgress: async (
    contentId: string,
    progressPercent: number,
    lastWatchedPosition: number
  ): Promise<void> => {
    await apiClient.put('/watch-progress', {
      contentId,
      progressPercent,
      lastWatchedPosition,
    })
  },

  getProgress: async (contentId: string): Promise<WatchProgress | null> => {
    const { data, status } = await apiClient.get<WatchProgress>(`/watch-progress/${contentId}`, {
      validateStatus: (s) => s === 200 || s === 204,
    })
    if (status === 204) return null
    return data
  },

  saveFeedback: async (contentId: string, feedback: 'HELPFUL' | 'NOT_HELPFUL'): Promise<void> => {
    await apiClient.put('/watch-progress/feedback', { contentId, feedback })
  },
}
