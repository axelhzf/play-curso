package controllers;

import java.util.List;
import java.util.Map;

import play.db.jpa.JPA;
import play.mvc.Controller;
import models.*;

public class Timeline extends Controller {

    public static void index(){
        List<Tweet> tweets = Tweet.find("order by date desc").fetch();   
        render(tweets);
    }

    
    public static void stats(){
        List<Object[]> user_tweets = JPA.em().createQuery("select t.author.username, count(t) from Tweet t group by t.author").getResultList();
        render(user_tweets);
    }
}