---
layout: default
title: Vista
subtitle: Template engine
---
{% assign solucion = false %}


<div class="alert-message warning">
	<p>Enlace a la documentación de play </p>
<a href="http://www.playframework.org/documentation/1.2.3/templates">http://www.playframework.org/documentation/1.2.3/templates</a>
</div>

El sistema de plantillas por defecto de play está basado en [Groovy](http://groovy.codehaus.org/). La versión de play de Scala, viene con un sistema de plantilla basado en Scala. La ventaja de este sistema de plantilla es que Scala es un lenguaje de tipado estático, por lo tanto si hay error de tipo los dará en tiempo de compilación y no en tiempo de compilación. Para la versión 2.0 de Play tienen planeado sustituir el motor de plantillas de Groovy por el de Scala.


Los elementos básicos del sistema de plantilla son:

* Expresiones **${}**
* Decoradores **#{extends /}** y **#{doLayout /}**
* Tags **#{tagName /}**
* Acciones **@{…}** (Relativas) or **@@{…}** (Absolutas)
* Mensajes **&{…}**
* Comentarios **\*{…}\***
* Scripts **%{…}%**

## Nuestra primera plantilla

Ya teníamos creado nuestro primero controlador, ahora nos hace falta la primera plantilla para poder ver los resultados. Lo que haremos es listar la lista de tweets.

Creamos el fichero con apps/views/Timeline/index.html

<div class="alert-message warning">
	Cuando se utiliza el método render, por defecto play busca una plantilla en la carpeta <strong>apps/views/{Controlador}/{action}</strong>. Si quieres especificar otro nombre a la plantilla puedes utilizar el método renderTemplate("plantilla", parametros...)
</div>

{% highlight html linenos %}
#{extends 'main.html' /}
#{set title:'Timeline' /}

${tweets}
{% endhighlight %}

Con el extends estamos diciendo que plantilla queremos utilizar. El set nos permite fijar variables que se van a utilizar dentro de la plantilla. Con ${tweets} estamos mostrando los tweets, que fueron pasados como parámetros del render.

Abrimos el navegados [http://localhost:9000/](http://localhost:9000/)

Y deberíamos ver algo tipo:

[]

Esta es la representación que hace Groovy de una lista vacía. Nuestra lista de tweets está vacia, así que vamos a llenarla con algunos datos de pruebas.

## Poblando la base de datos al arranque de la aplicación

Como vamos a utilizar una base de datos en memoria, nos interesa que cada vez que arranque la aplicación se llene con algunos datos de prueba. Para ellos vamos a utilizar los Jobs de Play.

Creamos una nueva clase en app/app/Bootstrap.java

{% highlight java linenos %}
package app;

import models.User;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.test.Fixtures;

@OnApplicationStart
public class Bootstrap extends Job {

    public void doJob(){
        if(User.count() == 0){
            Fixtures.loadModels("initial-data.yml");
        }
    }

}
{% endhighlight %}

Con la anotación @OnApplicationStart le estamos diciendo a Play que queremos que nuestro Job se ejecute cada vez que arranque la aplicación. Los Job deben extender de la clase **play.jobs.Job** e implementar el método **doJob** que es el método que se invocará. En el método estamos utilizando la clase **Fixtures** que ya conocemos para cargar datos en base de datos a partir de un fichero yml.

Además de la anotación @OnApplicationStart existen otras anotaciones para programar Jobs:

* @Every - Ejecuta tareas en intervalos de tiempo
* @On    - Permite utilizar una sintaxis tipo CRON

<div class="alert-message info">
	Documentación de los Jobs : <a href="http://www.playframework.org/documentation/1.2.3/jobs">http://www.playframework.org/documentation/1.2.3/jobs</a>
</div>

Si actualizamos la página <a href="http://localhost:9000/">http://localhost:9000/</a> deberíamos ver algo parecido a:

[Tweet[9], Tweet[8], Tweet[7], Tweet[6], Tweet[5], Tweet[4], Tweet[3], Tweet[2], Tweet[1]]

## Utilizando scripts dentro de las vistas

Hasta ahora lo que estábamos mostrando es el .toString() de los objetos, que por defecto muestra NombreClase[id]. Vamos a cambiar nuestra vista para que muestra la información de cada uno de los tweets. 

Dentro de los bloques de %{}% podemos utilizar código groovy. Por ejemplo, un bucle for para recorrer todos los tweets.

{% highlight html linenos %}
#{extends 'main.html' /}
#{set title:'Timeline' /}
%{
	for(tweet in tweets){
}%
	<div class="tweet">
        <div class="tweet-author">${tweet.author.username}</div>
        <div class="tweet-msg">${tweet.msg}</div>
        <div class="tweet-date">${tweet.date}</div>
	</div>
%{
	}
%}
{% endhighlight %}

## Utilizando tags
Los bloques de scripts están bien, pero a veces puede quedar la sintaxis un poco engorrosa. Para solucionar esto están los #{tags /}. Play viene con una serie de tags por defecto.

<div class="alert-message info">
	Documentación built-in tags: <a href="http://www.playframework.org/documentation/1.2.3/tags">http://www.playframework.org/documentation/1.2.3/tags</a>
</div>
	
En este caso nos vendría bien utilizar el tag #{list /}

{% highlight html linenos %}
#{extends 'main.html' /}
#{set title:'Timeline' /}

#{list items:tweets, as:'tweet'}
	<div class="tweet">
		<div class="tweet-author">${tweet.author.username}</div>
		<div class="tweet-msg">${tweet.msg}</div>
		<div class="tweet-date">${tweet.date}</div>
	</div>
#{/list}
{% endhighlight %}	

## Creando nuestros propios tags

Puedes definirte tus propios tags y escribirlos en Groovy o directamente en Java. Es una buena práctica crear tags para elementos que repitas en varias páginas, así el código no se duplica, sino se sustituye por llamadas al tag.

En este caso, parece que la forma de representar un tweet puede repetirse en varias páginas, por ejemplo el timeline, la lista de mensajes privados y así. Vamos a crear un tag para no tener que repetir el código.

Los tags se crean en la carpeta app/view/tags. El nombre del fichero, será el nombre del tag. Los tags pueden recibir parámetro. Dentro del tag estarán disponibles con $_nombreParametro. El parámetro por defecto es $_arg.

app/views/tags/renderTweet.html

{% highlight html linenos %}
<div class="tweet">
    <div class="tweet-author">${_arg.author.username}</div>
    <div class="tweet-msg">${_arg.msg}</div>
    <div class="tweet-date">${_arg.date}</div>
</div>
{% endhighlight %}

Cambiamos nuestra vista para hacer la llamada al tag

app/view/Timeline/index.html

{% highlight html linenos %}
#{extends 'main.html' /}
#{set title:'Timeline' /}

#{list items:tweets, as:'tweet'}
	#{renderTweet tweet /}
#{/list}
{% endhighlight %}


## Ejercicio: Tag para mostrar gráfica

Como ejercicio vamos a crear un tag que permita mostrar una graáfica.

Pasos:

* Crear una nueva página de estadísticas en /stats
* Consultar cuantos tweets ha escrito cada uno de los usuarios
* Utilizando Google Chart [http://code.google.com/intl/es-ES/apis/chart/interactive/docs/gallery/columnchart.html](http://code.google.com/intl/es-ES/apis/chart/interactive/docs/gallery/columnchart.html) crear una gráfica que muestre en el eje de las X los usuarios y en el eje de las Y el número de tweets que ha publicado.
* Crear un tag que permite reutilizar el elemento de las gráficas. La llamada al tags debe ser algo de la forma:

{% highlight html linenos %}
	#{grafica items:user_tweets, 
		      title:'Tweets de usuarios', 
		      xTitle:'Usuario', 
		      yTitle:'Tweets', 
		      id:'user_tweet', 
		      width:500, 
		      height:400 
	/}
{% endhighlight %}



Consejos:

* Utiliza **group by** y **count** para contar cuantos tweets ha escrito cada usuario [Documentación](http://www.objectdb.com/java/jpa/query/jpql/group)
* El tag lista permite obtener el indice del elemento por el que va el bucle en la variable item_index

{% if solucion == true %}
## Solución

Crea la nueva ruta

conf/routes

{% highlight bash linenos %}
GET     /stats									Timeline.stats
{% endhighlight %}

Crea el nuevo método del controlador

app/controllers/Timeline.java

{% highlight java linenos %}
public static void stats(){
    List<Object[]> user_tweets = JPA.em().createQuery("select t.author.username, count(t) from Tweet t group by t.author").getResultList();
    render(user_tweets);
}
{% endhighlight %}

Crea el tag, utilizando la documentación de Google Chart y haciendo la sustitución de variables en los lugares adecuados.

apps/views/tags/grafica.html

{% highlight html linenos %}
<script src="https://www.google.com/jsapi"></script>
<script>
      google.load("visualization", "1", {packages:["corechart"]});
      google.setOnLoadCallback(drawChart);
      function drawChart() {
        var data = new google.visualization.DataTable();
        data.addColumn('string', '${_xTitle}');
        data.addColumn('number', '${_yTitle}');
                        
        data.addRows(${_items.size()});
        #{list items:_items, as:'item'}
        	data.setValue(${item_index -1}, 0, '${item[0]}');
        	data.setValue(${item_index -1}, 1, ${item[1]});        
        #{/list}
        
        var chart = new google.visualization.ColumnChart(document.getElementById('${_id}'));
        chart.draw(data, {width: ${_width}, height: ${_height}, title: '${_title}'});
      }
</script>
<div id="${_id}"></div>
{% endhighlight %}

Crea la vista donde se llame al tag

apps/views/Timeline/stats.js

{% highlight html linenos %}
	#{extends 'main.html' /} 
	#{set title:'Estadísticas' /} 

	#{grafica items:user_tweets, 
		      title:'Tweets de usuarios', 
		      xTitle:'Usuario', 
		      yTitle:'Tweets', 
		      id:'user_tweet', 
		      width:500, 
		      height:400 
	/}
{% endhighlight %}

<div class="alert-message warning">
Puedes descargarte el código completo desde <a href="https://github.com/axelhzf/play-curso/tree/ea2682636fb1e36b22826324fce9ee13f93f687e">https://github.com/axelhzf/play-curso/tree/ea2682636fb1e36b22826324fce9ee13f93f687e</a>
</div>
{% endif %}