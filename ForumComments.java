package kuzak.kuba.filmweb.model;

public class ForumComments {

	long thread_id;
	long comment_id;
	String text;
	String username;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public long getThread_id() {
		return thread_id;
	}
	public void setThread_id(long thread_id) {
		this.thread_id = thread_id;
	}
	public long getComment_id() {
		return comment_id;
	}
	public void setComment_id(long comment_id) {
		this.comment_id = comment_id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	
	
}
