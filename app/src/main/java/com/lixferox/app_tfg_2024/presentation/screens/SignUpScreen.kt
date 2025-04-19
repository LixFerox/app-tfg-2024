package com.lixferox.app_tfg_2024.presentation.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.lixferox.app_tfg_2024.common.verificationEmail
import com.lixferox.app_tfg_2024.data.datasource.createAccountFirebase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// VENTANA DE CREACION DE CUENTA

@Composable
fun SignUpScreen(
    paddingValues: PaddingValues,
    navigateToLogin: () -> Unit,
    auth: FirebaseAuth,
    db: FirebaseFirestore
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Logo()
        Form(navigateToLogin = navigateToLogin, auth = auth, db = db)
        SignUp(navigateToLogin = navigateToLogin)
    }
}

// COMPONENTE QUE MUESTRA EL LOGO DE LA APLICACION

@Composable
private fun Logo() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 24.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.app_icon),
            contentDescription = "Logo de la aplicación",
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "¡Regístrate para ser parte del cambio!",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color(color = 0xFF2196F3)
        )
    }
}

// COMPONENTE QUE TIENE EL FORMULARIO A RELLENAR CON LOS DATOS DEL USUARIO

@Composable
private fun Form(navigateToLogin: () -> Unit, auth: FirebaseAuth, db: FirebaseFirestore) {
    var pickerIsVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var email by rememberSaveable { mutableStateOf("") }
    var username by rememberSaveable { mutableStateOf("") }
    var birth by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var repassword by rememberSaveable { mutableStateOf("") }
    var isAssistant by rememberSaveable { mutableStateOf(false) }
    var address by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var dni by rememberSaveable { mutableStateOf("") }
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(text = "Email") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(color = 0xFF2196F3),
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                singleLine = true
            )
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text(text = "Nombre de usuario") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(color = 0xFF2196F3),
                ),
                singleLine = true
            )
            OutlinedTextField(
                value = dni,
                onValueChange = { dni = it },
                label = { Text(text = "DNI") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(color = 0xFF2196F3),
                ),
                singleLine = true
            )
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = birth,
                    onValueChange = { },
                    label = { Text(text = "Fecha de nacimiento (dd/MM/yyyy)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(color = 0xFF2196F3)
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
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text(text = "Dirección") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                trailingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.address),
                        contentDescription = "Icono de la ubicación"
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(color = 0xFF2196F3),
                ),
                singleLine = true
            )
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text(text = "Teléfono") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                trailingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.phone),
                        contentDescription = "Icono del teléfono"
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(color = 0xFF2196F3),
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                ),
                singleLine = true
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(text = "Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
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
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(color = 0xFF2196F3),
                ),
                singleLine = true
            )
            OutlinedTextField(
                value = repassword,
                onValueChange = { repassword = it },
                label = { Text(text = "Repetir contraseña") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
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
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(color = 0xFF2196F3),
                ),
                singleLine = true
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = isAssistant,
                    onCheckedChange = { isAssistant = it }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Soy ayudante")
            }

            Button(
                onClick = {
                    createAccountFirebase(
                        onSuccess = {
                            navigateToLogin()
                            verificationEmail(auth)
                        },
                        onError = { message -> errorMessage = message },
                        auth = auth,
                        db = db,
                        email = email,
                        password = password,
                        repassword = repassword,
                        username = username,
                        birth = birth,
                        isHelper = isAssistant,
                        address = address,
                        phone = phone,
                        dni = dni,
                        context = context
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(color = 0xFF2196F3))
            ) {
                Text(
                    text = "Crear cuenta",
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
    if (pickerIsVisible) {
        BirthPicker(onDismiss = { pickerIsVisible = false }, onAccept = { task ->
            birth = task
            pickerIsVisible = false
        })
    }
}

// COMPONENTE QUE EN CASO DE TENER CUENTA MUEVE AL USUARIO A LA PAGINA DE INICIO DE SESION

@Composable
private fun SignUp(navigateToLogin: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "¿Ya tienes una cuenta?",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.DarkGray
            )
            TextButton(onClick = { navigateToLogin() }) {
                Text(
                    text = "Inicia sesión",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(color = 0xFF2196F3)
                )
            }
        }
    }
}

// COMPONENTE QUE PERMITE ELEGIR UNA FECHA

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