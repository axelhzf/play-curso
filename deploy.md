---
layout : default
title : Desplegando la aplicación
---

A la hora de poner tu aplicación en producción, en la documentación de Play siempre recomiendan utilizar el servidor que trae Play integrado (basado en Netty). De esta forma conseguirás el mejor rendimiento de la aplicación. Muchas veces esto no es posible, porque en nuestra organización tenemos ya tenemos algunos servidores de aplicaciones corriendo y tenemos unos administradores de sistemas muy vagos que no quieren instalarnos un nuevo servidor. Por este tipo de situaciones, las aplicaciones de Play también se pueden desplegar dentro de servidores de aplicaciones.

Consulta la documentación [http://www.playframework.org/documentation/1.2.3/deployment](http://www.playframework.org/documentation/1.2.3/deployment) para ver con qué servidores de aplicaciones es compatible Play.

## Desplegando la aplicación en Tomcat

Vamos a hacer una prueba y desplegar nuestra aplicación dentro de un Tomcat. Para ellos lo primero que tenemos que hacer es empaquetar nuestra aplicación en un war.

{% highlight bash linenos %}
play war twitter -o twitter-war --zip
{% endhighlight %}

Ya con esto tenemos creado nuestro war. Ahora descargamos Tomcat 6 (El 7 no está soportado)

[http://tomcat.apache.org/download-60.cgi](http://tomcat.apache.org/download-60.cgi)

Copia el el war a la carpeta webapps del tomcat y arranca el servidor.

## Desplegando en la nube

###[Heroku!](http://www.heroku.com/)

[http://www.jamesward.com/2011/08/29/getting-started-with-play-framework-on-heroku](http://www.jamesward.com/2011/08/29/getting-started-with-play-framework-on-heroku)

