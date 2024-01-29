package kuzak.kuba.filmweb;

import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kuzak.kuba.filmweb.model.Forum;
import kuzak.kuba.filmweb.model.ForumComments;
import kuzak.kuba.filmweb.model.Movie;
import kuzak.kuba.filmweb.model.MovieComment;
import kuzak.kuba.filmweb.model.User;

public class DBController {

	final String connection_string = "jdbc:postgresql://127.0.0.1:5432/postgres";
	final String username = "app_user";
	final String password = "1j2a3k4u5b";
	
	public void addComment(String username, String time ,String text, int movie_id) {
        String SQL_INSERT = "INSERT INTO movie_comment (username, time, text, movie_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(connection_string, this.username, this.password);
             PreparedStatement preparedStatement = conn.prepareStatement(SQL_INSERT)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, time);
            preparedStatement.setString(3, text);
            preparedStatement.setInt(4, movie_id);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	List<MovieComment> getCommentsByMovie(long id) {
		
		List<MovieComment> comments  = new ArrayList<>();

		String SQL_SELECT = "SELECT * FROM movie_comment WHERE movie_id = ?";

        try (Connection conn = DriverManager.getConnection(
        		connection_string, username, password);
             PreparedStatement preparedStatement = conn.prepareStatement(SQL_SELECT)) {

        	preparedStatement.setLong(1, id);
        		
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
            	MovieComment comment = extractCommentFromResultSet(resultSet);
                comments.add(comment);
            }

            

        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return comments;
		
	}
	
	 private MovieComment extractCommentFromResultSet(ResultSet resultSet) throws SQLException {
	        String username = resultSet.getString("username");
	        String time = resultSet.getString("time");
	        String text = resultSet.getString("text");

	        MovieComment comment = new MovieComment();
	        comment.setUsername(username);
	        comment.setTime(time);
	        comment.setText(text);

	        return comment;
	    }
	
