package com.lixferox.app_tfg_2024.data.model

import kotlinx.serialization.Serializable

/**
 * CONTIENE LOS NOMBRES DE LAS TABLAS DE LA BASE DE DATOS.
 *
 * @property users TABLA QUE CONTEINE LOS USUARIOS.
 * @property requests TABLA QUE CONTIENE LAS SOLICITUDES CREADAS POR LOS USUARIOS.
 * @property stats TABLA QUE CONTEINE LAS ESTADÍSTICAS DE CADA USUARIO.
 * @property activity TABLA QUE CONTIENE LA ACTIVIDAD DE CADA USUARIO.
 * */
@Serializable
object Tables {
    const val users = "users"
    const val requests = "requests"
    const val stats = "stats"
    const val activity = "activity"
}