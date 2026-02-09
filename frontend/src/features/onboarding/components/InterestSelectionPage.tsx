import { useState, useEffect } from 'react'
import { useNavigate } from '@tanstack/react-router'
import { interestApi, type Interest } from '../api/interestApi'
import { InterestCard } from './InterestCard'
import './InterestSelectionPage.css'

export function InterestSelectionPage() {
  const navigate = useNavigate()
  const [interests, setInterests] = useState<Interest[]>([])
  const [selectedInterestIds, setSelectedInterestIds] = useState<Set<string>>(new Set())
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState<string | null>(null)

  // Fetch interests on mount
  useEffect(() => {
    const fetchInterests = async () => {
      try {
        setLoading(true)
        const data = await interestApi.getAllInterests()
        setInterests(data)
        setError(null)
      } catch (err) {
        setError('Failed to load interests. Please refresh the page.')
        console.error('Failed to fetch interests:', err)
      } finally {
        setLoading(false)
      }
    }
    fetchInterests()
  }, [])

  const toggleInterest = (interestId: string) => {
    setSelectedInterestIds((prev) => {
      const newSet = new Set(prev)
      if (newSet.has(interestId)) {
        newSet.delete(interestId)
      } else {
        newSet.add(interestId)
      }
      return newSet
    })
  }

  const handleContinue = async () => {
    if (selectedInterestIds.size === 0) return

    try {
      setSaving(true)
      setError(null)
      await interestApi.saveUserInterests(Array.from(selectedInterestIds))
      navigate({ to: '/home' })
    } catch (err) {
      setError('Failed to save interests. Please try again.')
      console.error('Failed to save interests:', err)
    } finally {
      setSaving(false)
    }
  }

  if (loading) {
    return (
      <div className="interest-selection-page">
        <div className="loading">Loading interests...</div>
      </div>
    )
  }

  return (
    <div className="interest-selection-page">
      <div className="interest-selection__container">
        <header className="interest-selection__header">
          <h1>What do you want to learn?</h1>
          <p>Select one or more topics to personalize your home feed.</p>
        </header>

        {error && (
          <div className="interest-selection__error" role="alert">
            {error}
          </div>
        )}

        <div className="interest-selection__grid">
          {interests.map((interest) => (
            <InterestCard
              key={interest.id}
              interest={interest}
              selected={selectedInterestIds.has(interest.id)}
              onToggle={() => toggleInterest(interest.id)}
            />
          ))}
        </div>

        <div className="interest-selection__footer">
          <button
            type="button"
            onClick={handleContinue}
            disabled={selectedInterestIds.size === 0 || saving}
            className="btn-continue"
          >
            {saving ? 'Saving...' : 'Continue'}
          </button>
        </div>
      </div>
    </div>
  )
}
