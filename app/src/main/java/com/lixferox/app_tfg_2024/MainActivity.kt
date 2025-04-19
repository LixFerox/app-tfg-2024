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

class MainActivity : ComponentActivity() {
    //VARIABLES
    private lateinit var navHostController: NavHostController
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private val viewModel: FirestoreDataSource by viewModels()
    ///////////

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        db = Firebase.firestore
        enableEdgeToEdge()
        setContent {
            navHostController = rememberNavController()
           IsLogged(auth = auth, navHostController = navHostController)
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

