import { useState } from 'react';

function hueFrom(text) {
  let h = 0;
  for (const c of text) h = (h * 31 + c.charCodeAt(0)) % 360;
  return h;
}

// iTunes artwork URLs end in a size such as /600x600bb.jpg and the server serves other sizes too,
// so small thumbnails can ask for a much smaller file. Other URLs are left alone.
function sized(url, size) {
  return size ? url.replace(/\/\d+x\d+bb\.(jpg|png)$/, `/${size}x${size}bb.$1`) : url;
}

// The album title is always written next to the cover, so the image itself is decorative.
export default function Cover({ album, size }) {
  const [failed, setFailed] = useState(false);

  if (album.coverUrl && !failed) {
    return (
      <img
        className="cover"
        src={sized(album.coverUrl, size)}
        alt=""
        loading="lazy"
        decoding="async"
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
