package com.lixferox.app_tfg_2024.common

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri

/**
 * METODO QUE PERMITE LLAMAR AL NÚMERO DE TELÉFONO INDICADO.
 *
 * @param context CONTEXTO DESDE EL CUAL SE INICIA LA ACTIVIDAD.
 * @param phone NÚMERO DE TELÉFONO AL QUE SE QUIERE LLAMAR.
 * */

fun callPhone(context: Context, phone: String) {
    val intent = Intent(Intent.ACTION_DIAL, "tel:$phone".toUri())
    context.startActivity(intent)
}