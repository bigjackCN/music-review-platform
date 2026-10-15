import { formatDate } from '../lib/format';

export default function ReviewList({ reviews, currentUser, onDelete }) {
  if (reviews.length === 0) {
    return <p className="empty">No reviews yet. Be the first.</p>;
  }

  return (
    <ul className="reviews">
      {reviews.map((r) => (
        <li className="review" key={r.id}>
          <div className="review-head">
            <strong className="review-user">{r.username}</strong>
            <span className="stars" role="img" aria-label={`${r.rating} out of 5`}>
              {'★'.repeat(r.rating)}
              <span className="off">{'★'.repeat(5 - r.rating)}</span>
            </span>
            <time className="review-date" dateTime={r.createdAt}>{formatDate(r.createdAt)}</time>
            {currentUser === r.username && (
              <button type="button" className="link-btn" onClick={() => onDelete(r.id)}>
                Delete
              </button>
            )}
          </div>
          {r.comment && <p className="review-text">{r.comment}</p>}
        </li>
      ))}
    </ul>
  );
}
