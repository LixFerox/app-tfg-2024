package com.lixferox.app_tfg_2024.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lixferox.app_tfg_2024.R
import com.lixferox.app_tfg_2024.data.model.Tables
import com.lixferox.app_tfg_2024.model.Request

@Composable
fun NavBar(
    navigateToHome: () -> Unit,
    navigateToSearch: () -> Unit,
    navigateToTasks: () -> Unit,
    navigateToStats: () -> Unit,
    indexBar: Int,
    auth: FirebaseAuth,
    db: FirebaseFirestore
) {
    var showCreateRequest by remember { mutableStateOf(false) }

    data class ItemNavBar(
        val title: String,
        val icon: Int,
        val onClick: () -> Unit
    )

    val optionsMenu = listOf(
        ItemNavBar(title = "Inicio", icon = R.drawable.home, onClick = { navigateToHome() }),
        ItemNavBar(title = "Buscar", icon = R.drawable.search, onClick = { navigateToSearch() }),
        ItemNavBar(
            title = "Crear",
            icon = R.drawable.add_task,
            onClick = { showCreateRequest = true }),
        ItemNavBar(title = "Tareas", icon = R.drawable.task, onClick = { navigateToTasks() }),
        ItemNavBar(title = "Análisis", icon = R.drawable.stats, onClick = { navigateToStats() })
    )
    var selectedItem by remember { mutableStateOf(optionsMenu[indexBar].title) }
    NavigationBar {
        optionsMenu.map { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(item.icon),
                        contentDescription = "Icono de la sección de ${item.title}"
                    )
                },
                onClick = {
                    selectedItem = item.title
                    item.onClick()
                },
                selected = selectedItem == item.title,
                label = { Text(item.title) }
            )
        }
    }
    if (showCreateRequest) {
        CreateRequest(onDismiss = { showCreateRequest = false }, auth, db)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateRequest(onDismiss: () -> Unit, auth: FirebaseAuth, db: FirebaseFirestore) {
    val uid = auth.currentUser?.uid
    var isHelper by remember { mutableStateOf<Boolean?>(null) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var urgency by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val opciones = listOf("Alta", "Media", "Baja")

    LaunchedEffect(uid) {
        uid.let {
            db.collection(Tables.users).whereEqualTo("uid", uid).get()
                .addOnCompleteListener { task ->
                    val currentUser = task.result.documents.firstOrNull()
                    if (currentUser != null) {
                        isHelper = currentUser.getBoolean("helper") ?: false
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

    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(onClick = {
                createRequest(title, description, urgency, isHelper!!, uid!!, db, onDismiss)
                onDismiss()
            }) { Text(text = "Crear") }
        },
        dismissButton = { TextButton(onClick = { onDismiss() }) { Text(text = "Cancelar") } },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Crear petición",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(text = "Título") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF2196F3),
                        unfocusedBorderColor = Color.LightGray
                    ),
                    singleLine = true
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(text = "Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF2196F3),
                        unfocusedBorderColor = Color.LightGray
                    ),
                    singleLine = true
                )
                if (!isHelper!!) {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = urgency,
                            onValueChange = { urgency = it },
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
                                DropdownMenuItem(
                                    text = { Text(opcion) },
                                    onClick = {
                                        urgency = opcion
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

            }
        }
    )
}

private fun createRequest(
    title: String,
    description: String,
    urgency: String,
    isHelper: Boolean,
    uid: String,
    db: FirebaseFirestore,
    onSuccess: () -> Unit
) {

    db.collection(Tables.users).whereEqualTo("uid", uid).get().addOnCompleteListener { task ->
        val document = task.result.documents.firstOrNull()
        if (document != null) {
            val username = document.getString("username")
            val address = document.getString("address")
            val phone = document.getString("phone")

            val docRef = db.collection(Tables.requests).document()

            val currentRequest = Request(
                id = docRef.id,
                uidOlder = if (!isHelper) uid else "",
                uidHelper = if (isHelper) uid else "",
                title = title,
                description = description,
                urgency = if (!isHelper) urgency else "",
                olderUsername = if (!isHelper) username else "",
                helperUsername = if (isHelper) username else "",
                olderAddress = if (!isHelper) address else "",
                helperAddress = if (isHelper) address else "",
                olderPhone = if (!isHelper) phone ?: "" else "",
                helperPhone = if (isHelper) document.getString("phone") ?: "" else "",
                acceptedByUid = "",
                dateCreated = Timestamp.now(),
                status = "Creada"
            )
            db.collection(Tables.requests).add(currentRequest).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                }
            }
        }
    }
}