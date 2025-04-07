package com.lixferox.app_tfg_2024.data.datasource

import com.google.firebase.auth.FirebaseAuth

fun loginFirebase(
    onSuccess: () -> Unit,
    onError: (String) -> Unit,
    auth: FirebaseAuth,
    email: String,
    password: String
) {
    if (email.isEmpty() || password.isEmpty()) {
        onError("El email o contraseña están vacías")
        return
    }
    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
        if (task.isSuccessful) {
            onSuccess()
        } else {
            onError(task.exception?.message ?: "Error desconocido al iniciar sesión")
        }
    }
}