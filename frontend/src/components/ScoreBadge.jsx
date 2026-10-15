// An album is "golden" when at least this share of its reviews are positive (4 or 5 stars).
export const GOLDEN_AT = 60;

export default function ScoreBadge({ rate, size = 20 }) {
  if (rate == null) return null;
  const golden = rate >= GOLDEN_AT;
  return (
    <img
      className="score-icon"
      src={golden ? '/brand/score-golden.svg' : '/brand/score-rotten.svg'}
      width={size}
      height={size}
      alt={golden ? 'Golden' : 'Rotten'}
    />
  );
}
