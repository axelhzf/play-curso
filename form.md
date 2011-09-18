---
layout: default
title: Formularios
---

Una parte importante de todas las aplicaciones web son los formularios. Donde se le permite al usuario enviar información al servidor. En nuestro caso, el formulario más importante es el que le permite a un usuario enviar un nuevo tweet.

Un ejemplo de formulario en play sería el siguiente:

vista

{% highlight html linenos %}
<form action="@{Controlador.action}">
	<input type="text" name="parametro">
	<input type="submit">
</form>	
{% endhighlight %}

controlador

{% highlight java linenos %}
package controllers;

import play.mvc.*;

public class Controlador extends Controller {
	public static void action(String parametro){
		//parametro valdrá lo que envie el usuario
	}
}
{% endhighlight %}

Play se encarga automáticamente de convertir los parámetros que envíe el usuario, a parámetros de nuestra acción.

<div class="alert-message warning">
Más información sobre el binding automático que hace play <a href="http://www.playframework.org/documentation/1.2.3/controllers">http://www.playframework.org/documentation/1.2.3/controllers</a>
</div>

## Consideraciones de seguridad

Play viene con ayudas para hacer tu página más segura. Uno de los ataques que te puede ayudar a resolver son los [Cross-Site Request Forgery(CSRF)](http://es.wikipedia.org/wiki/Cross_Site_Request_Forgery).

Para evitar este tipo de ataque lo que nos hace falta es trabajar con un authenticityToken. Será un parámetro que añadiremos a nuestros formularios y que comprobaremos en nuestras acciones. De esta forma evitamos, que un link no generado por nosotros mismo intente enviar el formulario.

vista

{% highlight html linenos %}
<form method="post" action="/account/destroy">
    #{authenticityToken /}
    <input type="submit" value="destroy my account">
</form>
{% endhighlight %}

El tag #{authenticityToken} añade un parámetro oculto a nuestro formulario con el token.

controlador

{% highlight java linenos %}
public static destroyMyAccount() {
    checkAuthenticity();
    …
}
{% endhighlight %}

El método checkAuthenticityToken comprueba que el token es correcto. Debes recordar ponerlo en todas las acciones que recibas parámetros de formularios.

Para simplicar la vista. Play trae el tag #{form}, que crea un formulario html con el authenticityToken integrado

vista

{% highlight html linenos %}
#{form @destroyMyAccount()}
    <input type="submit" value="destroy my account">
#{/form}
{% endhighlight %}

## Ejercicio

Añadir un formulario en el Timeline para permitirle al usuario enviar un nuevo tweet.

* Añade a la vista el formulario para enviar el contenido del tweet. Utilizando el tag #{form}
* Utiliza los estilos de [Twitter Bootstrap](http://twitter.github.com/bootstrap/index.html#forms) para el aspecto del formulario
* Añade un método en el controlador que reciba el texto y cree un nuevo tweet. Asegúrate de asignarle el autor adecuado. Después de guardar el tweet, debe redirigir otra vez a la página principal.
* No te olvides de llamar al método checkAuthenticityToken()
* Añade una ruta de tipo POST en el fichero de rutas
* Prueba a enviar tweets con distintos usuarios
* Prueba que la página de estadísticas actualiza con los nuevos tweets

## Solución

apps/views/Timeline/index.html

{% highlight html linenos %}
#{form @Timeline.create()}
	<div class="clearfix">
		<textarea class="xxlarge" name="tweet" rows="3"></textarea>
	</div>
	<input type="submit" class="btn primary" value="Tweet">
#{/form}
{% endhighlight %}

app/controllers/Timeline.java

{% highlight java linenos %}
public static void create(String tweet){
	checkAuthenticity();
	Tweet t = new Tweet(tweet, Security.userConnected());
	t.save();
	index();
}
{% endhighlight %}

conf/routes

	POST    /create									Timeline.create
	
	
<div class="alert-message warning">
Puedes descargarte la solución completa desde <a href="https://github.com/axelhzf/play-curso/commit/c089b30c58b2de7641631ec803c1afc280533e4b">https://github.com/axelhzf/play-curso/commit/c089b30c58b2de7641631ec803c1afc280533e4b</a>
</div>