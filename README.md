# EntreApp

Tourism application for Erasmus+ in collaboration with Seville's City Council and collaborators in Italy, France and the Basque Country.
Available information here: https://twinspace.etwinning.net/70996/home

Technologies used:

- REST API developed with MongoDB, Mongoose and Node.js
- Web App created with Angular + Angular Material
- Native Java Android App.

## Folder Structure
- Android App can be found in 'MyApplication' folder.
- MEN REST API can be found in 'api' folder.
- Web App can be found in 'frontend-web' folder.

## Descripción detallada del sistema
---
VisitOn es una aplicación de turismo para estudiante ERASMUS+, en la que podremos encontrar información de los diferentes puntos de interés de sevilla para así visitarlos y podernos informar sobre ellos a través de los textos y audioguías que hay en la aplicación en varios idiomas. En el siguiente punto procederemos a explicar las diferente funcionalidades de la aplicación:

- Administración de la aplicación a través de Angular: 
    - Inicio Sesión: Se deberá iniciar sesión con tu correo y contraseña.Puedes iniciar sesión desde cualquier usuario, pero solo podrás      administrar la aplicación con usuarios con el rol de administrador.

    - Puntos de interés: Tendremos una lista que muestre todos los puntos de interés, podremos entrar en cada punto de interés para ver       su detalle junto a sus descripciones y audioguías. Dentro de los detalles podremos editar  todos los datos respectivos a ese punto       de interés, tanto las fotos (Que podremos tanto subirlas como borrarlas, cada POI tendrá tres fotos como máximo),
      como las audioguías(que podremos borrarlas y subir otras nuevas, pudiendo incluso seleccionar el idioma en el que está), como la   
      descripción del texto en los diferentes idiomas disponibles en la aplicación y los datos sobre el monumentos como lo son sus 
      coordenadas, precio, horario …., y borrar el punto de interés si estamos con rol de administrador.
      Podemos valorar los puntos de interés

    - Usuarios: Podemos acceder a una lista de usuario, en dicha lista podemos buscar un usuario, crear un usuario, borrar un usuario,         editar un usuario y acceder a las imágenes que cada usuario individualmente ha subido de cada POI con el fin de poder moderar           estas misma y poder marcar como inapropiada cualquier fotos  que haya subido el usuario, para que a los quince días sea borrada 
      automáticamente y el usuario le de tiempo de volver a descargarla desde la aplicación android para conservarla.

     - Medallas: Podemos acceder a la lista de Medallas, en dicha lista podemos buscar medallas, crear una medalla(Al crear la medalla          debes introducir su nombre, los puntos que otorga, una descripción, una imagen, y los puntos de interés a los que está      
       relacionado), borrar   una medalla y editar una medalla para añadirle nuevos puntos de interés a la misma.

     - Categorías: Podemos acceder a la lista de Categorias, en dicha lista podemos buscar una categoría, crear una categoría, borrar          una categoría y editar una categoría. Las categorías se utilizan para añadirlas a un punto de interés y poder visualizarlas              dentro del mismo para saber a qué sección pertenece..

- Aplicación cliente en Android:

    - Inicio de sesión: Tenemos la posibilidad de iniciar sesión con nuestra cuenta de usuario, también tenemos la posibilidad de    
      registrarnos desde este menú con un correo, nuestra contraseña y el idioma para tu usuario.

    - Medallas: Tenemos disponibles una lista de medallas que nos muestran su foto, su nombre, una descripción y los puntos que otorga,       podemos filtrar las insignias por conseguidas. En la lista tenemos la posibilidad de buscar una medalla por su nombre y de ordenar       las medallas alfabéticamente de forma descendente y ascendente Podemos entrar en los detalles de las medallas y podemos ver los 
      puntos de interés que son necesarios para desbloquear dicha medalla.

    - Usuarios: Tenemos disponible una lista de usuarios en la que podemos ver los diferentes usuarios y entrar en los detalles de los         mismos.

    - Puntos de interés: Podemos visualizar la lista de los puntos de interés, en la list podemos visualizar la foto del punto, podemos       ver el precio, el nombre y tenemos la posibilidad de añadirlo a lista de favoritos del usuario. Podemos filtrar la lista por los   
      monumentos visitados y los monumentos favoritos. Desde la lista de puntos puede abrir el escáner QR. Dentro de los detalles del         punto podemos ver la descripción del punto en el idioma del usuario y en caso de que no haya en ese idioma se mostrará en español,       dentro del detalle tenemos la posibilidad de escuchar las audioguía, entrar en el escaneador de códigos QR y añadir imágenes 
      propias al punto que solo serán visibles para ti, estas imágenes tenemos la posibilidad de descargarlas.También tenemos la   
      posibilidad de valorar el punto de interés, ver la valoración del usuario y ver la media de valoración. Desde la lista de puntos   
      podemos abrir un mapa que nos muestras la localización de los puntos de interés, desde el mapa se puede abrir el escáner QR, puede       filtrar por visitados, favoritos y cercanos, y desde un botón del mapa puedes volver a la lista.
      
