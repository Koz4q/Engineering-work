package kuzak.kuba.filmweb;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kuzak.kuba.filmweb.MovieHandler.MovieList;
import kuzak.kuba.filmweb.model.Forum;
import kuzak.kuba.filmweb.model.ForumComments;
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


@Path("/forum")
public class ForumHandler {

	@PUT
    @Path("/addThread")
    public Response addThread(
    		@QueryParam("thread_id") long thread_id,
            @QueryParam("username") String username,
            @QueryParam("title") String title,
    		@QueryParam("content") String content){
        DBController dbc = new DBController();
        dbc.addThread(thread_id, username, title, content);
        return Response.ok()
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "PUT, OPTIONS")
                .header("Access-Control-Allow-Headers", "Content-Type")
                .entity("Update successful")
                .build();
    }

    @GET
    @Path("/getThreads")
    @Produces("application/json")
    public String getThreads() throws JsonProcessingException {
        
    	ForumList forumList = new ForumList();
    	
    	DBController dbc = new DBController();
    	List<Forum> getThreads = dbc.getThreads();
    	forumList.threads = getThreads;
    	ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(forumList);
    }
    
    @GET
    @Path("/getComments")
    @Produces("application/json")
    public String getComments() throws JsonProcessingException {
        
    	ForumCommentsList forumCommentsList = new ForumCommentsList();
    	
    	DBController dbc = new DBController();
    	List<ForumComments> getComments = dbc.getComments();
    	forumCommentsList.comments = getComments;
    	ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(forumCommentsList);
    }
    
    @PUT
    @Path("/addComment")
    public Response addComment(
    		@QueryParam("thread_id") long thread_id,
            @QueryParam("comment_id") long comment_id,
            @QueryParam("text") String text,
    		@QueryParam("username") String username){
        DBController dbc = new DBController();
        dbc.addComment(thread_id, comment_id, text, username);
        return Response.ok()
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "PUT, OPTIONS")
                .header("Access-Control-Allow-Headers", "Content-Type")
                .entity("Update successful")
                .build();
    }
    
    class ForumList {
    	List<Forum> threads;

		public List<Forum> getThreads() {
			return threads;
		}

		public void setMovies(List<Forum> threads) {
			this.threads = threads;
		}
    	
    }
    
    class ForumCommentsList {
    	List<ForumComments> comments;

		public List<ForumComments> getComments() {
			return comments;
		}

		public void setComments(List<ForumComments> comments) {
			this.comments = comments;
		}
    	
    }
	
	
}
