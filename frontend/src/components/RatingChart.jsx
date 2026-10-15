import { useEffect, useRef, useState } from 'react';
import Cover from './Cover';

const plural = (n, word) => `${n} ${word}${n === 1 ? '' : 's'}`;

export default function RatingChart({ albums }) {
  const host = useRef(null);
  const [tip, setTip] = useState(null);
  const [grown, setGrown] = useState(false);

  // Bars grow from zero once, on first paint.
  useEffect(() => {
    const id = requestAnimationFrame(() => setGrown(true));
    return () => cancelAnimationFrame(id);
  }, []);

  const rated = albums.filter((a) => a.reviewCount > 0);

  const place = (e, album) => {
    const box = host.current.getBoundingClientRect();
    const row = e.currentTarget.getBoundingClientRect();
    const x = e.type === 'focus' ? row.left - box.left + row.width / 2 : e.clientX - box.left;
    setTip({ album, x, y: row.top - box.top });
  };

  return (
    <div className="ranking-chart" ref={host}>
      <ul className="rank-list">
        {rated.map((a) => (
          <li
            className="rank-row"
            key={a.id}
            tabIndex={0}
            aria-label={`${a.title}, average ${a.averageRating.toFixed(1)} out of 5 from ${plural(a.reviewCount, 'review')}`}
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
                <div className="bar-fill" style={{ width: grown ? `${(a.averageRating / 5) * 100}%` : '0%' }} />
              </div>
            </div>
            <span className="rank-value">{a.averageRating.toFixed(1)}</span>
          </li>
        ))}
      </ul>

      {tip && (
        <div className="tip" style={{ left: tip.x, top: tip.y }} role="status">
          <strong>{tip.album.averageRating.toFixed(1)} out of 5</strong>
          {tip.album.title}, {plural(tip.album.reviewCount, 'review')}
        </div>
      )}
    </div>
  );
}
