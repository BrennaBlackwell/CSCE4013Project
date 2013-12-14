package edu.uark.spARK.entity;

import java.util.Date;


@SuppressWarnings("serial")
public class Bulletin extends Content {

	public Bulletin(int id, String title, String text) {
		super(id, title, text);
	}
	
	public Bulletin(int id, String title, String text, User creator) {
		super(id, title, text, creator);
	}
	
	public Bulletin(int id, String title, String text, User creator, String latitude, String longitude) {
		super(id, title, text, creator, latitude, longitude);
	}
	
	public Bulletin(int id, String title, String text, User creator, Date creationDate) {
		super(id, title, text, creator, creationDate);
	}
	
	public Bulletin(int id, String title, String text, User creator, Date creationDate, String latitude, String longitude) {
		super(id, title, text, creator, creationDate, latitude, longitude);
	}

	public Bulletin(int id, String title, String text, User creator, Date creationDate, String latitude, String longitude, Group groupAttached) {
		super(id, title, text, creator, creationDate, latitude, longitude, groupAttached);
	}
	
	public Bulletin(int id, String title, String text, User creator, Date creationDate, String latitude, String longitude, Group groupAttached, boolean favorited) {
		super(id, title, text, creator, creationDate, latitude, longitude, groupAttached, favorited);
	}
}
