package com.lixferox.app_tfg_2024.common

/**
 * METODO QUE COMPRUEBA SI UN DNI ES VÁLIDO.
 *
 * @param dni DNI INTRODUCIDO POR EL USUARIO, DEBE DE TENER 8 DÍGITOS Y UNA LETRA.
 * @return TRUE EN CASO DE QUE EL DNI TENGA EL FORMATO CORRECTO, FALSE EN CASO CONTRARIO.
 * */

fun checkDni(dni: String): Boolean {
    val upperDNI = dni.trim().uppercase()

    if (upperDNI.length != 9) return false

    val numbers = upperDNI.substring(0, 8).toIntOrNull() ?: return false

    val chars = "TRWAGMYFPDXBNJZSQVHLCKE"

    val calRestoChar = chars[numbers % 23]

    return upperDNI[8] == calRestoChar
}