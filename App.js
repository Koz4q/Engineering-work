import React, { useEffect, useState } from 'react';
import AccountPage from './AccountPage.js';
import './App.css';
import ForumPage from './ForumPage.js';
import MoviePage from './MoviePage.js';

function App() {
  const [userData, setUserData] = useState(null);
  const [isLoggedIn, setLoggedIn] = useState(false);
  const [isLoginPopupOpen, setLoginPopupOpen] = useState(false);
  const [usernameInput, setUsernameInput] = useState('');
  const [loggedUsername, setloggedUsername] = useState('');
  const [passwordInput, setPasswordInput] = useState('');
  const [movieData, setMovieData] = useState([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState([]);
  const [isRegisterPopupOpen, setRegisterPopupOpen] = useState(false);
  const [registerUsername, setRegisterUsername] = useState('');
  const [registerPassword, setRegisterPassword] = useState('');
  const [registerNick, setRegisterNick] = useState('');
  const [posterData, setPosterData] = useState([]);
  const [isAccountPageVisible, setAccountPageVisible] = useState(false);

  const [isMainVisible, setisMainVisible] = useState(true);

  const [userDescription, setUserDescription] = useState('');
  const [favoriteGenres, setFavoriteGenres] = useState([]);
  const [favoriteMovies, setFavoriteMovies] = useState([]);

  const [isMoviePageVisible, setMoviePageVisible] = useState(false);
  const [selectedMovie, setSelectedMovie] = useState(null);

  const [isForumPageVisible, setForumPageVisible] = useState(false);
  const [progress, setProgress] = useState(0);

  const [imageURL, setImageURL] = useState('');
  
  useEffect(() => {
    fetchMovieData();
    fetchPosterData();
    
    const interval = setInterval(() => {
      setProgress((prevProgress) => {
        if (prevProgress < 100) {
          return prevProgress + 1;
        } else {
          fetchPosterData();
          return 0;
        }
      });
    }, 150)
    return () => {
      clearInterval(interval);
    }
  }, []);

  const handleMovieClick = (movie) => {
    console.log("Movie Click: "+JSON.stringify(movie))
    setSelectedMovie(movie);
    
    setMoviePageVisible(true);
    setisMainVisible(false);
  
    setSearchQuery('');
    setSearchResults([]);
  };

  async function fetchPosterData() {
    try {
      const response = await fetch('http://localhost:9999/movie/get_all_movies');
      const data = await response.json();

      if (Array.isArray(data.movies)) {
        const uniquePosters = chooseUniquePosters(data.movies, 3);
        setPosterData(uniquePosters);
      } else {
        console.error('Invalid poster data received:', data);
      }
    } catch (error) {
      console.error('Error fetching poster data:', error);
    }
  }
  
  function chooseUniquePosters(movies, count) {
    const shuffledMovies = movies.sort(() => Math.random() - 0.5);
    
    return shuffledMovies.slice(0, count);
  }

  async function fetchUserData() {
    try {
      console.log("Start fetch");
      const response = await fetch(`http://localhost:9999/user/auth?username=${usernameInput}&password=${passwordInput}`, {
      });
      
      const data = await response.text();
      console.log(response);
      setUserData(data);
      if(data === "OK"){
        setLoggedIn(true);
        setloggedUsername(usernameInput)
        loadedUserData(usernameInput); //HERE
      }else{
        setLoggedIn(false);
      }
      console.log("Data:",data);
    } catch (error) {
      console.error('Error fetching user data:', error);
    }
  }

  async function fetchMovieData() {
    try {
      const response = await fetch(`http://localhost:9999/movie/get_all_movies`);
      const data = await response.json();
      
      if (Array.isArray(data.movies)) {
        setMovieData(data.movies);
      } else {
        console.error('Invalid movie data received:', data);
      }
    } catch (error) {
      console.error('Error fetching movie data:', error);
    }
  }
  
  async function fetchAccountData() {
    try {
      console.log("Start Account fetch");
      const response = await fetch(`http://localhost:9999/user/info?username=${loggedUsername}`);
      const data = await response.json();
  
      if (data.id =! null) {
        setUserData(data);
        setisMainVisible(false);
        setAccountPageVisible(true);
        console.log("Data:");
        console.log(data);
      } else {
        console.error('Invalid user data received:', data);
      }
    } catch (error) {
      console.error('Error fetching user data:', error);
    }
  }
  
  const handleAccountButtonClick = () => {
    fetchAccountData();
  }

  async function loadedUserData(loadedUser) {
    try {
      console.log("Start loadedUserData fetch " + loadedUser);
      const response = await fetch(`http://localhost:9999/user/info?username=${loadedUser}`);
      const data = await response.json();
  
      if (data.id =! null) {
        setUserData(data);
        console.log("Data:");
        console.log(data);
      } else {
        console.error('Invalid user data received:', data);
      }
    } catch (error) {
      console.error('Error fetching user data:', error);
    }
  }

  const handleRegister = async () => {
    try {
      const response = await fetch(`http://localhost:9999/user/create?username=${registerUsername}&password=${registerPassword}&nick=${registerNick}`, {
      });
        console.log(response);
    } catch (error) {
        console.error('Error registering user:', error);
    }
    setRegisterPopupOpen(false);
};

  const handleSearch = () => {
  if (searchQuery.trim() === '') {
    setSearchResults([]);
    return;
  }

    const filteredMovies = movieData.filter(movie =>
      movie.title.toLowerCase().includes(searchQuery.toLowerCase())
    );
    setSearchResults(filteredMovies.slice(0, 5));
    
  };
  

  const handleLogin = () => {
    if (usernameInput.trim() === '' || passwordInput.trim() === '') {
      console.error('Please enter both username and password.');
      setLoginPopupOpen(false);
      return;
    }
    console.log("Start handleLogin");
    fetchUserData();
    

    if(userData === "OK"){
      setLoggedIn(true);
    }else{
      setLoggedIn(false);
    }
    setLoginPopupOpen(false);
  };

  const openLoginPopup = () => {
    setLoginPopupOpen(true);
    if(userData !== "OK"){
      setLoggedIn(false);
      setUserData("Empty");
    }
    
  };

  const closeLoginPopup = () => {
    setLoginPopupOpen(false);
  };

  return (
    <div className="app-container">
      
      {isMainVisible && (
        /* Header section */
      <div>
        <div className="header">
      <div className="logo">
        Vide Hub
      </div>
      <div className="search-bar">
        <input
          type="text"
          placeholder="Search movies..."
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
        />
        <button onClick={handleSearch}>Search</button>
        {searchResults.length > 0 && (
          <div className="search-results">
            {searchResults.map(movie => (
              <div key={movie.id} onClick={() => console.log(`Selected movie: ${movie.title}`)}>
              </div>
            ))}
          </div>
        )}
      </div>
      {isLoggedIn && userData && (
        <div className="welcome-message">
          Welcome, {loggedUsername || 'Guest'}!
        </div>
      )}
      <div className="buttons">
        <button onClick={openLoginPopup}>{isLoggedIn ? 'Logout' : 'Login'}</button>
        <button onClick={() => setRegisterPopupOpen(true)}>Register</button>
        {isLoggedIn && (
          <button onClick={() => {handleAccountButtonClick()}    }>Account</button>
        )}
      </div>
    </div>
    {/* Poster section */}
    <div className="posters">
      {posterData.map(poster => (
        <div key={poster.id} className="poster">
          <img src={poster.posterUrl} alt={poster.title} />
          <p>{poster.title}</p>
        </div>
      ))}
    </div>
    <div>
      <h2>Progress: {progress}%</h2>
      <progress value={progress} max="100" />
    </div>

      </div>

      )}
  
      {/* Login and Register Popup */}
      {isLoginPopupOpen && (
          <div className="popup">
            <div className="popup-content">
              <span className="close" onClick={closeLoginPopup}>
                &times;
              </span>
              <h2>Login</h2>
              <div className="popup-values">
              <label htmlFor="username">Username: </label>
              <input
                type="text"
                id="username"
                value={usernameInput}
                onChange={(e) => setUsernameInput(e.target.value)}
              />
              <label htmlFor="password">  Password: </label>
              <input
                type="password"
                id="password"
                value={passwordInput}
                onChange={(e) => setPasswordInput(e.target.value)}
              />
              </div>
              <div className="login-button">
              <button onClick={handleLogin}>Login</button>
              </div>
            </div>
          </div>
        )}
  
  {isRegisterPopupOpen && (
  <div className="popup">
    <div className="popup-content">
      <span className="close" onClick={() => setRegisterPopupOpen(false)}>
        &times;
      </span>
      <h2>Register</h2>
      <div className="popup-values">
        <label htmlFor="register-username">Username:</label>
        <input
          type="text"
          id="register-username"
          value={registerUsername}
          onChange={(e) => setRegisterUsername(e.target.value)}
        />
        <label htmlFor="register-password">Password:</label>
        <input
          type="password"
          id="register-password"
          value={registerPassword}
          onChange={(e) => setRegisterPassword(e.target.value)}
        />
        <label htmlFor="register-nick">Nick:</label>
        <input
          type="text"
          id="register-nick"
          value={registerNick}
          onChange={(e) => setRegisterNick(e.target.value)}
        />
      </div>
      <div className="create-button">
        <button onClick={handleRegister}>Create</button>
      </div>
    </div>
  </div>
)}
  
      {/* Main content section */}
      <div className="content">
        {searchResults.length > 0 ? (
          // Display only the searched movies
          <div className="movies-container">
    {searchResults.map((movie) => (
      <div key={movie.id} className="movie" onClick={() => {handleMovieClick(movie);setisMainVisible(false)}}>
        <h2>{movie.title}</h2>
        <div className="poster">
          <img src={movie.posterUrl} alt={movie.title} />
        </div>
      </div>
    ))}
  </div>
        ) : (
          // Display nothing if there are no search results
          null
        )}
      </div>
  
      {/* Account Page */}
      {isAccountPageVisible && (
        <AccountPage
        imgURL={userData.image}
        userNick={userData.nick || 'Guest'}
        userDescription={userData.description}
        favoriteGenres={userData.generes}
        favoriteMovies={userData.watchedMovies}
        onMain={() => {setAccountPageVisible(false); setisMainVisible(true); }}
        onForum={() => {setAccountPageVisible(false); setisMainVisible(false); setForumPageVisible(true)}}
        onSave={(imageURL, editedDescription, editedGenres, editedMovies) => {
          // Handle saving changes to the database
          setImageURL(imageURL);
          setUserDescription(editedDescription);
          setFavoriteGenres(editedGenres);
          setFavoriteMovies(editedMovies);
        }}
      />
      )}

        {/* Movie Page */}
      {isMoviePageVisible && selectedMovie && (
        <MoviePage movie={selectedMovie} onMain={() => { setMoviePageVisible(false); setisMainVisible(true); }} 
        isLoggedIn={isLoggedIn}
        loggedUsername={loggedUsername}
        userData={userData}
        />
      )}

      {/* Forum Page */}
      {isForumPageVisible && (
        <ForumPage
          onMain={() => {
            setForumPageVisible(false);
            setisMainVisible(true);
          }}
          isLoggedIn={isLoggedIn}
          loggedUsername={loggedUsername}
          userData={userData}
        />
      )}

    </div>
  );
}

export default App;
