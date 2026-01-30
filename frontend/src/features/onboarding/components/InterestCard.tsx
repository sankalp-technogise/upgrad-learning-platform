import {
  LineChart,
  Palette,
  Megaphone,
  Server,
  Shield,
  Atom,
  Coins,
  Binary,
  type LucideIcon,
} from 'lucide-react'
import type { Interest } from '../api/interestApi'

interface InterestCardProps {
  interest: Interest
  selected: boolean
  onToggle: () => void
}

// Map icon names to Lucide icon components matching the reference design
const iconMap: Record<string, LucideIcon> = {
  puzzle: Binary, // Python Programming (Approximation)
  chart: LineChart, // Data Science
  palette: Palette, // UI/UX Design
  megaphone: Megaphone, // Digital Marketing
  server: Server, // Cloud Computing
  shield: Shield, // Cybersecurity
  atom: Atom, // React Framework
  dollar: Coins, // Personal Finance
}

export function InterestCard({ interest, selected, onToggle }: InterestCardProps) {
  const IconComponent = iconMap[interest.iconName] || Binary

  return (
    <button
      type="button"
      onClick={onToggle}
      className={`interest-card ${selected ? 'selected' : ''}`}
      aria-pressed={selected}
    >
      <div className="interest-card__icon">
        <IconComponent size={32} strokeWidth={1.5} />
      </div>
      <div className="interest-card__content">
        <h3 className="interest-card__name">{interest.name}</h3>
        {interest.description && (
          <p className="interest-card__description">{interest.description}</p>
        )}
      </div>
      {selected && (
        <div className="interest-card__check" aria-label="Selected">
          ✓
        </div>
      )}
    </button>
  )
}
