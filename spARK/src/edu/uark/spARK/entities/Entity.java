package edu.uark.spARK.entities;

import java.io.Serializable;


@SuppressWarnings("serial")
public class Entity implements Serializable {
	
	private final int id;
	private String title;
	private String text;
	
	public Entity(int id, String title, String text) {
		this.id = id;
		this.title = title;
		this.text = text;
	}
	
	public int getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}
	
	public String getText() {
		return text;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
}
