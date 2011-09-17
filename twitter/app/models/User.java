package models;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import play.db.jpa.Model;

@Entity
public class User extends Model {

	public String username;

	public String password;

	public boolean admin;
	
	@ManyToMany
	public List<User> follows;

	public User(String username, String password) {
		super();
		this.username = username;
		this.password = password;
		this.admin = false;
		follows = new ArrayList<User>();
	}
}
