---
layout: default
title: Introducción a Git
---

El código completo de los ejercicios resueltos está alojado en github, en el repositorio [https://github.com/axelhzf/play-curso](https://github.com/axelhzf/play-curso)

Todo está hecho sobre el mismo proyecto, de forma incremental. En la pestaña commits puedes consultar el estado después de realizar cada uno de los ejercicios. Puedes ver los detalles del commits para ver que modificaciones se hicieron para resolver el problema.

Si prefieres descargarte el código completo del proyecto, puedes clonar el repositorio completo siguiendo estas instrucciones:

### 1. Instala git

[http://git-scm.com/](http://git-scm.com/)

### 2. Clona el repositorio

	git clone git://github.com/axelhzf/play-curso.git

### 3. Lista el historial

	git logs

o mejor
	git log --oneline

	4eac009 Ejercicio de Selenium Test
	27d48a2 consumo de servicios web
	282c8e2 solución busqueda tiempo real
	9836828 base de búsquedas
	772fe9a Búsqueda
	9645a20 Solución a ejercicio Ajax
	4a9bc29 ajax : enviando formulario
	777fcd1 ejemplo de internacionalización
	4ac2865 ejercicio de validacion
	c089b30 formulario para enviar tweet
	60d3d27 ejercicio de módulo de seguridad
	ea26826 Ejercicio tag de gráfica
	47d94be Añadido ejercicio de Modelo
	b7a16fc proyecto para ver las modificaciones de bytecode
	5763655 Master readme added



### 4. Cambia a un estado anterior

	git checkout <HASH>

Por ejemplo

	git checkout b7a16fc //Estado al añadir proyecto para las modificaciones del bytecode

### 5. Vuelve a la última version

	git checkout master	
		

## Usa git!

Esto es una pequeña introducción a Git explicando lo que te hará falta para poder descargarte el código de los ejercicios y trabajar con él. Si en tu día a día no utilizas un sistema de control de versiones te recomiendo que utilices uno, te puede salvar de muchos problemas. Si utilizas otro sistema de control de versiones como Subversion, te recomiendo que te informes de las ventajas que aporta git. En internet existe mucha documentación y tutoriales sobre git, te recomiendo que para empezar consultes este [http://gitref.org/](http://gitref.org/)

