package edu.uark.spARK.entity;

import java.util.Date;


@SuppressWarnings("serial")
public class Event extends Content {
	private Date startDate;
	private Date endDate;
	
	public Event(int id, String title, String text) {
		super(id, title, text);
	}
	
	public Event(int id, String title, String text, User creator) {
		super(id, title, text, creator);
	}
	
	public Event(int id, String title, String text, User creator, String latitude, String longitude) {
		super(id, title, text, creator, latitude, longitude);
	}
	
	public Event(int id, String title, String text, User creator, Date creationDate) {
		super(id, title, text, creator, creationDate);
	}
	
	public Event(int id, String title, String text, User creator, Date creationDate, String latitude, String longitude) {
		super(id, title, text, creator, creationDate, latitude, longitude);
	}

	public Event(int id, String title, String text, User creator, Date creationDate, String latitude, String longitude, Group groupAttached) {
		super(id, title, text, creator, creationDate, latitude, longitude, groupAttached);
	}
	
	public Event(int id, String title, String text, User creator, Date creationDate, String latitude, String longitude, Group groupAttached, boolean favorited) {
		super(id, title, text, creator, creationDate, latitude, longitude, groupAttached, favorited);
	}
}
