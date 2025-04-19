package com.lixferox.app_tfg_2024.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lixferox.app_tfg_2024.R
import com.lixferox.app_tfg_2024.data.datasource.obtainUserInfo
import com.lixferox.app_tfg_2024.model.User
import com.lixferox.app_tfg_2024.common.openWeb

// SECCION DE NAVEGACION

@Composable
fun Header(
    modifier: Modifier = Modifier,
    navigateToLogin: () -> Unit,
    navigateToSettings: () -> Unit,
    navigateToProfileInfo: () -> Unit,
    auth: FirebaseAuth,
    db: FirebaseFirestore
) {
    val uid = auth.currentUser?.uid
    val context = LocalContext.current
    var isExpandedSettings by remember { mutableStateOf(false) }
    var isExpandedProfile by remember { mutableStateOf(false) }
    var currentUser by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(uid) {
        obtainUserInfo(auth = auth, db = db) { obtainedUser ->
            currentUser = obtainedUser
        }
    }

    val user = currentUser

    if (user == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val image by remember { mutableStateOf(user.image) }
    var isError by remember { mutableStateOf(false) }
    val loadBase = "data:image/webp;base64,$image"

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box {
            IconButton(onClick = { isExpandedSettings = !isExpandedSettings }) {
                Icon(
                    painter = painterResource(id = R.drawable.settings),
                    contentDescription = "Icono de los ajustes de usuario",
                    tint = Color.Gray
                )
            }
            DropMenuSettings(
                expanded = isExpandedSettings,
                onDismiss = { isExpandedSettings = false },
                onSettingsClick = { navigateToSettings() },
                onProfileClick = { navigateToProfileInfo() },
                onHelpClick = { openWeb(context = context, url = "http://vitalist.lixferox.es") }
            )
        }
        Box {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .clickable { isExpandedProfile = !isExpandedProfile }) {
                AsyncImage(
                    onError = { isError = true },
                    model = loadBase,
                    contentDescription = "Icono del perfil de usuario",
                    modifier = Modifier
                        .size(24.dp)
                        .clip(shape = CircleShape),
                    contentScale = ContentScale.Crop,
                    error = painterResource(R.drawable.profile),
                    colorFilter = if (isError) ColorFilter.tint(color = Color.Gray) else null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = user.username, style = MaterialTheme.typography.bodyMedium)
            }
            DropMenuProfile(
                expanded = isExpandedProfile,
                onDismiss = { isExpandedProfile = false },
                onLogoutClick = {
                    navigateToLogin()
                    auth.signOut()
                }
            )
        }

    }
}

// MENU DESPLEGABLE DE AJUSTES

@Composable
private fun DropMenuSettings(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onProfileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onHelpClick: () -> Unit
) {
    data class ItemMenu(
        val title: String,
        val icon: Int,
        val onClick: () -> Unit
    )

    val listItems = listOf(
        ItemMenu(title = "Perfil", icon = R.drawable.person, onClick = onProfileClick),
        ItemMenu(title = "Ajustes", icon = R.drawable.settings, onClick = onSettingsClick),
        ItemMenu(title = "Ayuda", icon = R.drawable.help, onClick = onHelpClick)
    )

    DropdownMenu(expanded = expanded, onDismissRequest = { onDismiss() }) {
        listItems.map { item ->
            DropdownMenuItem(
                text = { Text(text = item.title) },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = "Icono de la sección ${item.title}"
                    )
                },
                onClick = {
                    item.onClick()
                    onDismiss()
                })
            if (item.title.uppercase() == "AJUSTES") {
                HorizontalDivider()
            }
        }
    }
}

//MENU DESPLEGABLE PARA CERRAR LA SESION DEL USUARIO

@Composable
private fun DropMenuProfile(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onLogoutClick: () -> Unit,
) {
    data class ItemMenu(
        val title: String,
        val icon: Int
    )

    val listItems = listOf(
        ItemMenu(title = "Cerrar sesión", icon = R.drawable.logout)
    )

    DropdownMenu(expanded = expanded, onDismissRequest = { onDismiss() }) {
        listItems.map { item ->
            DropdownMenuItem(
                text = { Text(text = item.title) },
                onClick = { onLogoutClick() },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = "Icono de la sección ${item.title}"
                    )
                }
            )
        }
    }
}