import { useEffect, useRef, useState } from 'react';
import { formatDuration } from '../lib/format';

// Plays one 30-second preview at a time. Starting another track stops the current one.
export default function TrackList({ tracks }) {
  const audio = useRef(null);
  const [playingId, setPlayingId] = useState(null);
  const [progress, setProgress] = useState(0);

  useEffect(() => {
    const el = audio.current;
    return () => el?.pause();
  }, []);

  const toggle = (track) => {
    const el = audio.current;
    if (playingId === track.id) {
      el.pause();
      setPlayingId(null);
      setProgress(0);
      return;
    }
    el.src = track.previewUrl;
    setPlayingId(track.id);
    setProgress(0);
    el.play().catch(() => setPlayingId(null));
  };

  return (
    <>
      <ol className="tracks">
        {tracks.map((t) => {
          const active = playingId === t.id;
          return (
            <li className={`track${active ? ' is-playing' : ''}`} key={t.id}>
              <button
                type="button"
                className="track-play"
                aria-label={`${active ? 'Pause' : 'Play'} preview of ${t.title}`}
                aria-pressed={active}
                onClick={() => toggle(t)}
              >
                <svg width="14" height="14" viewBox="0 0 14 14" fill="currentColor" aria-hidden="true">
                  {active ? (
                    <path d="M3 2h3v10H3zM8 2h3v10H8z" />
                  ) : (
                    <path d="M4 2.5v9l7.5-4.5z" />
                  )}
                </svg>
              </button>
              <span className="track-num">{t.trackNumber}</span>
              <span className="track-title">{t.title}</span>
              <span className="track-time">{formatDuration(t.durationMs)}</span>
              {active && (
                <div className="track-progress" aria-hidden="true">
                  <span style={{ width: `${progress * 100}%` }} />
                </div>
              )}
            </li>
          );
        })}
      </ol>
      <audio
        ref={audio}
        onTimeUpdate={(e) => {
          const { currentTime, duration } = e.currentTarget;
          if (duration) setProgress(currentTime / duration);
        }}
        onEnded={() => {
          setPlayingId(null);
          setProgress(0);
        }}
        onError={() => setPlayingId(null)}
      />
    </>
  );
}
