---
layout: default
title: Controlador
---

## Router
Como vimos en la sección del patrón [Modelo Vista Controlador](/curso/mvc.html) el router es el que a partir de una petición HTTP, invoca al método del controlador adecuado.

Para la configuración de las rutas se utiliza el fichero conf/routes

{% highlight bash %}
# Routes
# This file defines all application routes (Higher priority routes first)
# ~~~~

# Home page
GET     /                                       Application.index

# Ignore favicon requests
GET     /favicon.ico                            404

# Map static resources from the /app/public folder to the /public path
GET     /public/                                staticDir:public

# Catch all
*       /{controller}/{action}                  {controller}.{action}
{% endhighlight %}

### Syntaxis del fichero de rutas

{% highlight bash %}
Method		UriPattern		ControllerCall
{% endhighlight %}

#### Methods

* GET
* POST
* PUT
* DELETE
* HEAD

Play también soporta WS para indicar una petición de WebSocket

####UriPattern

{% highlight bash %}
#Directo
/clients/all

#Parámetro que se pasara al controlador
/clients/{id}  

#Expresión regular por defecto /[^/]+/
/clients/{id}/accounts/{accountId}  

#Expresiones regulares
/clients/{<[0-9]+>id}  
/clients/{<[a-z]{4,10}>id}
{% endhighlight %}

#### ControllerCall

{% highlight bash %}
package.Controller.method  //El paquete por defecto es controller
{% endhighlight %}

<div class="alert-message warning">
<strong>Prioridad en las rutas:</strong> Si hay conflicto se llama a la primera ruta, siguiendo el orden en el que fueron definidas.
</div>

## Controlador

Los controladores son básicamente clases Java que extiende de la clase play.mvc.Controller y que tienen una serie de métodos "public static void" que son las acciones de cada controlador.

Signature de una acción del controlador

{% highlight java %}
public static void action_name(params...);
{% endhighlight %}

Play hará el mapeo automático entre los parámetros que vienen de la petición HTTP y los parámetros que tiene el controlador. Normalmente las acciones no retornan ningún valor. La ejecución del método termina con llamada a un método **result**, por ejemplo el método **render(...)**

### Parámetros de las acciones del controlador


{% highlight java linenos %}

// HTTP Request /clients?id=1451

public static void show() {
    Long id = params.get("id", Long.class);
}

public static void show(String id) {
    System.out.println(id); 
}

public static void show(Long id) {
    System.out.println(id);  
}

public static void show(Long[] id) {
    for(String anId : id) {
        System.out.println(anid); 
    }
}

public static void show(List<Long> id) {
    for(String anId : id) {
        System.out.println(anid); 
    }
}

// JPA Object binding!!!
// HTTP Request:
// user?user.id=1&user.name=axel&user.address.id=34&user.address.street=Street

public static void save(User user) {
    user.save();
}


{% endhighlight %}

<div class="alert-message warning">
<p>Puedes consultar los binding avanzados de parámetros en la documentación de play</p>
<a href="http://www.playframework.org/documentation/1.2.3/controllers">http://www.playframework.org/documentation/1.2.3/controllers</a>
</div>

### Result types

* **render(...)**
* **renderText(String)**
* **renderBinary**
* **renderTemplate**
* **redirect**
* **renderJson**
* **renderTemplate**

Desde una acción, si llamas a otra acción, se enviará un **Redirect**.
No hay un equivalente al **forward** de los Servlet.

El método más común es el método render 

{% highlight java %}
protected static void render(Object... args)
{% endhighlight %}

Este método lo que hace es renderizar la plantilla definida para esta acción. 

Si tenemos un controllador en 

{% highlight java %}
controller/Application.java    con un metodo que se llama   index
{% endhighlight %}

Renderizará la plantilla

{% highlight java %}
views/Application/index.hml
{% endhighlight %}

Los parámetros que se le pasan al método, se le pasan directamente a la plantilla.

## Creando nuestro controlador


app/controller/Timeline.java



{% highlight java linenos %}
package controllers;

import java.util.List;
import play.mvc.Controller;
import models.*;

public class Timeline extends Controller {

    public static void index(){
        List<Tweet> tweets = Tweet.find("order by date desc").fetch();
        render(tweets);
    }

}

{% endhighlight %}

conf/routes

{% highlight bash linenos %}
GET   /  Timeline.index
{% endhighlight %}


Muy bien, ya tenemos el controlador creado, pero... ¿cómo lo probamos? Nos hace falta la vista para poder probarlo. Pasamos a la siguiente sección.