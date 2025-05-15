package com.lixferox.app_tfg_2024.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.lixferox.app_tfg_2024.presentation.navigation.SHome

/**
 * METODO QUE COMPRUEBA SI EXISTE UN USUARIO AUTENTICADO EN EL DISPOSITIVO, EN CASO DE HABERLO,
 * NAVEGA AUTOMÁTICAMENTE A LA PANTALLA DE INICIO.
 *
 * @param auth INSTANCIA DE FIREBASE PARA OBTENER EL USUARIO ACTUAL.
 * @param navHostController CONTROLADOR DE NAVEGACIÓN PARA PODER REALIZAR NAVEGACIÓN ENTRE VENTANAS.
 * */

@Composable
fun IsLogged(auth: FirebaseAuth, navHostController: NavHostController) {
    val currentUser = auth.currentUser
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            navHostController.navigate(SHome)
        }
    }
}