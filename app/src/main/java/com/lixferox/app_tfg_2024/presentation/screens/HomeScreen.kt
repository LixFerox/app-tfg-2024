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
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lixferox.app_tfg_2024.R
import com.lixferox.app_tfg_2024.ui.components.Header
import com.lixferox.app_tfg_2024.ui.components.NavBar
import com.lixferox.app_tfg_2024.common.callPhone
import com.lixferox.app_tfg_2024.common.openMaps
import com.lixferox.app_tfg_2024.data.datasource.FirestoreDataSource
import com.lixferox.app_tfg_2024.data.datasource.deleteRequest
import com.lixferox.app_tfg_2024.data.datasource.obtainUserInfo
import com.lixferox.app_tfg_2024.data.datasource.obtainUserStats
import com.lixferox.app_tfg_2024.data.model.Tables
import com.lixferox.app_tfg_2024.model.Activity
import com.lixferox.app_tfg_2024.model.Request
import com.lixferox.app_tfg_2024.model.Stats
import com.lixferox.app_tfg_2024.model.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.abs

/**
 * VENTANA PRINCIPAL DE LA APLICACIÓN.
 *
 * @param paddingValues ESPACIANDO QUE SE APLICA EN LAS VENTANAS DE LA APLICACIÓN.
 * @param navigateToLogin CALLBACK PARA NAVEGAR A LA VENTANA DE LOGIN.
 * @param navigateToSettings CALLBACK PARA NAVEGAR A LA VENTANA DE AJUSTES.
 * @param navigateToProfileInfo CALLBACK PARA NAVEGAR A LA VENTANA DE INFORMACIÓN DEL USUARIO.
 * @param navigateToHome CALLBACK PARA NAVEGAR A LA VENTANA DE INICIO.
 * @param navigateToSearch CALLBACK PARA NAVEGAR A LA VENTANA DE BÚSQUEDA.
 * @param navigateToTask CALLBACK PARA NAVEGAR A LA VENTANA DE TAREAS.
 * @param navigateToStats CALLBACK PARA NAVEGAR A LA VENTANA DE ESTADÍSTICAS.
 * @param auth INSTANCIA DE FIREBASE PARA OBTENER EL USUARIO ACTUAL.
 * @param db INSTANCIA DE FIREBASEFIRESTORE QUE PERMITE LEER LA INFORMACIÓN DEL USUARIO.
 * @param viewModel VIEWMODEL QIUE TIENE LA LÓGICA PARA PODER ACCEDER A LOS DATOS.
 * */

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

/**
 * COMPONENTE DEL CONTENIDO PRINCIPAL DE LA VENTANA DE INICIO, MUESTRA LAS ESTADÍSTICAS, SOLICITUDES Y ACTIVIDAD RECIENTE.
 *
 * @param modifier MODIFICADOR QUE PERMITE PERSONALIZAR EL LAYOUT.
 * @param auth INSTANCIA DE FIREBASE PARA OBTENER EL USUARIO ACTUAL.
 * @param db INSTANCIA DE FIREBASEFIRESTORE QUE PERMITE LEER LA INFORMACIÓN DEL USUARIO.
 * @param viewModel VIEWMODEL QUE TIENE LA LÓGICA PARA PODER ACCEDER A LOS DATOS.
 * */

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
            RequestsCreated(db = db, auth = auth, viewModel = viewModel, request = requestCreated)
            Spacer(modifier = Modifier.width(32.dp))
            RecientlyActivitySection(currentActivity = currentActivity)
        }
        EmergencyButton(modifier = Modifier.align(Alignment.BottomCenter))
    }
}

/**
 * COMPONENTE QUE MUESTRA EL NOMBRE DEL USUARIO AUTENTICADO.
 *
 * @param username NOMBRE DEL USUARIO AUTENTICADO.
 * */

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

/**
 * COMPONENTE QUE MUESTRA LAS TAREAS COMPLETADAS Y EL PROGRESO.
 *
 * @param totalCompletedTasks LISTA CON EL NÚMERO DE TAREAS COMPLETADAS POR DÍA DE LA SEMANA.
 * @param tasksInProgress NÚMERO DE TAREAS EN PROGRESO.
 * */

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

/**
 * COMPONENTE QUE MUESTRA LAS SOLICITUDES CREADAS, ADEMÁS PERMITE ELIMINARLA O VER DETALLES DE LA SOLICITUD.
 *
 * @param db INSTANCIA DE FIREBASEFIRESTORE QUE PERMITE LEER LA INFORMACIÓN DEL USUARIO.
 * @param auth INSTANCIA DE FIREBASE PARA OBTENER EL USUARIO ACTUAL.
 * @param viewModel VIEWMODEL QUE TIENE LA LÓGICA PARA PODER ACCEDER A LOS DATOS.
 * @param request LISTA DE SOLICITUDES DEL USUARIO.
 * */

