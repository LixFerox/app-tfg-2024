package com.lixferox.app_tfg_2024.presentation.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lixferox.app_tfg_2024.R
import com.lixferox.app_tfg_2024.ui.components.Header
import com.lixferox.app_tfg_2024.ui.components.NavBar
import com.lixferox.app_tfg_2024.common.callPhone
import com.lixferox.app_tfg_2024.data.datasource.FirestoreDataSource
import com.lixferox.app_tfg_2024.data.datasource.deleteRequest
import com.lixferox.app_tfg_2024.data.datasource.obtainUserInfo
import com.lixferox.app_tfg_2024.data.datasource.obtainUserStats
import com.lixferox.app_tfg_2024.model.Activity
import com.lixferox.app_tfg_2024.model.Request
import com.lixferox.app_tfg_2024.model.Stats
import com.lixferox.app_tfg_2024.model.User
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.abs

// VENTANA PRINCIPAL DEL INICIO

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    paddingValues: PaddingValues,
    navigateToLogin: () -> Unit,
    navigateToSettings: () -> Unit,
    navigateToProfileInfo: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToSearch: () -> Unit,
    navigateToTask: () -> Unit,
    navigateToStats: () -> Unit,
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    viewModel: FirestoreDataSource
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
                db = db,
                viewModel = viewModel
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
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 16.dp), auth = auth, db = db, viewModel = viewModel
            )
        }
    }

}

// COMPONENTE QUE IMPORTARA TODAS LAS DEMAS SECCIONES DE LA VENTANA

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun Content(
    modifier: Modifier = Modifier,
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    viewModel: FirestoreDataSource
) {
    var currentStats by remember { mutableStateOf<Stats?>(null) }
    var currentUser by remember { mutableStateOf<User?>(null) }
    var currentActivity by remember { mutableStateOf<List<Activity>>(emptyList()) }
    var requestCreated by remember { mutableStateOf<List<Request>>(emptyList()) }

    LaunchedEffect(auth.currentUser) {
        obtainUserStats(auth = auth, db = db) { obtainedStats ->
            currentStats = obtainedStats
        }
        obtainUserInfo(auth = auth, db = db) { obtainedUser ->
            currentUser = obtainedUser
        }
        viewModel.obtainActivity(auth = auth, db = db) { obtainedActivity ->
            currentActivity = obtainedActivity
        }
        viewModel.obtainRequests(auth = auth, db = db) { obtainedRequest ->
            requestCreated = obtainedRequest
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
    val weekCompletedTasks by remember { mutableStateOf(stats.weekCompletedTasks) }
    val tasksInProgress by remember { mutableIntStateOf(stats.tasksInProgress) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            WelcomeSection(username = username)
            CardsSection(
                totalCompletedTasks = weekCompletedTasks,
                tasksInProgress = tasksInProgress
            )
            RequestsCreated(db = db, request = requestCreated)
            Spacer(modifier = Modifier.width(32.dp))
            RecientlyActivitySection(currentActivity = currentActivity)
        }
        EmergencyButton(modifier = Modifier.align(Alignment.BottomCenter))
    }
}

// COMPONENTE QUE MUESTRA EL NOMRE DEL USUARIO JUNTOA LA FECHA

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun WelcomeSection(username: String) {
    val currentDateTime = remember { mutableStateOf(LocalDateTime.now()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1_000L)
            currentDateTime.value = LocalDateTime.now()
        }
    }

    val locale = Locale("es", "ES")
    val dateFormatter = DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM", locale)
    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", locale)
    val formattedDate = currentDateTime.value.format(dateFormatter)
    val formattedTime = currentDateTime.value.format(timeFormatter)

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row {
            Text(
                text = "¡Bienvenido,",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
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
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(R.drawable.calendar),
                contentDescription = "Icono de calendario", tint = Color.Gray
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = formattedDate, color = Color.Gray)

            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                painter = painterResource(R.drawable.clock),
                contentDescription = "Icono de reloj",
                tint = Color.Gray
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = formattedTime, color = Color.Gray)
        }
    }
}

//COMPONENTE QUE  MUESTRA LAS TAREAS DEL USUARIO

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun CardsSection(totalCompletedTasks: List<Double>, tasksInProgress: Int) {
    val date = LocalDate.now()
    val dateOfWeek = when (date.dayOfWeek) {
        DayOfWeek.MONDAY -> 0
        DayOfWeek.TUESDAY -> 1
        DayOfWeek.WEDNESDAY -> 2
        DayOfWeek.THURSDAY -> 3
        DayOfWeek.FRIDAY -> 4
        DayOfWeek.SATURDAY -> 5
        DayOfWeek.SUNDAY -> 6
    }
    val compteteDayOfWeek = totalCompletedTasks[dateOfWeek].toInt()

    data class ItemCard(
        val title: String,
        val description: String,
        val icon: Int,
        val color: Color
    )

    val listItems = listOf(
        ItemCard(
            title = "Tareas completadas",
            description = "$compteteDayOfWeek/20",
            icon = R.drawable.completed,
            color = Color(
                color = 0xFF8BC34A
            )
        ),
        ItemCard(
            title = "Tareas en progreso",
            description = "$tasksInProgress",
            icon = R.drawable.in_progress,
            color = Color(color = 0xFFFFEB3B)
        ),
    )
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        listItems.map { item ->
            Card(shape = RoundedCornerShape(8.dp)) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                            Text(
                                text = item.description,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(item.color, shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(item.icon),
                                contentDescription = "Icono de la sección ${item.title}"
                            )
                        }
                    }
                }
            }
        }
    }
}

