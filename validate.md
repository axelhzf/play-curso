---
layout: default
title: Validación
---

<div class="alert-message warning">
Página de documentación de validacion <a href="http://www.playframework.org/documentation/1.2.3/validation">http://www.playframework.org/documentation/1.2.3/validation</a>
</div>

Existen principalmente dos formas de especificar la validación en nuestra acción. Anotando los parámetros de nuestra acción o anotando los campos de nuestra entidad.

#### Anotar los parámetros de la acción

{% highlight java linenos %}
public static void save(@Required String name) {

}
{% endhighlight %}

#### Anotar los campos de la entidad

{% highlight java linenos %}
@Entity
public class Tweet extends Model {

	@Required
	public String msg;
	
	...
}
{% endhighlight %}

En el caso de que estemos pasando un objeto entero mediante parámetro, podemos validarlo utilizando la anotación @Valid. Comprobará todos sus campos y las anotaciones que tiene.

{% highlight java linenos %}
public static void create(@Valid Tweet tweet) {

}
{% endhighlight %}

Es muy común el patrón de: Si la entidad está correcta, almacenala en base de datos, en caso contrario no hagas nada.

{% highlight java linenos %}
public static void create(@Valid Tweet tweet) {
	if(!validation.hasErrors()){
		tweet.save();
	}
}
{% endhighlight %}

La simplificación de este método sería

{% highlight java linenos %}
public static void create(Tweet tweet){
	tweet.validateAndSave();
}
{% endhighlight %}

## Validaciones disponibles

Puedes consultar las validaciones incluidas por defecto en [http://www.playframework.org/documentation/1.2.3/validation-builtin](http://www.playframework.org/documentation/1.2.3/validation-builtin)

## Mostrando los errores de validación

Play trae una serie de tags que te permiten mostrar los errores de validación.

* \#{error 'fieldName'/} - Muestra el error para un campo en concreto
* \#{errorClass /} - Equivalente a ${errors.forKey('name') ? 'hasError' : ''}. Se suele utilizar para añadir una clase de css distinta y resaltar los errores.
* \#{errors /} - Permite iterar en todos los errores
* \#{ifError 'user.name'} 
* \#{ifErrors} 

## Ejercicio

Añade validación al formulario que envía el tweet.

* Un tweet no puede estar vacío
* Un tweet no puede tener más de 140 caracteres
* Muestra los mensajes de error con el estilo adecuado de [Twitter Bootstrap](http://twitter.github.com/bootstrap/index.html#alerts)

## Solución

apps/controllers/Timeline.java

{% highlight java linenos %}
public static void create(String tweet){
 	checkAuthenticity();
 	Tweet t = new Tweet(tweet, Security.userConnected());
 	t.validateAndSave();
 	
 	if(validation.hasErrors()){
 		validation.keep();
 	}
 	
 	index();
}
{% endhighlight %}


app/views/Timeline/index.html

{% highlight html linenos %}
	#{ifErrors}
		<div class="alert-message error">
		#{errors}
	       <p>${error}</p>
	   #{/errors}
		</div>
	#{/ifErrors}
{% endhighlight %}

<div class="alert-message warning">
Puedes descargarte el código desde <a href="https://github.com/axelhzf/play-curso/tree/4ac2865ae7546a82fc3e602f3eae2aabec83696c">https://github.com/axelhzf/play-curso/tree/4ac2865ae7546a82fc3e602f3eae2aabec83696c</a>
</div>
