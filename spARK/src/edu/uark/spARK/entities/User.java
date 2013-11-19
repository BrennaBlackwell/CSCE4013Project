package edu.uark.spARK.entities;


@SuppressWarnings("serial")
public class User extends Entity {

	private int points;
	
	public User(int id, String name, String rank) {
		super(id, name, rank);
		this.points = 0;
	}
	
	public User(int id, String name, String rank, int points) {
		this(id, name, rank);
		this.points = points;
	}
	
	public String getName() {
		return getTitle();
	}
	
	public String getRank() {
		return getText();
	}
	
	public int getPoints() {
		return points;
	}
	
	public void setName(String name) {
		setTitle(name);
	}
	
	public void setRank(String rank) {
		setText(rank);
	}
	
	public void setPoints(int points) {
		this.points = points;
	}
	
}
