package com.lixferox.app_tfg_2024.common

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri

/**
 * METODO QUE PERMITE ABRIR GOOGLE MAPS CON LA DIRECCIÓN INDICADA.
 *
 * @param context CONTEXTO DESDE EL CUAL SE INICIA LA ACTIVIDAD.
 * @param ubication UBICACIÓN CON LAS COORDENADAS QUE SE QUIERE VISUALIZAR.
 * */

fun openMaps(context: Context, ubication: String) {
    val intent = Intent(Intent.ACTION_VIEW, "geo:0,0?q=$ubication".toUri())
    context.startActivity(intent)
}