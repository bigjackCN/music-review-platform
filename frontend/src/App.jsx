import { Link, Route, Routes } from 'react-router-dom';
import AlbumList from './pages/AlbumList';
import AlbumDetail from './pages/AlbumDetail';

export default function App() {
  return (
    <>
      <header className="site-header">
        <div className="page">
          <Link className="brand" to="/">
            <img src="/brand/logo.svg" alt="" width="34" height="34" />
            <span className="wordmark">Rotten Potatoes</span>
          </Link>
        </div>
      </header>
      <main>
        <Routes>
          <Route path="/" element={<AlbumList />} />
          <Route path="/albums/:id" element={<AlbumDetail />} />
          <Route
            path="*"
            element={
              <div className="notice page">
                <strong>Page not found</strong>
                <Link to="/">Back to all albums</Link>
              </div>
            }
          />
        </Routes>
      </main>
      <footer className="site-footer">
        <div className="page">
          <p>An independent project inspired by Rotten Tomatoes. Not affiliated with it.</p>
          <p>Album artwork and previews from the iTunes Search API. They belong to their respective owners.</p>
        </div>
      </footer>
    </>
  );
}
