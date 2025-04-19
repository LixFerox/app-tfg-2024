package com.lixferox.app_tfg_2024.model

import com.google.firebase.Timestamp

// FILAS DE CADA TABLA DE LA BASE DE DATOS

data class User(
    val uid: String,
    val email: String,
    val username: String,
    val birth: Timestamp,
    val phone: String,
    val address: String,
    val isHelper: Boolean,
    val dni: String,
    val image: String
)

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

data class Activity(
    val uid: String,
    val time: Timestamp,
    val title: String,
    val description: String
)