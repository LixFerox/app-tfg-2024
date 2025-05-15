package com.lixferox.app_tfg_2024.common

import com.google.firebase.auth.FirebaseAuth

/**
 * METODO QUE ENVÍA UN CORREO DE VERIFICACIÓN AL USUARIO PARA VALIDAR SU CUENTA.
 *
 * @param auth INSTANCIA DE FIREBASE PARA OBTENER EL USUARIO ACTUAL.
 * */

fun verificationEmail(auth: FirebaseAuth) {
    val currentUser = auth.currentUser
    currentUser?.sendEmailVerification()
}
