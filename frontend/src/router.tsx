import { createRouter, createRoute, createRootRoute, Link, Outlet } from '@tanstack/react-router'

import { LoginPage } from '@/features/auth/components/LoginPage'
import { OtpPage } from '@/features/auth/components/OtpPage'

const rootRoute = createRootRoute({
  component: () => (
    <>
      <Outlet />
      {/* <TanStackRouterDevtools /> */}
    </>
  ),
})

const indexRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: '/',
  component: () => (
    <div style={{ padding: 20 }}>
      <h3>Welcome Home!</h3>
      <Link to="/login">Go to Login</Link>
    </div>
  ),
})

const loginRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: '/login',
  component: LoginPage,
})

const otpRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: '/auth/otp',
  component: OtpPage,
  validateSearch: (search: Record<string, unknown>) => {
    return {
      email: search.email as string,
    }
  },
})

const routeTree = rootRoute.addChildren([indexRoute, loginRoute, otpRoute])

export const router = createRouter({ routeTree })

declare module '@tanstack/react-router' {
  interface Register {
    router: typeof router
  }
}
