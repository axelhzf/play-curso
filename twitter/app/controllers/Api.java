package controllers;

import java.util.List;

import flexjson.JSONSerializer;

import models.Tweet;
import models.User;
import play.mvc.Controller;
import play.utils.Utils;
import responses.ApiResponse;

public class Api extends Controller {
	
	public static void tweetsNew(String msg){
		Tweet t = new Tweet(msg, Security.userConnected());
		t.validateAndSave();
		ApiResponse resp = new ApiResponse();
		if(validation.hasErrors()){
			resp.status = "ERROR";
			resp.message = Utils.join(validation.errors(), ",");
		}else{
			resp.status = "OK";
			resp.result = t;
		}
		JSONSerializer serializer = new JSONSerializer().include("status", "message", "result.msg", "result.date", "result.author.username").exclude("*");
		renderJSON(serializer.serialize(resp));
	}
	
	public static void tweetsAll(){
		List<Tweet> tweets = Tweet.find("order by date desc").fetch();
		renderJSON(new JSONSerializer().include("msg", "date", "author.username").exclude("*").serialize(tweets));
	}
	
	public static void tweetsFromUser(String username){		
		List<Tweet> tweets = Tweet.find("select tweet from Tweet tweet where tweet.author.username = ? order by tweet.date desc", username).fetch();
		renderJSON(new JSONSerializer().include("msg", "date", "author.username").exclude("*").serialize(tweets));
	}
	
}
