DeezerProyecto
Descripción del proyecto
Este proyecto es una aplicación móvil desarrollada en Kotlin para Android que simula una experiencia de plataforma musical tipo Deezer o Spotify. El objetivo principal es permitir al usuario escuchar previews de canciones, gestionar su biblioteca, conectarse con amigos y explorar contenido musical, todo con autenticación y sincronización en la nube mediante Firebase.

Contenido de la publicación
El repositorio incluye los siguientes directorios y archivos:

app/src/main/java/: Contiene el código fuente de la aplicación Android.

app/src/main/res/: Contiene los recursos visuales como layouts XML, imágenes, estilos, etc.

app/src/main/AndroidManifest.xml: Archivo de configuración del entorno Android.

google-services.json: Configuración de Firebase para Android (no incluido por seguridad).

README.md: Este archivo con información detallada sobre el proyecto.

Desarrollo del proyecto
El proyecto fue desarrollado utilizando Kotlin, siguiendo la arquitectura típica de Android con actividades y fragments. Se integró Firebase para la autenticación y almacenamiento en la nube, y se utilizó la API pública de Deezer para mostrar información musical y reproducir previews de 30 segundos. La interfaz gráfica ha sido diseñada con soporte para tema claro/oscuro y navegación fluida mediante BottomNavigationView.

Despliegue
Cómo ejecutar la aplicación
Clonar el repositorio:

bash
Copiar
Editar
git clone https://github.com/rubens122/DAMTFG.git
Abrir el proyecto en Android Studio.

Añadir el archivo google-services.json dentro de app/.

Asegurarse de tener configurado un dispositivo o emulador con cuenta Google activa para probar el inicio de sesión con Google.

Ejecutar el proyecto desde Android Studio.

Construido con
[Kotlin]

[Firebase Authentication]

[Firebase Realtime Database]

[Deezer API]

[Picasso]

[Android Jetpack]

[ConstraintLayout]

Versionado
Este proyecto utiliza el control de versiones proporcionado por Git y está alojado en GitHub.

Autores
Rubén Lozano Sánchez – Desarrollador principal – rubens122

Ismael Morales Montesinos – Colaborador

Licencia
Este proyecto se publica con fines educativos. Para usos comerciales o distribución del contenido musical completo, es necesario cumplir con las licencias de Deezer y Firebase.

Recursos adicionales
Repositorio en GitHub
