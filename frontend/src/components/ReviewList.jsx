import { formatDate } from '../lib/format';
import Stars from './Stars';

export default function ReviewList({ reviews, currentUser }) {
  if (reviews.length === 0) {
    return <p className="empty">No reviews yet. Be the first.</p>;
  }

  return (
    <ul className="reviews">
      {reviews.map((r) => (
        <li className="review" key={r.id}>
          <div className="review-head">
            <strong className="review-user">{r.username}</strong>
            {currentUser === r.username && <span className="you">You</span>}
            <Stars rating={r.rating} />
            <time className="review-date" dateTime={r.createdAt}>{formatDate(r.createdAt)}</time>
          </div>
          {r.comment && <p className="review-text">{r.comment}</p>}
        </li>
      ))}
    </ul>
  );
}
