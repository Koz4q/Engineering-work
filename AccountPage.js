import React, { useEffect, useState } from 'react';
import './AccountPage.css';

function AccountPage({imgURL, userNick, userDescription, favoriteGenres, favoriteMovies, onEdit, onSave, onMain, onForum }) {
    const [isEditing, setEditing] = useState(false);
    const [editedDescription, setEditedDescription] = useState(userDescription);
    const [editedGenres, setEditedGenres] = useState([...favoriteGenres]);
    const [editedMovies, setEditedMovies] = useState([...favoriteMovies]);
    const [previewImage, setPreviewImage] = useState(null);
    const [imageURL, setImageURL] = useState(imgURL);
    const [movieData, setMovieData] = useState([]);
  
    useEffect(() => {
      fetchMovieData()
      setImageURL(imageURL);
      if (imageURL && isValidURL(imageURL)) {
        setPreviewImage(imageURL);
      } else if (imageURL) {
        const reader = new FileReader();
        reader.onloadend = () => {
          setPreviewImage(reader.result);
        };
        reader.readAsDataURL(imageURL);
      }

      setEditedMovies(editedMovies);
    }, [imageURL]);
  
    async function fetchMovieData() {
      try {
        const response = await fetch(`http://localhost:9999/movie/get_all_movies`);
        const movie_data = await response.json();
        
        console.log("Movie Data:");
        console.log(movie_data);
        if (Array.isArray(movie_data.movies)) {
          setMovieData(movie_data.movies);
        } else {
          console.error('Invalid movie data received:', movie_data);
        }
      } catch (error) {
        console.error('Error fetching movie data:', error);
      }
    }
  
  const isValidURL = (url) => {
    try {
      new URL(url);
      return true;
    } catch (error) {
      return false;
    }
  };
  
    const handleEditSave = async () => {
      if (isEditing) {
        
        const data = {
          username: userNick,
          description: editedDescription,
          genres: editedGenres.join(','),
          watchedMovies: editedMovies.join(','),
          image: imageURL ? imageURL : '',
        };
  
        try {
          const response = await fetch(
            `http://localhost:9999/user/update?username=${data.username}&description=${data.description}&genres=${data.genres}&watchedMovies=${data.watchedMovies}&image=${data.image}`
            ,{
              method: 'PUT',
            }
          );
  
          if (response.ok) {
            onSave(editedDescription, editedGenres, editedMovies, imageURL);
          } else {
            console.error('Failed to update profile');
          }
        } catch (error) {
          console.error('Error updating profile:', error);
        }
      }
      setEditing(!isEditing);
    };
  
    const handleGenreToggle = (genre) => {
      if (isEditing) {
        const updatedGenres = editedGenres.includes(genre)
          ? editedGenres.filter((g) => g !== genre)
          : [...editedGenres, genre];
        setEditedGenres(updatedGenres);
      }
    };
  
    const allGenres = [
      'Crime',
      'Drama',
      'Animation',
      'Adventure',
      'Comedy',
      'Mystery',
      'Action',
      'Fantasy',
      'Romance',
      'Western',
      'Sci-Fi',
      'Biography',
      'History',
      'Thriller',
      'Family',
      'War',
      'Music',
      'Film-Noir',
    ];
  
    const [isModalOpen, setModalOpen] = useState(false);
  
    const toggleModal = () => {
      setModalOpen(!isModalOpen);
    };
  
    const handleMovieToggle = (movie) => {
      let updatedMovies;
      if (editedMovies.includes(movie)) {
        updatedMovies = editedMovies.filter((m) => m !== movie);
      } else {
        updatedMovies = [...editedMovies, movie];
      }
    
      setEditedMovies(updatedMovies);
    };
  
    return (
      <div className="account-page">
        <div className="header">
          <div className="buttons">
            <button onClick={onMain}>Main</button>
            <button onClick={onForum}>Forum</button>
          </div>
          <div className="edit-button">
            <button onClick={handleEditSave}>{isEditing ? 'Save' : 'Edit'}</button>
          </div>
        </div>
  
        <div className="content">
          <div className="user-info">
            <div className={`user-details ${isEditing ? 'editable' : ''}`}>
            <div className={`user-image ${isEditing ? 'editable' : ''}`}>
  
              {isEditing ? (
              <>
                <div className="modal-content">
                      <input
                        type="text"
                        value={imageURL}
                        onChange={(e) => setImageURL(e.target.value)}
                        placeholder="Paste image URL here"
                      />
                      {/* Display the preview if the URL is valid */}
                      {imageURL && isValidURL(imageURL) && <img src={imageURL} alt="Preview" />}
                    </div>
              </>
            ) : (
              <div className="profile">
      {imageURL ? (
        <img src={imageURL} alt="Preview" />
      ) : (
        <span>Profile</span>
      )}
    </div>
            )}
          </div>
  
        
              <div className="user-nick">{userNick}</div>
              {isEditing ? (
                <div className="user-description-edit">
                  <input
                    type="text"
                    placeholder="Description..."
                    value={editedDescription}
                    onChange={(e) => setEditedDescription(e.target.value)}
                  />
                </div>
              ) : (
                <div className="user-description">{editedDescription || 'No description available'}</div>
              )}
  
  
            </div>
          </div>
  
          <div className="favorite-genres">
            <h3>Favorite Genres</h3>
            {isEditing ? (
              <div className="genre-checkboxes">
                {allGenres.map((genre) => (
                  <label key={genre} className="genre-checkbox">
                    <input
                      type="checkbox"
                      value={genre}
                      checked={editedGenres.includes(genre)}
                      onChange={() => handleGenreToggle(genre)}
                    />
                    {genre}
                  </label>
                ))}
              </div>
            ) : (
              <div className="user-genres">
                {editedGenres.map((genre) => (
                  <div key={genre} className="user-genre">
                    {genre}
                  </div>
                ))}
              </div>
            )}
          </div>
  
          <div className="favorite-movies">
    <h3>Favorite Movies</h3>
    {isEditing ? (
      <div>
        {/* Checkboxes for editing favorite movies */}
        {editedMovies.map((movie) => (
          <label key={movie} className="movie-checkbox">
            <input
              type="checkbox"
              value={movie}
              checked={editedMovies.includes(movie)}
              onChange={() => handleMovieToggle(movie)}
            />
            {movie}
          </label>
        ))}
      </div>
    ) : (
      <div className="user-movies">
        {editedMovies.map((movie, index) => (
          <div key={index} className="user-movie">
            {movie}
          </div>
        ))}
      </div>
    )}
  </div>
        </div>
      </div>
    );
    
  }

  export default AccountPage;