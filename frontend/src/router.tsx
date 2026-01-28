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
  beforeLoad: () => {
    const token = localStorage.getItem('token')
    if (token) {
      throw redirect({ to: '/home' })
    }
  },
  component: LandingPage,
})

const homeRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: '/home',
  beforeLoad: () => {
    const token = localStorage.getItem('token')
    if (!token) {
      throw redirect({ to: '/login' })
    }
  },
  component: HomePage,
})

const loginRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: '/login',
  beforeLoad: () => {
    const token = localStorage.getItem('token')
    if (token) {
      throw redirect({ to: '/home' })
    }
  },
  component: LoginPage,
})

const otpRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: '/auth/otp',
  validateSearch: (search: Record<string, unknown>) => {
    return {
      email: search.email as string,
    }
  },
  component: OtpPage,
})

const routeTree = rootRoute.addChildren([indexRoute, homeRoute, loginRoute, otpRoute])

export const router = createRouter({ routeTree })

declare module '@tanstack/react-router' {
  interface Register {
    router: typeof router
  }
}
