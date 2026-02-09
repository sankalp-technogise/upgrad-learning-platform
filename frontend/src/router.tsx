import {
  createRouter,
  createRoute,
  createRootRoute,
  Outlet,
  redirect,
  Navigate,
  isRedirect,
} from '@tanstack/react-router'
import { z } from 'zod'

import { LoginPage } from '@/features/auth/components/LoginPage'
import { OtpPage } from '@/features/auth/components/OtpPage'
import { LandingPage } from '@/features/landing/LandingPage'
import { HomePage } from '@/features/home/HomePage'
import { InterestSelectionPage } from '@/features/onboarding/components/InterestSelectionPage'
import { authApi } from '@/features/auth/api/authApi'

const rootRoute = createRootRoute({
  component: () => (
    <>
      <Outlet />
    </>
  ),
})

const indexRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: '/',
  beforeLoad: async () => {
    try {
      await authApi.getMe()
      // If successful, we are logged in
      throw redirect({ to: '/home' })
    } catch (error: unknown) {
      if (isRedirect(error)) {
        throw error
      }
      if (
        (error as { response?: { status?: number } }).response?.status === 401 ||
        (error as { response?: { status?: number } }).response?.status === 403
      ) {
        // Not logged in, stay on landing page
        return
      }
      throw error
    }
  },
  component: LandingPage,
})

const homeRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: '/home',
  beforeLoad: async () => {
    try {
      const user = await authApi.getMe()
      // Check if onboarding is completed
      if (!user.onboardingCompleted) {
        throw redirect({ to: '/onboarding/interests' })
      }
    } catch (error: unknown) {
      if (isRedirect(error)) {
        throw error
      }
      if (
        (error as { response?: { status?: number } }).response?.status === 401 ||
        (error as { response?: { status?: number } }).response?.status === 403
      ) {
        throw redirect({ to: '/login' })
      }
      throw error
    }
  },
  component: HomePage,
})

const onboardingRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: '/onboarding/interests',
  beforeLoad: async () => {
    try {
      const user = await authApi.getMe()
      // If onboarding already completed, redirect to home
      if (user.onboardingCompleted) {
        throw redirect({ to: '/home' })
      }
    } catch (error: unknown) {
      if (isRedirect(error)) {
        throw error
      }
      if (
        (error as { response?: { status?: number } }).response?.status === 401 ||
        (error as { response?: { status?: number } }).response?.status === 403
      ) {
        throw redirect({ to: '/login' })
      }
      throw error
    }
  },
  component: InterestSelectionPage,
})

const loginRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: '/login',
  beforeLoad: async () => {
    try {
      await authApi.getMe()
      throw redirect({ to: '/home' })
    } catch (error: unknown) {
      if (isRedirect(error)) {
        throw error
      }
      if (
        (error as { response?: { status?: number } }).response?.status === 401 ||
        (error as { response?: { status?: number } }).response?.status === 403
      ) {
        // Not logged in, proceed to login page
        return
      }
      throw error
    }
  },
  component: LoginPage,
})

const otpSearchSchema = z.object({
  email: z.string().email(),
})

const otpRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: '/auth/otp',
  validateSearch: (search) => otpSearchSchema.parse(search),
  component: OtpPage,
  errorComponent: () => <Navigate to="/login" />,
})

const routeTree = rootRoute.addChildren([
  indexRoute,
  homeRoute,
  onboardingRoute,
  loginRoute,
  otpRoute,
])

export const router = createRouter({ routeTree })

declare module '@tanstack/react-router' {
  interface Register {
    router: typeof router
  }
}