@Composable
private fun RequestsCreated(
    db: FirebaseFirestore,
    auth: FirebaseAuth,
    viewModel: FirestoreDataSource,
    request: List<Request>
) {
    var deleteIsVisible by remember { mutableStateOf(false) }
    var requestIsVisible by remember { mutableStateOf(false) }
    var userRequestId by remember { mutableStateOf("") }
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
                text = "Solicitudes creadas", style = MaterialTheme.typography.titleLarge,
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
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
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
                                        modifier = Modifier.fillMaxWidth(0.8f),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
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
                            Button(
                                onClick = {
                                    requestIsVisible = true
                                    userRequestId = item.id
                                },
                                modifier = Modifier,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(color = 0xFF2196F3))
                            ) {
                                Text(
                                    text = "Ver solicitud",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Medium
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
    if (requestIsVisible) {
        AlertRequest(
            auth = auth,
            db = db,
            viewModel = viewModel,
            onDismiss = { requestIsVisible = false },
            onAccept = { requestIsVisible = false },
            id = userRequestId
        )
    }
}

/**
 * COMPONENTE QUE MUESTRA LA INFORMACIÓN DE LA SOLICITUD CREADA POR EL USUARIO, PUEDE LLAMAR O VER LA UBICACIÓN DEL USUARIO QUE HA ACEPTADO LA SOLICITUD.
 *
 * @param db INSTANCIA DE FIREBASEFIRESTORE QUE PERMITE LEER LA INFORMACIÓN DEL USUARIO.
 * @param auth INSTANCIA DE FIREBASE PARA OBTENER EL USUARIO ACTUAL.
 * @param viewModel VIEWMODEL QUE TIENE LA LÓGICA PARA PODER ACCEDER A LOS DATOS.
 * @param onDismiss CALLBACK QUE SE EJECUTA AL CANCELAR EL DIÁLOGO.
 * @param onAccept CALLBACK QUE SE EJECUTA AL ACEPTAR.
 * @param id ID DE LA SOLICITUD.
 * */

@Composable
private fun AlertRequest(
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    viewModel: FirestoreDataSource,
    onDismiss: () -> Unit,
    onAccept: () -> Unit,
    id: String
) {
    val user = auth.currentUser
        ?: run {
            return
        }
    val uid = user.uid

    var isHelper by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(true) }
    var currentRequest by remember { mutableStateOf<Request?>(null) }
    val context = LocalContext.current

    data class Item(
        val id: String,
        val acceptedBy: String,
        val createdBy: String,
        val dateCreated: String,
        val description: String,
        val address: String,
        val phone: String,
        val title: String,
        val username: String,
    )

    LaunchedEffect(uid) {
        val snap = db.collection(Tables.users)
            .whereEqualTo("uid", uid)
            .get()
            .await()
        isHelper = snap.documents
            .firstOrNull()
            ?.getBoolean("helper")
            ?: false
        viewModel.getCurrentRequest(db = db, id = id) { request ->
            currentRequest = request
            loading = false
        }
    }

    if (loading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val item = currentRequest?.let { task ->
        val dateObject = task.dateCreated.toDate()
        val formatDate = SimpleDateFormat("dd/MM/yyyy hh:mm", Locale.getDefault())
        val dateTask = formatDate.format(dateObject)

        Item(
            id = task.id,
            acceptedBy = task.acceptedByUid ?: "Usuario desconocido",
            createdBy = task.createdByUid,
            dateCreated = dateTask,
            description = task.description,
            address = if (isHelper) task.olderAddress
                ?: "Dirección desconocida" else task.helperAddress ?: "Dirección desconocida",
            phone = if (isHelper) task.olderPhone else task.helperPhone,
            title = task.title,
            username = if (isHelper) task.olderUsername
                ?: "Usuario desnococido" else task.helperUsername ?: "Usuario desnococido"
        )
    }



    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = { TextButton(onClick = { onAccept() }) { Text(text = "Aceptar") } },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Ver solicitud",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                item?.let {
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.title,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .padding(end = 8.dp)
                                        .weight(1f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = item.dateCreated,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Gray,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.widthIn(min = 100.dp)
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(R.drawable.profile),
                                    contentDescription = "Icono del perfil",
                                    modifier = Modifier.size(48.dp),
                                    tint = Color.Gray
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Aceptado por:",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = item.username,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Normal
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = "Dirección:",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = item.address,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )
                            }
                            Button(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    callPhone(context = context, phone = item.phone)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(
                                        0xFF2196F3
                                    )
                                )
                            ) {
                                Text(
                                    text = "Llamar",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Button(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    openMaps(context = context, ubication = item.address)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(
                                        0xFF009688
                                    )
                                )
                            ) {
                                Text(
                                    text = "Ubicación",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                }
            }
        })
}

/**
 * COMPONENTE QUE MUESTRA UNA ALERTA PARA CONFIRMAR LA ELIMINACIÓN DE LA SOLICITUD.
 *
 * @param onDismiss CALLBACK QUE SE EJECUTA AL CANCELAR EL DIÁLOGO.
 * @param onAccept CALLBACK QUE SE EJECUTA AL CONFIRMAR LA ELIMINACIÓN DE LA SOLICITUD.
 * */

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

/**
 * COMPONENTE QUE MUESTRA LA ACTIVIDAD RECIENTE DEL USUARIO.
 *
 * @param currentActivity LISTA DE LA ACTIVIDAD RECIENTE DEL USUARIO.
 * */

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
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
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

/**
 * COMPONENTE QUE CONTIENE EL BOTÓN DE EMERGENCIA QUE PERMITE REALIZAR UNA LLAMADA AL 061.
 *
 * @param modifier MODIFICADOR QUE PERMITE PERSONALIZAR EL LAYOUT.
 * */

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