Caja de Herramientas para Cacao Android
===============
> Aprendiendo e innovando sobre el cacao en sistemas agroforestales

La Caja de Herramientas para Cacao: Aprendiendo e Innovando sobre el Manejo Sostenible del Cultuvo de Cacao en Sistemas Agroforestales se compone de 10 guías prácticas para pequeños productores de cacao y sus cooperativas en la región de América Latina.

## Informacion

El projecto Caja de Herramientas para Cacao Android esta elaborado usando el lenguaje de programacion [Java](http://www.java.com/es/about/) para el sistema operativo [Android](http://www.android.com/), este proyecto hace uso de la libreria [Retrofit](http://square.github.io/retrofit/) para poder obtener las guias de la version web usando una API provista por la version web, la cual fue creada usando [Django Rest Framework](http://www.django-rest-framework.org/).

## Instalacion

* Java 1.7
* Android Studio 1.1
* Retrofit 1.9

### Instalando Java

Por defecto Java viene instalado en tu sistema operativo revisa la version en tu sistema ejecutando el siguiente comando, el cual te retornara la version de Java que tienes instalada actualmente

	java -version

En caso de que no tengas instalado Java en tu sistema operativo puedes [dirigirte a su pagina para descargarlo](http://www.java.com/es/download/) y seguir los pasos necesarios durante la instalacion.

### Instalando Android Studio

Android Studio es solo el IDE que se utiliza para el desarrollo de aplicaciones en Android, puedes descargarlo para tu sistema operativo desde el siguiente [enlace.](https://developer.android.com/sdk/index.html)

En el momento que descargas Android Studio este trae por defecto:

* Android SDK
* Android NDK
* Android AVD Manager
* Gradle

#### Nota

Debes de tomar en cuenta las especificaciones necesarias para cada sistema operativo y asi poder tener un entorno de desarrollo optimo, puedes revisar las especificaciones en este [enlace.](https://developer.android.com/sdk/index.html#Requirements)

### Instalacion de Retrofit

Retrofit es solo una dependencia del proyecto, por lo cual no necesitas tenerlo instalado en tu entorno el proyecto trae por defecto dentro de sus dependencias esta libreria. Puedes obtener mas informacion sobre retrofit en este [enlace.](http://square.github.io/retrofit/)

## Configuracion

Una ves que todas las dependencias esten cumplidas, asegurate de tener todo actulizado y seigue los siguiente pasos:

* Abre la aplicacion de Android Studio.
* Selecciona la direccion donde tengas alojado el codigo fuente de la aplicacion.
* Sincroniza las dependencias del proyecto.
* Crea un Android Virtual Device desde el AVD Manager.
    * Si deseas probar en un dispositivo fisico asegurate de tener activado las opciones de desarrollador.
* Ejecuta el proyecto y espera a que este compile exitosamente.

### Nota

Asegurate de tener una conexion a internet disponible para poder descargar las guias de cacao en tu dispositivo Android, de lo contrario la aplicacion no mostrara la informacion contenida en la API.

Revisa que tengas instalado Google Play Services en su version mas actual.


## Contribucion

Si quieres contribuir a este proyecto, por favor, lea el archivo de contribuyentes y realice los siguientes pasos

    # Fork this repository
    # Clone your fork

    git checkout -b feature_branch
    # Implement your feature and tests
    git add . && git commit
    git push -u origin feature_branch
    # Send a pull request for your feature branch

## Licencia

Caja de Herramientas para Cacao: Aprendiendo e Innovando sobre el Manejo Sostenible del Cultivo de Cacao en Sistemas Agroforestales por Lutheran World Relief se distribuye bajo una [Licencia Creative Commons Atribución-NoComercial-CompartirIgual 4.0 Internacional.](http://creativecommons.org/licenses/by-nc-sa/4.0/deed.es)

El código de la aplicación Cacao Móvil y su versión web han sido liberados bajo Licencia Pública General de GNU versión 3 (GPLv3)
