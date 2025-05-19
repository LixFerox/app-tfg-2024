package com.lixferox.app_tfg_2024.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lixferox.app_tfg_2024.data.datasource.FirestoreDataSource
import com.lixferox.app_tfg_2024.presentation.screens.HomeScreen
import com.lixferox.app_tfg_2024.presentation.screens.LoginScreen
import com.lixferox.app_tfg_2024.presentation.screens.ProfileInfoScreen
import com.lixferox.app_tfg_2024.presentation.screens.SearchRequestsScreen
import com.lixferox.app_tfg_2024.presentation.screens.SettingsScreen
import com.lixferox.app_tfg_2024.presentation.screens.SignUpScreen
import com.lixferox.app_tfg_2024.presentation.screens.StatsProfileScreen
import com.lixferox.app_tfg_2024.presentation.screens.TasksListScreen

/**
 * METODO QUE SE ENCARGA DE NAVEGAR ENTRE LAS DIFERENTES VENTANAS QUE HAY CON JETPACK COMPOSE.
 *
 * @param navHostController CONTROLADOR DE NAVEGACIÓN PARA PODER REALIZAR NAVEGACIÓN ENTRE VENTANAS.
 * @param auth INSTANCIA DE FIREBASE PARA OBTENER EL USUARIO ACTUAL.
 * @param db INSTANCIA DE FIREBASEFIRESTORE QUE PERMITE LEER LA INFORMACIÓN DEL USUARIO..
 * @param padding ESPACIANDO QUE SE APLICA EN LAS VENTANAS DE LA APLICACIÓN.
 * @param viewModel VIEWMODEL QIUE TIENE LA LÓGICA PARA PODER ACCEDER A LOS DATOS.
 * */

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationWrapper(
    navHostController: NavHostController,
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    padding: PaddingValues,
    viewModel: FirestoreDataSource
) {
    NavHost(navController = navHostController, startDestination = SLogin) {

        composable<SLogin> {
            LoginScreen(
                padding,
                auth,
                navigateToSignUp = {
                    navHostController.navigate(SSignUp) {
                        popUpTo(SLogin) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                navigateToHome = {
                    navHostController.navigate(SHome) {
                        popUpTo(SLogin) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                db
            )
        }

        composable<SSignUp> {
            SignUpScreen(
                padding,
                navigateToLogin = {
                    navHostController.navigate(SLogin) {
                        popUpTo(SSignUp) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                auth, db
            )
        }

        composable<SHome> {
            HomeScreen(
                padding,
                navigateToLogin = {
                    navHostController.navigate(SLogin) {
                        popUpTo(SHome) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                navigateToSettings = {
                    navHostController.navigate(SSettings) {
                        popUpTo(SHome) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                navigateToProfileInfo = {
                    navHostController.navigate(SProfileInfo) {
                        popUpTo(SHome) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                navigateToHome = {
                    navHostController.navigate(SHome) {
                        popUpTo(SHome) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                navigateToSearch = {
                    navHostController.navigate(SSearch) {
                        popUpTo(SHome) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                navigateToTask = {
                    navHostController.navigate(STask) {
                        popUpTo(SHome) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                navigateToStats = {
                    navHostController.navigate(SStats) {
                        popUpTo(SHome) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                auth, db, viewModel
            )
        }

        composable<SSettings> {
            SettingsScreen(
                padding,
                navigateToLogin = {
                    navHostController.navigate(SLogin) {
                        popUpTo(SSettings) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                navigateToSettings = {
                    navHostController.navigate(SSettings) {
                        popUpTo(SSettings) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                navigateToProfileInfo = {
                    navHostController.navigate(SProfileInfo) {
                        popUpTo(SSettings) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                navigateToHome = {
                    navHostController.navigate(SHome) {
                        popUpTo(SSettings) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                navigateToSearch = {
                    navHostController.navigate(SSearch) {
                        popUpTo(SSettings) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                navigateToTask = {
                    navHostController.navigate(STask) {
                        popUpTo(SSettings) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                navigateToStats = {
                    navHostController.navigate(SStats) {
                        popUpTo(SSettings) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                auth, db, viewModel
            )
        }

        composable<SProfileInfo> {
            ProfileInfoScreen(
                padding,
                navigateToLogin = {
                    navHostController.navigate(SLogin) {
                        popUpTo(SProfileInfo) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                navigateToSettings = {
                    navHostController.navigate(SSettings) {
                        popUpTo(SProfileInfo) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                navigateToProfileInfo = {
                    navHostController.navigate(SProfileInfo) {
                        popUpTo(SProfileInfo) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                navigateToHome = {
                    navHostController.navigate(SHome) {
                        popUpTo(SProfileInfo) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                navigateToSearch = {
                    navHostController.navigate(SSearch) {
                        popUpTo(SProfileInfo) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                navigateToTask = {
                    navHostController.navigate(STask) {
                        popUpTo(SProfileInfo) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                navigateToStats = {
                    navHostController.navigate(SStats) {
                        popUpTo(SProfileInfo) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                auth, db, viewModel
            )
        }
        composable<SSearch> {
            SearchRequestsScreen(
                padding, navigateToLogin = {
                    navHostController.navigate(SLogin) {
                        popUpTo(SSearch) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                navigateToSettings = {
                    navHostController.navigate(SSettings) {
                        popUpTo(SSearch) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                navigateToProfileInfo = {
                    navHostController.navigate(SProfileInfo) {
                        popUpTo(SSearch) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                navigateToHome = {
                    navHostController.navigate(SHome) {
                        popUpTo(SSearch) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                navigateToSearch = {
                    navHostController.navigate(SSearch) {
                        popUpTo(SSearch) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                navigateToTask = {
                    navHostController.navigate(STask) {
                        popUpTo(SSearch) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                navigateToStats = {
                    navHostController.navigate(SStats) {
                        popUpTo(SSearch) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                auth, db, viewModel
            )
        }
        composable<STask> {
            TasksListScreen(
                padding, navigateToLogin = {
                    navHostController.navigate(SLogin) {
                        popUpTo(STask) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                navigateToSettings = {
                    navHostController.navigate(SSettings) {
                        popUpTo(STask) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                navigateToProfileInfo = {
                    navHostController.navigate(SProfileInfo) {
                        popUpTo(STask) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                navigateToHome = {
                    navHostController.navigate(SHome) {
                        popUpTo(STask) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                navigateToSearch = {
                    navHostController.navigate(SSearch) {
                        popUpTo(STask) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                navigateToTask = {
                    navHostController.navigate(STask) {
                        popUpTo(STask) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                navigateToStats = {
                    navHostController.navigate(SStats) {
                        popUpTo(STask) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                auth, db, viewModel
            )
        }
        composable<SStats> {
            StatsProfileScreen(
                padding, navigateToLogin = {
                    navHostController.navigate(SLogin) {
                        popUpTo(SStats) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                navigateToSettings = {
                    navHostController.navigate(SSettings) {
                        popUpTo(SStats) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                navigateToProfileInfo = {
                    navHostController.navigate(SProfileInfo) {
                        popUpTo(SStats) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                navigateToHome = {
                    navHostController.navigate(SHome) {
                        popUpTo(SStats) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                navigateToSearch = {
                    navHostController.navigate(SSearch) {
                        popUpTo(SStats) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                navigateToTask = {
                    navHostController.navigate(STask) {
                        popUpTo(SStats) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                navigateToStats = {
                    navHostController.navigate(SStats) {
                        popUpTo(SStats) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                auth, db, viewModel
            )
        }
    }
}
