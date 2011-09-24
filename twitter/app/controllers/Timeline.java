package controllers;

import java.util.List;
import java.util.Map;

import org.elasticsearch.index.query.xcontent.QueryBuilders;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import play.Logger;
import play.db.jpa.JPA;
import play.libs.WS;
import play.modules.elasticsearch.ElasticSearch;
import play.modules.elasticsearch.search.SearchResults;
import play.mvc.Controller;
import play.mvc.With;
import responses.ApiResponse;
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
 
    
	public static void searchInTwitter(String query){
		if(query != null && !query.isEmpty()){
			JsonObject json = WS.url("http://search.twitter.com/search.json?q="+query).get().getJson().getAsJsonObject();
			JsonArray results = json.get("results").getAsJsonArray();
			
			for(JsonElement item : results){
				JsonObject itemObject = item.getAsJsonObject();
				String msg = itemObject.get("text").getAsString();
				String user = itemObject.get("from_user").getAsString();
				createTweetFromTwitter(user, msg);
			}
		}
		search(query);
	}
	
	private static void createTweetFromTwitter(String username, String msg){
		User user = User.find("byUsername", username).first();
		if(user == null){
			user = new User(username, username);
			user.save();
		}
		
		Tweet t = Tweet.create(msg, user);
		t.create();
	}
}