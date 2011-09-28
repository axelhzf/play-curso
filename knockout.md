---
layout: default
title: knockout.js
---

[knockout.js](http://knockoutjs.com/) es una librería javascript que implementa el patrón Model View View-Model.

## Patrón Model View View-Model

Patrón Model View View-Model (MVVM) es un patrón diseñado para construir interfaces de usuario. Describe cómo mantener simple una interfaz de usuario sofisticada dividiéndola en tres partes:

- Model: Los datos de la aplicación.
- View-Model : Representación de los datos y las operaciones de la interfaz gráfica. No es la interfaz de usuario en sí, no tiene los conceptos de botones o estilos.
- Vista : Representación gráfica del estado del View-Model. Muestra la información del View-Model y envía comandos para ejecutar las acciones.

### Aplicado a Knockout

- Modelo : Normalmente llamadas Ajax para leer o escribir en el servidor.
- View-Model : Código javascript puro.
- Vista : Código HTML con "bindings" declarativos para enlazar el view-model. También se pueden utilizar templates para generar el código html.

## Ejemplo Simple

[http://jsfiddle.net/h7tgN/](http://jsfiddle.net/h7tgN/)

<iframe style="width: 100%; height: 300px" src="http://jsfiddle.net/h7tgN/embedded/"></iframe>

En el ejemplo tenemos un campo de texto donde estamos escribiendo en la variable valor. La variable se esta actualizando cuando se presiona una tecla. Después tenemos una etiqueta span donde estamos mostrando el contenido de la variable. Vemos que a medida que vamos escribiendo en el campo de texto se va actualizando la etiqueta automáticamente.

Recomiendo que pruebes los tutoriales de knockout.js [http://learn.knockoutjs.com/](http://learn.knockoutjs.com/). Están bastante trabajador y tienen una web donde puedes ir probando el código en la página web directamente y viendo los resultados.


## Ejemplo más completo

El ejemplo simple fue únicamente para probar la funcionalidad básica de la aplicación. Este ejemplo es un caso más real: crearemos una tabla de Posts, donde mostraremos el título y el texto. Le añadiremos a la tabla filtrado y edición inline. Con esto veremos cómo utilizar variables observables, dependentObservables y observableArrays. También haremos uso de jquery tmpl para renderizar la vista.

<iframe style="width: 100%; height: 300px" src="http://jsfiddle.net/9mSBY/embedded/"></iframe>

### Esqueleto de la página

Enlazamos los css y js que vamos a utilizar:

* Twitter Bootstrap (Para el aspecto de la página)
* jQuery
* jQuery tmpl
* knockout.js


{% highlight html linenos %}
<!doctype html>
<html>
<head>
    <title></title>

    <link rel="stylesheet" href="http://twitter.github.com/bootstrap/1.3.0/bootstrap.min.css">

    <script type="text/javascript " src="https://ajax.googleapis.com/ajax/libs/jquery/1.6.4/jquery.min.js"></script>
    <script type="text/javascript" src="http://cloud.github.com/downloads/SteveSanderson/knockout/jquery.tmpl.min.js"></script>
    <script type="text/javascript" src="http://cloud.github.com/downloads/SteveSanderson/knockout/knockout-1.2.1.js"></script>
</head>
<body>
</body>
</html>
{% endhighlight %}

### Tabla

Añadimos la tabla donde mostraremos el listado de Post en 3 columnas. La primera de selección, la segunda el título y luego el texto.


{% highlight html linenos %}
<div class="container">
    <table>
        <thead>
        <tr>
            <th></th>
            <th>Title</th>
            <th>Text</th>
        </tr>
        </thead>
        <tbody />
    </table>
</div>
{% endhighlight %}

### ViewModel

El viewModel contará con un listado de post que se mostrarán en la tabla

{% highlight javascript linenos %}
<script>
     function containsIgnoreCase(s, q){
        return s.toLowerCase().indexOf(q.toLowerCase()) != -1;
     }

     function Post(title, text){
        this.title = ko.observable(title);
        this.text = ko.observable(text);
        this.selected = ko.observable(false);

        this.containsText = function(query){
            return containsIgnoreCase(this.title(), query)
                || containsIgnoreCase(this.text(), query);
        }
     }

     var viewModel = {
        posts : ko.observableArray([])
     }

     viewModel.posts.push(new Post('title1', 'text1'));
     viewModel.posts.push(new Post('title2', 'text2'));

     ko.applyBindings(viewModel);
</script>
{% endhighlight %}

En la clase Post se han definido los 3 campos como variables observables, para que los cambios que se hagan en esas variables se actualicen automáticamente. En el viewModel hemos definido un observableArray con el listado de post. Añadimos a esta lista dos posts de ejemplo.

Para mostrar los posts en la tabla. Creamos una nueva plantilla que mostrará el contenido de cada fila.

{% highlight html linenos %}
<script type="text/html" id="postRow">
    <tr>
        <td><input type="checkbox" data-bind="checked: selected" /></td>
        <td>${title}</td>
        <td>${text}</td>
    </tr>
</script>
{% endhighlight %}

Y añadimos en binding en la tabla para que renderice la plantilla "postRow" con el listado de "posts".

{% highlight html linenos %}
    <tbody data-bind="template : {name : 'postRow', foreach: posts}" />
{% endhighlight %}

Con este código ya tenemos sincronizada la tabla con la lista de Posts. Para hacer una prueba podemos abrir la consola javascript y añadir un nuevos post.

{% highlight html linenos %}
    viewModel.posts.push(new Post('prueba', 'desde la consola de javascript!');
{% endhighlight %}

La tabla se tiene que haber actualizado automáticamanete con la nueva fila añadida.


### Filtrado

Para añadir filtrado de la tabla vamos a almacenar dos arrays, uno con la lista de posts completos y otro con la lista de posts filtrados.

Añadimos un campo de texto donde vamos a escribir el filtro. El evento por defecto que actualiza el viewModel es cuando pierde foco. Para hacer un filtrado en tiempo real podemos cambiar el evento de actualización a 'afterkeydown'.

{% highlight html linenos %}
<div class="container">
    ...

    <form>
    <div class="clearfix">
        <input class="xxlarge" type="text" data-bind="value : filterQuery, valueUpdate:'afterkeydown'" placeholder="Filter"/>
    </div>
    </form>
</div>
{% endhighlight %}


Modificamos el viewModel para añadir el texto por el que se filtra y la lista de posts filtrados. Definimos la variable filteredPost como dependentObservable, de esta forma, cada vez que se actualice la lista de post, se volverá a evaluar la lista de post filtrados. En el caso de que el filtro esté vacio, mostramos la lista de todos los posts.

{% highlight javascript linenos %}
var viewModel = {
    posts : ko.observableArray([]),
    filterQuery : ko.observable('')
}

viewModel.filteredPosts = ko.dependentObservable(function(){
    var query = this.filterQuery();
    if(query){
        var filtered = [];
        $.each(viewModel.posts(), function(i, post){
            if(post.containsText(query)){
                filtered.push(post);
            }
        });
        return filtered;
    }
    //Not filtering
    return viewModel.posts();
}, viewModel);
{% endhighlight %}

Modificamos el binding de la tabla para mostrar la lista de posts filtrados.

{% highlight html linenos %}
    <tbody data-bind="template : {name : 'postRow', foreach: filteredPosts}" />
{% endhighlight %}


### Edición inline

Para permitir la edición inline vamos a añadir una nueva variable que nos indique si estamos en modo edición. En el modo de edición, en la tabla aparecerán campos de texto donde el usuario podrá modificar las filas. También añadiremos un botón de nuevo y un botón para borrar las filas seleccionados.

Botones para realizar las acciones.

{% highlight html linenos %}
<div class="container">
    ...

    <form>
    <div class="clearfix">
            <a href="#" class="btn primary" data-bind="click : newPost">New post</a>
            <a href="#" class="btn primary" data-bind="visible: editMode, click: toggleEditMode">Save</a>
            <a href="#" class="btn" data-bind="visible: !editMode(), click : toggleEditMode">Edit</a>
            <a href="#" class="btn danger" data-bind="click : deletePosts">Delete</a>
    </div>
     ...

{% endhighlight %}

La plantilla muestra el texto o un input dependiendo del campo editMode.

{% highlight javascript linenos %}
<script type="text/html" id="postRow">
    <tr>
        <td><input type="checkbox" data-bind="checked: selected" /></td>
        {{if viewModel.editMode()}}
            <td><input type="text" data-bind="value : title" /></td>
            <td><input type="text" data-bind="value : text" /></td>
        {{else}}
            <td>${title}</td>
            <td>${text}</td>
        {{/if}}
    </tr>
</script>
{% endhighlight %}


Añadimos las acciones al modelo.

{% highlight javascript linenos %}
var viewModel = {
    posts : ko.observableArray([]),
    filterQuery : ko.observable(''),
    editMode : ko.observable(false)
}

viewModel.selectedPosts = ko.dependentObservable(function(){
    var result = [];
    $.each(this.posts(), function(i, post){
        if(post.selected()){
            result.push(post);
        }
    });
    return result;
}, viewModel);

viewModel.toggleEditMode = function(){
    viewModel.editMode(!viewModel.editMode());
}

viewModel.newPost = function(){
    viewModel.posts.push(new Post('',''));
    viewModel.editMode(true);
}

viewModel.deletePosts = function(){
    viewModel.posts.removeAll(viewModel.selectedPosts());
}

{% endhighlight %}


Declarando la variable selectedPosts como dependentObservable nos aseguramos que esté sincronizada con la lista de posts. A la hora de borrar los posts de la lista utilizamos la función removeAll y le pasamos la lista completa de posts seleccionados.

### Conclusiones

En el ejemplo hemos visto como podemos utilizar knockout.js para tener sincronizada la interfaz de usuario con el modelo. Haciendo uso de variables dependientes y bindings hemos conseguido darle comportamiento dinámico a una tabla estática. En pocas lineas de código hemos conseguido que nuestra tabla sea completamente editable y filtrable.