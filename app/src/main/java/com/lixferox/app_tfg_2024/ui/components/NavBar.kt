package com.lixferox.app_tfg_2024.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lixferox.app_tfg_2024.R
import com.lixferox.app_tfg_2024.data.datasource.FirestoreDataSource
import com.lixferox.app_tfg_2024.data.datasource.createRequest
import com.lixferox.app_tfg_2024.data.model.Tables
import kotlinx.coroutines.tasks.await

/**
 * BARRA DE NAVEGACIÓN QUE PERMITE CAMBIAR ENTRE LAS DIFERENTES VENTANAS QUE HAY O CREAR UNA NUEVA SOLICITUD DE USUARIO.
 *
 * @param navigateToHome CALLBACK PARA NAVEGAR A LA VENTANA DE INICIO.
 * @param navigateToSearch CALLBACK PARA NAVEGAR A LA VENTANA DE BÚSQUEDA.
 * @param navigateToTasks CALLBACK PARA NAVEGAR A LA VENTANA DE TAREAS.
 * @param navigateToStats CALLBACK PARA NAVEGAR A LA VENTANA DE ESTADÍSTICAS.
 * @param indexBar ÍNDICE DE LA SECCIÓN QUE SE HA PULSADO ACTUALMENTE.
 * @param auth INSTANCIA DE FIREBASE PARA OBTENER EL USUARIO ACTUAL.
 * @param db INSTANCIA DE FIREBASEFIRESTORE QUE PERMITE LEER LA INFORMACIÓN DEL USUARIO.
 * @param viewModel VIEWMODEL QUE GESTIONA LA CREACIÓN DE PETICIONES.
 * */

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavBar(
    navigateToHome: () -> Unit,
    navigateToSearch: () -> Unit,
    navigateToTasks: () -> Unit,
    navigateToStats: () -> Unit,
    indexBar: Int,
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    viewModel: FirestoreDataSource
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
                        painter = painterResource(id = item.icon),
                        contentDescription = "Icono de la sección de ${item.title}"
                    )
                },
                onClick = {
                    selectedItem = item.title
                    item.onClick()
                },
                selected = selectedItem == item.title,
                label = { Text(text = item.title) }
            )
        }
    }
    if (showCreateRequest) {
        FormRequest(
            onDismiss = { showCreateRequest = false },
            auth = auth,
            db = db,
            viewModel = viewModel
        )
    }
}

/**
 * FORMULARIO QUE NOS PERMITE CREAR NUEVAS SOLICITUDES, CONTROLANDO EL LÍMITE DE SOLICITUDES POR USUARIO.
 *
 * @param onDismiss CALLBACK QUE SE EJECUTA AL CANCELAR LA TAREA.
 * @param auth INSTANCIA DE FIREBASE PARA OBTENER EL USUARIO ACTUAL.
 * @param db INSTANCIA DE FIREBASEFIRESTORE QUE PERMITE LEER LA INFORMACIÓN DEL USUARIO.
 * @param viewModel VIEWMODEL QUE GESTIONA LA CREACIÓN DE PETICIONES.
 * */

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormRequest(
    onDismiss: () -> Unit,
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    viewModel: FirestoreDataSource,
) {
    var showLimit by remember { mutableStateOf(false) }
    var emptyData by remember { mutableStateOf(false) }

    val user = auth.currentUser
        ?: run {
            onDismiss()
            return
        }
    val uid = user.uid

    var isHelper by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(uid) {
        val snap = db.collection(Tables.users)
            .whereEqualTo("uid", uid)
            .get()
            .await()
        isHelper = snap.documents
            .firstOrNull()
            ?.getBoolean("helper")
            ?: false
        loading = false
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

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var urgency by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val opciones = listOf("Alta", "Media", "Baja")

    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(onClick = {
                viewModel.limitCreated(auth = auth, db = db) { limit ->
                    if (limit == 3) {
                        showLimit = true
                    } else if (title.isEmpty() || description.isEmpty()) {
                        emptyData = true
                    } else {
                        createRequest(
                            title = title,
                            description = description,
                            urgency = urgency,
                            isHelper = isHelper,
                            uid = uid,
                            db = db,
                            auth = auth,
                            onDismiss
                        )
                    }
                }
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
                        focusedBorderColor = Color(color = 0xFF2196F3),
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
                        focusedBorderColor = Color(color = 0xFF2196F3),
                        unfocusedBorderColor = Color.LightGray
                    ),
                    singleLine = true
                )
                if (!isHelper) {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = urgency,
                            onValueChange = { urgency = it },
                            readOnly = true,
                            label = { Text(text = "Nivel de urgencia") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            modifier = Modifier
                                .menuAnchor(type = MenuAnchorType.PrimaryEditable, enabled = true)
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(color = 0xFF2196F3),
                                unfocusedBorderColor = Color.LightGray
                            ),
                            singleLine = true
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            opciones.map { option ->
                                DropdownMenuItem(
                                    text = { Text(text = option) },
                                    onClick = {
                                        urgency = option
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
    if (showLimit) {
        LimitRequest(onDismiss = {
            onDismiss()
            showLimit = false
        })
    }
    if (emptyData) {
        EmptyData(onDismiss = {
            onDismiss()
            emptyData = false
        })
    }
}

/**
 * COMPONENTE QUE LIMITA EL LÍMITE DE CREACIÓN DE SOLICITUDES POR USUARIO, MOSTRANDO UNA ALERTA.
 *
 * @param onDismiss CALLBACK QUE SE EJECUTA AL CERRAR EL DIÁLOGO.
 * */

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
                Text(text = "No puedes crear más solicitudes, espera a que se completen las creadas o elimina alguna.")
            }
        })
}

/**
 * COMPONENTE QUE MUESTRA UNA ALERTA SI NO SE RELLENAN TODOS LOS DATOS.
 *
 * @param onDismiss CALLBACK QUE SE EJECUTA AL CERRAR EL DIÁLOGO.
 * */

@Composable
private fun EmptyData(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = { TextButton(onClick = { onDismiss() }) { Text(text = "Aceptar") } },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Datos vacíos",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(text = "Debes de rellenar todos los campos")
            }
        })
}