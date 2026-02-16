import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { FeedbackModal } from './FeedbackModal'
import { vi, describe, it, expect } from 'vitest'
import '@testing-library/jest-dom'

describe('FeedbackModal', () => {
  it('renders nothing when open is false', () => {
    const onSubmit = vi.fn()
    const { container } = render(<FeedbackModal open={false} onSubmit={onSubmit} />)
    expect(container.firstChild).toBeNull()
  })

  it('renders the modal with heading and buttons when open is true', () => {
    const onSubmit = vi.fn()
    render(<FeedbackModal open={true} onSubmit={onSubmit} />)

    expect(screen.getByText('Did you like this video?')).toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Yes' })).toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'No' })).toBeInTheDocument()
  })

  it('calls onSubmit with true when Yes is clicked', async () => {
    const user = userEvent.setup()
    const onSubmit = vi.fn()
    render(<FeedbackModal open={true} onSubmit={onSubmit} />)

    await user.click(screen.getByRole('button', { name: 'Yes' }))
    expect(onSubmit).toHaveBeenCalledWith(true)
    expect(onSubmit).toHaveBeenCalledTimes(1)
  })

  it('calls onSubmit with false when No is clicked', async () => {
    const user = userEvent.setup()
    const onSubmit = vi.fn()
    render(<FeedbackModal open={true} onSubmit={onSubmit} />)

    await user.click(screen.getByRole('button', { name: 'No' }))
    expect(onSubmit).toHaveBeenCalledWith(false)
    expect(onSubmit).toHaveBeenCalledTimes(1)
  })
})
