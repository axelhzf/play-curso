---
layout: default
title : Test con selenium
---

[Selenium](http://seleniumhq.org/) es una librería que te permite probar las interfaces de usuario. Permite automatizar las acciones que puede realiza un usuario en la aplicación, de forma que se puede comprar si la interfaz de usuario se comporta correctamente.

## Utilizando Selenium IDE

Selenium IDE es una extensión de Firefox que te permite escribir test de Selenium y ejecutarlos directamente desde el navegador. Descarga Selenium IDE [http://seleniumhq.org/projects/ide/](http://seleniumhq.org/projects/ide/) y prueba a crear un test en un página. 

## Utilizando los tags de Play

Play trae integración por defecto con selenium por medio del tag #{selenium /}

{% highlight html linenos %}
\#{selenium 'Test security'}
	...Comandos Selenium...
\#{/selenium}
{% endhighlight %}


## Ejercicio

Vamos a crear una página donde pondremos unas pestaña (tabs). La implementación de las pestañas la haremos nosotros a mano utilizando knockout.js por lo tanto es recomendable que creemos unos test para comprobar que nuestra implementación funciona correctamente.

* Crea una página con unos tabs, con los estilos de twitter bootstrap
* Añade el comportambién de pestañas con knockout.js
* Crea un test de selenium, que haga click en los diferentes tabs y compruebe que el contenido que se muestra es el correcto

## Solución

{% highlight html linenos %}
#{extends 'main.html' /} #{set title:'Tabs' /}

Vista de la página con tabs

<ul class="tabs">
	<li data-bind="click : function(){viewModel.selectedTab(1)}, css : { active : viewModel.selectedTab() === 1}">
		<a href="#">Tab1</a>
	</li>
	<li data-bind="click : function(){viewModel.selectedTab(2)}, css : { active : viewModel.selectedTab() === 2}">
		<a href="#">Tab2</a>
	</li>
	<li data-bind="click : function(){viewModel.selectedTab(3)}, css : { active : viewModel.selectedTab() === 3}">
		<a href="#">Tab3</a>
	</li>
</ul>

<div data-bind="visible : viewModel.selectedTab() === 1">
	Contenido de Tab1
</div>

<div data-bind="visible : viewModel.selectedTab() === 2">
	Contenido de Tab2
</div>

<div data-bind="visible : viewModel.selectedTab() === 3">
	Contenido de Tab3
</div>

<script>
	var viewModel = {
		selectedTab : ko.observable()
	}
	
	ko.applyBindings(viewModel);	
</script>
{% endhighlight %}

Test de selenium

{% highlight html linenos %}
#{selenium 'Testings Tabs!'}
    
    open('/Tabs/tabs')
    click('link=Tab1')
    assertTextPresent('Contenido de Tab1')
    assertTextNotPresent('Contenido de Tab2')
    assertTextNotPresent('Contenido de Tab3')
    
    click('link=Tab2')
    assertTextPresent('Contenido de Tab2')
    assertTextNotPresent('Contenido de Tab1')
    assertTextNotPresent('Contenido de Tab3')
    
    click('link=Tab3')
    assertTextPresent('Contenido de Tab3')
    assertTextNotPresent('Contenido de Tab1')
    assertTextNotPresent('Contenido de Tab2')
    
#{/selenium}
{% endhighlight %}
