# Vitalist - Plataforma de Asistencia Social

![Icono de la app](./app/src/main/ic_launcher-playstore.png)

## 📱 Descripción

Vitalist es una aplicación móvil Android diseñada para conectar a personas que necesitan asistencia con voluntarios dispuestos a ayudar. Esta plataforma facilita la creación y gestión de solicitudes de ayuda, estableciendo un sistema basado en la comunidad donde los usuarios pueden asistir a quienes lo necesitan.

## ✨ Características Principales

- **Sistema de doble rol**: Usuarios pueden registrarse como solicitantes de ayuda o como asistentes voluntarios
- **Creación y gestión de solicitudes**: Interfaz intuitiva para crear, aceptar y completar tareas
- **Sistema de niveles y puntuación**: Los usuarios acumulan puntos por completar tareas y suben de nivel
- **Estadísticas visuales**: Gráficos semanales que muestran la actividad del usuario
- **Perfil personalizado**: Gestión completa del perfil de usuario
- **Botón de emergencia**: Acceso rápido a servicios de emergencia (061)
- **Historial de actividad**: Registro de todas las actividades realizadas

## 🚀 Tecnologías Utilizadas

- **Kotlin** - Lenguaje de programación principal
- **Jetpack Compose** - Framework moderno para UI
- **Firebase Authentication** - Gestión de usuarios, verificación de correo y restablecimiento de contraseña
- **Firestore** - Base de datos NoSQL
- **Material Design 3** - Diseño de interfaz moderno
- **Navigation Compose** - Navegación entre pantallas
- **Compose Charts** - Visualización de datos estadísticos

## 🛠️ Arquitectura

La aplicación sigue una arquitectura MVVM (Model-View-ViewModel) con los siguientes componentes:

- **Modelo**: Clases de datos como `User`, `Request`, `Stats`, y `Activity`
- **Vista**: Pantallas Compose como `HomeScreen`, `ProfileInfoScreen`, `TasksListScreen`, etc.
- **ViewModel**: Lógica de negocio implementada en `FirestoreDataSource`

## 📋 Requisitos

- Android SDK 24+
- Dispositivo Android con versión 7.0 (Nougat) o superior
- Permisos: CALL_PHONE (para la función de emergencia)

## 🏗️ Instalación

1. Clona el repositorio:

```bash
git clone --depth 1 git@github.com:LixFerox/app-tfg-2024.git
```

2. Abre el proyecto con Android Studio
3. Configura tu archivo **google-services.json** para Firebase
4. Ejecuta la aplicación en un emulador o dispositivo físico

## 📱 Pantallas Principales

- **Login/Registro**: Autenticación de usuarios
- **Home**: Pantalla principal con resumen de actividad
- **Búsqueda**: Explorar solicitudes de ayuda disponibles
- **Tareas**: Gestión de tareas aceptadas
- **Estadísticas**: Análisis de actividad y nivel
- **Perfil**: Información y configuración del usuario

## 👥 Roles de Usuario

- **Ayudantes**: Usuarios que ofrecen asistencia y aceptan tareas
- **Solicitantes**: Usuarios que crean solicitudes de ayuda

## 🔗 Enlaces

- Web informativa: http://vitalist.lixferox.es

## 📄 Licencia

Este proyecto está licenciado bajo la Licencia MIT. Consulta el archivo [LICENSE](./LICENSE.md) para más detalles.

## 👨‍💻 Proyecto Académico

Este proyecto fue desarrollado como Trabajo Final de Grado (TFG) para el curso académico 2024-2025.
