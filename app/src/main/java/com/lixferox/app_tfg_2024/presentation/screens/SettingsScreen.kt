package com.lixferox.app_tfg_2024.presentation.screens

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lixferox.app_tfg_2024.common.UploadImage
import com.lixferox.app_tfg_2024.common.parseImage
import com.lixferox.app_tfg_2024.data.datasource.FirestoreDataSource
import com.lixferox.app_tfg_2024.data.datasource.obtainUserInfo
import com.lixferox.app_tfg_2024.data.datasource.updateInfo
import com.lixferox.app_tfg_2024.model.User
import com.lixferox.app_tfg_2024.ui.components.Header
import com.lixferox.app_tfg_2024.ui.components.NavBar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * VENTANA DE LOS AJUSTES DEL USUARIO.
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
fun SettingsScreen(
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
            FormOptions(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()), auth = auth, db = db
            )
        }
    }
}

/**
 * COMPONENTE QUE CONTIENE EL FORMULARIO DONDE ESTÁN LOS DATOS DEL USUARIO.
 *
 * @param modifier MODIFICADOR QUE PERMITE PERSONALIZAR EL LAYOUT.
 * @param auth INSTANCIA DE FIREBASE PARA OBTENER EL USUARIO ACTUAL.
 * @param db INSTANCIA DE FIREBASEFIRESTORE QUE PERMITE LEER LA INFORMACIÓN DEL USUARIO.
 * */

@Composable
private fun FormOptions(modifier: Modifier = Modifier, auth: FirebaseAuth, db: FirebaseFirestore) {
    val context = LocalContext.current
    var showModal by remember { mutableStateOf(false) }

    var currentUser by remember { mutableStateOf<User?>(null) }
    LaunchedEffect(auth.currentUser) {
        obtainUserInfo(auth = auth, db = db) { obtainedUser ->
            currentUser = obtainedUser
        }
    }

    if (currentUser == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val user = requireNotNull(currentUser)
    val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val birthCovert = format.format(user.birth.toDate()).toString()
    var pickerIsVisible by remember { mutableStateOf(false) }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var image by remember { mutableStateOf(user.image) }

    LaunchedEffect(imageUri) {
        imageUri?.let { uri ->
            parseImage(context, uri).let { parse ->
                image = parse
            }
        }
    }

    var email by remember { mutableStateOf(user.email) }
    var username by remember { mutableStateOf(user.username) }
    var phone by remember { mutableStateOf(user.phone) }
    var dni by remember { mutableStateOf(user.dni) }
    var birth by remember { mutableStateOf(birthCovert) }
    var address by remember { mutableStateOf(user.address) }

    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Configuración de tu cuenta",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(color = 0xFF2196F3)
        )
        UploadImage(image) { picture ->
            imageUri = picture
        }
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(text = "Email") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(color = 0xFF2196F3),
                unfocusedBorderColor = Color.LightGray
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            enabled = false,
            readOnly = true,
            singleLine = true
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text(text = "Usuario") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF2196F3),
                unfocusedBorderColor = Color.LightGray
            ),
            singleLine = true
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text(text = "Teléfono") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(color = 0xFF2196F3),
                unfocusedBorderColor = Color.LightGray
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next
            ),
            singleLine = true,
        )
        Spacer(Modifier.height(8.dp))
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = birth,
                onValueChange = { },
                label = { Text(text = "Fecha de nacimiento (dd/MM/yyyy)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(color = 0xFF2196F3),
                    unfocusedBorderColor = Color.LightGray
                ),
                readOnly = true,
                singleLine = true
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { pickerIsVisible = true }
            )
        }

        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text(text = "Dirección") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(color = 0xFF2196F3),
                unfocusedBorderColor = Color.LightGray
            ),
            singleLine = true
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = dni,
            onValueChange = { dni = it },
            label = { Text(text = "DNI") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(color = 0xFF2196F3),
                unfocusedBorderColor = Color.LightGray
            ),
            enabled = false,
            readOnly = true,
            singleLine = true,
        )
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = { showModal = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(color = 0xFF2196F3))
        ) {
            Text(
                text = "Guardar cambios",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
    if (showModal) {
        ChangeSettings(onDismiss = { showModal = false }, onAccept = {
            showModal = false
            updateInfo(
                auth = auth,
                db = db,
                email = email,
                username = username,
                phone = phone,
                birth = birth,
                address = address,
                dni = dni,
                image = image
            )
        })
    }
    if (pickerIsVisible) {
        BirthPicker(onDismiss = { pickerIsVisible = false }, onAccept = { task ->
            birth = task
            pickerIsVisible = false
        })
    }
}

/**
 * COMPONENTE QUE MUESTRA UNA ALERTA DONDE PERMITE ELEGIR UNA FECHA DE NACIMIENTO NUEVA.
 *
 * @param onDismiss CALLBACK QUE SE EJECUTA AL CANCELAR EL DIÁLOGO.
 * @param onAccept CALLBACK QUE SE EJECUTA AL ELEGIR UNA FECHA.
 * */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BirthPicker(onDismiss: () -> Unit, onAccept: (String) -> Unit) {
    val datePickerState = rememberDatePickerState()
    DatePickerDialog(onDismissRequest = { onDismiss() }, confirmButton = {
        TextButton(onClick = {
            val birth = datePickerState.selectedDateMillis
            val birthFormatted = if (birth != null) {
                val date = Date(birth)
                val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                format.format(date)
            } else {
                ""
            }
            onAccept(birthFormatted)
        }) { Text(text = "Aceptar") }
    }, dismissButton = { TextButton(onClick = { onDismiss() }) { Text(text = "Cancelar") } }) {
        DatePicker(state = datePickerState)
    }
}

/**
 * COMPONENTE QUE MUESTRA UNA ALERTA INDICANDO QUE SI SE QUIEREN GUARDAR LOS CAMBIOS.
 *
 * @param onDismiss CALLBACK QUE SE EJECUTA AL CANCELAR EL DIÁLOGO.
 * @param onAccept CALLBACK QUE SE EJECUTA AL CONFIRMAR LA ACCIÓN.
 * */

@Composable
private fun ChangeSettings(onDismiss: () -> Unit, onAccept: () -> Unit) {
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
                    text = "Guardar cambios",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(text = "¿Estás seguro que quieres guardar los cambios?")
            }
        })
}