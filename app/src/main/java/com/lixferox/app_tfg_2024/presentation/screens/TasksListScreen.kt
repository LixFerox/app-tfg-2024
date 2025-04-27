package com.lixferox.app_tfg_2024.presentation.screens

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lixferox.app_tfg_2024.R
import com.lixferox.app_tfg_2024.common.callPhone
import com.lixferox.app_tfg_2024.common.openMaps
import com.lixferox.app_tfg_2024.data.datasource.FirestoreDataSource
import com.lixferox.app_tfg_2024.data.datasource.updatePuntuationStars
import com.lixferox.app_tfg_2024.data.model.Tables
import com.lixferox.app_tfg_2024.model.Request
import com.lixferox.app_tfg_2024.ui.components.Header
import com.lixferox.app_tfg_2024.ui.components.NavBar
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale

// VENTANA DE LAS TAREAS ACEPTADAS POR EL USUARIO

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TasksListScreen(
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
                indexBar = 3,
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
                    .padding(horizontal = 16.dp),
                auth = auth,
                db = db,
                viewModel = viewModel
            )
        }
    }
}

// COMPONENTE QUE IMPORTARA TODAS LAS SECCIONES A MOSTRAR

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun Content(
    modifier: Modifier = Modifier,
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    viewModel: FirestoreDataSource
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Tareas aceptadas",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(color = 0xFF2196F3)
        )
        Spacer(modifier = Modifier.height(16.dp))
        ListRequest(auth = auth, db = db, viewModel = viewModel)
    }
}

// COMPONENTE QUE MUESTRA LA LISTA DE PETICIONES ACEPTADAS DEL USUARIO

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun ListRequest(auth: FirebaseAuth, db: FirebaseFirestore, viewModel: FirestoreDataSource) {
    val user = auth.currentUser
        ?: run {
            return
        }
    val uid = user.uid


    var isHelper by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(true) }
    var listRequest by remember { mutableStateOf<List<Request>>(emptyList()) }


    val context = LocalContext.current
    var showModal by remember { mutableStateOf(false) }
    var showPuntuation by remember { mutableStateOf(false) }
    var textModal by remember { mutableStateOf("") }
    var currentUid by remember { mutableStateOf("") }
    var onAcceptAction by remember { mutableStateOf({}) }

    data class ItemMenu(
        val id: String,
        val title: String,
        val description: String,
        val username: String,
        val address: String,
        val date: String,
        val phone: String,
        val isHelper: Boolean,
        val uid: String
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
        viewModel.getAcceptedRequest(db = db, auth = auth) { request ->
            listRequest = request
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

    val listItems = listRequest.map { task ->
        val dateObject = task.dateCreated.toDate()
        val formatDate = SimpleDateFormat("dd/MM/yyyy hh:mm", Locale.getDefault())
        val dateTask = formatDate.format(dateObject)

        ItemMenu(
            id = task.id,
            title = task.title,
            description = task.description,
            username = if (isHelper) task.olderUsername
                ?: "Usuario desconocido" else task.helperUsername ?: "Usuario desconocido",
            address = if (isHelper) task.olderAddress
                ?: "Dirección desconocida" else task.helperAddress ?: "Dirección desconocida",
            date = dateTask,
            phone = if (isHelper) task.olderPhone else task.helperPhone,
            isHelper = isHelper,
            uid = if (isHelper) task.uidOlder ?: "Usuario desconocido" else task.uidHelper
                ?: "Usuario desconocido"
        )
    }

    if (listItems.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No hay solicitudes aceptadas",
                style = MaterialTheme.typography.titleMedium
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
        ) {
            items(listItems) { item ->
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
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 8.dp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = item.date,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.End,
                                modifier = Modifier.padding(start = 8.dp),
                                color = Color.Gray
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(R.drawable.profile),
                                contentDescription = "Icono del perfil",
                                modifier = Modifier
                                    .size(80.dp), tint = Color.Gray
                            )
                            Row {
                                Text(
                                    text = "Nombre: ",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                                Text(
                                    text = item.username,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                        }
                        Row {
                            Text(
                                text = "Dirección: ",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = item.address,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Gray
                            )

                        }
                        Row {
                            Text(
                                text = "Descripción: ",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = item.description,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Gray
                            )

                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = {
                                    showModal = true
                                    textModal = "Completar"
                                    onAcceptAction = {
                                        viewModel.actionAcceptedRequest(
                                            index = item.id,
                                            action = "complete",
                                            isHelper = item.isHelper,
                                            db = db,
                                            auth = auth
                                        )
                                        showModal = false
                                    }
                                    currentUid = item.uid
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(
                                        color = 0xFF4CAF50
                                    )
                                ),
                            ) {
                                Text(
                                    text = "Completar",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Button(
                                onClick = {

                                    showModal = true
                                    textModal = "Cancelar"
                                    onAcceptAction = {
                                        viewModel.actionAcceptedRequest(
                                            index = item.id,
                                            action = "cancel",
                                            isHelper = item.isHelper,
                                            db = db,
                                            auth = auth
                                        )
                                        showModal = false
                                    }
                                    currentUid = item.uid
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(
                                        color = 0xFFF44336
                                    )
                                ),
                            ) {
                                Text(
                                    text = "Cancelar",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp)
                        ) {
                            Button(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    callPhone(context = context, phone = item.phone)
                                }, colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(color = 0xFF2196F3)
                                )
                            ) {
                                Text(
                                    text = "Llamar",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Button(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    openMaps(context = context, ubication = item.address)
                                }, colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(color = 0xFF009688)
                                )
                            ) {
                                Text(
                                    text = "Ubicación",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }
                }

            }
        }
    }
    if (showModal) {
        AlertRequest(onDismiss = { showModal = false }, onAccept = {
            onAcceptAction()
            showPuntuation = true
        }, textModal = textModal)
    }
    if (showPuntuation) {
        AlertStartPuntuation(onAccept = { points ->
            showPuntuation = false
            updatePuntuationStars(db = db, uid = currentUid, points = points)
        }, onDismiss = { showPuntuation = false })
    }
}

// COMPONENTE QUE MUESTRA UNA ALERTA EN CASO DE COMPLETAR O CANCELAR LA TAREA

@Composable
private fun AlertRequest(onDismiss: () -> Unit, onAccept: () -> Unit, textModal: String) {
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
                    text = "$textModal tarea",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(text = "¿Estás seguro que quieres ${textModal.uppercase()} la tarea?")
            }
        })
}

@Composable
private fun AlertStartPuntuation(onAccept: (Int) -> Unit, onDismiss: () -> Unit) {
    var puntuation by remember { mutableIntStateOf(0) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(onClick = {
                onAccept(puntuation)
                println("Ha pulsado $puntuation")
            }) { Text(text = "Aceptar") }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Puntuar usuario",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    for (i in 1..5) {
                        Icon(
                            painter = painterResource(R.drawable.points),
                            contentDescription = "Icono de la estrella $i",
                            modifier = Modifier
                                .size(36.dp)
                                .clickable {
                                    puntuation = i
                                },
                            tint = if (i <= puntuation) Color(color = 0xFFFFC107) else Color.Gray
                        )
                    }
                }
            }
        }
    )
}