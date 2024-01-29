package kuzak.kuba.filmweb;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kuzak.kuba.filmweb.model.Movie;
import kuzak.kuba.filmweb.model.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/user")
public class UserHandler {
	

	@GET
    @Path("/info")
    @Produces("application/json")
	public String getUserInfo(@QueryParam("username") String username) throws JsonProcessingException {
        DBController dbc = new DBController();
        User user = dbc.getFullUserInfo(username);

        if (user == null) {
            throw new NotFoundException("No user with username: " + username);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(user);
    }
	
    @GET
    @Path("/auth")
    public String Auth(@QueryParam("username") String username, @QueryParam("password") String password) throws JsonProcessingException {
        DBController dbc = new DBController();

        if (username == null || password == null) {
            throw new BadRequestException("Both username and password are required");
        }
        if (dbc.authenticateUser(username, password)) {
        	
        	return "OK";
        }else {
        	return "Not OK";
        }

        
    }
    
    
    
    @GET
    @Path("/create")
    public void createUser(@QueryParam("username") String username, @QueryParam("password") String password, @QueryParam("nick") String nick) {
        DBController dbc = new DBController();
        dbc.createUser(username, password, nick);
    }
    
    
    @PUT
    @Path("/update")
    public Response Update(@QueryParam("username") String username,
                           @QueryParam("description") String description,
                           @QueryParam("genres") String genres,
                           @QueryParam("watchedMovies") String watchedMovies,
                           @QueryParam("image") String image) {
        DBController dbc = new DBController();

        dbc.updateUser(username, description, genres, watchedMovies, image);

        return Response.ok()
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "PUT, OPTIONS")
                .header("Access-Control-Allow-Headers", "Content-Type")
                .entity("Update successful")
                .build();
    }
     
    @PUT
    @Path("/addFavorite")
    public Response addFavorite(@QueryParam("username") String username, @QueryParam("watchedMovies") Long favoriteMovie)
    {
        DBController dbc = new DBController();
        dbc.addUserFavorite(username, favoriteMovie);
        
     // Add CORS headers
        return Response.ok()
                .header("Access-Control-Allow-Origin", "*") // Set the appropriate origin or "*" for any origin
                .header("Access-Control-Allow-Methods", "PUT, OPTIONS") // Adjust based on your needs
                .header("Access-Control-Allow-Headers", "Content-Type")
                .entity("Update successful") // You may adjust the response entity
                .build();
    }
    
    @PUT
    @Path("/removeFavorite")
    public Response removeFavorite(@QueryParam("username") String username, @QueryParam("watchedMovies") Long favoriteMovie)
    {
        DBController dbc = new DBController();
        dbc.removeUserFavorite(username, favoriteMovie);
        
     // Add CORS headers
        return Response.ok()
                .header("Access-Control-Allow-Origin", "*") // Set the appropriate origin or "*" for any origin
                .header("Access-Control-Allow-Methods", "PUT, OPTIONS") // Adjust based on your needs
                .header("Access-Control-Allow-Headers", "Content-Type")
                .entity("Update successful") // You may adjust the response entity
                .build();
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