// COMPONENTE QUE MUESTRA LAS SOLICITUDES CREADAS

@Composable
private fun RequestsCreated(db: FirebaseFirestore, request: List<Request>) {
    var deleteIsVisible by remember { mutableStateOf(false) }
    var currentId by remember { mutableStateOf("") }
    var currentUid by remember { mutableStateOf("") }
    var currentDescription by remember { mutableStateOf("") }

    data class ItemMenu(
        val uid: String,
        val id: String,
        val title: String,
        val description: String,
        val date: String
    )

    val listItems = request.map { task ->
        val dateObject = task.dateCreated.toDate()
        val formatDate = SimpleDateFormat("dd/MM/yyyy hh:mm", Locale.getDefault())
        val dateTask = formatDate.format(dateObject)
        ItemMenu(
            uid = task.createdByUid,
            id = task.id,
            title = task.title,
            description = task.description,
            date = dateTask,
        )
    }

    if (listItems.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Actividad reciente", style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No has creado ninguna solicitud",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Solicitudes creadas", style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                listItems.map { item ->
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.DarkGray
                                )
                                Text(
                                    text = "${item.date}  • ${item.description}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                            IconButton(onClick = {
                                currentId = item.id
                                currentUid = item.uid
                                currentDescription = item.description
                                deleteIsVisible = true
                            }) {
                                Icon(
                                    painter = painterResource(R.drawable.delete),
                                    contentDescription = "Icono de eliminar",
                                    modifier = Modifier.size(32.dp),
                                    tint = Color(color = 0xFFF44336)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    if (deleteIsVisible) {
        AlertDelete(onDismiss = { deleteIsVisible = false }, onAccept = {
            deleteRequest(
                db = db,
                uid = currentUid,
                description = currentDescription,
                id = currentId
            )
            deleteIsVisible = false
        })
    }
}

@Composable
private fun AlertDelete(onDismiss: () -> Unit, onAccept: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        dismissButton = { TextButton(onClick = { onDismiss() }) { Text(text = "Cancelar") } },
        confirmButton = { TextButton(onClick = { onAccept() }) { Text(text = "Aceptar") } },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Eliminar solicitud",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(text = "¿Estás seguro que quieres eliminar la solicitud?")
            }
        })
}

// COMPONENTE QUE MUESTRA LA ACTIVIDAD RECUENTE DEL USUARIO

@Composable
private fun RecientlyActivitySection(currentActivity: List<Activity>) {
    data class ItemCard(
        val title: String,
        val date: Timestamp,
        val description: String
    )

    val listItems = currentActivity.map { task ->
        val title = task.title
        val date = task.time
        val description = task.description

        ItemCard(title = title, date = date, description = description)
    }

    if (listItems.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Actividad reciente", style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay actividad reciente",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Actividad reciente", style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                listItems.map { item ->

                    val timeInMillis = item.date.toDate().time
                    val now = System.currentTimeMillis()
                    val diff = abs(timeInMillis - now)

                    val seconds = diff / 1000
                    val minutes = seconds / 60
                    val hours = minutes / 60
                    val days = hours / 24

                    val timeTask = when {
                        seconds < 60 -> "Hace $seconds segundo${if (seconds == 1L) "" else "s"}"
                        minutes < 60 -> "Hace $minutes minuto${if (minutes == 1L) "" else "s"}"
                        hours < 24 -> "Hace $hours hora${if (hours == 1L) "" else "s"}"
                        else -> "Hace $days día${if (days == 1L) "" else "s"}"
                    }

                    Card(
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .drawBehind {
                                drawLine(
                                    color = Color.LightGray,
                                    start = Offset.Zero,
                                    end = Offset(0f, this.size.height),
                                    strokeWidth = 5f,
                                    cap = StrokeCap.Round
                                )
                            },
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.DarkGray
                            )
                            Text(
                                text = "$timeTask  • ${item.description}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

// METODO QUE LLAMA A EMERGENCIAS

@Composable
private fun EmergencyButton(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Row(modifier.padding(8.dp)) {
        Button(
            onClick = { callPhone(context = context, phone = "061") },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(color = 0xFFF44336))
        ) {
            Text(
                text = "Llamada de emergencia",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}