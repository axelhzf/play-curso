---
layout: default
title: Simulación de properties
---

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

<div class="alert-message info">
Entiéndase AntiJava como una persona que ha probado
lenguajes de programación como Ruby, Javascript y a los que Java le parece un lenguaje muy verbose.
</div>


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

## Properties PlayStyle

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

## Probando la creación automática de properties con JUnit

Vamos a crear un proyecto de prueba con un test de JUnit donde probaremos la creación automática de properties. Así de paso aprenderemos como crear test unitarios utilizando play.

	play new bytecode
	cd bytecode
	play eclipsify
	
Importa el proyecto desde eclipse.

Crea la clase Entidad en app/models/Entidad.java

	package models;

	public class Entidad {
		public String nombre;
	}

Crea un nuevo test en test/BytecodeTest.java

	import models.Entidad;
	import org.junit.Assert;
	import org.junit.Test;
	import play.test.UnitTest;

	public class BytecodeTest extends UnitTest {

		@Test
		public void bytecodetest() {
			Entidad e = new Entidad();
			e.nombre = "Play!";
			Assert.assertEquals("Play!", e.nombre);
		}

	}

## ¿Cómo ejecutar un test?

### Desde Eclipse

Botón derecho en la clase de los test Run As/JUnit Test

### Desde linea de comando

	play test
	
Accede a [http://localhost:9000/@tests](http://localhost:9000/@tests)
Verás una web donde puede seleccionar el test que quieres ejecutar y pulsar Start! para ejecutar el test. En el caso de que el test no se pasara, mostraría la información del error en el navegador directamente.

### Desde línea de comando de forma automática

	play auto-test
	
Simula la ejecución de un "play test". Abriendo un navegador y ejecutando todos los test automáticamente. Muy útil para realizar [integración continua](http://en.wikipedia.org/wiki/Continuous_integration).

## Modificando el test

Es un test bastante sencillo estamos probando la asignación de una variable pública y comprobando que tiene el valor adecuado. Play está haciendo su magia, pero todavía no nos hemos dado cuenta. Vamos a hacer algunos cambios para poder verla.

Modificamos el archivo app/models/Entidad.java

	package models;

	public class Entidad {
		public String nombre;
	
		public String getNombre(){
			return "Getter interceptado!";
		}
	}

Volvemos a ejecutar el test y vemos como el test ahora falla:

	org.junit.ComparisonFailure: expected:<[Play]!> but was:<[Getter interceptado]!>
		at org.junit.Assert.assertEquals(Assert.java:123)
		at org.junit.Assert.assertEquals(Assert.java:145)
		at BytecodeTest.bytecodetest(BytecodeTest.java:12)
		...

**¿Qué está pasando?**

Cuando hacemos la llamada a "entidad.nombre", play la está sustituyendo, en tiempo de compilación por una llamada a "entidad.getNombre()".


<div class="alert-message warning">
	Puedes descargar el proyecto completo desde <a href="https://github.com/axelhzf/play-curso/tree/master/bytecode">https://github.com/axelhzf/play-curso/tree/master/bytecode</a>
	
</div>	

## ¿Cómo hace esto play?

Básicamente lo que hace Play es utilizar la libería [Javassist](http://www.csg.is.titech.ac.jp/~chiba/javassist/) para realizar modificaciones en el bytecode de nuestra clase compilada.

Vamos a ver por encima cómo lo hace

[https://github.com/playframework/play/blob/master/framework/src/play/classloading/enhancers/PropertiesEnhancer.java](https://github.com/playframework/play/blob/master/framework/src/play/classloading/enhancers/PropertiesEnhancer.java)



