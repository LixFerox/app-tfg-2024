package com.lixferox.app_tfg_2024.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lixferox.app_tfg_2024.data.model.Tables
import com.lixferox.app_tfg_2024.presentation.navigation.SHome

/**
 * METODO QUE COMPRUEBA SI EXISTE UN USUARIO AUTENTICADO EN EL DISPOSITIVO, EN CASO DE HABERLO,
 * NAVEGA AUTOMÁTICAMENTE A LA PANTALLA DE INICIO.
 *
 * @param auth INSTANCIA DE FIREBASE PARA OBTENER EL USUARIO ACTUAL.
 * @param db INSTANCIA DE FIREBASEFIRESTORE QUE PERMITE LEER LA INFORMACIÓN DEL USUARIO.
 * @param navHostController CONTROLADOR DE NAVEGACIÓN PARA PODER REALIZAR NAVEGACIÓN ENTRE VENTANAS.
 * */

@Composable
fun IsLogged(auth: FirebaseAuth, db: FirebaseFirestore, navHostController: NavHostController) {
    val currentUser = auth.currentUser
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            db.collection(Tables.users).whereEqualTo("uid", currentUser.uid).get().addOnSuccessListener { task->
                val document = task.documents.firstOrNull()
                if(document !=null) {
                    val isValid = document.getBoolean("valid") ?: false
                    if(isValid){
                        navHostController.navigate(SHome)
                    }
                }
            }
        }
    }
}