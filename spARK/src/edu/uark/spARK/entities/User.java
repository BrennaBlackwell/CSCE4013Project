package edu.uark.spARK.entities;


@SuppressWarnings("serial")
public class User extends Entity {

	private int points;
	
	public User(int id, String title, String text) {
		super(id, title, text);
		this.points = 0;
	}
	
	public User(int id, String title, String text, int points) {
		this(id, title, text);
		this.points = points;
	}
	
	public int getPoints() {
		return points;
	}
	
	public void setPoints(int points) {
		this.points = points;
	}
}
