---
layout: default
title : Servicios web
---

Ya hemos visto como crearnos nuestro propio servicio web, con el API que hemos puesto pública para utilizar desde nuestros javascript y también para que terceras personas puedan interactuar con nuestra web. En esta sección vamos a ver como consumir servicios webs.

Vamos a utilizar el API de twitter, para hacer consultas y rellenar nuestra base de datos en función de algún termino. Después utilizaremos el buscador que ya tenemos implementado para buscar entre esos tweets.


app/controllers/Timeline.java

{% highlight java linenos %}
    
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
{% endhighlight %}

app/views/search.html

{% highlight html linenos %}
<h3>Buscar en Twitter <small>(y almacenarlos en la base de datos)</small></h3>
#{form @Timeline.searchInTwitter()}
	<input type="text" name="query" value="${query}" class="xxlarge" placeholder="Busqueda""/>
	<input type="submit" class="btn primary" value="Buscar"/>
#{/form}
{% endhighlight %}