## Documento de historias de usuario
---
| ID   |      Historia de usuario      | Prueba  | Esfuerzo  | Asignado a |
|------|:-----------------------------:|:--------:|:---------:|:----------:|
|01    | Como usuario, quiero cambiar mi foto de perfil por una que este en la galeria de mi movil    |  Para comprobar esta funcionalidad, inicaremos sesión como usuario en la app, iremos hacia mi perfil, pulsaremos en la fotos , esta se hara más grande y tendra un icono de editar, el cual pulsandolo nos dejara cambiar la imagen por una de nuestra galeria |     60     |Antonio Durán Falero|
|02  | Como un usuario, quiero subir imagenes de un monumento desde la galería, las cuales solo las va a poder ver el propio usuario y el administrador     |  Para comprobar esta funcionalidad, tendremos que entrar en los detalles de un punto de interés y clickar en el ícono de la cámara en el mismo, en ese dialogo debemos clickar en el icono de la galeria y seleccionar la imagen que quieras, después de eso subirlo la imagen desde el botón post   |     40     |José Miguel Zamorano|
|03    | Como un usuario, quiero poder editar los datos de mi perfil en la aplicación android             |  Para comprobar esta funcionalidad, se inicia sesión como usuario en la app, se va al apartado de 'Mi perfil', y se le dará al botón e editar que aparece en la esquina inferior derecha de la pantalla. Una vez que se haya editado los campos deseados, se le dará al botón de confirmar para hacer efectivos esos cambios. |     40     |Alberto Santiago Rodríguez|
|04    | Como un contribuidor quiero ver unificada la interfaz de angular e impecable      |  Revisar la interfaz de la aplicación web   |     15     |José María Ruíz Laguna|
|05    | Corregir errores de la aplicación de angular y limpiar el código  |  Corregir errores menores   |     20     |José María Ruíz Laguna|
|06    | Como un usuario, quiero subir imagenes de un monumento desde la cámara, las cuales solo las va a poder ver el propio usuario y el administrador   |  Para comprobar esta funcionalidad, tendremos que entrar en los detalles de un punto de interés y clickar en el ícono de la cámara en el mismo, en ese dialogo debemos clickar en el icono de la cámara y hacer la foto, después de eso subirlo la imagen desde el botón post.   |     60     |José Miguel Zamorano|
|07    | Como usuario, quiero poder ver ampliada la foto de mi perfil de una manera nítida   |  para comprobar esta funcionalidad, iremos hacien nuestro perfil y pulsaremos en la foto de perfil está se hara más grande pudiendose ver en perfecta resolución.   |     30     |Antonio Durán Falero|
|08    | Como usuario, quiero cambiar mi foto de perfil por una foto que puede sacar con mi camara en el mismo momento               |  Para comprobar esta funcionalidad, inicaremos sesión como usuario en la app, iremos hacia mi perfil, pulsaremos en la fotos , esta se hara más grande y tendra un icono de editar, el cual pulsandolo nos dejara cambiar la imagen por que podremos sacar al instante   |     60     |Antonio Durán Falero|
|09    | Solucionar sustitución de atributos adicionales en el usuario actual al editar imagenes del usuario y el perfil de usuario          |  Corregir error en el formulario de edición de usuario y en el añadir imagenes a los POI   |     15     |José Miguel Zamorano|



#### ©2020

#### Authors:
- [José María Ruiz Laguna](https://github.com/raykrai)
- Antonio Durán Falero
- Alberto Santiago Rodríguez
- José Miguel Zamorano Rodríguez
- Mario Martinez Sanz
- Jesus Palma Lopez
- Juan Antonio Ortiz Guerra
- Jose Alberto Vazquez Lopez
