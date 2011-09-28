import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityTransaction;

import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;
import models.Tweet;
import models.User;
import play.db.jpa.GenericModel.JPAQuery;
import play.db.jpa.JPA;
import play.db.jpa.JPABase;
import play.test.Fixtures;
import play.test.UnitTest;


public class ModelTest extends UnitTest {
	
	@Before
	public void setup(){
		Fixtures.deleteDatabase();
	}
	
	@Test
	public void test1(){
		User u1 = new User("user1", "password1");
		u1.save();
		User u2 = new User("user2", "password2");
		u2.save();
		User u3 = new User("user3", "password3");
		u3.save();
		
		long count = User.count();
		Assert.assertEquals(3, count);
		
		User u1Found = User.findById(u1.id);
		Assert.assertNotNull(u1Found);
		Assert.assertEquals("user1", u1Found.username);
		Assert.assertEquals("password1", u1Found.password);
	}
	
	@Test
	public void test2(){
		User u1 = new User("user1", "password1");
		u1.save();
		
		Tweet t1 = Tweet.create("msg1", u1);
		t1.save();
		
		Tweet t2 = Tweet.create("msg2", u1);
		t2.save();
		
		Tweet t3 = Tweet.create("msg3", u1);
		t3.save();
		
		long count = Tweet.count();
		Assert.assertEquals(3, count);
		
		Tweet t1Found = Tweet.findById(t1.id);
		Assert.assertNotNull(t1Found);
		Assert.assertEquals("msg1", t1Found.msg);
		Assert.assertEquals("user1", t1Found.author.username);
	}
	
	@Test
	public void test3(){
		User u1 = new User("user1", "password1");
		u1.save();
		
		User u2 = new User("user2", "password2");
		u2.save();
		
		Tweet t1 = Tweet.create("msg1", u1);
		t1.save();
		
		Tweet t2 = Tweet.create("msg2", u1);
		t2.save();
		
		Tweet t3 = Tweet.create("msg3", u2);
		t3.save();
		
		long count = Tweet.count();
		Assert.assertEquals(3, count);
		
		List<Tweet> founds = Tweet.find("byAuthor", u1).fetch();
		Assert.assertNotNull(founds);
		Assert.assertEquals(2, founds.size());
		Assert.assertTrue(founds.contains(t1));
		Assert.assertTrue(founds.contains(t2));
		Assert.assertFalse(founds.contains(t3));
	}
	
	@Test
	public void test4() throws ParseException{
	    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

		Tweet t1 = Tweet.create("tweet1", (User)null);
		t1.date = formatter.parse("01/01/2000");
		t1.save();
		Tweet t2 = Tweet.create("tweet2", (User)null);
		t2.date = formatter.parse("01/02/2000");
		t2.save();
		Tweet t3 = Tweet.create("tweet3",(User)null);
		t3.date = formatter.parse("02/01/2010");
		t3.save();
		
		List<Tweet> founds = Tweet.find("byDateGreaterThan", formatter.parse("01/01/2010")).fetch();
		Assert.assertNotNull(founds);
		Assert.assertEquals(1, founds.size());
		Assert.assertTrue(founds.contains(t3));
		Assert.assertFalse(founds.contains(t1));
		Assert.assertFalse(founds.contains(t2));
	}
	
	@Test
	public void test5(){
		User u1 = new User("usur1", "password1");
		u1.save();
		
		User u2 = new User("user2", "password2");
		u2.save();
		
		User u3 = new User("user3", "password3");
		u3.save();
		
		User u4 = new User("user4", "password4");
		u4.follows.add(u1);
		u4.follows.add(u2);
		u4.follows.add(u3);
		u4.save();
		
		User uFound = User.findById(u4.id);
		Assert.assertNotNull(uFound);
		Assert.assertEquals(3, uFound.follows.size());
		Assert.assertTrue(uFound.follows.contains(u1));
		Assert.assertTrue(uFound.follows.contains(u2));
		Assert.assertTrue(uFound.follows.contains(u3));
	}
	
	@Test
	public void test6(){
		User u1 = new User("usur1", "password1");
		u1.save();
		
		User u2 = new User("user2", "password2");
		u2.follows.add(u1);
		u2.save();
		
		User u3 = new User("user3", "password3");
		u3.follows.add(u1);
		u3.save();
		
		User u4 = new User("user4", "password4");
		u4.follows.add(u1);
		u4.save();
		
		List<User> founds = User.find("select user from User user where ? member of user.follows",u1).fetch();
		Assert.assertNotNull(founds);
		Assert.assertEquals(3, founds.size());
		Assert.assertTrue(founds.contains(u2));
		Assert.assertTrue(founds.contains(u3));
		Assert.assertTrue(founds.contains(u4));
	}
	
	@Test
	public void test7(){
		Fixtures.loadModels("initial-data.yml");
		long usersCount = User.count();
		Assert.assertEquals(4, usersCount);
		long tweetsCount = Tweet.count();
		Assert.assertEquals(9, tweetsCount);
	}
	
	@Test
	public void test8(){
		EntityTransaction tx = JPA.em().getTransaction();
		
		User u1 = new User("user1", null);
		u1.save();
		tx.commit();
		
		tx.begin();
		User userFound = User.findById(u1.id);
		Assert.assertNotNull(userFound);
		Assert.assertEquals("user1", userFound.username);
		
		userFound.username = "not user1";
		userFound.save();
		tx.rollback();
		
		tx.begin();
		User userFound2 = User.findById(u1.id);
		Assert.assertNotNull(userFound2);
		Assert.assertEquals("user1", userFound2.username);		
	}
}
