import { useState } from 'react';
import { api } from '../api';
import { useAuth } from '../auth/useAuth';
import { formatDate } from '../lib/format';
import ReviewForm from './ReviewForm';
import Stars from './Stars';

// The signed-in user's own review: a form when there is none (or while editing),
// otherwise a card with Edit and Delete.
export default function ReviewPanel({ albumId, myReview, onChanged }) {
  const { username } = useAuth();
  const [mode, setMode] = useState('view'); // 'view' | 'edit' | 'confirm'
  const [saved, setSaved] = useState(false);
  const [error, setError] = useState('');
  const [busy, setBusy] = useState(false);

  if (!username || !myReview || mode === 'edit') {
    return (
      <div className="review-panel">
        <ReviewForm
          albumId={albumId}
          existing={myReview}
          onSaved={async () => {
            await onChanged();
            setMode('view');
            setSaved(true);
          }}
          onCancel={myReview ? () => setMode('view') : undefined}
        />
      </div>
    );
  }

  async function remove() {
    setBusy(true);
    setError('');
    try {
      await api.del(`/api/reviews/${myReview.id}`);
      setMode('view');
      setSaved(false);
      await onChanged();
    } catch (e) {
      setError(e.message);
    } finally {
      setBusy(false);
    }
  }

  return (
    <div className="review-panel">
      <section className="my-review" aria-label="Your review">
        <div className="my-review-head">
          <span className="my-review-label">Your review</span>
          <Stars rating={myReview.rating} />
          <time className="review-date" dateTime={myReview.createdAt}>{formatDate(myReview.createdAt)}</time>
        </div>
        {myReview.comment && <p className="review-text">{myReview.comment}</p>}
        {saved && <p className="saved" role="status">Review saved.</p>}
        {error && <p className="form-error" role="alert">{error}</p>}

        {mode === 'confirm' ? (
          <div className="review-actions">
            <span className="confirm-text">Delete this review?</span>
            <button type="button" className="btn btn--sm btn--danger" onClick={remove} disabled={busy}>
              {busy ? 'Deleting' : 'Delete'}
            </button>
            <button type="button" className="btn btn--sm btn--outline" onClick={() => setMode('view')} disabled={busy}>
              Cancel
            </button>
          </div>
        ) : (
          <div className="review-actions">
            <button
              type="button"
              className="btn btn--sm btn--outline"
              onClick={() => {
                setSaved(false);
                setMode('edit');
              }}
            >
              Edit
            </button>
            <button
              type="button"
              className="btn btn--sm btn--outline btn--danger-text"
              onClick={() => {
                setSaved(false);
                setMode('confirm');
              }}
            >
              Delete
            </button>
          </div>
        )}
      </section>
    </div>
  );
}
