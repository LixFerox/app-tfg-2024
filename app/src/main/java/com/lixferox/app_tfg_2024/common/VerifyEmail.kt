package com.lixferox.app_tfg_2024.common

import com.google.firebase.auth.FirebaseAuth

// ENVIO DE UN CORREO DE VERIFICACION DE CORREO ELECTRONICO

fun verificationEmail(auth: FirebaseAuth) {
    val currentUser = auth.currentUser
    currentUser?.sendEmailVerification()
}
