package models;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import play.db.jpa.Model;

@Entity
public class Tweet extends Model {

	public String msg;

	@ManyToOne
	public User author;

	public Date date;

	public Tweet(String msg, User author) {
		this.msg = msg;
		this.author = author;
		date = new Date();
	}

}