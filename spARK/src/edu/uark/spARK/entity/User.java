package edu.uark.spARK.entity;

@SuppressWarnings("serial")
public class User extends Entity {

	private int points;
	private String fullname = "";
	private String desc = "";
	
	public User(int id, String name, String rank) {
		super(id, name, rank);
		this.points = 0;
	}
	
	public User(int id, String name, String rank, int points) {
		this(id, name, rank);
		this.points = points;
	}
	
	public User(int id, String name, String rank, String fullname, String desc, int points) {
		this(id, name, rank);
		this.setFullname(fullname);
		this.setDesc(desc);
	}
	
	public String getName() {
		return getTitle();
	}
	
	public String getRank() {
		return getText();
	}
	
	public void setName(String name) {
		setTitle(name);
	}
	
	public void setRank(String rank) {
		setText(rank);
	}
	
	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public int getPoints() {
		return points;
	}
	
	public void setPoints(int points) {
		this.points = points;
	}
	
}
