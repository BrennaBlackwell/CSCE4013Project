package edu.uark.spARK.entities;

import java.util.Date;

import android.text.format.DateFormat;


/* BULLETINS/DISCUSSIONS EXTEND THIS BASE CLASS 
 * This is the easiest way to separate them by using the class name
 * */

public class Content {
	
	int intScore = 0;
	String title;
	String content;
	String group;
	Date creationDate;
	User author;
	
	public Content(User user, String title, String content, String group) {
		this.author = user;
		this.title = title;
		this.group = group;
		this.content = content;
		this.creationDate = new Date(System.currentTimeMillis());
	}

	public Content(User user, String title, String content, String group,
			Date date) {
		this.author = user;
		this.title = title;
		this.group = group;
		this.content = content;
		this.creationDate = date;
	}

	public String getTitle() {
		return title;
	}
	
	public String getDescription() {
		return content;
	}
	
	public String getGroup() {
		return group;
	}
	
	public User getAuthor() {
		return author;
	}
	
	public String getDate() {
		return (String) DateFormat.format("MM/dd/yy hh:mm aa", creationDate.getTime());
	}
	
	public void changeScore(int amt) {
		intScore += amt;
	}
	
	public void increaseScore() {
		intScore++;
	}
	
	public void decreaseScore() {
		intScore--;
	}
	
	public int getScore() {
		return intScore;
	}
}
