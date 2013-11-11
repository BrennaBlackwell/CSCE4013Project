package edu.uark.spARK.entities;

import java.util.Date;

/**
 * Class created for bulletins. We will probably want to load items from the server into the bulletin object.
 * 
 */

public class Bulletin extends Content {
	
	public Bulletin(User user, String title, String content, String group) {
		super(user, title, content, group);
	}

	public Bulletin(User user, String title, String content, String group,
			Date date) {
		super(user, title, content, group, date);
	}
}
