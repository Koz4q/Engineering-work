import React, { useEffect, useState } from 'react';
import './MoviePage.css';

function MoviePage({ movie, onMain, isLoggedIn, loggedUsername, userData }) {

    useEffect(() => {

        if(isCommentsLoaded === false){
            let DataById = fetchMovieDataById(movie.id);
        }
        fetchMovieScore();
    }, []);

    let isMovieFavorite = false;
    if(userData != null){
      isMovieFavorite = userData.watchedMovies.includes(movie.id);
    }
    console.log("Movie Page: " + JSON.stringify(movie));
    const [userScore, setUserScore] = useState(0);
    const [comments, setComments] = useState([]);
    const [isFavorite, setIsFavorite] = useState(isMovieFavorite);
  
    const [commentText, setCommentText] = useState('');
  
    const handleScoreChange = async (value) => {
      const newValue = parseFloat((userScore + value).toFixed(1));
      const clampedValue = Math.min(10, Math.max(0, newValue));
      setUserScore(isNaN(clampedValue) ? 0 : clampedValue);
    
      try {
        const response = await fetch(`http://localhost:9999/movie/updateScore?userScore=${clampedValue}&username=${loggedUsername}&movieId=${movie.id}`, {
          method: 'PUT',
        });
    
        if (response.ok) {
          console.log('Score updated successfully');
        } else {
          console.error('Failed to update score:', response.status, response.statusText);
        }
      } catch (error) {
        console.error('Error while updating score:', error);
      }
    };
    
  
    const [isCommentsLoaded, setIsCommentsLoaded] = useState(false);
    

    async function fetchMovieDataById(id) {
        try {
          const response = await fetch('http://localhost:9999/movie/'+id);
          const data = await response.json();
          console.log("Movie Data: "+JSON.stringify(data))
          setComments(data.comments);
          setIsCommentsLoaded(true);
          return data;
    
        } catch (error) {
          console.error('Error fetchMovieDataById', error);
        }
      }

    async function fetchMovieScore() {
      try {
        const response = await fetch(`http://localhost:9999/movie/getScore?username=${loggedUsername}&movieId=${movie.id}`);
        const data = await response.json();
        console.log("Score data: "+JSON.stringify(data))
        setUserScore(data);
  
      } catch (error) {
        console.error('Error fetchMovieScore', error);
      }

    }

    
    const backgroundImageStyle = {
      backgroundImage: `url(${movie.posterUrl})`,
      backgroundSize: 'cover',
      backgroundPosition: 'center',
      backgroundRepeat: 'no-repeat',
      opacity: 1,
    };
  
    const handleAddComment = () => {
      if (commentText.trim() === '') {
        return;
      }
  
      const newComment = {
        username: loggedUsername,
        text: commentText,
        time: new Date().toLocaleString(),
      };
  
      setComments([...comments, newComment]);
      setCommentText('');
    };
  
    const handleToggleFavorite = async () => {
      try {
        const response = '?';
        if(isFavorite){
          response = await fetch(
            `http://localhost:9999/user/removeFavorite?username=${loggedUsername}&watchedMovies=${movie.id}`,
            {
              method: 'PUT',
            }
          );
        }else{
          response = await fetch(
            `http://localhost:9999/user/addFavorite?username=${loggedUsername}&watchedMovies=${movie.id}`,
            {
              method: 'PUT',
            }
          );
        }
        
        if (response.ok) {
          console.log('Movie marked as watched!');
        } else {
          console.error('Failed to update watched movies.');
        }
      } catch (error) {
        console.error('Error updating watched movies:', error);
      }
  
      setIsFavorite(!isFavorite);
    };
  
    return (
      <div className="movie-page" style={backgroundImageStyle}>
        <div className="header">
          <div className="buttons">
            <button onClick={onMain}>Main</button>
          </div>
          <div className="favorite-button">
            {isLoggedIn && (
              <button onClick={handleToggleFavorite} className={isFavorite ? 'favorite' : 'not-favorite'}>
                {isFavorite ? 'Set to not favorite' : 'Set favorite'}
              </button>
            )}
          </div>
        </div>
        <div className="content" style={backgroundImageStyle}>
          <h2>{movie.title}</h2>
          <div className="details-container">
            <div className="poster">
              <img src={movie.posterUrl} alt={movie.title} />
            </div>
            <div className="overview">
              <h3>Overview</h3>
              <p>{movie.overview}</p>
            </div>
          </div>
          <div className="scores">
            <div className="score">
              <h3>Score</h3>
              <p>{movie.rating}</p>
            </div>
            <div className="user-score">
              <h3>User Score</h3>
              <div className="score-adjustment">
                <p>{userScore}</p>
                <button onClick={() => handleScoreChange(-0.1)}>-</button>
                <button onClick={() => handleScoreChange(-0.5)}>-5</button>
                <button onClick={() => handleScoreChange(0.1)}>+</button>
                <button onClick={() => handleScoreChange(0.5)}>+5</button>
              </div>
            </div>
            <div className="director-stars">
              <h3>Director</h3>
              <p>{movie.director}</p>
              <h3>Stars</h3>
              <p>
                {movie.star1}, {movie.star2}, {movie.star3}, {movie.star4}
              </p>
            </div>
          </div>
          <div className="details-row">
            <div className="year">
              <h3>Year</h3>
              <p>{movie.released_year}</p>
            </div>
            <div className="genres">
              <h3>Genres</h3>
              <p>{movie.genre}</p>
            </div>
            <div className="time">
              <h3>Time</h3>
              <p>{movie.runt}</p>
            </div>
          </div>
          <div className="comments">
            <h3>Comments</h3>
            <div className="comments-list">
            {comments.map((comment, index) => (
              <div key={index} className="comment">
                <p>
                  <strong>{comment.username}</strong> - {comment.time}
                </p>
                <p>{comment.text}</p>
              </div>
            ))}
          </div>
          {isLoggedIn && (
            <div className="add-comment">
              <textarea
                placeholder="Enter your comments..."
                value={commentText}
                onChange={(e) => setCommentText(e.target.value)}
              ></textarea>
              <button onClick={handleAddComment}>Add Comment</button>
            </div>
          )}
          </div>
        </div>
      </div>
    );
  }

  export default MoviePage;