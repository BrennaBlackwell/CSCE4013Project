package edu.uark.spARK.entity;

import java.util.Date;


@SuppressWarnings("serial")
public class Event extends Content {
	private String location;
	private String startDate;
	private String startTime;
	private String endDate;
	private String endTime;
	
	public Event(int id, String title, String text) {
		super(id, title, text);
	}
	
	public Event(int id, String title, String text, User creator) {
		super(id, title, text, creator);
	}
	
	public Event(int id, String title, String text, User creator, String location, String latitude, String longitude, String startDate, String startTime, String endDate, String endTime) {
		super(id, title, text, creator, latitude, longitude);
		this.startDate = startDate;
		this.startTime = startTime;
		this.endDate = endDate;
		this.endTime = endTime;
		this.location = location;
	}
	
	public Event(int id, String title, String text, User creator, Date creationDate, String startDate, String startTime, String endDate, String endTime) {
		super(id, title, text, creator, creationDate);
		this.startDate = startDate;
		this.startTime = startTime;
		this.endDate = endDate;
		this.endTime = endTime;
	}
	
	public Event(int id, String title, String text, User creator, Date creationDate, String location, String latitude, String longitude, String startDate, String startTime, String endDate, String endTime) {
		super(id, title, text, creator, creationDate, latitude, longitude);
		this.startDate = startDate;
		this.startTime = startTime;
		this.endDate = endDate;
		this.endTime = endTime;
		this.location = location;
	}

	public Event(int id, String title, String text, User creator, Date creationDate, String location, String latitude, String longitude, Group groupAttached, String startDate, String startTime, String endDate, String endTime) {
		super(id, title, text, creator, creationDate, latitude, longitude, groupAttached);
		this.startDate = startDate;
		this.startTime = startTime;
		this.endDate = endDate;
		this.endTime = endTime;
		this.location = location;
	}
	
	public Event(int id, String title, String text, User creator, Date creationDate, String location, String latitude, String longitude, Group groupAttached, boolean favorited, String startDate, String startTime, String endDate, String endTime) {
		super(id, title, text, creator, creationDate, latitude, longitude, groupAttached, favorited);
		this.startDate = startDate;
		this.startTime = startTime;
		this.endDate = endDate;
		this.endTime = endTime;
		this.location = location;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
}
