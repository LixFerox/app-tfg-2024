package com.lixferox.app_tfg_2024.presentation.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lixferox.app_tfg_2024.R
import com.lixferox.app_tfg_2024.data.datasource.obtainUserInfo
import com.lixferox.app_tfg_2024.data.datasource.obtainUserStats
import com.lixferox.app_tfg_2024.model.Stats
import com.lixferox.app_tfg_2024.model.User
import com.lixferox.app_tfg_2024.ui.components.Header
import com.lixferox.app_tfg_2024.ui.components.NavBar
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.Line

// VENTANA DE LAS ESTADISTICAS DEL USUARIO

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StatsProfileScreen(
    paddingValues: PaddingValues,
    navigateToLogin: () -> Unit,
    navigateToSettings: () -> Unit,
    navigateToProfileInfo: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToSearch: () -> Unit,
    navigateToTask: () -> Unit,
    navigateToStats: () -> Unit,
    auth: FirebaseAuth,
    db: FirebaseFirestore
) {
    Scaffold(
        topBar = {
            Header(
                modifier = Modifier.padding(paddingValues),
                navigateToLogin = navigateToLogin,
                navigateToSettings = navigateToSettings,
                navigateToProfileInfo = navigateToProfileInfo,
                auth = auth,
                db = db
            )
        },
        bottomBar = {
            NavBar(
                navigateToHome = navigateToHome,
                navigateToSearch = navigateToSearch,
                navigateToTasks = navigateToTask,
                navigateToStats = navigateToStats,
                indexBar = 4,
                auth = auth,
                db = db
            )
        }
    ) { innerpadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerpadding)
        ) {
            Content(
                modifier = Modifier
                    .align(Alignment.TopCenter), auth = auth, db = db
            )
        }
    }
}

// COMPONENTE QUE IMPORTARA TODAS LAS SECCIONES A MOSTRAR

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun Content(modifier: Modifier = Modifier, auth: FirebaseAuth, db: FirebaseFirestore) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Estadísticas",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(color = 0xFF2196F3)
        )
        Spacer(modifier = Modifier.height(16.dp))
        InfoStats(
            modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            auth = auth, db = db
        )
    }
}

// COMONENTE QUE MUESTR LAS ESTADISTICAS DEL USUARIO

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun InfoStats(modifier: Modifier = Modifier, auth: FirebaseAuth, db: FirebaseFirestore) {
    var currentStats by remember { mutableStateOf<Stats?>(null) }
    var currentUser by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(auth.currentUser) {
        obtainUserStats(auth = auth, db = db) { obtainedStats ->
            currentStats = obtainedStats
        }
        obtainUserInfo(auth = auth, db = db) { obtainedUser ->
            currentUser = obtainedUser
        }
    }
    if (currentUser == null || currentStats == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val stats = requireNotNull(currentStats)

    val acceptedTasks by remember { mutableIntStateOf(stats.totalCompletedTasks) }
    val totalWeekCompleted = stats.weekCompletedTasks.sum().toInt()
    val userLevel by remember { mutableIntStateOf(stats.level) }
    val userPoints by remember { mutableIntStateOf(stats.points) }
    val listTasksWeek by remember { mutableStateOf(stats.weekCompletedTasks) }


    data class ItemStats(
        val title: String,
        val icon: Int,
        val value: String,
        val color: Color
    )

    val listItems = listOf(
        ItemStats(
            title = "Solicitudes Aceptadas",
            icon = R.drawable.trending,
            value = "$acceptedTasks",
            color = Color(color = 0xFF2196F3)
        ),
        ItemStats(
            title = "Solicitudes por Semana",
            icon = R.drawable.clock,
            value = "$totalWeekCompleted",
            color = Color(color = 0xFFFFC107)
        )
    )


    Column(
        modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listItems.map { item ->
                Card(
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(item.icon),
                                contentDescription = "Icono de la sección ${item.title}",
                                tint = item.color
                            )
                        }
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = item.value,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Nivel de usuario",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.DarkGray
                    )
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(color = 0xFFFFC107), shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$userLevel",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Puntos acumulados",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                    )
                    Text(
                        text = "$userPoints/500",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                LevelBar(levelBar = userPoints, fraction = 1f)
            }
        }
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "Tendencias semanales",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.DarkGray
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                ) {
                    LineChart(
                        data = remember {
                            listOf(
                                Line(
                                    label = "Tareas",
                                    values = listTasksWeek,
                                    color = Brush.radialGradient(
                                        colors = listOf(
                                            Color(color = 0xFF00BCD4),
                                            Color(color = 0xFF00BCD4)
                                        )
                                    )
                                )
                            )
                        },
                        indicatorProperties = HorizontalIndicatorProperties(
                            textStyle = TextStyle.Default.copy(
                                color = Color.Transparent,
                                fontSize = 0.sp
                            ),
                        ),
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }

}