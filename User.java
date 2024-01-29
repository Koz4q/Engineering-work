package kuzak.kuba.filmweb.model;

import java.util.List;

public class User {

	String username;
	String password;
	String nick;
	int id;
	String description;
	List<String> genres;
	List<Long> watchedMovies;
	String image;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	
	public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}
	
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public List<String> getGeneres() {
		return genres;
	}
	public void setGeneres(List<String> generes) {
		this.genres = generes;
	}
	public List<Long> getWatchedMovies() {
		return watchedMovies;
	}
	public void setWatchedMovies(List<Long> watchedMovies) {
		this.watchedMovies = watchedMovies;
	}
}
