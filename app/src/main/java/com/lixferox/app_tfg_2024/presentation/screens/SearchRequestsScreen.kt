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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lixferox.app_tfg_2024.R
import com.lixferox.app_tfg_2024.data.datasource.FirestoreDataSource
import com.lixferox.app_tfg_2024.data.model.Tables
import com.lixferox.app_tfg_2024.model.Request
import com.lixferox.app_tfg_2024.ui.components.Header
import com.lixferox.app_tfg_2024.ui.components.NavBar
import java.text.SimpleDateFormat
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SearchRequestsScreen(
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
                1,
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    modifier: Modifier = Modifier,
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    viewModel: FirestoreDataSource
) {
    val uid = auth.currentUser?.uid
    var isHelper by remember { mutableStateOf<Boolean?>(null) }
    var expanded by remember { mutableStateOf(false) }
    val opciones = listOf("Quitar filtro", "Alta", "Media", "Baja")
    var filter by remember { mutableStateOf("") }
    var listRequest by remember { mutableStateOf<List<Request>>(emptyList()) }

    LaunchedEffect(uid) {
        uid.let {
            db.collection(Tables.users).whereEqualTo("uid", uid).get()
                .addOnCompleteListener { task ->
                    val currentUser = task.result.documents.firstOrNull()
                    if (currentUser != null) {
                        isHelper = currentUser.getBoolean("helper") ?: false
                        viewModel.obtainAllRequest(db, isHelper!!) { requests ->
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

    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Tareas disponibles",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2196F3)
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (isHelper == true) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = filter,
                    onValueChange = { filter = it },
                    readOnly = true,
                    label = { Text("Nivel de urgencia") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier
                        .menuAnchor(type = MenuAnchorType.PrimaryEditable, enabled = true)
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF2196F3),
                        unfocusedBorderColor = Color.LightGray
                    ),
                    singleLine = true
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    opciones.map { opcion ->
                        if (opcion == "Quitar filtro") {
                            HorizontalDivider(modifier = Modifier.height(4.dp))
                            DropdownMenuItem(
                                text = {
                                    Text(opcion)
                                },
                                onClick = {
                                    filter = ""
                                    expanded = false
                                }
                            )
                            HorizontalDivider(modifier = Modifier.height(4.dp))
                        } else {
                            DropdownMenuItem(
                                text = {
                                    Text(opcion)
                                },
                                onClick = {
                                    filter = opcion
                                    expanded = false
                                }
                            )
                        }

                    }
                }
            }
        }
        ListRequest(filter, auth, db, viewModel, listRequest, isHelper!!)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun ListRequest(
    filter: String,
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    viewModel: FirestoreDataSource,
    listRequest: List<Request>,
    isHelper: Boolean
) {
    var showModal by remember { mutableStateOf(false) }
    var showLimit by remember { mutableStateOf(false) }
    var indexTask by remember { mutableStateOf<String?>(null) }


    data class ItemMenu(
        val id: String,
        val title: String,
        val description: String,
        val urgency: String,
        val username: String,
        val date: String
    )

    val listItems = listRequest.map { task ->
        val dateObject = task.dateCreated.toDate()
        val formatDate = SimpleDateFormat("dd/MM/yyyy hh:mm", Locale.getDefault())
        val dateTask = formatDate.format(dateObject)


        ItemMenu(
            id = task.id,
            title = task.title,
            description = task.description,
            urgency = if (task.urgency.isNullOrEmpty()) "Desconocido" else task.urgency,
            username = if (isHelper) task.olderUsername
                ?: "Usuario desconocido" else task.helperUsername ?: "Usuario desconocido",
            date = dateTask,
        )
    }
    val filteredItems = if (filter.isBlank()) {
        listItems
    } else {
        listItems.filter { it.urgency.equals(filter, ignoreCase = true) }
    }

    if (filteredItems.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No hay solicitudes disponibles",
                style = MaterialTheme.typography.titleMedium
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
        ) {
            items(filteredItems) { item ->
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.profile),
                                    contentDescription = "Icono del perfil de usuario",
                                    tint = Color.Gray,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .padding(end = 8.dp)
                                )
                                Text(
                                    text = item.username,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )
                            }
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.DarkGray
                            )
                        }
                        Text(
                            text = item.date,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.End)
                                .background(
                                    color = if (item.urgency.uppercase() == "ALTA") Color(0xFFF44336)
                                    else if (item.urgency.uppercase() == "MEDIA") Color(0xFFFF9800)
                                    else if (item.urgency.uppercase() == "BAJA") Color(0xFF4CAF50) else Color.Gray,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = item.urgency,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Button(
                            onClick = {
                                showModal = true
                                indexTask = item.id
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                        ) {
                            Text(
                                text = "Aceptar solicitud",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

            }
        }
    }
    if (showModal) {
        AcceptRequest(
            onDismiss = { showModal = false },
            onAccept = {
                viewModel.limitRequest(auth, db) { limit ->
                    if (limit == 3) {
                        showLimit = true
                    } else {
                        viewModel.acceptRequest(indexTask!!, db, auth)
                        showModal = false
                    }
                }
            },
            indexTask
        )
    }
    if (showLimit) {
        LimitRequest(onDismiss = {
            showLimit = false
            showModal = false
        })
    }
}

@Composable
private fun AcceptRequest(onDismiss: () -> Unit, onAccept: () -> Unit, index: String?) {
    if (index?.isNotEmpty() == true) {
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
                        text = "Aceptar solicitud",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = "¿Estás seguro que quieres aceptar la solicitud?")
                }
            })
    }
}

@Composable
private fun LimitRequest(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = { TextButton(onClick = { onDismiss() }) { Text(text = "Aceptar") } },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Limite solicitudes",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(text = "No puedes aceptar más solicitudes, primero completa alguna de las que ya tienes para poder aceptar una nueva")
            }
        })
}