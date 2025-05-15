package com.lixferox.app_tfg_2024.common

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri

/**
 * METODO QUE PERMITE ABRIR EL NAVEGADOR CON LA URL INDICADA.
 *
 * @param context CONTEXTO DESDE EL CUAL SE INICIA LA ACTIVIDAD.
 * @param url PÁGINA WEB A LA QUE SE QUIERE VISITAR.
 * */

fun openWeb(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = url.toUri()
    context.startActivity(intent)
}