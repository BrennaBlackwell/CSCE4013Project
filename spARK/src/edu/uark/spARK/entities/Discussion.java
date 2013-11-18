package edu.uark.spARK.entities;

import java.util.*;

import android.location.Location;


@SuppressWarnings("serial")
public class Discussion extends Content {
	
	private List<Comment> comments;
	
	public Discussion(int id, String title, String text) {
		super(id, title, text);
		comments = new ArrayList<Comment>();
	}
	
	public Discussion(int id, String title, String text, User creator) {
		super(id, title, text, creator);
		comments = new ArrayList<Comment>();
	}
	
	public Discussion(int id, String title, String text, User creator, Location location) {
		super(id, title, text, creator, location);
		comments = new ArrayList<Comment>();
	}
	
	public Discussion(int id, String title, String text, User creator, Location location, List<Comment> comments) {
		super(id, title, text, creator, location);
		this.comments = comments;
	}
	
	public Discussion(int id, String title, String text, User creator, Date creationDate) {
		super(id, title, text, creator, creationDate);
		this.comments = new ArrayList<Comment>();
	}
	
	public Discussion(int id, String title, String text, User creator, Date creationDate, List<Comment> comments) {
		super(id, title, text, creator, creationDate);
		this.comments = comments;
	}
	
	public Discussion(int id, String title, String text, User creator, Date creationDate, Location location) {
		super(id, title, text, creator, creationDate, location);
		this.comments = new ArrayList<Comment>();
	}
	
	public Discussion(int id, String title, String text, User creator, Date creationDate, Location location, List<Comment> comments) {
		super(id, title, text, creator, creationDate, location);
		this.comments = comments;
	}
	
	public List<Comment> getComments() {
		return comments;
	}
	
	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}
	
	public boolean addComment(Comment comment) {
		return comments.add(comment);
	}
	
	public boolean removeComment(Comment comment) {
		return comments.remove(comment);
	}
	
}
