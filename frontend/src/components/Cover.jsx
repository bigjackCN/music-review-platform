import { useState } from 'react';

function hueFrom(text) {
  let h = 0;
  for (const c of text) h = (h * 31 + c.charCodeAt(0)) % 360;
  return h;
}

// The album title is always written next to the cover, so the image itself is decorative.
export default function Cover({ album }) {
  const [failed, setFailed] = useState(false);

  if (album.coverUrl && !failed) {
    return (
      <img
        className="cover"
        src={album.coverUrl}
        alt=""
        loading="lazy"
        onError={() => setFailed(true)}
      />
    );
  }

  return (
    <div className="cover cover--fallback" aria-hidden="true" style={{ '--hue': hueFrom(album.title) }}>
      <span>{album.title}</span>
    </div>
  );
}
