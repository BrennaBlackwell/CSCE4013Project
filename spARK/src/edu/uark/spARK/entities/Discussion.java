package edu.uark.spARK.entities;

import java.util.*;

import com.google.android.gms.maps.model.LatLng;


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
	
	public Discussion(int id, String title, String description, User creator, String latitude, String longitude) {
		super(id, title, description, creator, latitude, longitude);
		this.comments = new ArrayList<Comment>();
	}
	
	public Discussion(int id, String title, String description, User creator, String latitude, String longitude, List<Comment> comments) {
		super(id, title, description, creator, latitude, longitude);
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
	
	public Discussion(int id, String title, String description, User creator, Date creationDate, String latitude, String longitude) {
		super(id, title, description, creator, creationDate, latitude, longitude);
		this.comments = new ArrayList<Comment>();
	}
	
	public Discussion(int id, String title, String description, User creator, Date creationDate, String latitude, String longitude, List<Comment> comments) {
		super(id, title, description, creator, creationDate, latitude, longitude);
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
