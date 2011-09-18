---
layout: default
title: Ajax
---

Vamos a mejorar nuestro Timeline añadiendo [AJAX](http://en.wikipedia.org/wiki/AJAX) para evitar la recarga completa de la página cuando se envía un nuevo tweet. El objetivo es enviar un tweet mediante AJAX y si todo va bien, consultar de nuevo el timeline y actualizarlo.

Vamos a crear una API para nuestra aplicación, que nos permita desde javascript hacer las consultas adecuadas. Como formato para las respuesta vamos a utilizar [JSON](http://en.wikipedia.org/wiki/JSON), por su facilidad para trabajar con él desde javascript.

## Creando el API

Vamos a crear un controlador nuevo que se va tener las acciones disponibles en nuestra API.

Empezamos con un método que nos permite ver todos los tweets que ha escrito un usuario

app/controllers/Api.java

{% highlight java linenos %}
package controllers;

import java.util.List;

import models.Tweet;
import models.User;
import play.mvc.Controller;

public class Api extends Controller {

	public static void tweetsAll(){
		List<Tweet> tweets = Tweet.find("order by date desc").fetch();
		renderJSON(tweets);
	}
	
}
{% endhighlight %}

Con el método renderJSON estamos transformando nuestra lista de tweets a formato JSON. Este método hace un recorrido en profundidad.

Añadimos la ruta al fichero de rutas

conf/routes

	GET     /api/tweets/all              Api.tweetsAll


Para probar que funciona correctamente entramos en [http://localhost:9000/api/tweets/all](http://localhost:9000/api/tweets/all).

Si nos fijamos en la respuesta, vemos que se muestran todos y cada uno de los campos. Se muestra por ejemplo el campo contraseña, algo no demasiado seguro y en la lista de usuarios que sigue alguien, se muestran todos los datos de ese usuario, incluso los usuarios a los que sigue.

## Problema de referencia circular

¿Qué pasa si el user1 sigue al user2 y el user2 sigue al user1?

Vamos a hacer una modificación rápida en el código para probarlo

{% highlight java linenos %}
package controllers;

import java.util.List;

import models.Tweet;
import models.User;
import play.mvc.Controller;

public class Api extends Controller {

	public static void tweetsAll(){
		User user1 = User.find("byUsername", "user1").first();
		User user2 = User.find("byUsername", "user2").first();
		user1.follows.add(user2);
		user2.follows.add(user1);
		user1.save();
		user2.save();
		
		List<Tweet> tweets = Tweet.find("order by date desc").fetch();
		renderJSON(tweets);
	}
	
}
{% endhighlight %}

Consultamos los tweets de user1

[http://localhost:9000/api/tweets/all](http://localhost:9000/api/tweets/all)

y

![Error de referencia circular](images/jsonError.png)

## Resolviendo el problema de referencia circulares

Para resolver nuestro problema lo que tenemos que hacer es seleccionar la información que vamos a devolver. Del autor vamos a devolver únicamente su username y no su contraseña ni la lista de personas a las que sigue.

Por defecto play utilizar la librería GSON para convertir a JSON. Esta librería permite escribir [Serializer personalizados](https://sites.google.com/site/gson/gson-user-guide#TOC-Custom-Serialization-and-Deserializ). El código queda un poco engorroso, así que vamos a utilizar otra librería que en mi opinión tiene una mejor interfaz, [flexjson](http://flexjson.sourceforge.net/).

Lo primero que debemos hacer es añadir la dependencias de la librería nueva

{% highlight bash linenos %}
	# Application dependencies

	require:
	    - play
	    - play -> secure
	    - net.sf.flexjson -> flexjson 2.2-SNAPSHOT
{% endhighlight %}

Ejecutamos

	play dependencies
	play eclipsify
	
Con el comando de dependencias play se descargará automáticamente la librería del repositorio Central de Maven.

Actualiza el proyecto en Eclipse y reinicia el servidor.

Modifica el método para filtrar los campos que se devuelven

{% highlight java linenos %}
package controllers;

import java.util.List;

import flexjson.JSONSerializer;

import models.Tweet;
import models.User;
import play.mvc.Controller;

public class Api extends Controller {

	public static void tweetsAll(){
		List<Tweet> tweets = Tweet.find("order by date desc").fetch();
		renderJSON(new JSONSerializer().include("msg", "date", "author.username").exclude("*").serialize(tweets));
	}
	
}
{% endhighlight %}	

Prueba de nuevo la consulta

[http://localhost:9000/api/tweetsFromUser/user1](http://localhost:9000/api/tweetsFromUser/user1)

## Añadiendo javascript a la vista

Para la vista vamos a utilizar [jQuery](http://jquery.com/) y [Knockout.js](http://knockoutjs.com/). 

* [jQuery](http://jquery.com/) es una de las librerías más utilizadas en la actualidad. La utilizaremos para manipular el DOM y para hacer peticiones AJAX
* [Knockout.js](http://knockoutjs.com/) implementa el patrón Model-View-View Model y permite tener sincronizada la interfaz del usuario de una forma muy sencilla

## Introducción a jQuery


## Introducción a Knockout.js


## Implementación

### Primer paso: Trabajar con dependentArray

Lo primero que vamos a hacer es sentar las bases con knockout.js y vamos a implementar de nuevo nuestro modelo.

{% highlight javascript linenos %}
<script>
	var Tweet = function(msg, date, author){
		this.msg = msg;
		this.date = date;
		this.author = author;
	}
	
	var viewModel = {
		tweets : ko.observableArray([
			new Tweet("mensaje1", "01-03-2010", "user1"),
			new Tweet("mensaje2", "02-03-2010", "user2"),
		])
	}
	
	ko.applyBindings(viewModel);
</script>
{% endhighlight %}

En nuestro modelo tenemos los tweets, que son un observable array con dos Tweets de prueba.

Ahora necesitamos una plantilla para pintar esos tweets en la pantalla.

{% highlight html linenos %}
<script type="text/html" id="renderTweet">
<div class="tweet">
    <div class="tweet-author">{{= author}}</div>
    <div class="tweet-msg">{{= msg}}</div>
    <div class="tweet-date">{{= date}}</div>
</div>
</script>
{% endhighlight %}

Utilizamos la misma estrategia que usamos anteriormente cuando creamos el tag. Pero esta vez estamos haciendo una plantilla para utilizar con jquery tmpl.

Para probar que todo funciona [http://localhost:9000/](http://localhost:9000/) y debería aparecer el mensaje1 y el mensaje2.

El objetivo del dependentArray es que cuando añadamos un tweet nuevo al array. La interfaz gráfica se actualice automáticamente. Para probarlo tenemos que abrir la consola javascript (En Chrome Menu/Herramientas/Consola Javascript).

Añadimos un nuevo tweet al array

{% highlight javascript linenos %}
viewModel.tweets.push(new Tweet('tweet3','03-03-2010', 'user3'))
{% endhighlight %}

![Consola javascript de Chrome](images/knockout-consola.png)

## Segundo paso: Poblando el array mediante AJAX

Añadimos una nueva función que se va a encargar de hacer una petición y añadir los tweets de la respuesta.

{% highlight javascript linenos %}
viewModel.updateTweets = function(){
	$.get('@{Api.tweetsAll()}', function(data){
		var tweets = $.map(data, function(item){
			return new Tweet(item.msg, item.date, item.author.username)
		})
		viewModel.tweets(tweets);
	});
}
{% endhighlight %}


Añadimos una llamada a la función cuando la página se ha cargado

{% highlight javascript linenos %}
$(function(){
	viewModel.updateTweets();
});
{% endhighlight %}

## Tercer paso: Enviando tweets mediante AJAX

Añade un nuevo método a la API que permita enviar tweets y nos devuelva el tweet creado en caso de que todo fue bien.

{% highlight java linenos %}
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
{% endhighlight %}

Crea la clase ApiResponse que nos va a permitir encapsular las respuesta de nuestra API. Por si se producen error poder tener un lugar donde enviar los mensajes de error.

{% highlight java linenos %}
package responses;

public class ApiResponse {
	public String status;
	public String message;
	public Object result;
}
{% endhighlight %}

Añade la ruta

	GET     /api/tweets/new                              Api.tweetsNew
	
Modifica la vista

La función createTweet se encarga de hacer la petición a la acción tweetsNew pasándole por parámetro el mensaje del tweet nuevo. Si la respuesta fue correcta lo añade a la lista de tweets. Si hubo error lo almacena en el modelo para actualizar la interfaz.

{% highlight javascript linenos %}
function parseTweet(tweet){
	return new Tweet(tweet.msg, new Date(tweet.date), tweet.author.username);
}

viewModel.createTweet = function(){
	var action = #{jsAction @Api.tweetsNew(':msg')/}
	$.get(action({msg : viewModel.newTweet()}), function(data){
		if(data.status === 'OK'){
			viewModel.tweets.unshift(parseTweet(data.result));
			viewModel.newTweet('');
			viewModel.clearError();
		}else{
			viewModel.error(data.message);
		}
	});
	return false;
}
{% endhighlight %}

Cambiamos nuestro formulario, para que al hacer submit, se ejecute la función createTweet.

{% highlight html linenos %}
<form data-bind="submit:createTweet">
	<div class="clearfix">
		<textarea class="xxlarge" name="tweet" rows="3" data-bind="value:newTweet"></textarea>
	</div>
	<input type="submit" class="btn primary" value="Tweet">
</form>
{% endhighlight %}

Ponemos el código para mostrar los errores

{% highlight html linenos %}
<div class="alert-message error" data-bind="visible: error() != null">
	<a class="close" href="#" data-bind="click: clearError">×</a>
	<p data-bind="text: error"></p>
</div>
{% endhighlight %}

Añadimos una función para limpiar los errores. Esta función se ejecutará cuando en tweet se envíe correctamente o cuando el usuario pulse la 'x' del mensaje.

{% highlight javascript linenos %}
viewModel.clearError = function(){
	viewModel.error(null);
	return false;
}
{% endhighlight %}

La aspecto final de la vista es

{% highlight html linenos %}
#{extends 'main.html' /} 
#{set title:'Timeline' /} 

#{set 'secondaryContent'}
<ul>
	<li>Following: </li>
	<li>Followers: </li>
</ul>
#{/set}


<div class="alert-message error" data-bind="visible: error() != null">
	<a class="close" href="#" data-bind="click: clearError">×</a>
	<p data-bind="text: error"></p>
</div>



<form data-bind="submit:createTweet">
	<div class="clearfix">
		<textarea class="xxlarge" name="tweet" rows="3" data-bind="value:newTweet"></textarea>
	</div>
	<input type="submit" class="btn primary" value="Tweet">
</form>

<div data-bind="template :{name:'renderTweet', foreach:tweets}">

</div>

<script type="text/html" id="renderTweet">
<div class="tweet">
    <div class="tweet-author">{{= author}}</div>
    <div class="tweet-msg">{{= msg}}</div>
    <div class="tweet-date">{{= date.toLocaleDateString()}}</div>
</div>
</script>

<script>
	var Tweet = function(msg, date, author){
		this.msg = msg;
		this.date = date;
		this.author = author;
	}
	
	var viewModel = {
		newTweet : ko.observable(''),
		error : ko.observable(null),
		tweets : ko.observableArray([])
	}
	
	function parseTweet(tweet){
		return new Tweet(tweet.msg, new Date(tweet.date), tweet.author.username);
	}
	
	viewModel.updateTweets = function(){
		$.get('@{Api.tweetsAll()}', function(data){
			var tweets = $.map(data, function(item){
				return parseTweet(item);
			})
			viewModel.tweets(tweets);
		});
	}
	
	viewModel.createTweet = function(){
		var action = #{jsAction @Api.tweetsNew(':msg')/}
		$.get(action({msg : viewModel.newTweet()}), function(data){
			if(data.status === 'OK'){
				viewModel.tweets.unshift(parseTweet(data.result));
				viewModel.newTweet('');
				viewModel.clearError();
			}else{
				viewModel.error(data.message);
			}
		});
		return false;
	}
	
	viewModel.clearError = function(){
		viewModel.error(null);
		return false;
	}
	
	ko.applyBindings(viewModel);
	
	$(function(){
		viewModel.updateTweets();
	});
</script>
{% endhighlight %}

<div class="alert-message warning">
Puedes descargarla desde <a href="https://github.com/axelhzf/play-curso/commit/4a9bc29137016576141c1237410f066d3d9728d4">https://github.com/axelhzf/play-curso/commit/4a9bc29137016576141c1237410f066d3d9728d4</a>
</div>

## Ejercicio

Hasta ahora nuestra aplicación no es más que una especie de tablón de anuncios donde los usuarios pueden escribir sus mensajes. Vamos a dotarle de las funcionalidades de twitter.

* Consultar los tweets que ha enviado un usuario en concreto
* Limitar los tweets que aparecen en el timeline a los de personas a las que sigue el usuario y los propios
* Mostrar información de follows y followers
* Mostrar botones de follow y unfollow
* Hacerlo implementando los métodos en el API y haciendo las llamadas mediante javascript

