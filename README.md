# DeezerProyecto

## Descripción del proyecto

Este proyecto es una aplicación móvil desarrollada en Kotlin para Android que simula una experiencia de plataforma musical tipo Deezer o Spotify. El objetivo principal es permitir al usuario escuchar previews de canciones, gestionar su biblioteca, conectarse con amigos y explorar contenido musical, todo con autenticación y sincronización en la nube mediante Firebase.

## Contenido de la publicación

El repositorio incluye los siguientes directorios y archivos:

- `app/src/main/java/`: Contiene el código fuente de la aplicación Android.
- `app/src/main/res/`: Contiene los recursos visuales como layouts XML, imágenes, estilos, etc.
- `app/src/main/AndroidManifest.xml`: Archivo de configuración del entorno Android.
- `google-services.json`: Configuración de Firebase para Android (no incluido por seguridad).
- `README.md`: Este archivo con información detallada sobre el proyecto.

## Desarrollo del proyecto

El proyecto fue desarrollado utilizando Kotlin, siguiendo la arquitectura típica de Android con actividades y fragments. Se integró Firebase para la autenticación y almacenamiento en la nube, y se utilizó la API pública de Deezer para mostrar información musical y reproducir canciones. La interfaz gráfica ha sido diseñada con soporte para tema claro/oscuro y navegación fluida mediante `BottomNavigationView`.

![image](https://github.com/user-attachments/assets/5e740cb2-38ac-462f-b326-e6cbf91edb9f)

## Despliegue

### Cómo ejecutar la aplicación

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/rubens122/DAMTFG.git
   ```

2. Abrir el proyecto en Android Studio.

3. Añadir el archivo `google-services.json` dentro de `app/`.

4. Asegurarse de tener configurado un dispositivo o emulador con cuenta Google activa para probar el inicio de sesión con Google.

5. Ejecutar el proyecto desde Android Studio.

## Construido con

- [Kotlin]
- [Firebase Authentication]
- [Firebase Realtime Database]
- [Deezer API]
- [Picasso]
- [Android Jetpack]
- [ConstraintLayout]

## Versionado

Este proyecto utiliza el control de versiones proporcionado por Git y está alojado en GitHub.
# Firebase Gestor

**Firebase Gestor** es una aplicación de escritorio desarrollada en **Java** utilizando **JavaFX** que permite gestionar usuarios, sus playlists, canciones y amigos, conectándose en tiempo real a **Firebase Realtime Database**.

## Funcionalidades

- Sistema de login seguro basado en credenciales externas definidas en un archivo `login.properties`.
- Operaciones **CRUD** sobre los usuarios.
- Visualización y edición de **playlists** (nombre, descripción, privacidad).
- Visualización de las **canciones** asociadas a cada playlist.
- Gestión de **amigos**: visualización y eliminación de amigos vinculados a un usuario.
  
### Cómo ejecutar la aplicación

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/rubens122/DAMTFG.git
   ```

2. Abrir el proyecto en FireBase_Gestor-main en Intel IJ.

3. Añadir el archivo google-services.json dentro de resources/ con el nombre proyectofinaldam.json.

4. Ejecutar el proyecto desde el menú lateral de Maven/Plugins/javafx/javafx:run.

## Construido con

- Java
- JavaFX
- Firebase Realtime Database
  
### Mejores prácticas aplicadas:

- Estandarización de títulos
- Separación clara entre proyecto Android y JavaFX
- Notas de seguridad (`google-services.json`)

## Autores

- **Rubén Lozano Sánchez** - *Desarrollador principal* - [rubens122](https://github.com/rubens122)
- **Ismael Morales Montesinos** *Desarrollador principal* - [ismaelmmm102](https://github.com/ismaelmmm102)

## Licencia

Este proyecto se publica con fines educativos. Para usos comerciales o distribución del contenido musical completo, es necesario cumplir con las licencias de Deezer y Firebase.

## Recursos adicionales

- [Repositorio en GitHub](https://github.com/rubens122/DAMTFG)
