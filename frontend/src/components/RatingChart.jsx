import { useEffect, useRef, useState } from 'react';
import Cover from './Cover';
import ScoreBadge from './ScoreBadge';

const plural = (n, word) => `${n} ${word}${n === 1 ? '' : 's'}`;

// Horizontal bars: one per album, length = share of positive reviews (0 to 100%).
export default function RatingChart({ albums }) {
  const host = useRef(null);
  const [tip, setTip] = useState(null);
  const [grown, setGrown] = useState(false);

  // Bars grow from zero once, on first paint.
  useEffect(() => {
    const id = requestAnimationFrame(() => setGrown(true));
    return () => cancelAnimationFrame(id);
  }, []);

  const place = (e, album) => {
    const box = host.current.getBoundingClientRect();
    const row = e.currentTarget.getBoundingClientRect();
    const x = e.type === 'focus' ? row.left - box.left + row.width / 2 : e.clientX - box.left;
    setTip({ album, x, y: row.top - box.top });
  };

  return (
    <div className="ranking-chart" ref={host}>
      <ul className="rank-list">
        {albums.map((a) => (
          <li
            className="rank-row"
            key={a.id}
            tabIndex={0}
            aria-label={`${a.title}, ${a.positiveRate}% positive reviews, average ${a.averageRating.toFixed(1)} out of 5, ${plural(a.reviewCount, 'review')}`}
            onPointerMove={(e) => place(e, a)}
            onPointerLeave={() => setTip(null)}
            onFocus={(e) => place(e, a)}
            onBlur={() => setTip(null)}
          >
            <Cover album={a} />
            <div>
              <p className="rank-name">
                {a.title} <span>{a.artist}</span>
              </p>
              <div className="bar">
                <div className="bar-fill" style={{ width: grown ? `${a.positiveRate}%` : '0%' }} />
              </div>
            </div>
            <span className="rank-value">
              <ScoreBadge rate={a.positiveRate} size={20} />
              {a.positiveRate}%
            </span>
          </li>
        ))}
      </ul>

      {tip && (
        <div className="tip" style={{ left: tip.x, top: tip.y }} role="status">
          <strong>{tip.album.positiveRate}% positive</strong>
          {tip.album.title}, average {tip.album.averageRating.toFixed(1)} from {plural(tip.album.reviewCount, 'review')}
        </div>
      )}
    </div>
  );
}
