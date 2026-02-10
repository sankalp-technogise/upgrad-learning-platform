import { apiClient } from '@/lib/apiClient'

export interface ContentDetail {
  id: string
  title: string
  description: string
  thumbnailUrl: string
  videoUrl: string
  category: string
  episodeNumber: number
  durationSeconds: number
  createdAt: string
}

export const contentApi = {
  getContent: async (id: string): Promise<ContentDetail> => {
    const { data } = await apiClient.get<ContentDetail>(`/contents/${id}`)
    return data
  },
}
