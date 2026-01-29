import { apiClient } from '@/lib/apiClient'

export interface Interest {
  id: string
  name: string
  description: string
  iconName: string
}

export interface SaveInterestsRequest {
  interestIds: string[]
}

export const interestApi = {
  getAllInterests: async (): Promise<Interest[]> => {
    const { data } = await apiClient.get<Interest[]>('/interests')
    return data
  },

  saveUserInterests: async (interestIds: string[]): Promise<void> => {
    await apiClient.post<void>('/user/interests', {
      interestIds,
    } as SaveInterestsRequest)
  },
}
