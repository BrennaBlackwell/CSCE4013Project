package edu.uark.spARK.entities;

import java.util.Date;

import android.location.Location;


@SuppressWarnings("serial")
public class Bulletin extends Content {

	public Bulletin(int id, String title, String text) {
		super(id, title, text);
	}
	
	public Bulletin(int id, String title, String text, User creator) {
		super(id, title, text, creator);
	}
	
	public Bulletin(int id, String title, String text, User creator, Location location) {
		super(id, title, text, creator, location);
	}
	
	public Bulletin(int id, String title, String text, User creator, Date creationDate) {
		super(id, title, text, creator, creationDate);
	}
	
	public Bulletin(int id, String title, String text, User creator, Date creationDate, Location location) {
		super(id, title, text, creator, creationDate, location);
	}

}
