package kuzak.kuba.filmweb;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kuzak.kuba.filmweb.model.Movie;
import kuzak.kuba.filmweb.model.MovieComment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path("/movie")
public class MovieHandler {

	
	@PUT
    @Path("/addComment")
    public Response addComment(
    		@QueryParam("username") String username,
    		@QueryParam("time") String time,
    		@QueryParam("text") String text,
    		@QueryParam("movie_id") int movie_id
    ) {
		DBController dbc = new DBController();
        dbc.addComment(username, time, text, movie_id);

        return Response.ok()
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "POST, OPTIONS")
                .header("Access-Control-Allow-Headers", "Content-Type")
                .entity("Comment added successfully")
                .build();
    }
	

	@PUT
    @Path("/updateScore")
    public Response updateUserScore(
    		@QueryParam("userScore") double userScore,
            @QueryParam("username") String username,
            @QueryParam("movieId") int movieId){
        DBController dbc = new DBController();
        dbc.updateUserScore(username, movieId, userScore);
        return Response.ok()
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "PUT, OPTIONS")
                .header("Access-Control-Allow-Headers", "Content-Type")
                .entity("Update successful")
                .build();
    }

    @GET
    @Path("/getScore")
    @Produces("application/json")
    public String getUserScore(
            @QueryParam("username") String username,
            @QueryParam("movieId") int movieId) {
        DBController dbc = new DBController();
        double userScore = dbc.getUserScore(username, movieId);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
			return objectMapper.writeValueAsString(userScore);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return "";
    }
	
    @GET
    @Path("/{id}")
    @Produces("application/json")
    public String getMovieById(@PathParam("id") long id) throws JsonProcessingException {
        DBController dbc = new DBController();
        Movie movie = dbc.getMovieById(id);

        if (movie == null) {
            throw new NotFoundException("No movie with id " + id);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(movie);
    }
    
    
    @GET
    @Path("/get_all_movies")
    @Produces("application/json")
    public String get_all_movies() throws JsonProcessingException {
        
    	MovieList movieList = new MovieList();
    	
    	DBController dbc = new DBController();
    	List<Movie> get_all_movies = dbc.get_all_movies();
    	movieList.movies = get_all_movies;
    	ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(movieList);
    }
    
    
    class MovieList {
    	List<Movie> movies;

		public List<Movie> getMovies() {
			return movies;
		}

		public void setMovies(List<Movie> movies) {
			this.movies = movies;
		}
    	
    }
    
    
}
