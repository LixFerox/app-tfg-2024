package com.lixferox.app_tfg_2024.data.datasource

import android.content.Context
import android.widget.Toast
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lixferox.app_tfg_2024.data.model.Tables
import com.lixferox.app_tfg_2024.model.Stats
import com.lixferox.app_tfg_2024.model.User
import java.text.SimpleDateFormat
import java.util.Locale

// COMPROBACION DE QUE HAYA DATOS EN LOS INPUT DEL FORMULARIO PARA INICIAR SESION

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

// METODO QUE CREA UNA CUENTA NUEVA EN LA BASE DE DATOS

fun createAccountFirebase(
    onSuccess: () -> Unit,
    onError: (String) -> Unit,
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    email: String,
    password: String,
    repassword: String,
    username: String,
    birth: String,
    isHelper: Boolean,
    address: String,
    phone: String,
    context: Context,
) {
    if (password != repassword) {
        onError("Las contraseñas no coinciden")
        return
    }
    if (email.isEmpty() || password.isEmpty() || repassword.isEmpty() || birth.isEmpty()) {
        onError("Debes rellenar todos los campos")
        return
    }

    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
        val uid = auth.currentUser?.uid
        if (task.isSuccessful) {
            if (uid == null) {
                onError(task.exception?.message ?: "No se puedo obtener el usuario")
                return@addOnCompleteListener
            }
            val currentDate = Timestamp.now()
            val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val convertBirth = format.parse(birth)
            val currentUser = User(
                uid = uid,
                email = email,
                username = username,
                birth = Timestamp(convertBirth),
                isHelper = isHelper,
                address = address,
                phone = phone
            )
            db.collection(Tables.users).add(currentUser).addOnCompleteListener { added ->
                if (added.isSuccessful) {
                    val listTasksWeek = listOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
                    val statsUser = Stats(
                        uid = uid,
                        level = 0,
                        points = 0,
                        totalCompletedTasks = 0,
                        weekCompletedTasks = listTasksWeek,
                        tasksInProgress = 0,
                        puntuation = 0.0,
                        ratingPoints = 0L,
                        joinedIn = currentDate,
                        resetWeekValues = false
                    )
                    db.collection(Tables.stats).add(statsUser).addOnCompleteListener {
                        onSuccess()
                    }
                }
                Toast.makeText(
                    context,
                    "¡Se ha creado la cuenta correctamente!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            onError(task.exception?.message ?: "Error desconocido al crear la cuenta")
            return@addOnCompleteListener
        }
    }
}