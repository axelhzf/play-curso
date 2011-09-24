package controllers;

import java.util.List;

import org.elasticsearch.index.query.xcontent.QueryBuilders;

import flexjson.JSONSerializer;

import models.Tweet;
import models.User;
import play.modules.elasticsearch.ElasticSearch;
import play.modules.elasticsearch.search.SearchResults;
import play.mvc.Controller;
import play.utils.Utils;
import responses.ApiResponse;

public class Api extends Controller {
	
	private static final JSONSerializer tweetsSerializer = new JSONSerializer().include("status", "message", "result.msg", "result.date", "result.author.username").exclude("*");
	private static final JSONSerializer statusSerializer = new JSONSerializer().include("status", "message", "result").exclude("*");

	public static void tweetsNew(String msg){
		Tweet t = Tweet.create(msg, Security.userConnected());
		t.validateAndSave();
		ApiResponse resp = new ApiResponse();
		if(validation.hasErrors()){
			resp.status = "ERROR";
			resp.message = Utils.join(validation.errors(), ",");
		}else{
			resp.status = "OK";
			resp.result = t;
		}
		renderJSON(tweetsSerializer.serialize(resp));
	}
	
	public static void tweetsAll(){
		List<Tweet> tweets = Tweet.find("order by date desc").fetch();
		renderJSON(new JSONSerializer().include("msg", "date", "author.username").exclude("*").serialize(tweets));
	}
	
	public static void timeline(){
	    List<Object> tweets = Tweet.find("select tweet from Tweet tweet, User user where user = ? and (tweet.author = user or tweet.author member of user.follows) order by tweet.date desc", Security.userConnected()).fetch();
	    ApiResponse resp = new ApiResponse();
	    resp.status = "OK";
	    resp.result = tweets;
		renderJSON(tweetsSerializer.serialize(resp));
	}
	
	public static void tweetsFromUser(String username){		
		List<Tweet> tweets = Tweet.find("select tweet from Tweet tweet where tweet.author.username = ? order by tweet.date desc", username).fetch();
	    ApiResponse resp = new ApiResponse();
	    resp.status = "OK";
	    resp.result = tweets;
		renderJSON(tweetsSerializer.serialize(resp));
	}
	
	public static void userInfo(String username){
		User user = User.find("byUsername", username).first();
		ApiResponse resp = new ApiResponse();
		if(user!= null){
			resp.status = "OK";
			resp.result = user;
		}else{
			resp.status = "ERROR";
			resp.message = "User not found";
		}
		renderJSON(new JSONSerializer().include("status", "message", "result.username", "result.followsNumber", "result.followersNumber").exclude("*").serialize(resp));
	}
	
	public static void follow(String user){
	    User usuarioConectado = Security.userConnected();
	    User usuarioConsultado = User.find("byUsername", user).first();
	    
	    usuarioConectado.follows.add(usuarioConsultado);
	    usuarioConectado.save();
	    
	    ApiResponse resp = new ApiResponse();
	    resp.status = "OK";
	    renderJSON(statusSerializer.serialize(resp));
	}
	
	public static void unfollow(String user){
	    User usuarioConectado = Security.userConnected();
	    User usuarioConsultado = User.find("byUsername", user).first();
	    
	    usuarioConectado.follows.remove(usuarioConsultado);
	    usuarioConectado.save();
	    
	    ApiResponse resp = new ApiResponse();
	    resp.status = "OK";
	    renderJSON(statusSerializer.serialize(resp));
	}
	
	public static void isFollowing(String user){
		User usuarioConectado = Security.userConnected();
	    User usuarioConsultado = User.find("byUsername", user).first();
	    
	    ApiResponse resp = new ApiResponse();
	    resp.status = "OK";
	    resp.result = usuarioConectado.follows.contains(usuarioConsultado);
	    renderJSON(statusSerializer.serialize(resp));
	}
	
	public static void search(String query){
		ApiResponse resp = new ApiResponse();		
    	if(query != null && !query.isEmpty()){
    		resp.status = "OK";
    		SearchResults<Tweet> tweets = ElasticSearch.search(QueryBuilders.fieldQuery("msg", query), Tweet.class);
    		resp.result = tweets.objects;
    	}else{
    		resp.status = "ERROR";
    		resp.message = "Empty query";
    	}
    	
    	renderJSON(new JSONSerializer().include("status", "message", "result.msg", "result.id").exclude("*").serialize(resp));
	}
	
}
