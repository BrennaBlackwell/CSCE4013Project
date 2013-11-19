package edu.uark.spARK.entities;

import java.util.*;

import android.location.Location;


@SuppressWarnings("serial")
public class Discussion extends Content {
	
	private List<Comment> comments;
	
	public Discussion(int id, String title, String description) {
		super(id, title, description);
		this.comments = new ArrayList<Comment>();
	}
	
	public Discussion(int id, String title, String description, User creator) {
		super(id, title, description, creator);
		this.comments = new ArrayList<Comment>();
	}
	
	public Discussion(int id, String title, String description, User creator, Location location) {
		super(id, title, description, creator, location);
		this.comments = new ArrayList<Comment>();
	}
	
	public Discussion(int id, String title, String description, User creator, Location location, List<Comment> comments) {
		super(id, title, description, creator, location);
		this.comments = comments;
	}
	
	public Discussion(int id, String title, String description, User creator, Date creationDate) {
		super(id, title, description, creator, creationDate);
		this.comments = new ArrayList<Comment>();
	}
	
	public Discussion(int id, String title, String description, User creator, Date creationDate, List<Comment> comments) {
		super(id, title, description, creator, creationDate);
		this.comments = comments;
	}
	
	public Discussion(int id, String title, String description, User creator, Date creationDate, Location location) {
		super(id, title, description, creator, creationDate, location);
		this.comments = new ArrayList<Comment>();
	}
	
	public Discussion(int id, String title, String description, User creator, Date creationDate, Location location, List<Comment> comments) {
		super(id, title, description, creator, creationDate, location);
		this.comments = comments;
	}
	
	public String getDescription() {
		return getText();
	}
	
	public List<Comment> getComments() {
		return comments;
	}
	
	public void setDescription(String description) {
		setText(description);
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
	
	public int getNumComments() {
		return comments.size();
	}
	
}
