package edu.uark.spARK.entities;

import java.util.Date;

import android.location.Location;
import android.text.format.DateFormat;

@SuppressWarnings("serial")
public class Content extends Entity {
	
	private final User creator;
	private final Date creationDate;
	private Location location;
	
	public Content(int id, String title, String text) {
		super(id, title, text);
		this.creator = null;
		this.creationDate = new Date(System.currentTimeMillis());
		this.location = null;
	}
	
	public Content(int id, String title, String text, User creator) {
		super(id, title, text);
		this.creator = creator;
		this.creationDate = new Date(System.currentTimeMillis());
		this.location = null;
	}
	
	public Content(int id, String title, String text, User creator, Location location) {
		this(id, title, text, creator);
		this.location = location;
	}
	
	public Content(int id, String title, String text, User creator, Date creationDate) {
		super(id, title, text);
		this.creator = creator;
		this.creationDate = creationDate;
		this.location = null;
	}
	
	public Content(int id, String title, String text, User creator, Date creationDate, Location location) {
		this(id, title, text, creator, creationDate);
		this.location = location;
	}
	
	public User getCreator() {
		return creator;
	}
	
	public Date getCreationDate() {
		return creationDate;
	}
	
	public String getCreationDateString() {
		return (String) DateFormat.format("MM/dd/yy hh:mm aa", creationDate.getTime());
	}
	
	public Location getLocation() {
		return location;
	}
	
	public void setLocaton(Location location) {
		this.location = location;
	}
	
	public boolean hasCreator() {
		return (creator != null);
	}
	
	public boolean hasLocation() {
		return (location != null);
	}
}
