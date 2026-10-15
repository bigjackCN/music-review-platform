import AlbumList from './pages/AlbumList';

export default function App() {
  return (
    <>
      <header className="site-header">
        <div className="page">
          <span className="wordmark">Music Review Platform</span>
        </div>
      </header>
      <main>
        <AlbumList />
      </main>
      <footer className="site-footer">
        <div className="page">Album artwork from the iTunes Search API. Artwork belongs to its respective owners.</div>
      </footer>
    </>
  );
}
