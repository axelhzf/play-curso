package models;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;

import play.db.jpa.Model;

@Entity
public class User extends Model {

	public String username;

	public String password;

	public boolean admin;
	
	@Transient
	public Long followsNumber;
	
	@Transient
	public Long followersNumber;
	
	
	@ManyToMany
	public List<User> follows;

	public User(String username, String password) {
		super();
		this.username = username;
		this.password = password;
		this.admin = false;
		follows = new ArrayList<User>();
	}
	
	/**
	 * Cuenta el número de persona que sigue un usuario
	 * @return
	 */
	public Long getFollowsNumber(){
		if(followsNumber == null){
			followsNumber = User.count("select count(follow) from User user join user.follows follow where user = ?", this);
		}
		return followsNumber;		
	}
	
	/**
	 * Cuenta el número de persona que siguen a un usuario
	 * @return
	 */
	public Long getFollowersNumber(){
		if(followersNumber == null){
			followersNumber = User.count("select count(user) from User user where ? member of user.follows", this);
		}
		return followersNumber;
	}
	
}
