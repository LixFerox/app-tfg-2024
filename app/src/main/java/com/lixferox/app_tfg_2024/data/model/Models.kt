package com.lixferox.app_tfg_2024.data.model

import kotlinx.serialization.Serializable

// TABLAS DE LA BASE DE DATOS

@Serializable
object Tables {
    const val users = "users"
    const val requests = "requests"
    const val stats = "stats"
    const val activity = "activity"
}