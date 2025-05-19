package com.lixferox.app_tfg_2024.data.datasource

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lixferox.app_tfg_2024.common.checkDni
import com.lixferox.app_tfg_2024.data.model.Tables
import com.lixferox.app_tfg_2024.model.Stats
import com.lixferox.app_tfg_2024.model.User
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * MÉTODO QUE REALIZA LA AUTENTICACIÓN DEL USUARIO, ADEMÁS COMPRUEBA QUE LOS CAMPOS CONTENGAN DATOS Y NO ESTÉN VACÍOS.
 *
 * @param onSuccess CALLBACK QUE SE EJECUTA AL HABERSE AUTENTICADO CORRECTAMENTE.
 * @param onError CALLBACK QUE SE EJECUTA EN CASO DE ERROR.
 * @param onNotValidated CALLBACK QUE SE EJECUTA CUANDO EL USUARIO NO SE HA VALIDADO
 * @param auth  INSTANCIA DE FIREBASE PARA OBTENER EL USUARIO ACTUAL.
 * @param email CORREO ELECTRÓNICO DEL USUARIO.
 * @param password CONTRASEÑA DEL USUARIO.
 * */

fun loginFirebase(
    onSuccess: () -> Unit,
    onError: (String) -> Unit,
    onNotValidated: (String) -> Unit,
    auth: FirebaseAuth,
    email: String,
    password: String,
    db: FirebaseFirestore,
) {
    if (email.isEmpty() || password.isEmpty()) {
        onError("El email o contraseña están vacías")
        return
    }
    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
        if (task.isSuccessful) {
            auth.currentUser?.reload()?.addOnCompleteListener { reload ->
                if (reload.isSuccessful) {
                    val user = auth.currentUser
                    if (user == null) {
                        return@addOnCompleteListener
                    }
                    if (!user.isEmailVerified) {
                        onError("Tu correo no está verificado.\nRevisa tu bandeja y pulsa el enlace que te hemos enviado.")
                        return@addOnCompleteListener
                    }
                    db.collection(Tables.users).whereEqualTo("uid", user.uid).get()
                        .addOnCompleteListener { valid ->
                            if (valid.isSuccessful) {
                                val document = valid.result.documents.firstOrNull()
                                if (document != null) {
                                    val isValid = document.getBoolean("valid") ?: false
                                    if (!isValid) {
                                        onNotValidated(user.uid)
                                        return@addOnCompleteListener
                                    }
                                    onSuccess()
                                }
                            }
                        }
                }
            }
        } else {
            onError(task.exception?.message ?: "Error desconocido al iniciar sesión")
        }
    }
}

/**
 * MÉTODO QUE CREA UNA CUNETA DE USUARIO NUEVA EN LA BASE DE DATOS.
 *
 * @param onSuccess CALLBACK QUE SE EJECUTA AL HABERSE CREADO LA CUENTA CORRECTAMENTE.
 * @param onError CALLBACK QUE SE EJECUTA EN CASO DE ERROR.
 * @param auth  INSTANCIA DE FIREBASE PARA OBTENER EL USUARIO ACTUAL.
 * @param db  INSTANCIA DE FIREBASE PARA ALMACENAR DATOS.
 * @param email CORREO ELECTRÓNICO DEL USUARIO.
 * @param password CONTRASEÑA DEL USUARIO.
 * @param repassword REPETICIÓN DE CONTRASEÑA PARA VALIDAR QUE SEA LA MISMA.
 * @param username NOMBRE DE USUARIO.
 * @param birth FECHA DE NACIMIENTO DEL USUARIO.
 * @param isHelper INDICA SI EL USUARIO ES AYUDANTE O ANCIANO.
 * @param address DIRECCIÓN DEL USUARIO.
 * @param phone TELÉFONO DEL USUARIO.
 * @param dni DNI DEL USUARIO.
 * @param context  CONTEXTO DESDE EL CUAL SE INICIA LA ACTIVIDAD.
 * */

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
    dni: String,
    context: Context,
) {
    if (password != repassword) {
        onError("Las contraseñas no coinciden")
        return
    }
    if (email.isEmpty() || password.isEmpty() || repassword.isEmpty() || birth.isEmpty() || dni.isEmpty()) {
        onError("Debes rellenar todos los campos")
        return
    }
    if (!checkDni(dni)) {
        onError("El DNI no es válido")
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
                phone = phone,
                dni = dni,
                image = "TODO",
                dniImage = "",
                isValid = false,
                type = "CURRENT"
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

fun uploadDniVerify(dni: String, uid: String, db: FirebaseFirestore) {
    db.collection(Tables.users).whereEqualTo("uid", uid).get().addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val document = task.result.documents.firstOrNull()
            if (document != null) {
                document.reference.update(
                    "dniImage", dni,
                ).addOnCompleteListener { update ->
                    if (update.isSuccessful) {
                        Log.i(
                            "UpdateInfo",
                            "Se ha enviado el DNI"
                        )
                    }
                }
            }
        }
    }
}