	public void updateUserScore(String usern, int movieId, double userScore) {
        String SQL_UPDATE = "INSERT INTO movie_score (username, movie_id, user_score)\r\n"
        		+ "VALUES (?, ?, ?)\r\n"
        		+ "ON CONFLICT (username, movie_id) DO UPDATE\r\n"
        		+ "SET user_score = EXCLUDED.user_score;";

        try (Connection conn = DriverManager.getConnection(connection_string, username, password);
             PreparedStatement preparedStatement = conn.prepareStatement(SQL_UPDATE)) {

        	preparedStatement.setDouble(3, userScore);
            preparedStatement.setString(1, usern);
            preparedStatement.setInt(2, movieId);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public double getUserScore(String usern, int movieId) {
        String SQL_SELECT = "SELECT user_score FROM movie_score WHERE username = ? AND movie_id = ?";

        try (Connection conn = DriverManager.getConnection(connection_string, username, password);
                PreparedStatement preparedStatement = conn.prepareStatement(SQL_SELECT)) {

               preparedStatement.setString(1, usern);
               preparedStatement.setInt(2, movieId);
               ResultSet resultSet = preparedStatement.executeQuery();

               if (resultSet.next()) {
                   double userScore = resultSet.getDouble("user_score");
                   return userScore;
               } else {
                   System.out.println("User Score not found");
                   return -1;
               }

           } catch (SQLException e) {
               System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
           } catch (Exception e) {
               e.printStackTrace();
           }

           return -1;
    }
	
	
	Movie getMovieById(long id) {
		List<MovieComment> commentsByMovie = getCommentsByMovie(id);
        String SQL_SELECT = "SELECT * FROM movies_gen WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(
                connection_string, username, password);
             PreparedStatement preparedStatement = conn.prepareStatement(SQL_SELECT)) {

            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Movie extractMovieFromResultSet = extractMovieFromResultSet(resultSet);
                extractMovieFromResultSet.setComments(commentsByMovie);
				return extractMovieFromResultSet;
            }

        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
	
	User getFullUserInfo(String usern) {
	    String SQL_SELECT = "SELECT * FROM account WHERE username = ?";

	    try (Connection conn = DriverManager.getConnection(
	            connection_string, username, password);
	         PreparedStatement preparedStatement = conn.prepareStatement(SQL_SELECT)) {

	        preparedStatement.setString(1, usern);

	        ResultSet resultSet = preparedStatement.executeQuery();

	        if (resultSet.next()) {
	            return extractFullUserFromResultSet(resultSet);
	        }

	    } catch (SQLException e) {
	        System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return null;
	}

	
	public void addUserFavorite(String username, Long favoriteMovies) {
		System.out.println("Adding user favorite.");
		 String SQL_UPDATE = "UPDATE account SET top_movies = CONCAT(top_movies, ',', ?) WHERE username = ?";
		 
       try (Connection conn = DriverManager.getConnection(
               connection_string, this.username, this.password);
            PreparedStatement preparedStatement = conn.prepareStatement(SQL_UPDATE)) {

       	
           preparedStatement.setLong(1, favoriteMovies);
           preparedStatement.setString(2, username);
           preparedStatement.executeUpdate();

       } catch (SQLException e) {
           System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
       } catch (Exception e) {
           e.printStackTrace();
       }
   }
	
	public void removeUserFavorite(String username, Long favoriteMovies) {
		 User fullUserInfo = getFullUserInfo(username);
		 List<Long> watchedMovies = fullUserInfo.getWatchedMovies();
		 watchedMovies.removeIf(item -> item.equals(favoriteMovies));
		 String SQL_UPDATE = "UPDATE account SET top_movies = ? WHERE username = ?";

      try (Connection conn = DriverManager.getConnection(
              connection_string, this.username, this.password);
           PreparedStatement preparedStatement = conn.prepareStatement(SQL_UPDATE)) {

      	
          preparedStatement.setString(1, String.join(",", watchedMovies.stream().map(Object::toString).toArray(String[]::new)));
          preparedStatement.setString(2, username);
          preparedStatement.executeUpdate();

      } catch (SQLException e) {
          System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
      } catch (Exception e) {
          e.printStackTrace();
      }
  }
	
	
	public boolean authenticateUser(String username, String password) {
        String SQL_SELECT = "SELECT * FROM account WHERE username = ? AND pass = ?";

        try (Connection conn = DriverManager.getConnection(
                connection_string, this.username, this.password);
             PreparedStatement preparedStatement = conn.prepareStatement(SQL_SELECT)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();

            // If a record with the given username and password exists, return true
            return resultSet.next();

        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
	
	public void updateUser(String username, String description, String genres, String watchedMovies, String image) {
		 String SQL_UPDATE = "UPDATE account SET description = ?, generes = ?, top_movies = ?, image = ? WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(
                connection_string, this.username, this.password);
             PreparedStatement preparedStatement = conn.prepareStatement(SQL_UPDATE)) {

        	preparedStatement.setString(1, description);
        	preparedStatement.setString(2, genres);
            preparedStatement.setString(3, watchedMovies);
            preparedStatement.setString(4, image);
            preparedStatement.setString(5, username);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	
	public void createUser(String username, String password, String nick) {
	    String SQL_INSERT = "INSERT INTO account (username, pass, nick) VALUES (?, ?, ?)";

	    try (Connection conn = DriverManager.getConnection(
	            connection_string, this.username, this.password);
	         PreparedStatement preparedStatement = conn.prepareStatement(SQL_INSERT)) {

	        preparedStatement.setString(1, username);
	        preparedStatement.setString(2, password);
	        preparedStatement.setString(3, nick);

	        preparedStatement.executeUpdate();

	    } catch (SQLException e) {
	        System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	private Movie extractMovieFromResultSet(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong("id");
        String title = resultSet.getString("title");
        long released_year = resultSet.getLong("released_year");
    	String certificate = resultSet.getString("certificate");
    	String runt = resultSet.getString("runt");
    	String genre = resultSet.getString("genre");
    	long rating = resultSet.getLong("rating");
    	String overview = resultSet.getString("overview");
    	long score = resultSet.getLong("score");
    	String director = resultSet.getString("director");
    	String star1 = resultSet.getString("star1");
    	String star2 = resultSet.getString("star2");
    	String star3 = resultSet.getString("star3");
    	String star4 = resultSet.getString("star4");
    	long votes = resultSet.getLong("votes");
    	String posters = resultSet.getString("posters");

    	Movie movie = new Movie();
    	movie.setId(id);
    	movie.setTitle(title);
    	movie.setReleased_year(released_year);
    	movie.setCertificate(certificate);
    	movie.setRunt(runt);
    	movie.setGenre(genre);
    	movie.setRating(rating);
    	movie.setOverview(overview);
    	movie.setScore(score);
    	movie.setDirector(director);
    	movie.setStar1(star1);
    	movie.setStar2(star2);
    	movie.setStar3(star3);
    	movie.setStar4(star4);
    	movie.setVotes(votes);
    	movie.setPosterUrl(posters);

        return movie;
    }
	
	private User extractFullUserFromResultSet(ResultSet resultSet) throws SQLException {
	    int id = resultSet.getInt("id");
	    String username = resultSet.getString("username");
	    String password = resultSet.getString("pass");
	    String nick = resultSet.getString("nick");
	    String description = resultSet.getString("description");

	    String genresString = resultSet.getString("generes");
	    List<String> genres = parseGenres(genresString);

	    String watchedMoviesString = resultSet.getString("top_movies");
	    List<Long> watchedMovies = parseWatchedMovies(watchedMoviesString);

	    String image = resultSet.getString("image");

	    User user = new User();
	    user.setId(id);
	    user.setUsername(username);
	    user.setPassword(password);
	    user.setNick(nick);
	    user.setDescription(description);
	    user.setGeneres(genres);
	    user.setWatchedMovies(watchedMovies);
	    user.setImage(image);

	    return user;
	}

	private List<String> parseGenres(String genresString) {
	    // Split the comma-separated string into a list
	    return Arrays.asList(genresString.split(","));
	}
	
	private List<Long> parseWatchedMovies(String watchedMoviesString) {
		if(watchedMoviesString == null || watchedMoviesString.isEmpty()) {
			return new ArrayList<Long>();
		}
		String[] split = watchedMoviesString.split(",");
		 ArrayList<Long> arrayList = new ArrayList<Long>();
		 for(String s : split) {
			 arrayList.add(Long.parseLong(s));
		 }
		Arrays.asList(split);
		return arrayList;
	}
	 
	
	List<Movie> get_all_movies() {
		
		List<Movie> result = new ArrayList<>();

        String SQL_SELECT = "Select * from movies_gen";

        // auto close connection and preparedStatement
        try (Connection conn = DriverManager.getConnection(
        		connection_string, username, password);
             PreparedStatement preparedStatement = conn.prepareStatement(SQL_SELECT)) {

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                long id = resultSet.getLong("id");
                String title = resultSet.getString("title");
                long released_year = resultSet.getLong("released_year");
            	String certificate = resultSet.getString("certificate");
            	String runt = resultSet.getString("runt");
            	String genre = resultSet.getString("genre");
            	long rating = resultSet.getLong("rating");
            	String overview = resultSet.getString("overview");
            	long score = resultSet.getLong("score");
            	String director = resultSet.getString("director");
            	String star1 = resultSet.getString("star1");
            	String star2 = resultSet.getString("star2");
            	String star3 = resultSet.getString("star3");
            	String star4 = resultSet.getString("star4");
            	long votes = resultSet.getLong("votes");
            	String posters = resultSet.getString("posters");

                Movie obj = new Movie();
                obj.setId(id);
                obj.setTitle(title);
                obj.setReleased_year(released_year);
                obj.setCertificate(certificate);
                obj.setRunt(runt);
                obj.setGenre(genre);
                obj.setRating(rating);
                obj.setOverview(overview);
                obj.setScore(score);
                obj.setDirector(director);
                obj.setStar1(star1);
                obj.setStar2(star2);
                obj.setStar3(star3);
                obj.setStar4(star4);
                obj.setVotes(votes);
                obj.setPosterUrl(posters);
                
                result.add(obj);

            }

        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
		return result;
		
	}
	
	List<Forum> getThreads() {
		
		List<Forum> result = new ArrayList<>();

        String SQL_SELECT = "SELECT * FROM threads";

        try (Connection conn = DriverManager.getConnection(
        		connection_string, username, password);
             PreparedStatement preparedStatement = conn.prepareStatement(SQL_SELECT)) {

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                long thread_id = resultSet.getLong("thread_id");
                String title = resultSet.getString("title");
            	String content = resultSet.getString("content");
            	String username = resultSet.getString("username");

                Forum obj = new Forum();
                obj.setThread_id(thread_id);
                obj.setTitle(title);
                obj.setContent(content);
                obj.setUsername(username);
                
                
                result.add(obj);

            }

        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
		return result;
		
	}
	
	List<ForumComments> getComments() {
		
		List<ForumComments> result = new ArrayList<>();

        String SQL_SELECT = "SELECT * FROM comments";

        try (Connection conn = DriverManager.getConnection(
        		connection_string, username, password);
             PreparedStatement preparedStatement = conn.prepareStatement(SQL_SELECT)) {

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                long thread_id = resultSet.getLong("thread_id");
                long comment_id = resultSet.getLong("comment_id");
            	String text = resultSet.getString("text");
            	String username = resultSet.getString("username");

                ForumComments obj = new ForumComments();
                obj.setThread_id(thread_id);
                obj.setComment_id(comment_id);
                obj.setText(text);
                obj.setUsername(username);
                
                
                result.add(obj);

            }

        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
		return result;
		
	}
	
	public void addThread(long thread_id, String username, String title ,String content) {
        String SQL_INSERT = "INSERT INTO threads (thread_id, title, content, username) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(connection_string, this.username, this.password);
             PreparedStatement preparedStatement = conn.prepareStatement(SQL_INSERT)) {

            preparedStatement.setLong(1, thread_id);
            preparedStatement.setString(2, title);
            preparedStatement.setString(3, content);
            preparedStatement.setString(4, username);
            preparedStatement.executeUpdate();

            
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	public void addComment(long thread_id, long comment_id, String text , String username) {
        String SQL_INSERT = "INSERT INTO comments (comment_id, thread_id, text, username) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(connection_string, this.username, this.password);
             PreparedStatement preparedStatement = conn.prepareStatement(SQL_INSERT)) {

            preparedStatement.setLong(1, comment_id);
            preparedStatement.setLong(2, thread_id);
            preparedStatement.setString(3, text);
            preparedStatement.setString(4, username);
            preparedStatement.executeUpdate();

            
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	public static void main(String[] args) {

        

    }
	
}
