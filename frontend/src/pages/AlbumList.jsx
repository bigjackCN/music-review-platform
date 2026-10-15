import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { api } from '../api';
import Cover from '../components/Cover';
import RatingChart from '../components/RatingChart';
import ScoreBadge from '../components/ScoreBadge';
import { plural } from '../lib/format';

export default function AlbumList() {
  const [data, setData] = useState(null);
  const [error, setError] = useState('');
  const [genre, setGenre] = useState('All');

  useEffect(() => {
    document.title = 'Rotten Potatoes';
    Promise.all([api.get('/api/albums/stats'), api.get('/api/ranking?limit=8')])
      .then(([albums, ranking]) => setData({ albums, ranking }))
      .catch((e) => setError(e.message));
  }, []);

  if (error) {
    return (
      <div className="notice page" role="alert">
        <strong>Couldn't load albums</strong>
        <p>Check that the backend is running on port 8080, then reload the page.</p>
      </div>
    );
  }
  if (!data) {
    return <div className="notice page" aria-busy="true">Loading albums</div>;
  }

  const { albums, ranking } = data;
  const top = ranking[0];
  const genres = ['All', ...new Set(albums.map((a) => a.genre).filter(Boolean))].sort((a, b) =>
    a === 'All' ? -1 : b === 'All' ? 1 : a.localeCompare(b)
  );
  const visible = genre === 'All' ? albums : albums.filter((a) => a.genre === genre);

  return (
    <div className="page">
      {top && (
        <section className="hero" aria-labelledby="hero-title">
          <figure className="hero-feature">
            <Link className="hero-cover-link" to={`/albums/${top.id}`} aria-hidden="true" tabIndex={-1}>
              <Cover album={top} />
            </Link>
            <figcaption>
              <h2 className="hero-title" id="hero-title">
                <Link className="hero-link" to={`/albums/${top.id}`}>{top.title}</Link>
              </h2>
              <p className="hero-artist">{top.artist}, {top.releaseYear}</p>
              <p className="hero-score">
                <span className="score">
                  {top.positiveRate}
                  <span className="score-unit">%</span>
                </span>
                <span className="score-note">
                  <ScoreBadge rate={top.positiveRate} size={40} />
                  Of {plural(top.reviewCount, 'review')} are positive. Average rating {top.averageRating.toFixed(1)} out of 5.
                </span>
              </p>
            </figcaption>
          </figure>

          <div className="ranking">
            <h2 className="section-title">Ranking</h2>
            <p className="section-sub">Share of reviews rated 4 or 5 stars</p>
            <RatingChart albums={ranking} />
          </div>
        </section>
      )}

      <section className="browse" aria-labelledby="browse-title">
        <h2 className="section-title" id="browse-title">Browse albums</h2>
        <div className="filters" role="group" aria-label="Filter by genre">
          {genres.map((g) => (
            <button
              key={g}
              type="button"
              className="chip"
              aria-pressed={g === genre}
              onClick={() => setGenre(g)}
            >
              {g}
            </button>
          ))}
        </div>

        <ul className="grid">
          {visible.map((a) => (
            <li className="album" key={a.id}>
              <Link className="album-link" to={`/albums/${a.id}`}>
                <Cover album={a} />
                <h3 className="album-title">{a.title}</h3>
                <p className="album-artist">{a.artist}, {a.releaseYear}</p>
                <p className="album-score">
                  {a.reviewCount > 0 ? (
                    <>
                      <ScoreBadge rate={a.positiveRate} size={18} />
                      <strong>{a.positiveRate}%</strong>
                      {plural(a.reviewCount, 'review')}
                    </>
                  ) : (
                    'No reviews yet'
                  )}
                </p>
              </Link>
            </li>
          ))}
        </ul>
      </section>
    </div>
  );
}
