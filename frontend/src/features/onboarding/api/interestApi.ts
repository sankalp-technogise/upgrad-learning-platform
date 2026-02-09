import { apiClient } from '@/lib/apiClient'

export interface Interest {
  id: string
  name: string
  description: string
  iconName: string
}

export interface SaveInterestsRequest {
  interestNames: string[]
}

export const interestApi = {
  getAllInterests: async (): Promise<Interest[]> => {
    const { data } = await apiClient.get<Interest[]>('/interests')
    return data
  },

  saveUserInterests: async (interestNames: string[]): Promise<void> => {
    await apiClient.post<void>('/user/interests', {
      interestNames,
    } as SaveInterestsRequest)
  },
}
