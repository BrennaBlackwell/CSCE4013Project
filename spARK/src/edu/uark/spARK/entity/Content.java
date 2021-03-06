package edu.uark.spARK.entity;

import java.io.Serializable;
import java.util.Date;

import org.ocpsoft.prettytime.PrettyTime;

@SuppressWarnings("serial")
public class Content extends Entity {
	
	private final User creator;
	private final Date creationDate;
	private final MyPrettyTime p = new MyPrettyTime();
	private String latitude;
	private String longitude;
	private int totalRating;
	private int userRating;
	private Group groupAttached;
	private boolean favorited;
	
	public Content(int id, String title, String text) {
		super(id, title, text);
		this.creator = null;
		this.creationDate = new Date(System.currentTimeMillis());
		this.latitude = null;
		this.longitude = null;
	}
	
	public Content(int id, String title, String text, User creator) {
		super(id, title, text);
		this.creator = creator;
		this.creationDate = new Date(System.currentTimeMillis());
		this.latitude = null;
		this.longitude = null;
	}
	
	public Content(int id, String title, String text, User creator, String latitude, String longitude) {
		this(id, title, text, creator);
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public Content(int id, String title, String text, User creator, Date creationDate) {
		super(id, title, text);
		this.creator = creator;
		this.creationDate = creationDate;
		this.latitude = null;
		this.longitude = null;
	}
	
	public Content(int id, String title, String text, User creator, Date creationDate, String latitude, String longitude) {
		this(id, title, text, creator, creationDate);
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public Content(int id, String title, String text, User creator, Date creationDate, String latitude, String longitude, Group gpt) {
		this(id, title, text, creator, creationDate);
		this.latitude = latitude;
		this.longitude = longitude;
		this.groupAttached = gpt;
	}
	
	public Content(int id, String title, String text, User creator, Date creationDate, String latitude, String longitude, Group gpt, boolean favorite) {
		this(id, title, text, creator, creationDate);
		this.latitude = latitude;
		this.longitude = longitude;
		this.groupAttached = gpt;
		this.favorited = favorite;
	}
	
	public User getCreator() {
		return creator;
	}
	
	public Group getGroupAttached() {
		return groupAttached;
	}

	public void setGroupAttached(Group groupAttached) {
		this.groupAttached = groupAttached;
	}

	public Date getCreationDate() {
		return creationDate;
	}
	
	public String getCreationDateAsPrettyTime() {
		return p.format(creationDate).toString();
	}

	public String getLatitude() {
		return latitude;
	}
	
	public String getLongitude() {
		return longitude;
	}
	
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	
	public boolean hasCreator() {
		return (creator != null);
	}
	
	public boolean hasLocation() {
		try {
			Double.parseDouble(latitude);
			Double.parseDouble(longitude);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public int getTotalRating() {
		return totalRating;
	}
	
	public int getUserRating() {
		return userRating;
	}
	
	public void setTotalRating(int rating) {
		this.totalRating = rating;
	}
	
	public void setUserRating(int rating) {
		this.userRating = rating;
	}
	
	public boolean isFavorited() {
		return favorited;
	}

	public void setFavorited(boolean favorited) {
		this.favorited = favorited;
	}

	public void incrementRating() {
		this.totalRating++;
		this.userRating++;
	}
	
	public void decrementRating() {
		this.totalRating--;
		this.userRating--;
	}
	
	public void addToRating(int amount) {
		this.totalRating += amount;
	}
	
	public void subtractFromRating(int amount) {
		this.totalRating -= amount;
	}
	
	private class MyPrettyTime extends PrettyTime implements Serializable {
		
	}

}
