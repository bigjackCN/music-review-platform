import AlbumList from './pages/AlbumList';

export default function App() {
  return (
    <>
      <header className="site-header">
        <div className="page">
          <a className="brand" href="/">
            <img src="/brand/logo.svg" alt="" width="34" height="34" />
            <span className="wordmark">Rotten Potatoes</span>
          </a>
        </div>
      </header>
      <main>
        <AlbumList />
      </main>
      <footer className="site-footer">
        <div className="page">
          <p>An independent project inspired by Rotten Tomatoes. Not affiliated with it.</p>
          <p>Album artwork from the iTunes Search API. Artwork belongs to its respective owners.</p>
        </div>
      </footer>
    </>
  );
}
