import { Link, Route, Routes } from 'react-router-dom';
import AlbumList from './pages/AlbumList';
import AlbumDetail from './pages/AlbumDetail';
import AuthPage from './pages/AuthPage';
import { useAuth } from './auth/useAuth';

export default function App() {
  const { username, logout } = useAuth();
  return (
    <>
      <header className="site-header">
        <div className="page">
          <Link className="brand" to="/">
            <img src="/brand/logo.svg" alt="" width="34" height="34" />
            <span className="wordmark">Rotten Potatoes</span>
          </Link>
          <nav className="user-nav" aria-label="Account">
            {username ? (
              <>
                <span className="user-name">{username}</span>
                <button type="button" className="btn btn--sm btn--outline" onClick={logout}>Sign out</button>
              </>
            ) : (
              <>
                <Link to="/login">Sign in</Link>
                <Link className="btn btn--sm" to="/register">Sign up</Link>
              </>
            )}
          </nav>
        </div>
      </header>
      <main>
        <Routes>
          <Route path="/" element={<AlbumList />} />
          <Route path="/albums/:id" element={<AlbumDetail />} />
          <Route path="/login" element={<AuthPage mode="login" />} />
          <Route path="/register" element={<AuthPage mode="register" />} />
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
          <p>Reviews from the demo accounts are sample data, not real people.</p>
          <p>Album artwork and previews from the iTunes Search API. They belong to their respective owners.</p>
        </div>
      </footer>
    </>
  );
}
