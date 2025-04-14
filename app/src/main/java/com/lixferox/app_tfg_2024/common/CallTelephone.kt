package com.lixferox.app_tfg_2024.common

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri

// LLAMAR AL TELEFONO INDICADO

fun callPhone(context: Context, phone: String) {
    val intent = Intent(Intent.ACTION_DIAL, "tel:$phone".toUri())
    context.startActivity(intent)
}