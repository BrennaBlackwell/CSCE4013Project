package edu.uark.spARK.entities;

import java.util.*;

import android.location.Location;

@SuppressWarnings("serial")
public class Group extends Content {
	
	private List<User> users;
	
	public Group(int id, String title, String text) {
		super(id, title, text);
		users = new ArrayList<User>();
	}
	
	public Group(int id, String title, String text, User creator) {
		super(id, title, text, creator);
		users = new ArrayList<User>();
		users.add(creator);
	}
	
	public Group(int id, String title, String text, User creator, Location location) {
		super(id, title, text, creator, location);
		users = new ArrayList<User>();
		users.add(creator);
	}
	
	public Group(int id, String title, String text, User creator, Location location, List<User> users) {
		super(id, title, text, creator, location);
		this.users = users;
	}
	
	public Group(int id, String title, String text, User creator, Date creationDate) {
		super(id, title, text, creator, creationDate);
		this.users = new ArrayList<User>();
		users.add(creator);
	}
	
	public Group(int id, String title, String text, User creator, Date creationDate, List<User> users) {
		super(id, title, text, creator, creationDate);
		this.users = users;
	}
	
	public Group(int id, String title, String text, User creator, Date creationDate, Location location) {
		super(id, title, text, creator, creationDate, location);
		this.users = new ArrayList<User>();
		users.add(creator);
	}
	
	public Group(int id, String title, String text, User creator, Date creationDate, Location location, List<User> users) {
		super(id, title, text, creator, creationDate, location);
		this.users = users;
	}
	
	public List<User> getUsers() {
		return users;
	}
	
	public void setUsers(List<User> users) {
		this.users = users;
	}
	
	public boolean addUser(User user) {
		return users.add(user);
	}
	
	public boolean removeUser(User user) {
		return users.remove(user);
	}
	
}
