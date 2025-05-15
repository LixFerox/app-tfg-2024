package com.lixferox.app_tfg_2024.common

import android.net.Uri
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.lixferox.app_tfg_2024.R
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale

/**
 * METODO QUE PERMITE MOSTRAR LA IMAGEN DE PERFIL DEL USUARIO DESDE UNA CADENA
 * DE BASE64, ADEMÁS DE PERMITIR AL USUARIO SELECCIONAR UNA NUEVA IMAGEN DE PERFIL DE SU GALERÍA.
 *
 * @param imageUri CADENA DE BASE64 DE LA IMAGEN QUE SE MOSTRARÁ.
 * @param onUpload FUNCIÓN CALLBACK QUE RECIBE LA IMAGEN SELECCIONADA POR EL USUARIO.
 * */

@Composable
fun UploadImage(imageUri: String, onUpload: (Uri) -> Unit) {

    var isError by remember { mutableStateOf(false) }

    val pickMedia = rememberLauncherForActivityResult(PickVisualMedia()) { uri ->
        if (uri != null) {
            onUpload(uri)
        }
    }

    val loadBase = "data:image/webp;base64,$imageUri"
    AsyncImage(
        onError = { isError = true },
        model = loadBase,
        contentDescription = "Icono del perfil de usuario",
        modifier = Modifier
            .size(120.dp)
            .clip(shape = CircleShape),
        contentScale = ContentScale.Crop,
        error = painterResource(R.drawable.profile),
        colorFilter = if (isError) ColorFilter.tint(color = Color.Gray) else null
    )
    Button(
        onClick = {
            pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
        },
        modifier = Modifier,
        colors = ButtonDefaults.buttonColors(containerColor = Color(color = 0xFF2196F3))
    ) {
        Text(
            text = "Cambiar imagen",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium
        )
    }
}