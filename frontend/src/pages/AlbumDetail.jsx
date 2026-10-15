import { useCallback, useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { api } from '../api';
import { useAuth } from '../auth/useAuth';
import ReviewPanel from '../components/ReviewPanel';
import Cover from '../components/Cover';
import ReviewList from '../components/ReviewList';
import ScoreBadge from '../components/ScoreBadge';
import TrackList from '../components/TrackList';
import { plural } from '../lib/format';

function BandcampLink({ album }) {
  const q = encodeURIComponent(`${album.title} ${album.artist}`);
  return (
    <a
      className="btn btn--outline"
      href={`https://bandcamp.com/search?q=${q}`}
      target="_blank"
      rel="noopener noreferrer"
    >
      Listen on Bandcamp
    </a>
  );
}

export default function AlbumDetail() {
  const { id } = useParams();
  const { username } = useAuth();
  const [state, setState] = useState({ status: 'loading' });

  useEffect(() => {
    let cancelled = false;
    Promise.all([api.get(`/api/albums/${id}`), api.get(`/api/albums/${id}/reviews`)])
      .then(([detail, reviews]) => {
        if (cancelled) return;
        document.title = `${detail.album.title} | Rotten Potatoes`;
        setState({ status: 'ready', id, detail, reviews });
      })
      .catch((e) => !cancelled && setState({ status: 'error', id, message: e.message }));
    return () => {
      cancelled = true;
    };
  }, [id]);

  // Reload album stats and reviews after a review is saved or deleted.
  const refresh = useCallback(async () => {
    const [detail, reviews] = await Promise.all([
      api.get(`/api/albums/${id}`),
      api.get(`/api/albums/${id}/reviews`),
    ]);
    setState({ status: 'ready', id, detail, reviews });
  }, [id]);

  if (state.status === 'loading' || state.id !== id) {
    return <div className="notice page" aria-busy="true">Loading album</div>;
  }
  if (state.status === 'error') {
    const missing = /not found/i.test(state.message);
    return (
      <div className="notice page" role="alert">
        <strong>{missing ? 'Album not found' : "Couldn't load this album"}</strong>
        <p>
          {missing ? 'It may have been removed. ' : 'Check that the backend is running, then reload the page. '}
          <Link to="/">Back to all albums</Link>
        </p>
      </div>
    );
  }

  const { detail, reviews } = state;
  const { album, tracks, positiveRate, averageRating, reviewCount } = detail;

  return (
    <div className="page">
      <Link className="back" to="/">All albums</Link>

      <header className="detail-head">
        <Cover album={album} />
        <div>
          <h1 className="detail-title">{album.title}</h1>
          <p className="detail-artist">{album.artist}</p>
          <p className="detail-meta">
            {[album.releaseYear, album.genre].filter(Boolean).join(', ')}
          </p>

          <div className="detail-score">
            {positiveRate != null ? (
              <>
                <span className="score score--md">
                  {positiveRate}
                  <span className="score-unit">%</span>
                </span>
                <span className="score-note">
                  <ScoreBadge rate={positiveRate} size={40} />
                  Of {plural(reviewCount, 'review')} are positive. Average rating {averageRating.toFixed(1)} out of 5.
                </span>
              </>
            ) : (
              <p className="empty">No reviews yet.</p>
            )}
          </div>
        </div>
      </header>

      <section className="detail-section" aria-labelledby="preview-title">
        <h2 className="section-title" id="preview-title">Preview</h2>
        {tracks.length > 0 ? (
          <>
            <p className="section-sub">30-second previews of each track</p>
            <TrackList tracks={tracks} />
          </>
        ) : album.bandcampId ? (
          <>
            <p className="section-sub">No 30-second previews for this release. Full album streams on Bandcamp.</p>
            <BandcampLink album={album} />
          </>
        ) : (
          <p className="empty">No preview available for this album.</p>
        )}
      </section>

      <section className="detail-section" aria-labelledby="reviews-title">
        <h2 className="section-title" id="reviews-title">Reviews</h2>
        <ReviewPanel
          key={`${id}-${username ?? 'guest'}`}
          albumId={id}
          myReview={reviews.find((r) => r.username === username)}
          onChanged={refresh}
        />
        <ReviewList reviews={reviews} currentUser={username} />
      </section>
    </div>
  );
}
