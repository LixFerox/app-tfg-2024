package com.lixferox.app_tfg_2024.presentation.screens

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lixferox.app_tfg_2024.R
import com.lixferox.app_tfg_2024.common.callPhone
import com.lixferox.app_tfg_2024.data.datasource.FirestoreDataSource
import com.lixferox.app_tfg_2024.data.datasource.updatePuntuationStars
import com.lixferox.app_tfg_2024.data.model.Tables
import com.lixferox.app_tfg_2024.model.Request
import com.lixferox.app_tfg_2024.ui.components.Header
import com.lixferox.app_tfg_2024.ui.components.NavBar
import java.text.SimpleDateFormat
import java.util.Locale

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
                navigateToLogin,
                navigateToSettings,
                navigateToProfileInfo,
                auth,
                db
            )
        },
        bottomBar = {
            NavBar(
                navigateToHome,
                navigateToSearch,
                navigateToTask,
                navigateToStats,
                3,
                auth,
                db
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
                auth,
                db,
                viewModel
            )
        }
    }
}

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
            color = Color(0xFF2196F3)
        )
        Spacer(modifier = Modifier.height(16.dp))
        ListRequest(auth, db, viewModel)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun ListRequest(auth: FirebaseAuth, db: FirebaseFirestore, viewModel: FirestoreDataSource) {
    val context = LocalContext.current
    var showModal by remember { mutableStateOf(false) }
    var showPuntuation by remember { mutableStateOf(false) }
    var isHelper by remember { mutableStateOf<Boolean?>(null) }
    var textModal by remember { mutableStateOf("") }
    var onAcceptAction by remember { mutableStateOf({}) }
    var listRequest by remember { mutableStateOf<List<Request>>(emptyList()) }
    var indexTask by remember { mutableStateOf<String?>(null) }
    var currentUid by remember { mutableStateOf("") }

    val uid = auth.currentUser?.uid

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
        uid.let {
            db.collection(Tables.users).whereEqualTo("uid", uid).get()
                .addOnCompleteListener { task ->
                    val currentUser = task.result.documents.firstOrNull()
                    if (currentUser != null) {
                        isHelper = currentUser.getBoolean("helper") ?: false
                        viewModel.getAcceptedRequest(db, auth) { requests ->
                            listRequest = requests
                        }
                    }
                }
        }
    }

    if (isHelper == null) {
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
            username = if (isHelper == true) task.olderUsername
                ?: "Usuario desconocido" else task.helperUsername ?: "Desconocida",
            address = if (isHelper == true) task.olderAddress
                ?: "Usuario desconocido" else task.helperAddress ?: "Desconocida",
            date = dateTask,
            phone = if (isHelper == true) task.olderPhone else task.helperPhone,
            isHelper = isHelper!!,
            uid = if (isHelper == true) task.uidOlder!! else task.uidHelper!!
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
                                        indexTask = item.id
                                        viewModel.actionAcceptedRequest(
                                            indexTask!!,
                                            "complete",
                                            item.isHelper,
                                            db,
                                            auth
                                        )
                                        showModal = false
                                    }
                                    currentUid = item.uid
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(
                                        0xFF4CAF50
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
                                        indexTask = item.id
                                        viewModel.actionAcceptedRequest(
                                            indexTask!!,
                                            "cancel",
                                            item.isHelper,
                                            db,
                                            auth
                                        )
                                        showModal = false
                                    }
                                    currentUid = item.uid
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(
                                        0xFFF44336
                                    )
                                ),
                            ) {
                                Text(
                                    text = "Cancelar",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Button(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    callPhone(context, item.phone)
                                }, colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF2196F3)
                                )
                            ) {
                                Text(
                                    "Llamar",
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
        }, textModal)
    }
    if (showPuntuation) {
        AlertStartPuntuation(onAccept = { points ->
            showPuntuation = false
            updatePuntuationStars(db, currentUid, points)
        }, onDismiss = { showPuntuation = false })
    }
}

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
                                }, tint = if (i <= puntuation) Color(0xFFFFC107) else Color.Gray
                        )
                    }
                }
            }
        }
    )
}