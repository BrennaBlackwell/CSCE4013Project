package edu.uark.spARK.entity;

import java.util.*;

@SuppressWarnings("serial")
public class Group extends Content {
	
	private List<User> users;
	private final boolean open;
	private final boolean visible;
	
	public Group(int id, String title, String description) {
		super(id, title, description);
		this.users = new ArrayList<User>();
		this.open = true;
		this.visible = true;
	}
	
	public Group(int id, String title, String description, boolean open, boolean visible) {
		super(id, title, description);
		this.users = new ArrayList<User>();
		this.open = open;
		this.visible = visible;
	}
	
	public Group(int id, String title, String description, User creator) {
		super(id, title, description, creator);
		this.users = new ArrayList<User>();
		users.add(creator);
		this.open = true;
		this.visible = true;
	}
	
	public Group(int id, String title, String description, User creator, boolean open, boolean visible) {
		super(id, title, description, creator);
		this.users = new ArrayList<User>();
		users.add(creator);
		this.open = open;
		this.visible = visible;
	}
	
	public Group(int id, String title, String description, User creator, String latitude, String longitude) {
		super(id, title, description, creator, latitude, longitude);
		this.users = new ArrayList<User>();
		users.add(creator);
		this.open = true;
		this.visible = true;
	}
	
	public Group(int id, String title, String description, User creator, String latitude, String longitude, boolean open, boolean visible) {
		super(id, title, description, creator, latitude, longitude);
		this.users = new ArrayList<User>();
		users.add(creator);
		this.open = open;
		this.visible = visible;
	}
	
	public Group(int id, String title, String description, User creator, String latitude, String longitude, List<User> users) {
		super(id, title, description, creator, latitude, longitude);
		this.users = users;
		this.open = true;
		this.visible = true;
	}
	
	public Group(int id, String title, String description, User creator, String latitude, String longitude, List<User> users, boolean open, boolean visible) {
		super(id, title, description, creator, latitude, longitude);
		this.users = users;
		this.open = open;
		this.visible = visible;
	}
	
	public Group(int id, String title, String description, User creator, Date creationDate) {
		super(id, title, description, creator, creationDate);
		this.users = new ArrayList<User>();
		users.add(creator);
		this.open = true;
		this.visible = true;
	}
	
	public Group(int id, String title, String description, User creator, Date creationDate, boolean open, boolean visible) {
		super(id, title, description, creator, creationDate);
		this.users = new ArrayList<User>();
		users.add(creator);
		this.open = open;
		this.visible = visible;
	}
	
	public Group(int id, String title, String description, User creator, Date creationDate, List<User> users) {
		super(id, title, description, creator, creationDate);
		this.users = users;
		this.open = true;
		this.visible = true;
	}
	
	public Group(int id, String title, String description, User creator, Date creationDate, List<User> users, boolean open, boolean visible) {
		super(id, title, description, creator, creationDate);
		this.users = users;
		this.open = open;
		this.visible = visible;
	}
	
	public Group(int id, String title, String description, User creator, Date creationDate, String latitude, String longitude) {
		super(id, title, description, creator, creationDate, latitude, longitude);
		this.users = new ArrayList<User>();
		users.add(creator);
		this.open = true;
		this.visible = true;
	}
	
	public Group(int id, String title, String description, User creator, Date creationDate, String latitude, String longitude, boolean open, boolean visible) {
		super(id, title, description, creator, creationDate, latitude, longitude);
		this.users = new ArrayList<User>();
		users.add(creator);
		this.open = open;
		this.visible = visible;
	}
	
	public Group(int id, String title, String description, User creator, Date creationDate, String latitude, String longitude, List<User> users) {
		super(id, title, description, creator, creationDate, latitude, longitude);
		this.users = users;
		this.open = true;
		this.visible = true;
	}
	
	public Group(int id, String title, String description, User creator, Date creationDate, String latitude, String longitude, List<User> users, boolean open, boolean visible) {
		super(id, title, description, creator, creationDate, latitude, longitude);
		this.users = users;
		this.open = open;
		this.visible = visible;
	}
	
	public String getDescription() {
		return getText();
	}
	
	public List<User> getUsers() {
		return users;
	}
	
	public boolean isOpen() {
		return open;
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public void setDescription(String description) {
		setText(description);
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
