import { apiClient } from '@/lib/apiClient'

export interface ContentDetail {
  id: string
  title: string
  description: string
  thumbnailUrl: string
  videoUrl: string
  category: string
  episodeNumber: number | null
  durationSeconds: number | null
  createdAt: string
}

export const contentApi = {
  getContent: async (id: string): Promise<ContentDetail> => {
    const { data } = await apiClient.get<ContentDetail>(`/contents/${id}`)
    return data
  },

  getNextEpisode: async (id: string): Promise<ContentDetail | null> => {
    const { data, status } = await apiClient.get<ContentDetail>(`/contents/${id}/next`, {
      validateStatus: (s) => s === 200 || s === 204,
    })
    if (status === 204) return null
    return data
  },
}
