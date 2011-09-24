---
layout: default
title: Búsquedas con Elastic Search
---

Vamos a añadir a nuestra página capacidades de buscas contenido en los tweets de los usuarios. Podríamos hacerlo mediante una consulta en la base de datos. Esto funcionaría bien con pocos tweets. Pero cuando el número de tweets creciera no tendríamos un buen rendimiento, tenemos que indexar el contenido de los tweets para poder hacer búsquedas mucho mas eficientes.

Para añadir esta funcionalidad vamos a utilizar el módulo [Elastic Search](http://www.playframework.org/modules/elasticsearch-0.2/home) creado por [@_felipera](http://twitter.com/#!/_felipera).

Lee la documentación del módulo pasar saber cómo instalarlo

[http://www.playframework.org/modules/elasticsearch-0.2/home](http://www.playframework.org/modules/elasticsearch-0.2/home) 

<div class="alert-message block-message">
Cuanto instalé el módulo no me descargó correctamente las dependencias. Tuve que modificar mi fichero de dependencias y añadirlas a mano.

{% highlight bash linenos %}
# Application dependencies

require:
    - play
    - play -> secure
    - net.sf.flexjson -> flexjson 2.1
    - play -> elasticsearch 0.1
    - org.elasticsearch -> elasticsearch 0.16.2
    - se.scalablesolutions.akka -> akka-amqp 1.1.2
    
    
repositories:
    - elasticsearch:
        type: iBiblio
        root: "http://oss.sonatype.org/content/repositories/releases/"
        contains:
            - org.elasticsearch -> *
            
    - akka:
        type: iBiblio
        root: "http://akka.io/repository/"
        contains:
            - se.scalablesolutions.akka -> *

{% endhighlight %}
</div>

Cuando tengamos el módulo instalado. Lo único que debemos hacer para habilitar las búsquedas es anotar nuestra clase.

apps/models/Tweet.java
{% highlight java linenos %}
@ElasticSearchable
@Entity
public class Tweet extends Model {

	@Required
	@MaxLength(140)
	public String msg;

	@ElasticSearchEmbedded(fields={"username"})
	@ManyToOne
	public User author;

	public Date date;

	public static Tweet create(String msg, User author){
		Tweet t = new Tweet();
		t.msg = msg;
		t.author = author;
		t.date = new Date();
		return t;
	}	
}
{% endhighlight %}

<div class="alert-message error">
Con el constructor que teníamos creado anteriormente, la búsqueda daba problemas. Por eso es mejor transformar el constructor en un método estático create que realice la misma acción. Recuerda cambiar todas las referencias al constructor que teníamos.
</div>

Creamos un método en el controlador que se encargue de hacer la búsqueda.

app/controllers/Timeline.java

{% highlight java linenos %}
public static void search(String query){
	if(query != null && !query.isEmpty()){
		SearchResults<Tweet> tweets = ElasticSearch.search(QueryBuilders.fieldQuery("msg", query), Tweet.class);
		render(tweets, query);
	}
	render();
}
{% endhighlight %}

Creamos la vista

app/views/Timeline/search.html

{% highlight html linenos %}
#{form @Timeline.search()}
	<input type="text" name="query" value="${query}" class="xxlarge"/>
	<input type="submit" class="btn primary" value="Buscar" />
#{/form}


#{if tweets}
<h3>Encontrados ${tweets.totalCount} tweets para la consulta '${query}'</h3>
<table>
	<thead>
		<tr>
			<th>id</th>
			<th>Tweet</th>
		</tr>
	</thead>
	<tbody>
		#{list items:tweets.objects, as:'tweet'}
		<tr>
			<td>${tweet.id}</td>
			<td>${tweet.msg}</td>	
		</tr>		
		#{/list}
	</tbody>
</table>
#{/if}
{% endhighlight %}


<div class="alert-message warning">
Puedes descarga el código desde <a href="https://github.com/axelhzf/play-curso/commit/9836828e398fdf21e1880747a30692726b6460e5">https://github.com/axelhzf/play-curso/commit/9836828e398fdf21e1880747a30692726b6460e5</a>
</div>


## Ejercicio

Implementar la búsqueda en tiempo real.

* Crear un nuevo método en el API que permita realizar la búsqueda
* A medida que el usuario va escribiendo en la caja de texto, realiza una nueva consulta

## Solución

app/controllers/Api.java

{% highlight java linenos %}
public static void search(String query){
	ApiResponse resp = new ApiResponse();		
	if(query != null && !query.isEmpty()){
		resp.status = "OK";
		SearchResults<Tweet> tweets = ElasticSearch.search(QueryBuilders.prefixQuery("msg", query), Tweet.class);
		resp.result = tweets.objects;
	}else{
		resp.status = "ERROR";
		resp.message = "Empty query";
	}
	
	renderJSON(new JSONSerializer().include("status", "message", "result.msg", "result.id").exclude("*").serialize(resp));
}
{% endhighlight %}


app/views/search.html

{% highlight html linenos %}
#{form @Timeline.search()}
	<input type="text" name="query" value="${query}" class="xxlarge" placeholder="Busqueda" data-bind="value:query, valueUpdate:'afterkeydown'"/>
#{/form}

<table>
	<thead>
		<tr>
			<th>id</th>
			<th>Tweet</th>
		</tr>
	</thead>
	<tbody data-bind="template:{name : 'tweet-row', foreach:tweets}">

	</tbody>
</table>


<script type="text/html" id="tweet-row">
		<tr>
			<td>{{= id}}</td>
			<td>{{= msg}}</td>	
		</tr>
</script>

#{set 'endScript'}
<script>
	viewModel = {
		query : ko.observable('${query}'),
		tweets : ko.observableArray([])
	}
	
	function Tweet(id, msg){
		this.id = id;
		this.msg = msg;
	}
	
	ko.dependentObservable(function() {
    	if (this.lastQueryRequest) this.lastQueryRequest.abort();
		var action = #{jsAction @Api.search(':query')/}
    	this.lastQueryRequest = $.get(action({query : this.query()}), function(data){
    		if(data.status === 'OK'){
    			var tweets = $.map(data.result, function(item){return new Tweet(item.id, item.msg);});
    			viewModel.tweets(tweets);		
    		}
    	});
	}, viewModel);
	
	ko.applyBindings(viewModel);
</script>
#{/set}
{% endhighlight %}


<div class="alert-message warning">
Puedes bajarte la solución desde <a href="https://github.com/axelhzf/play-curso/commit/282c8e2f80168db994415db60121bddf1e6badc4">https://github.com/axelhzf/play-curso/commit/282c8e2f80168db994415db60121bddf1e6badc4</a>
</div>