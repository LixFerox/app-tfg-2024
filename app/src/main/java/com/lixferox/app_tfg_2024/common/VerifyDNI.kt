package com.lixferox.app_tfg_2024.common

// FUNCION QUE COMPRUEBA SI EL DNI ES VALIDO

fun checkDni(dni: String): Boolean {
    val upperDNI = dni.trim().uppercase()

    if (upperDNI.length != 9) return false

    val numbers = upperDNI.substring(0, 8).toIntOrNull() ?: return false

    val chars = "TRWAGMYFPDXBNJZSQVHLCKE"

    val calRestoChar = chars[numbers % 23]

    return upperDNI[8] == calRestoChar
}