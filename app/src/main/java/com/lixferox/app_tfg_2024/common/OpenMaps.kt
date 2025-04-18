package com.lixferox.app_tfg_2024.common

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri

// METODO QUE NOS PERMITE ABRIR GOOGLE MAPS CON LA UBICACION QUE LE PASEMOS COMO PARAMETRO

fun openMaps(context: Context, ubication: String) {
    val intent = Intent(Intent.ACTION_VIEW, "geo:0,0?q=$ubication".toUri())
    context.startActivity(intent)
}