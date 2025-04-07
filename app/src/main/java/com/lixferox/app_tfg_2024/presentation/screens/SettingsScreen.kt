package com.lixferox.app_tfg_2024.presentation.screens

import android.util.Log
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lixferox.app_tfg_2024.data.datasource.obtainUserInfo
import com.lixferox.app_tfg_2024.data.model.Tables
import com.lixferox.app_tfg_2024.domain.model.User
import com.lixferox.app_tfg_2024.ui.components.Header
import com.lixferox.app_tfg_2024.ui.components.NavBar
import java.text.SimpleDateFormat
import java.util.Locale

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
    db: FirebaseFirestore
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
                0,
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
            FormOptions(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()), auth, db
            )
        }
    }
}

@Composable
private fun FormOptions(modifier: Modifier = Modifier, auth: FirebaseAuth, db: FirebaseFirestore) {
    var showModal by remember { mutableStateOf(false) }

    var currentUser by remember { mutableStateOf<User?>(null) }
    LaunchedEffect(auth.currentUser) {
        obtainUserInfo(auth, db) { obtainedUser ->
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
    val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val birthCovert = format.format(currentUser!!.birth.toDate()).toString()

    var email by remember { mutableStateOf(currentUser!!.email) }
    var username by remember { mutableStateOf(currentUser!!.username) }
    var phone by remember { mutableStateOf(currentUser!!.phone) }
    var birth by remember { mutableStateOf(birthCovert) }
    var address by remember { mutableStateOf(currentUser!!.address) }

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
            color = Color(0xFF2196F3)
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(text = "Email") },
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
                focusedBorderColor = Color(0xFF2196F3),
                unfocusedBorderColor = Color.LightGray
            ),
            singleLine = true
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = birth,
            onValueChange = { birth = it },
            label = { Text(text = "Fecha de nacimiento (dd/MM/yyyy)") },
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
            value = address,
            onValueChange = { address = it },
            label = { Text(text = "Dirección") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF2196F3),
                unfocusedBorderColor = Color.LightGray
            ),
            singleLine = true
        )
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = { showModal = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
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
            UpdateInfo(auth, db, email, username, phone, birth, address)
        })
    }
}

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

private fun UpdateInfo(
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    email: String,
    username: String,
    phone: String,
    birth: String,
    address: String
) {
    val uid = auth.currentUser?.uid
    db.collection(Tables.users).whereEqualTo("uid", uid).get().addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val document = task.result.documents.firstOrNull()
            if (document != null) {
                val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val convertBirth = format.parse(birth)
                document.reference.update(
                    "email", email,
                    "username", username,
                    "phone", phone,
                    "birth", convertBirth,
                    "address", address
                ).addOnCompleteListener { update ->
                    if (update.isSuccessful) {
                        Log.i(
                            "UpdateInfo",
                            "Se han actualizado los datos del usuario correctamente"
                        )
                    }
                }
            }
        }
    }
}