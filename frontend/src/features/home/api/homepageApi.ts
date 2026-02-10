import { apiClient } from '@/lib/apiClient'

export interface ContentItem {
  id: string
  title: string
  description: string
  thumbnailUrl: string
  category: string
}

export interface ContinueWatchingItem {
  contentId: string
  title: string
  description: string
  thumbnailUrl: string
  progressPercent: number
}

export interface HomepageSections {
  continueWatching?: ContinueWatchingItem
  recommended: ContentItem[]
  exploration: ContentItem[]
}

export const homepageApi = {
  getSections: async (): Promise<HomepageSections> => {
    const { data } = await apiClient.get<HomepageSections>('/homepage')
    return data
  },
}
