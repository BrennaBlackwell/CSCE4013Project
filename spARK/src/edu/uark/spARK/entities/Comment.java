package edu.uark.spARK.entities;

import java.util.Date;

import android.location.Location;

@SuppressWarnings("serial")
public class Comment extends Content {
	
	public Comment(int id, String text) {
		super(id, null, text);
		
	}
	
	public Comment(int id, String text, User creator) {
		super(id, null, text, creator);
	}
	
	public Comment(int id, String text, User creator, Location location) {
		super(id, null, text, creator, location);
	}
	
	public Comment(int id, String text, User creator, Date creationDate) {
		super(id, null, text, creator, creationDate);
	}
	
	public Comment(int id, String text, User creator, Date creationDate, Location location) {
		super(id, null, text, creator, creationDate, location);
	}
	
}
