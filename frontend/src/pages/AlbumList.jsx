import { useEffect, useState } from 'react';
import { api } from '../api';
import Cover from '../components/Cover';
import RatingChart from '../components/RatingChart';

const plural = (n, word) => `${n} ${word}${n === 1 ? '' : 's'}`;

export default function AlbumList() {
  const [albums, setAlbums] = useState(null);
  const [error, setError] = useState('');
  const [genre, setGenre] = useState('All');

  useEffect(() => {
    api.get('/api/albums/stats').then(setAlbums).catch((e) => setError(e.message));
  }, []);

  if (error) {
    return (
      <div className="notice page" role="alert">
        <strong>Couldn't load albums</strong>
        <p>Check that the backend is running on port 8080, then reload the page.</p>
      </div>
    );
  }
  if (!albums) {
    return <div className="notice page" aria-busy="true">Loading albums</div>;
  }

  const top = albums.find((a) => a.reviewCount > 0);
  const genres = ['All', ...new Set(albums.map((a) => a.genre).filter(Boolean))].sort((a, b) =>
    a === 'All' ? -1 : b === 'All' ? 1 : a.localeCompare(b)
  );
  const visible = genre === 'All' ? albums : albums.filter((a) => a.genre === genre);

  return (
    <div className="page">
      {top && (
        <section className="hero" aria-labelledby="hero-title">
          <figure className="hero-feature">
            <Cover album={top} />
            <figcaption>
              <h2 className="hero-title" id="hero-title">{top.title}</h2>
              <p className="hero-artist">{top.artist}, {top.releaseYear}</p>
              <p className="hero-score">
                <span className="score">{top.averageRating.toFixed(1)}</span>
                <span className="score-note">
                  Highest average rating, from {plural(top.reviewCount, 'review')}
                </span>
              </p>
            </figcaption>
          </figure>

          <div className="ranking">
            <h2 className="section-title">Ratings by album</h2>
            <p className="section-sub">Average rating out of 5</p>
            <RatingChart albums={albums} />
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
              <Cover album={a} />
              <h3 className="album-title">{a.title}</h3>
              <p className="album-artist">{a.artist}, {a.releaseYear}</p>
              <p className="album-score">
                {a.reviewCount > 0 ? (
                  <>
                    <strong>{a.averageRating.toFixed(1)}</strong>
                    {plural(a.reviewCount, 'review')}
                  </>
                ) : (
                  'No reviews yet'
                )}
              </p>
            </li>
          ))}
        </ul>
      </section>
    </div>
  );
}
