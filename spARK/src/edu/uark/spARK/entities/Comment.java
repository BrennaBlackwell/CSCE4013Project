package edu.uark.spARK.entities;

import java.io.Serializable;

public class Comment implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private User author;
	private String comment;
	
	public Comment(User user, String c) {
		this.author = user;
		this.comment = c;
	}
	
	public User getAuthor() {
		return author;
	}
	
	public String getComment() {
		return comment;
	}
}
