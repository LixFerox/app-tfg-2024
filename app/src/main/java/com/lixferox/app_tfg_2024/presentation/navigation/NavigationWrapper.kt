package com.lixferox.app_tfg_2024.presentation.navigation

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
                    navHostController.navigate(SSignUp)
                },
                navigateToHome = {
                    navHostController.navigate(SHome)
                }
            )
        }

        composable<SSignUp> {
            SignUpScreen(
                padding,
                navigateToLogin = {
                    navHostController.navigate(SLogin)
                },
                auth, db
            )
        }

        composable<SHome> {
            HomeScreen(
                padding,
                navigateToLogin = {
                    navHostController.navigate(SLogin)
                },
                navigateToSettings = {
                    navHostController.navigate(SSettings)
                },
                navigateToProfileInfo = {
                    navHostController.navigate(SProfileInfo)
                },
                navigateToHome = {
                    navHostController.navigate(SHome)
                },
                navigateToSearch = {
                    navHostController.navigate(SSearch)
                },
                navigateToTask = {
                    navHostController.navigate(STask)
                },
                navigateToStats = {
                    navHostController.navigate(SStats)
                },
                auth, db
            )
        }

        composable<SSettings> {
            SettingsScreen(
                padding,
                navigateToLogin = {
                    navHostController.navigate(SLogin)
                },
                navigateToSettings = {
                    navHostController.navigate(SSettings)
                },
                navigateToProfileInfo = {
                    navHostController.navigate(SProfileInfo)
                },
                navigateToHome = {
                    navHostController.navigate(SHome)
                },
                navigateToSearch = {
                    navHostController.navigate(SSearch)
                },
                navigateToTask = {
                    navHostController.navigate(STask)
                },
                navigateToStats = {
                    navHostController.navigate(SStats)
                },
                auth, db
            )
        }

        composable<SProfileInfo> {
            ProfileInfoScreen(
                padding,
                navigateToLogin = {
                    navHostController.navigate(SLogin)
                },
                navigateToSettings = {
                    navHostController.navigate(SSettings)
                },
                navigateToProfileInfo = {
                    navHostController.navigate(SProfileInfo)
                },
                navigateToHome = {
                    navHostController.navigate(SHome)
                },
                navigateToSearch = {
                    navHostController.navigate(SSearch)
                },
                navigateToTask = {
                    navHostController.navigate(STask)
                },
                navigateToStats = {
                    navHostController.navigate(SStats)
                },
                auth, db
            )
        }
        composable<SSearch> {
            SearchRequestsScreen(
                padding, navigateToLogin = {
                    navHostController.navigate(SLogin)
                },
                navigateToSettings = {
                    navHostController.navigate(SSettings)
                },
                navigateToProfileInfo = {
                    navHostController.navigate(SProfileInfo)
                },
                navigateToHome = {
                    navHostController.navigate(SHome)
                },
                navigateToSearch = {
                    navHostController.navigate(SSearch)
                },
                navigateToTask = {
                    navHostController.navigate(STask)
                },
                navigateToStats = {
                    navHostController.navigate(SStats)
                },
                auth, db, viewModel
            )
        }
        composable<STask> {
            TasksListScreen(
                padding, navigateToLogin = {
                    navHostController.navigate(SLogin)
                },
                navigateToSettings = {
                    navHostController.navigate(SSettings)
                },
                navigateToProfileInfo = {
                    navHostController.navigate(SProfileInfo)
                },
                navigateToHome = {
                    navHostController.navigate(SHome)
                },
                navigateToSearch = {
                    navHostController.navigate(SSearch)
                },
                navigateToTask = {
                    navHostController.navigate(STask)
                },
                navigateToStats = {
                    navHostController.navigate(SStats)
                },
                auth, db, viewModel
            )
        }
        composable<SStats> {
            StatsProfileScreen(
                padding, navigateToLogin = {
                    navHostController.navigate(SLogin)
                },
                navigateToSettings = {
                    navHostController.navigate(SSettings)
                },
                navigateToProfileInfo = {
                    navHostController.navigate(SProfileInfo)
                },
                navigateToHome = {
                    navHostController.navigate(SHome)
                },
                navigateToSearch = {
                    navHostController.navigate(SSearch)
                },
                navigateToTask = {
                    navHostController.navigate(STask)
                },
                navigateToStats = {
                    navHostController.navigate(SStats)
                },
                auth, db
            )
        }
    }
}
