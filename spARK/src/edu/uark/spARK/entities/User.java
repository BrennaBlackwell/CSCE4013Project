package edu.uark.spARK.entities;

import java.io.Serializable;

public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name = "empty";
	
	public User(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
