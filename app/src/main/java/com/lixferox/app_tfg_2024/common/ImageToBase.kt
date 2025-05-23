package com.lixferox.app_tfg_2024.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream
import androidx.core.graphics.scale

/**
 * METODO QUE CONVIERTE UNA IMAGEN A BASE64 Y FORMATO WEBP PARA PODER ALMACENARLA EN LA BASE DE DATOS.
 *
 * @param context CONTEXTO DESDE EL CUAL SE INICIA LA ACTIVIDAD.
 * @param imageUri URI DE LA IMAGEN A CONVERTIR.
 * @return CADENA DE BASE64 CON LA IMAGEN CONVERTIDA EN WEBP O NULL EN CASO DE QUE DE ERROR.
 * */

fun parseImage(context: Context, imageUri: Uri): String {
    val inputStream = context.contentResolver.openInputStream(imageUri) ?: return ""

    val image = BitmapFactory.decodeStream(inputStream)
    inputStream.close()

    val scale = image.scale(150, 150)
    if (scale != image) {
        image.recycle()
    }

    val parseWebp = ByteArrayOutputStream()
    scale.compress(Bitmap.CompressFormat.WEBP, 100, parseWebp)
    scale.recycle()

    val readBytes = parseWebp.toByteArray()
    return Base64.encodeToString(readBytes, Base64.NO_WRAP)
}