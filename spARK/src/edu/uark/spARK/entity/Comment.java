package edu.uark.spARK.entity;

import java.util.Date;

@SuppressWarnings("serial")
public class Comment extends Content {
	
	public Comment(int id, String text) {
		super(id, null, text);
	}
	
	public Comment(int id, String text, User creator) {
		super(id, null, text, creator);
	}
	
	public Comment(int id, String text, User creator, String latitude, String longitude) {
		super(id, null, text, creator, latitude, longitude);
	}
	
	public Comment(int id, String text, User creator, Date creationDate) {
		super(id, null, text, creator, creationDate);
	}
	
	public Comment(int id, String text, User creator, Date creationDate, String latitude, String longitude) {
		super(id, null, text, creator, creationDate, latitude, longitude);
	}
	
}
