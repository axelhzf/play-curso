---
layout: default
title: Modelo
---



## Simulación de properties

En Play, cuando estás definiendo el módelo, es normal declarar clases con atributos publicos.
Cualquier desarrollador con la mínima experiencia en Java te dirá que esto es una mala práctica. 
Que deberías declarar esa variable como privada y crearle métodos getter y setter.

{% highlight java linenos %}
public class Entidad {
	private String nombre;
	
	public String getNombre(){
		return nombre;
	}
	
	public void setNombre(String nombre){
		this.nombre = nombre;
	}
}
{% endhighlight %}

## Dialogo entre un ProJava y un AntiJava 
(Entiendase AntiJava como una persona que ha probado
lenguajes de programación como Ruby, Javascript y a los que Java le parece un lenguaje muy verbose).

**ProJava** - Esto está muy bien, es una buena convención. Provee un mayor encapsulamiento 
y produce un código más mantenible. 

**AntiJava** - Pero... ¿no te parece demasiado código para simplemente declarar una variable?
 
**ProJava** - No importa, Eclipse tiene un botón que los genera automáticamente.

**AntiJava** - Pero... ¿a la hora de ver el código no hay demasiado código superfluo que no aporta nada?
¿Qué aporta exactamente ver la definición de un getter y un setter?

**ProJava** - Tienes razon, a veces es un poco engorro. Pero es el precio que hay que pagar
tener un código más mantenible. Si declarases la variable pública ¿que pasaría si quisieras definir
un setter, con alguna lógica antes de realizar la asignación?

**AntiJava** - Bueno, hay una forma de solucionar esto. Hacerlo al modo play.

## Properties Play Style

Play genera automáticamente los getters y los setters por ti. La convención es que todas cualquier campo public, non-static, non-final es visto como una propiedad.
Se crearán automáticamente los getters y los setters.

Por ejemplo, la clase : 

{% highlight java linenos %}
public class Entidad {
	public String nombre;
}
{% endhighlight %}


En realidad, producira el código:

{% highlight java linenos %}
public class Entidad {
	private String nombre;
	
	public String getNombre(){
		return nombre;
	}
	
	public void setNombre(String nombre){
		this.nombre = nombre;
	}
}
{% endhighlight %}

Cuando accedas al campo

{% highlight java linenos %}
Entidad entidad = new Entidad();
entidad.nombre;
{% endhighlight %}

En realidad es como si estuvieras escribiendo
{% highlight java linenos %}
Entidad entidad = new Entidad();
entidad.getNombre();
{% endhighlight %}
 

No te lo crees? Vamos a hacer una prueba.

