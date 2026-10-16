package com.cibertec.inventario.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.LruCache
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.io.ByteArrayOutputStream
import java.io.InputStream

object ImageUtils {

    private val cache = object : LruCache<String, ImageBitmap>(60) {}

    fun uriABitmap(context: Context, uri: Uri, maxDimension: Int = 800): Bitmap? {
        return try {
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream, null, options)
            }

            var inSampleSize = 1
            if (options.outHeight > maxDimension || options.outWidth > maxDimension) {
                val halfHeight = options.outHeight / 2
                val halfWidth = options.outWidth / 2
                while ((halfHeight / inSampleSize) >= maxDimension && (halfWidth / inSampleSize) >= maxDimension) {
                    inSampleSize *= 2
                }
            }

            val decodeOptions = BitmapFactory.Options().apply {
                this.inSampleSize = inSampleSize
            }
            val decodedBitmap = context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream, null, decodeOptions)
            }
            decodedBitmap?.let { escalarBitmap(it, maxDimension) }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun escalarBitmap(bitmap: Bitmap, maxDimension: Int = 800): Bitmap {
        val ancho = bitmap.width
        val alto = bitmap.height
        if (ancho <= maxDimension && alto <= maxDimension) return bitmap

        val ratio = ancho.toFloat() / alto.toFloat()
        val nuevoAncho: Int
        val nuevoAlto: Int
        if (ancho > alto) {
            nuevoAncho = maxDimension
            nuevoAlto = (maxDimension / ratio).toInt().coerceAtLeast(1)
        } else {
            nuevoAlto = maxDimension
            nuevoAncho = (maxDimension * ratio).toInt().coerceAtLeast(1)
        }
        return Bitmap.createScaledBitmap(bitmap, nuevoAncho, nuevoAlto, true)
    }

    fun bitmapABase64(bitmap: Bitmap, calidad: Int = 75, maxDimension: Int = 800): String {
        val bitmapEscalado = escalarBitmap(bitmap, maxDimension)
        val stream = ByteArrayOutputStream()
        bitmapEscalado.compress(Bitmap.CompressFormat.JPEG, calidad, stream)
        val byteArray = stream.toByteArray()
        return "data:image/jpeg;base64," + Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    fun base64AImageBitmap(base64Str: String): ImageBitmap? {
        if (base64Str.isBlank()) return null

        val cached = cache.get(base64Str)
        if (cached != null) return cached

        return try {
            val limpia = if (base64Str.contains(",")) {
                base64Str.substringAfter(",")
            } else {
                base64Str
            }
            val bytes = Base64.decode(limpia, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            val imageBitmap = bitmap?.asImageBitmap()
            if (imageBitmap != null) {
                cache.put(base64Str, imageBitmap)
            }
            imageBitmap
        } catch (e: Exception) {
            null
        }
    }
}
