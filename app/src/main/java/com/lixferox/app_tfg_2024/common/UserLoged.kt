package com.lixferox.app_tfg_2024.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.lixferox.app_tfg_2024.presentation.navigation.SHome

// EN CASO DE TENER COOKIE DE SESION (UID) SE INICIA SESION AUTOMATICAMENTE

@Composable
fun IsLogged(auth: FirebaseAuth, navHostController: NavHostController) {
    val currentUser = auth.currentUser
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            navHostController.navigate(SHome)
        }
    }
}