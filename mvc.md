---
layout: default
title: Modelo Vista Controlador
---

<div class="alert-message warning">
Documentación en la página de Play
<a href="http://www.playframework.org/documentation/1.2.3/main">http://www.playframework.org/documentation/1.2.3/main</a>
</div>
## Definición

El **Modelo Vista Controlador (MVC)** es un patrón de arquitectura de software que separa los datos de una aplicación, 
la interfaz de usuario, y la lógica de control en tres componentes distintos. 

* Modelo
* Vista
* Controlador

![Diagrama MVC](images/diagrams_mvc.png)

![Diagrama MVC con router](images/diagrams_path.png)


## Análisis de una petición

1. El servidor recibe una petición HTTP
2. El router consulta la tabla de rutas y determina el método del controlador que debe ser invocado.
3. El controlador ejecuta su lógica de negocio. El caso más común:
	1. Validación de los parámetros
	2. Actualización y/o consulta del modelo
	3. Renderización de la vista
4. Se renderiza el formato adecuado de la vista
5. Se forma la respuesta HTTP

## ¿Como encaja esto con la estructura del proyecto creado por play?

En la estructura del proyecto creada se puede ver claramente el patrón reflejado en las carpetas **"models"**, **"controllers"** y **"views"**.

{% highlight bash %}
.
├── app
│   ├── controllers
│   │   └── Application.java
│   ├── models
│   └── views
│       ├── Application
│       │   └── index.html
│       ├── errors
│       │   ├── 404.html
│       │   └── 500.html
│       └── main.html
├── conf
│   ├── application.conf
│   ├── dependencies.yml
│   ├── messages
│   └── routes
├── lib
├── public
│   ├── images
│   │   └── favicon.png
│   ├── javascripts
│   │   └── jquery-1.5.2.min.js
│   └── stylesheets
│       └── main.css
└── test
    ├── Application.test.html
    ├── ApplicationTest.java
    ├── BasicTest.java
    └── data.yml
{% endhighlight %} 