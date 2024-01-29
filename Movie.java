package kuzak.kuba.filmweb.model;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.QueryParam;

import org.json.JSONObject;

public class Movie {

	long id;
	String title;
	long released_year;
	String certificate;
	String runt;
	String genre;
	double rating;
	String overview;
	long score;
	String director;
	String star1;
	String star2;
	String star3;
	String star4;
	long votes;
	
	String posterUrl;
	
	String username;
    double userScore;
    
    List<MovieComment> Comments = new ArrayList<MovieComment>();
    
	public List<MovieComment> getComments() {
		return Comments;
	}
	public void setComments(List<MovieComment> comments) {
		Comments = comments;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	public double getUserScore() {
		return userScore;
	}
	public void setUserScore(double userScore) {
		this.userScore = userScore;
	}
	
	
	public String getPosterUrl() {
		return posterUrl;
	}
	public void setPosterUrl(String posterUrl) {
		this.posterUrl = posterUrl;
	}
	public long getReleased_year() {
		return released_year;
	}
	public void setReleased_year(long released_year) {
		this.released_year = released_year;
	}
	public String getCertificate() {
		return certificate;
	}
	public void setCertificate(String certificate) {
		this.certificate = certificate;
	}
	public String getRunt() {
		return runt;
	}
	public void setRunt(String runt) {
		this.runt = runt;
	}
	public String getGenre() {
		return genre;
	}
	public void setGenre(String genre) {
		this.genre = genre;
	}
	public double getRating() {
		return rating;
	}
	public void setRating(double rating) {
		this.rating = rating;
	}
	public String getOverview() {
		return overview;
	}
	public void setOverview(String overview) {
		this.overview = overview;
	}
	public long getScore() {
		return score;
	}
	public void setScore(long score) {
		this.score = score;
	}
	public String getDirector() {
		return director;
	}
	public void setDirector(String director) {
		this.director = director;
	}
	public String getStar1() {
		return star1;
	}
	public void setStar1(String star1) {
		this.star1 = star1;
	}
	public String getStar2() {
		return star2;
	}
	public void setStar2(String star2) {
		this.star2 = star2;
	}
	public String getStar3() {
		return star3;
	}
	public void setStar3(String star3) {
		this.star3 = star3;
	}
	public String getStar4() {
		return star4;
	}
	public void setStar4(String star4) {
		this.star4 = star4;
	}
	public long getVotes() {
		return votes;
	}
	public void setVotes(long votes) {
		this.votes = votes;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
}
