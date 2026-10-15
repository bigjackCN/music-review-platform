export default function Stars({ rating }) {
  return (
    <span className="stars" role="img" aria-label={`${rating} out of 5`}>
      {'★'.repeat(rating)}
      <span className="off">{'★'.repeat(5 - rating)}</span>
    </span>
  );
}
