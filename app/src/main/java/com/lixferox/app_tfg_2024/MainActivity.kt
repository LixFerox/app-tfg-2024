package com.lixferox.app_tfg_2024

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.lixferox.app_tfg_2024.common.IsLogged
import com.lixferox.app_tfg_2024.data.datasource.FirestoreDataSource
import com.lixferox.app_tfg_2024.presentation.navigation.NavigationWrapper
import com.lixferox.app_tfg_2024.ui.theme.Apptfg2024Theme

/**
 * ACTIVIDAD PRINCIPAL QUE CONFIGURA FIREBASE, EL CONTROLADOR DE NAVEGACIÓN DE VENTANAS Y LA INTERFAZ.
 *
 * - INICIALIZA FIREBASEAUTH Y FIREBASEFIRESTORE.
 * - CONFIGURA EL NAVHOSTCONTROLLER Y COMPRUEBA SI SE HA INICIADO SESIÓN.
 * - ENVUELVE TODA LA INTERFAZ EN UN SCAFFOLD CON UNA UI PERSONALIZADA PARA CADA VENTANA.
 *
 * @property navHostController CONTROLADOR PARA NAVEGAR POR VENTANAS CON JETPACK COMPOSE.
 * @property auth INSTANCIA DE FIREBASEAUTH PARA LA AUTENTICACIÓN DE USUARIO.
 * @property db INSTANCIA DE FIREBASE FIRESTORE PARA EL ALMACENAMIENTO DE DATOS.
 * @property viewModel VIEWMODEL QUE MANEJA LAS OPERACIONES DE LOS DATOS.
 * */

class MainActivity : ComponentActivity() {
    private lateinit var navHostController: NavHostController
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private val viewModel: FirestoreDataSource by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // INICIALIZACIÓN DE FIREBASE AUTH Y FIRESTORE.
        auth = Firebase.auth
        db = Firebase.firestore

        enableEdgeToEdge()
        setContent {
            // CREACIÓN DEL NAVHOSTCONTROLLER.
            navHostController = rememberNavController()
            // LLAMADA AL METODO QUE COMPRUEBA SI EL USUARIO ESTÁ LOGEADO.
            IsLogged(auth = auth, db = db, navHostController = navHostController)
            // CREACIÓN DE LA INTERFÁZ PRINCIPAL.
            Apptfg2024Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavigationWrapper(
                        navHostController = navHostController,
                        auth = auth,
                        db = db,
                        padding = innerPadding,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

