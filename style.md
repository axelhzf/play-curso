---
layout: default
title: Añadiendo estilo
---

Por ahora nuestra página es bastante cutre. Play, a diferencia de otros frameworks como por ejemplo Richfaces, no trae elementos de interfaz gráfica. Esto puede parecer una desventaja, pero en realidad lo que permite es no atarte a utilizar un componente en concreto. En este tipo de framework puede ser muy complejo intentar modificar ese elemento visual para añadirle una funcionalidad que no tiene y te hace falta para tu aplicación.

En la actualidad existen muchas librerías javascript que te permiten tener una interfaz de usuario muy completa. Es mejor buscar entre las librerías disponible y encontrar la que mejor se adapta a tus necesidades. Normalmente este tipo de librerías tienen un mejor diseño y están mejor probadas para funcionar con todos los navegadores.

Por poner algunos ejemplos de librerías de este tipo:

* [jQuery UI](http://jqueryui.com/)
* [jQuery Mobile](http://jquerymobile.com/)
* [Dojo](http://dojotoolkit.org/)
* [Sencha ExtJS](http://www.sencha.com/)

Por ahora vamos únicamente a darle estilo a nuestra páginas y para ello ¿qué mejor que utilizar una librería de la propia gente de twitter?

Página principal de <a href="http://twitter.github.com/bootstrap/">Bootstrap, from Twittter</a>

<div class="alert-message block-message warning">
	Bootstrap está creado utilizando <a href="http://lesscss.org/">LESS</a>, hojas de estilo dinámicas. Añade variable y funciones a las hojas de estilo tradicionales.También tienes un módulo en play para poder utilizar LESS <a href="http://www.playframework.org/modules/less">http://www.playframework.org/modules/less</a>		
</div>

El ejemplo que utilizaremos para nuestra página será el que más se parece a la interfaz de twitter [http://twitter.github.com/bootstrap/examples/container-app.html](http://twitter.github.com/bootstrap/examples/container-app.html)

Modificamos nuestra plantilla para adaptar el contenido



app/views/main.html

{% highlight html %}
<!DOCTYPE html>

<html>
    <head>
        <title>#{get 'title' /}</title>
        <meta charset="utf-8">

        <link rel="shortcut icon" type="image/png" href="@{'/public/images/favicon.png'}">
        <script src="@{'/public/javascripts/jquery-1.5.2.min.js'}" type="text/javascript" charset="${_response_encoding}"></script>
        <link rel="stylesheet" href="http://twitter.github.com/bootstrap/1.3.0/bootstrap.min.css">
        <link rel="stylesheet" media="screen" href="@{'/public/stylesheets/main.css'}">
        #{get 'moreStyles' /}
        #{get 'moreScripts' /}
    </head>
    <body>
    
      <div class="topbar">
      <div class="fill">
        <div class="container">
          <a class="brand" href="#">twitter-play</a>
          <ul class="nav">
            <li><a href="@{Timeline.index}">Inicio</a></li>
            <li><a href="@{Timeline.stats}">Estadísticas</a></li>
          </ul>
          <form action="" class="pull-right">
            <button class="btn" type="submit">Salir</button>
          </form>
        </div>
      </div>
    </div>

	<div class="container">
		<div class="content">
			<div class="page-header">
				<h1>#{get 'title' /}</h1>
			</div>
			<div class="row">
				<div class="span10">#{doLayout /}</div>
				<div class="span4">#{get 'secondaryContent' /}</div>
			</div>
		</div>

		<footer>
			<p>
				<a href="http://axelhzf.github.com/play-curso/">Tutorial</a> - <a
					href="https://github.com/axelhzf/play-curso">Código fuente</a>
			</p>
		</footer>

	</div>
	<!-- /container -->

</body>
</html>
{% endhighlight %}

public/stylesheets/main.css

{% highlight css %}
html,body {
	background-color: #eee;
}

body {
	padding-top: 40px;
	/* 40px to make the container go all the way to the bottom of the topbar */
}

.container>footer p {
	text-align: center; /* center align it with the container */
}

.container {
	width: 820px;
	/* downsize our container to make the content feel a bit tighter and more cohesive. NOTE: this removes two full columns from the grid, meaning you only go to 14 columns and not 16. */
}

/* The white background content wrapper */
.content {
	background-color: #fff;
	padding: 20px;
	margin: 0 -20px;
	/* negative indent the amount of the padding to maintain the grid system */
	-webkit-border-radius: 0 0 6px 6px;
	-moz-border-radius: 0 0 6px 6px;
	border-radius: 0 0 6px 6px;
	-webkit-box-shadow: 0 1px 2px rgba(0, 0, 0, .15);
	-moz-box-shadow: 0 1px 2px rgba(0, 0, 0, .15);
	box-shadow: 0 1px 2px rgba(0, 0, 0, .15);
}

/* Page header tweaks */
.page-header {
	background-color: #f5f5f5;
	padding: 20px 20px 10px;
	margin: -20px -20px 20px;
}

/* Styles you shouldn't keep as they are for displaying this base example only */
.content .span10,.content .span4 {
	min-height: 500px;
}
/* Give a quick and non-cross-browser friendly divider */
.content .span4 {
	margin-left: 0;
	padding-left: 19px;
	border-left: 1px solid #eee;
}

.topbar .btn {
	border: 0;
}

.tweet {
	padding: 5px 10px;
}

.tweet-author {
	font-weight: bold;
}

.tweet-date {
	font-size: 12px;
	color: #666;
}

.tweet-msg {
	margin: 5px 0;
}
{% endhighlight %}