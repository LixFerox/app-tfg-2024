package com.lixferox.app_tfg_2024.common

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri

// METODO QUE NOS PERMITE ABRIR UNA PAGINA WEB QUE LE PASEMOS COMO PARAMETRO

fun openWeb(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = url.toUri()
    context.startActivity(intent)
}