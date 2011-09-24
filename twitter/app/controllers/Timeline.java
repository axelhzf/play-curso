package controllers;

import java.util.List;
import java.util.Map;

import org.elasticsearch.index.query.xcontent.QueryBuilders;

import play.Logger;
import play.db.jpa.JPA;
import play.modules.elasticsearch.ElasticSearch;
import play.modules.elasticsearch.search.SearchResults;
import play.mvc.Controller;
import play.mvc.With;
import models.*;

@With(Secure.class)
public class Timeline extends Controller {

    public static void index(){   
        render();
    }

    @Check("isAdmin")
    public static void stats(){
        List<Object[]> user_tweets = JPA.em().createQuery("select t.author.username, count(t) from Tweet t group by t.author").getResultList();
        render(user_tweets);
    }
    
    public static void create(String tweet){
    	checkAuthenticity();
    	Tweet t = Tweet.create(tweet, Security.userConnected());
    	t.validateAndSave();
    	
    	if(validation.hasErrors()){
    		validation.keep();
    	}
    	
    	index();
    }
    
    public static void search(String query){
    	if(query != null && !query.isEmpty()){
    		SearchResults<Tweet> tweets = ElasticSearch.search(QueryBuilders.fieldQuery("msg", query), Tweet.class);
    		render(tweets, query);
    	}
    	render();
    }
    
}