import { useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { api } from '../api';
import { useAuth } from '../auth/useAuth';

export default function ReviewForm({ albumId, existing, onSaved, onCancel }) {
  const { username } = useAuth();
  const location = useLocation();
  const [rating, setRating] = useState(existing?.rating ?? 0);
  const [hover, setHover] = useState(0);
  const [comment, setComment] = useState(existing?.comment ?? '');
  const [error, setError] = useState('');
  const [busy, setBusy] = useState(false);

  if (!username) {
    return (
      <p className="form-hint">
        <Link to="/login" state={{ from: location.pathname }}>Sign in</Link> to rate and review this album.
      </p>
    );
  }

  async function onSubmit(e) {
    e.preventDefault();
    if (!rating) {
      setError('Choose a rating from 1 to 5 stars.');
      return;
    }
    setError('');
    setBusy(true);
    try {
      await api.post(`/api/albums/${albumId}/reviews`, { rating, comment: comment.trim() || null });
      await onSaved();
    } catch (err) {
      setError(err.message);
    } finally {
      setBusy(false);
    }
  }

  const shown = hover || rating;

  return (
    <form className="form review-form" onSubmit={onSubmit}>
      <h3 className="form-title">{existing ? 'Edit your review' : 'Write a review'}</h3>
      <div className="star-input" role="radiogroup" aria-label="Rating" onMouseLeave={() => setHover(0)}>
        {[1, 2, 3, 4, 5].map((n) => (
          <button
            key={n}
            type="button"
            role="radio"
            aria-checked={rating === n}
            aria-label={`${n} star${n > 1 ? 's' : ''}`}
            className={n <= shown ? 'star on' : 'star'}
            onMouseEnter={() => setHover(n)}
            onClick={() => setRating(n)}
          >
            ★
          </button>
        ))}
      </div>
      <label className="field">
        <span>Comment (optional)</span>
        <textarea rows="3" maxLength={1000} value={comment} onChange={(e) => setComment(e.target.value)} />
      </label>
      {error && <p className="form-error" role="alert">{error}</p>}
      <div className="review-actions">
        <button className="btn" type="submit" disabled={busy}>
          {busy ? 'Saving' : existing ? 'Save changes' : 'Submit review'}
        </button>
        {onCancel && (
          <button className="btn btn--outline" type="button" onClick={onCancel} disabled={busy}>
            Cancel
          </button>
        )}
      </div>
    </form>
  );
}
