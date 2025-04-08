package com.lixferox.app_tfg_2024.model

import com.google.firebase.Timestamp

data class User(
    val uid: String,
    val email: String,
    val username: String,
    val birth: Timestamp,
    val phone: String,
    val address: String,
    val isHelper: Boolean,
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
    val dateCreated: Timestamp,
    val status: String
)

data class Stats(
    val uid: String,
    val level: Int,
    val points: Int,
    val totalCompletedTasks: Int,
    val weekCompletedTasks: List<Double>,
    val tasksInProgress: Int,
    val puntuation: Int,
    val joinedIn: Timestamp
)