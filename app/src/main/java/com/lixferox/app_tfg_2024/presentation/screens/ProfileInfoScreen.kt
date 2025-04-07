package com.lixferox.app_tfg_2024.presentation.screens


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
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.lixferox.app_tfg_2024.data.model.Tables
import com.lixferox.app_tfg_2024.ui.components.Header
import com.lixferox.app_tfg_2024.ui.components.NavBar

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
            ProfileData(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 16.dp),
                auth, db, navigateToLogin
            )
        }
    }
}

@Composable
private fun ProfileData(
    modifier: Modifier = Modifier,
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    navigateToLogin: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

        UserHeader()
        LevelBar()
        Spacer(Modifier.height(8.dp))
        StatsSection()
        Spacer(Modifier.height(8.dp))
        InfoSection()
        RequestButton(auth, db, onError = {}, onSuccess = { navigateToLogin() })
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

@Composable
private fun UserHeader() {
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
        Text(
            text = "Jorge Rosado Julián!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF2196F3)
        )
    }

    Text(
        text = "Nivel 0",
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.SemiBold,
        color = Color.DarkGray
    )
}

@Composable
private fun LevelBar() {
    var level by remember { mutableStateOf(20f) }

    LinearProgressIndicator(
        progress = { level / 100f },
        modifier = Modifier
            .fillMaxWidth(0.5f)
            .height(8.dp),
        color = Color(0xFF4CAF50),
        trackColor = Color.LightGray,
    )
}

@Composable
private fun StatsSection() {
    data class ItemMenu(
        val title: String,
        val data: String,
        val icon: Int,
        val color: Color
    )

    val listItems = listOf(
        ItemMenu(title = "5/5", data = "Puntuación", R.drawable.points, Color(0xFFFFC107)),
        ItemMenu(title = "32", data = "Peticiones", R.drawable.request, Color(0xFF00BCD4)),
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

@Composable
private fun InfoSection() {
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
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(32.dp),
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
                        tint = Color(0xFF3F51B5)
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "1",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "año",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
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
                        contentDescription = "Icono de la sección antiguedad",
                        tint = Color(0xFFFF9800)
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                }

            }
        }
    }
}

@Composable
private fun RequestButton(
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
            text = { Text("¿Estás seguro de que deseas eliminar tu cuenta? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        val uid = auth.currentUser!!.uid
                        db.collection(Tables.users).whereEqualTo("uid", uid).get()
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val userObtained = task.result.documents
                                    if (userObtained != null) {
                                        userObtained.map { user ->
                                            db.collection(Tables.users).document(user.id).delete()
                                                .addOnCompleteListener { delete ->
                                                    if (delete.isSuccessful) {
                                                        auth.currentUser?.delete()
                                                            ?.addOnCompleteListener { task ->
                                                                if (task.isSuccessful) {
                                                                    onSuccess()
                                                                } else {
                                                                    onError(
                                                                        task.exception?.message
                                                                            ?: "Ha ocurrido un error al eliminar la cuenta"
                                                                    )
                                                                }
                                                            }

                                                    } else {
                                                        onError(
                                                            task.exception?.message
                                                                ?: "Ha ocurrido un error al eliminar la cuenta"
                                                        )
                                                    }
                                                }
                                        }
                                    }
                                } else {
                                    onError(
                                        task.exception?.message
                                            ?: "Ha ocurrido un error al eliminar la cuenta"
                                    )
                                }
                            }

                        showDeleteDialog = false
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancelar")
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
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
    ) {
        Text(
            text = "Eliminar cuenta",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
}


