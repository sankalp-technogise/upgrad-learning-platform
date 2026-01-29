import {
  createRouter,
  createRoute,
  createRootRoute,
  Outlet,
  redirect,
} from '@tanstack/react-router'

import { LoginPage } from '@/features/auth/components/LoginPage'
import { OtpPage } from '@/features/auth/components/OtpPage'
import { LandingPage } from '@/features/landing/LandingPage'
import { HomePage } from '@/features/home/HomePage'
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
    } catch {
      // Not logged in, stay on landing page
    }
  },
  component: LandingPage,
})

const homeRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: '/home',
  beforeLoad: async () => {
    try {
      await authApi.getMe()
    } catch {
      throw redirect({ to: '/login' })
    }
  },
  component: HomePage,
})

const loginRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: '/login',
  beforeLoad: async () => {
    try {
      await authApi.getMe()
      throw redirect({ to: '/home' })
    } catch {
      // Not logged in, proceed to login page
    }
  },
  component: LoginPage,
})

const otpRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: '/auth/otp',
  component: OtpPage,
})

const routeTree = rootRoute.addChildren([indexRoute, homeRoute, loginRoute, otpRoute])

export const router = createRouter({ routeTree })

declare module '@tanstack/react-router' {
  interface Register {
    router: typeof router
  }
}
