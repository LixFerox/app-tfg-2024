package com.lixferox.app_tfg_2024.model

import com.google.firebase.Timestamp

// FILAS DE CADA TABLA DE LA BASE DE DATOS.

/**
 * DATOS QUE TIENEN LOS USUARIOS ALMACENADOS EN FIRESTORE.
 *
 * @property uid ID ÚNICO DE CADA USUARIO.
 * @property email CORREO ELECTRÓNICO DEL USUARIO.
 * @property username NOMBRE DEL USUARIO.
 * @property birth FECHA DE NACIMIENTO DEL USUARIO.
 * @property phone NÚMERO DE TELÉFONO DEL USUARIO.
 * @property address DIRECCIÓN DEL USUARIO.
 * @property isHelper INDENTIFICA SI UN USUARIO ES AYUDANTE O ANCIANO.
 * @property dni DNI DEL USUARIO.
 * @property image IMAGEN DE PERFIL DEL USUARIO.
 * @property dniImage IMAGEN DEL DNI PARA VALIDARLA.
 * @property isValid COMPRUEBA SI SE HA VALIDADO LA CUENTA DE USUARIO.
 * @property type COMPRUEBA SI ES ADMINISTRADOR O UN USUARIO.
 * */

data class User(
    val uid: String,
    val email: String,
    val username: String,
    val birth: Timestamp,
    val phone: String,
    val address: String,
    val isHelper: Boolean,
    val dni: String,
    val image: String,
    val dniImage: String,
    val isValid: Boolean,
    var type: String
)

/**
 * SOLICITUDES QUE SE CREAN POR LOS USUARIOS.
 *
 * @property id ID ÚNICO DE CADA SOLICITUD.
 * @property uidOlder UID DEL USUARIO CON EL ROL DE ANCIANO.
 * @property uidHelper UID DEL USUARIO CON EL ROL DE AYUDANTE.
 * @property title TÍTULO DE LA SOLICITUD.
 * @property description DESCRIPCIÓN DE LA SOLICITUD.
 * @property urgency URGENCIA DE LA SOLICITUD.
 * @property olderUsername NOMBRE DEL USUARIO CON EL ROL DE ANCIANO.
 * @property helperUsername NOMBRE DEL USUARIO CON EL ROL DE AYUDANTE.
 * @property olderAddress DIRECCIÓN DEL USUARIO CON EL ROL DE ANCIANO.
 * @property helperAddress DIRECCIÓN DEL USUARIO CON EL ROL DE AYUDANTE.
 * @property olderPhone TELÉFONO DEL USUARIO CON EL ROL DE ANCIANO.
 * @property helperPhone TELÉFONO DEL USUARIO CON EL ROL DE AYUDANTE.
 * @property acceptedByUid UID DEL USUARIO QUE ACEPTA LA SOLICITUD.
 * @property createdByUid UID DEL USUARIO QUE CREA LA SOLICITUD.
 * @property dateCreated FECHA EN LA QUE SE HA CREADO LA SOLICITUD.
 * @property status ESTADO DE LA SOLICITUD.
 * */

data class Request(
    val id: String,
    val uidOlder: String?,
    val uidHelper: String?,
    val title: String,
    val description: String,
    val urgency: String?,
    val olderUsername: String?,
    val helperUsername: String?,
    val olderAddress: String?,
    val helperAddress: String?,
    val olderPhone: String,
    val helperPhone: String,
    val acceptedByUid: String?,
    val createdByUid: String,
    val dateCreated: Timestamp,
    val status: String
)

/**
 * ESTADÍSTICAS DE LOS USUARIOS.
 *
 * @property uid ID ÚNICO DE CADA USUARIO.
 * @property level NIVEL DEL USUARIO.
 * @property points PUNTOS ACUMULADOS DEL USUARIO.
 * @property ratingPoints PUNTOS DE VALORACIÓN DEL USAURIO TOTALES.
 * @property totalCompletedTasks NÚMERO TOTAL DE SOLICITUDES COMPLETADAS.
 * @property weekCompletedTasks NÚMERO TOTAL DE SOLICITUDES COMPLETADAS POR DÍA EN LA SEMANA.
 * @property tasksInProgress NÚMERO DE TAREAS EM PROGRESO.
 * @property puntuation PUNTUACIÓN PROMEDIO.
 * @property joinedIn FECHA EN LA QUE EL USUARIO SE HA CREADO LA CUENTA.
 * @property resetWeekValues INDICA SI SE HAN REINICIADO LOS VALORES SEMANALES.
 * */

data class Stats(
    val uid: String,
    val level: Int,
    val points: Int,
    val ratingPoints: Long,
    val totalCompletedTasks: Int,
    val weekCompletedTasks: List<Double>,
    val tasksInProgress: Int,
    val puntuation: Double,
    val joinedIn: Timestamp,
    val resetWeekValues: Boolean
)

/**
 * REGISTRA LA ACTIVIDAD DEL USUARIO EN LA APLICACIÓN.
 *
 * @property uid ID ÚNICO DE CADA USUARIO.
 * @property time FECHA EN LA QUE SE HA REALIZADO LA ACCIÓN.
 * @property title TÍTULO DE LA ACTIVIDAD.
 * @property description DESCRIPCIÓN DE LA ACTIVIDAD.
 * */

data class Activity(
    val uid: String,
    val time: Timestamp,
    val title: String,
    val description: String
)