package com.lixferox.app_tfg_2024.presentation.screens


import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lixferox.app_tfg_2024.R
import com.lixferox.app_tfg_2024.data.datasource.deleteAccount
import com.lixferox.app_tfg_2024.data.datasource.obtainUserInfo
import com.lixferox.app_tfg_2024.data.datasource.obtainUserStats
import com.lixferox.app_tfg_2024.model.Stats
import com.lixferox.app_tfg_2024.model.User
import com.lixferox.app_tfg_2024.ui.components.Header
import com.lixferox.app_tfg_2024.ui.components.NavBar
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.Line
import java.util.Date

// VENTANA DE LA INFORMACION DEL USUARIO

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfileInfoScreen(
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
                indexBar = 0,
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
            ProfileData(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 16.dp),
                auth = auth, db = db, navigateToLogin = navigateToLogin
            )
        }
    }
}

// COMPONENTE QUE IMPORTARA TODAS LAS SECCIONES A MOSTRAR

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun ProfileData(
    modifier: Modifier = Modifier,
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    navigateToLogin: () -> Unit
) {
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

    val user = requireNotNull(currentUser)
    val stats = requireNotNull(currentStats)

    val username by remember { mutableStateOf(user.username) }
    val level by remember { mutableIntStateOf(stats.level) }
    val puntuation by remember { mutableDoubleStateOf(stats.puntuation) }
    val levelBar by remember { mutableIntStateOf(stats.points) }
    val totalRequests by remember { mutableIntStateOf(stats.totalCompletedTasks) }
    val joinedIn by remember { mutableStateOf(stats.joinedIn) }
    val listTasksWeek by remember { mutableStateOf(stats.weekCompletedTasks) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

        UserHeader(username = username, level = level)
        LevelBar(levelBar = levelBar, fraction = 0.5f)
        Spacer(Modifier.height(8.dp))
        StatsSection(puntuation = puntuation, totalRequests = totalRequests)
        Spacer(Modifier.height(8.dp))
        InfoSection(joinedIn = joinedIn, listTasksWeek = listTasksWeek)
        DeleteAccount(auth = auth, db = db, onError = { message ->
            errorMessage = message
        }, onSuccess = { navigateToLogin() })
        if (errorMessage != null) {
            AlertDialog(onDismissRequest = { errorMessage = null }, confirmButton = {
                TextButton(onClick = { errorMessage = null }) { Text(text = "Aceptar") }
            }, text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Ha ocurrido un error",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = errorMessage ?: "")
                }
            })
        }
    }
}

// COMPONENTE QUE MOSTRARA EL NOMBRE DEL USUARIO Y SU ICONO

@Composable
private fun UserHeader(username: String, level: Int) {
    Icon(
        painter = painterResource(R.drawable.profile),
        contentDescription = "Icono del perfil de usuario",
        modifier = Modifier
            .size(120.dp)
            .padding(8.dp),
        tint = Color.Gray
    )
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "¡Hola",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        Row {
            Text(
                text = username,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color(color = 0xFF2196F3)
            )
            Text(
                text = "!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
        }
    }

    Text(
        text = "Nivel $level",
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.SemiBold,
        color = Color.DarkGray
    )
}

// COMPONENTE QUE MOSTRARA LA BARRA DE NIVEL DEL USUARIO

@Composable
fun LevelBar(levelBar: Int, fraction: Float) {
    val points by remember { mutableFloatStateOf(levelBar.toFloat()) }

    LinearProgressIndicator(
        progress = { points / 500f },
        modifier = Modifier
            .fillMaxWidth(fraction)
            .height(8.dp),
        color = Color(color = 0xFF4CAF50),
        trackColor = Color.LightGray,
    )
}

// COMPONENTE QUE MOSTRARA LAS ESTADISTICAS DEL USUARIO

@Composable
private fun StatsSection(puntuation: Double, totalRequests: Int) {

    data class ItemMenu(
        val title: String,
        val data: String,
        val icon: Int,
        val color: Color
    )

    val listItems = listOf(
        ItemMenu(
            title = if (puntuation == 0.0) "0/5" else "$puntuation/5",
            data = "Puntuación",
            R.drawable.points,
            Color(color = 0xFFFFC107)
        ),
        ItemMenu(
            title = "$totalRequests",
            data = "Peticiones",
            R.drawable.request,
            Color(color = 0xFF00BCD4)
        ),
    )

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
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = item.data,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

// COMPONENTE QUE MOSTRARA LA ANTIGUEDAD DEL USUARIO Y LAS TAREAS DE LA SEMANA

@Composable
private fun InfoSection(joinedIn: Timestamp, listTasksWeek: List<Double>) {
    var joined by remember { mutableStateOf("") }
    var typeJoined by remember { mutableStateOf("") }

    val joinDate = joinedIn.toDate()
    val currentDate = Date()

    val diffMillis = currentDate.time - joinDate.time
    val diffDays = (diffMillis / (1000 * 60 * 60 * 24)).toInt()

    if (diffDays < 30) {
        joined = diffDays.toString()
        typeJoined = if (diffDays == 1) "Día" else "Días"
    } else if (diffDays < 365) {
        val diffMonths = diffDays / 30
        joined = diffMonths.toString()
        typeJoined = if (diffMonths == 1) "Mes" else "Meses"
    } else {
        val diffYears = diffDays / 365
        joined = diffYears.toString()
        typeJoined = if (diffYears == 1) "Año" else "Años"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
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
                verticalArrangement = Arrangement.spacedBy(
                    8.dp,
                    alignment = Alignment.CenterVertically
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Antiguedad",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                    Icon(
                        painter = painterResource(R.drawable.time),
                        contentDescription = "Icono de la sección antiguedad",
                        tint = Color(color = 0xFF3F51B5)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(
                        8.dp,
                        alignment = Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = joined,
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = typeJoined,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                    )
                }

            }
        }
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(32.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Semana",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                    Icon(
                        painter = painterResource(R.drawable.graphic),
                        contentDescription = "Icono del gráfico semanal",
                        tint = Color(color = 0xFFFF9800)
                    )
                }
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
                                        colors = listOf(Color(0xFF00BCD4), Color(0xFF00BCD4))
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

// COMPONENTE QUE ELIMINARA LA CUENTA DEL USUARIO

@Composable
private fun DeleteAccount(
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    onError: (String) -> Unit,
    onSuccess: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(text = "Confirmación") },
            text = { Text(text = "¿Estás seguro de que deseas eliminar tu cuenta? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        deleteAccount(auth, db, onSuccess, onError)
                        showDeleteDialog = false
                    }
                ) {
                    Text(text = "Confirmar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text(text = "Cancelar")
                }
            }
        )
    }
    Button(
        onClick = { showDeleteDialog = true },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(color = 0xFFF44336))
    ) {
        Text(
            text = "Eliminar cuenta",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
}


