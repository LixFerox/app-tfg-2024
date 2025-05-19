package com.lixferox.app_tfg_2024.presentation.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lixferox.app_tfg_2024.R
import com.lixferox.app_tfg_2024.common.parseImage
import com.lixferox.app_tfg_2024.data.datasource.loginFirebase
import com.lixferox.app_tfg_2024.data.datasource.uploadDniVerify
import java.lang.Error

/**
 * VENTANA DE LOGIN DE LA APLICACIÓN.
 *
 * @param paddingValues ESPACIANDO QUE SE APLICA EN LAS VENTANAS DE LA APLICACIÓN.
 * @param auth INSTANCIA DE FIREBASE PARA OBTENER EL USUARIO ACTUAL.
 * @param navigateToSignUp CALLBACK PARA NAVEGAR A LA VENTANA DE REGISTRO.
 * @param navigateToHome CALLBACK PARA NAVEGAR A LA VENTANA DE INICIO.
 * */

@Composable
fun LoginScreen(
    paddingValues: PaddingValues,
    auth: FirebaseAuth,
    navigateToSignUp: () -> Unit,
    navigateToHome: () -> Unit,
    db: FirebaseFirestore
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(24.dp)
    ) {
        Logo(modifier = Modifier.align(Alignment.TopCenter))
        Form(
            modifier = Modifier.align(Alignment.Center),
            navigateToHome = navigateToHome,
            auth = auth,
            db = db
        )
        SignIn(
            modifier = Modifier.align(Alignment.BottomCenter),
            navigateToSignUp = navigateToSignUp
        )
    }
}

/**
 * COMPONENTE QUE MUESTRA EL LOGO DE LA APLICACIÓN.
 *
 * @param modifier MODIFICADOR QUE PERMITE PERSONALIZAR EL LAYOUT.
 * */

@Composable
private fun Logo(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.app_icon),
            contentDescription = "Logo de la aplicación",
            modifier = Modifier.size(120.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "¡Bienvenido!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color(color = 0xFF2196F3)
        )
    }
}

/**
 * COMPONENTE QUE CONTIENE EL FORMULARIO DONDE EL USUARIO INTRODUCIRÁ SUS DATOS PARA AUTENTICARSE.
 *
 * @param modifier MODIFICADOR QUE PERMITE PERSONALIZAR EL LAYOUT.
 * @param navigateToHome CALLBACK PARA NAVEGAR A LA VENTANA DE INICIO.
 * @param auth INSTANCIA DE FIREBASE PARA OBTENER EL USUARIO ACTUAL.
 * */

@Composable
private fun Form(
    modifier: Modifier = Modifier,
    navigateToHome: () -> Unit,
    auth: FirebaseAuth,
    db: FirebaseFirestore
) {
    var context = LocalContext.current
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var resetPassword by remember { mutableStateOf(false) }
    var showNotValidated by remember { mutableStateOf(false) }
    var currentUid by remember { mutableStateOf("") }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Email",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = Color.DarkGray
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
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
                singleLine = true
            )

            Text(
                text = "Contraseña",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = Color.DarkGray
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(color = 0xFF2196F3),
                    unfocusedBorderColor = Color.LightGray
                ),
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            painter = painterResource(
                                id = if (isPasswordVisible) R.drawable.eye_open else R.drawable.eye_close
                            ),
                            contentDescription = "Icono de ocultar la contraseña"
                        )
                    }
                },
                singleLine = true
            )
            Text(
                text = "¿Has olvidado tu contraseña?",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = Color(color = 0xFF2196F3), modifier = Modifier.clickable {
                    resetPassword = true
                }
            )
            Button(
                onClick = {
                    loginFirebase(
                        onSuccess = { navigateToHome() },
                        onError = { message -> errorMessage = message },
                        onNotValidated = { uid->
                            currentUid= uid
                            showNotValidated = true },
                        auth = auth,
                        email = email,
                        password = password,
                        db = db
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(color = 0xFF2196F3))
            ) {
                Text(
                    text = "Acceder",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
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
    if (resetPassword) {
        ResetPassword(
            auth,
            onDismiss = { resetPassword = false },
            onAccept = { resetPassword = false })
    }
    if (showNotValidated && currentUid.isNotEmpty()) {
        AlertUploadImage(onSend = { base64->
            uploadDniVerify(base64, currentUid, db)
            showNotValidated = false
        }, onDismiss = {
            showNotValidated = false
        }, context = context)
    }
}

/**
 * COMPONENTE QUE PERMITE NAVEGAR AL USUARIO A LA VENTANA DE REGISRO EN CASO DE NO TENER CUENTA.
 *
 * @param modifier MODIFICADOR QUE PERMITE PERSONALIZAR EL LAYOUT.
 * @param navigateToSignUp CALLBACK PARA NAVEGAR A LA VENTANA DE REGISTRO.
 * */

@Composable
private fun SignIn(modifier: Modifier = Modifier, navigateToSignUp: () -> Unit) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "¿No tienes una cuenta?",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.DarkGray
            )
            TextButton(onClick = { navigateToSignUp() }) {
                Text(
                    text = "Regístrate",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(color = 0xFF2196F3)
                )
            }
        }
    }
}

/**
 * COMPONENTE QUE MUESTA UNA ALERTA EN CASO DE QUE EL USUARIO QUIERA CAMBIAR SU CONTRASEÑA.
 *
 * @param auth INSTANCIA DE FIREBASE PARA OBTENER EL USUARIO ACTUAL.
 * @param onDismiss CALLBACK QUE SE EJECUTA AL CANCELAR EL DIÁLOGO.
 * @param onAccept CALLBACK QUE SE EJECUTA AL ACEPTAR.
 * */

@Composable
private fun ResetPassword(auth: FirebaseAuth, onDismiss: () -> Unit, onAccept: () -> Unit) {
    var email by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = { onDismiss() },
        dismissButton = { TextButton(onClick = { onDismiss() }) { Text(text = "Cancelar") } },
        confirmButton = {
            TextButton(onClick = {
                if (email.isNotEmpty()) {
                    auth.sendPasswordResetEmail(email)
                    onAccept()
                }
            }) { Text(text = "Aceptar") }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Cambiar contraseña",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
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
                    singleLine = true
                )
            }
        })
}

@Composable
fun AlertUploadImage(onSend: (String) -> Unit, onDismiss: () -> Unit, context: Context) {


    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var base64image by remember { mutableStateOf("") }

    val pickDNI = rememberLauncherForActivityResult(PickVisualMedia()) { uri ->
        if (uri != null) {
            selectedUri = uri
            base64image = parseImage(context, uri)
        }
    }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        dismissButton = { TextButton(onClick = { onDismiss() }) { Text(text = "Cancelar") } },
        confirmButton = {
            TextButton(onClick = {
                base64image.let {
                    onSend(it)
                }

            }) { Text(text = "Enviar") }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Enviar DNI",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tu cuenta aún no ha sido validada.\nSube una imagen de tu DNI y espera a que un administrador te valide la cuenta.",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Justify,
                    fontWeight = FontWeight.Normal
                )
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = {
                        pickDNI.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
                    },
                    modifier = Modifier,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(color = 0xFF2196F3))
                ) {
                    Text(
                        text = "Subir imagen",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        })
}