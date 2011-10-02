---
layout : default
title: ¿Dónde están los servlets?
---

## ¿Qué es un servlet?

Un servlet es un objeto que se ejecuta en un servidor o contenedor JEE, especialmente diseñado para ofrecer contenido dinámico desde un servidor web. Forman parte de JEE (Java Enterprise Edition), que es una ampliación de JSE (Java Standard Edition).

	public class RecibirParametros extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
		
		protected void doGet(HttpServletRequest request, HttpServletResponse response){
		...
		}
		
		protected void doPost(HttpServletRequest request, HttpServletResponse response){
		...
		}
	}


Esta es la base de todos los frameworks de desarollo de aplicaciones web para Java. Bueno, de todos no, Play no utiliza servlets.

## ¿Por qué Play no los utiliza?

[http://iam.guillaume.bort.fr/post/558830013/why-there-is-no-servlets-in-play](http://iam.guillaume.bort.fr/post/558830013/why-there-is-no-servlets-in-play)

Resumen:

   * La primera versión de Play utilizaba servlets
   * Es una opción que a algunos les parece bien y a otros mal
   * Dos de los aspectos más importantes de Play: **stateless** y **auto-magically reload code changes**
   * La única forma de poder realizar de forma sencilla la recarga de código es utilizar una arquitectura sin estado. En caso contrario tendrías que recuperar el estado de la memoria antes de actualizar los cambios.
   * La API de los servlets mala. La gente no la utiliza por gusto, la utiliza para reutilizar el código ya existente.
   * Puedes crear una aplicación stateless utilizando servlets, pero desde el momento que quieras reutilizar algun filtro o librería, esta puede que haga uso de variables de sesión.
   * Los contenedores de servlets utilizan un hilo por petición. Es la mejor opción para ajax y websockets?