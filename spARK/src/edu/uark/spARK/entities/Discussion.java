package edu.uark.spARK.entities;

import java.util.Date;

/**
 * Class created for discussions. We will probably want to load items from the server into the discussion object.
 * 
 */

public class Discussion extends Content {

	public Discussion(User user, String title, String content, String group) {
		super(user, title, content, group);
	}
	
	public Discussion(User user, String title, String content, String group, Date date) {
		super(user, title, content, group, date);
	}
}
