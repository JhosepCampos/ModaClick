package com.cibertec.inventario.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.cibertec.inventario.modelo.Pedido
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object GeneradorReportesPdf {

    fun generarYDescargarReporte(
        context: Context,
        tituloReporte: String,
        pedidos: List<Pedido>,
        nombreSolicitante: String
    ): Result<String> {
        return try {
            val pdfDocument = PdfDocument()
            var paginaActual = 1
            var page = pdfDocument.startPage(PdfDocument.PageInfo.Builder(595, 842, paginaActual).create())
            var canvas = page.canvas

            val paintTitulo = Paint().apply {
                color = Color.rgb(212, 175, 55)
                textSize = 20f
                isFakeBoldText = true
            }

            val paintSubtitulo = Paint().apply {
                color = Color.rgb(40, 40, 40)
                textSize = 12f
                isFakeBoldText = true
            }

            val paintMeta = Paint().apply {
                color = Color.rgb(100, 100, 100)
                textSize = 9.5f
            }

            val paintOrdenBg = Paint().apply {
                color = Color.rgb(243, 244, 246)
            }

            val paintOrdenHeader = Paint().apply {
                color = Color.rgb(30, 30, 30)
                textSize = 10.5f
                isFakeBoldText = true
            }

            val paintOrdenHeaderSec = Paint().apply {
                color = Color.rgb(70, 70, 70)
                textSize = 10f
            }

            val paintOrdenTotal = Paint().apply {
                color = Color.rgb(212, 175, 55)
                textSize = 10.5f
                isFakeBoldText = true
            }

            val paintItemHeader = Paint().apply {
                color = Color.rgb(120, 120, 120)
                textSize = 8.5f
                isFakeBoldText = true
            }

            val paintItemCantidad = Paint().apply {
                color = Color.rgb(212, 175, 55)
                textSize = 9.5f
                isFakeBoldText = true
            }

            val paintItemTexto = Paint().apply {
                color = Color.rgb(50, 50, 50)
                textSize = 9.5f
            }

            val paintItemDetalle = Paint().apply {
                color = Color.rgb(110, 110, 110)
                textSize = 8.5f
            }

            val paintItemSubtotal = Paint().apply {
                color = Color.rgb(30, 30, 30)
                textSize = 9.5f
                isFakeBoldText = true
            }

            val paintLinea = Paint().apply {
                color = Color.rgb(220, 220, 220)
                strokeWidth = 1f
            }

            val paintLineaFina = Paint().apply {
                color = Color.rgb(235, 235, 235)
                strokeWidth = 0.8f
            }

            val paintLineaDorada = Paint().apply {
                color = Color.rgb(212, 175, 55)
                strokeWidth = 1.8f
            }

            val paintTotalGeneralBg = Paint().apply {
                color = Color.rgb(248, 246, 238)
            }

            val paintTotalGeneralLabel = Paint().apply {
                color = Color.rgb(40, 40, 40)
                textSize = 12f
                isFakeBoldText = true
            }

            val paintTotalGeneralValor = Paint().apply {
                color = Color.rgb(212, 175, 55)
                textSize = 13f
                isFakeBoldText = true
            }

            val paintPie = Paint().apply {
                color = Color.rgb(130, 130, 130)
                textSize = 8.5f
            }

            var yPos = 50f

            fun verificarNuevaPagina(espacioRequerido: Float) {
                if (yPos + espacioRequerido > 780f) {
                    canvas.drawText(
                        "ModaClick App © 2026 - Reporte emitido electrónicamente. | Página $paginaActual",
                        40f,
                        810f,
                        paintPie
                    )
                    pdfDocument.finishPage(page)
                    paginaActual++
                    page = pdfDocument.startPage(PdfDocument.PageInfo.Builder(595, 842, paginaActual).create())
                    canvas = page.canvas
                    yPos = 50f

                    canvas.drawText("ModaClick - $tituloReporte (Continuación)", 40f, yPos, paintTitulo)
                    yPos += 14f
                    canvas.drawLine(40f, yPos, 555f, yPos, paintLineaDorada)
                    yPos += 20f
                }
            }

            canvas.drawText("ModaClick - Tienda Exclusiva", 40f, yPos, paintTitulo)
            yPos += 20f
            canvas.drawText(tituloReporte, 40f, yPos, paintSubtitulo)
            yPos += 16f

            val fechaHoy = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
            canvas.drawText("Generado por: $nombreSolicitante | Fecha de emisión: $fechaHoy", 40f, yPos, paintMeta)
            yPos += 16f

            canvas.drawLine(40f, yPos, 555f, yPos, paintLineaDorada)
            yPos += 20f

            var sumaTotal = 0.0

            pedidos.forEach { pedido ->
                val cantItems = pedido.items.size
                val espacioEstimado = 70f + (cantItems * 18f)
                verificarNuevaPagina(espacioEstimado.coerceAtMost(300f))

                canvas.drawRoundRect(40f, yPos, 555f, yPos + 22f, 5f, 5f, paintOrdenBg)
                canvas.drawText("Pedido: ${pedido.id}", 48f, yPos + 15f, paintOrdenHeader)
                canvas.drawText("Fecha: ${pedido.fecha}", 170f, yPos + 15f, paintOrdenHeaderSec)
                canvas.drawText("Estado: ${pedido.estado}", 300f, yPos + 15f, paintOrdenHeaderSec)
                canvas.drawText(
                    String.format(Locale.US, "Total: S/ %.2f", pedido.total),
                    455f,
                    yPos + 15f,
                    paintOrdenTotal
                )
                yPos += 30f

                canvas.drawText("CANT.", 50f, yPos, paintItemHeader)
                canvas.drawText("PRODUCTO ADQUIRIDO", 95f, yPos, paintItemHeader)
                canvas.drawText("TALLA / COLOR", 310f, yPos, paintItemHeader)
                canvas.drawText("P. UNIT.", 415f, yPos, paintItemHeader)
                canvas.drawText("SUBTOTAL", 485f, yPos, paintItemHeader)
                yPos += 6f

                canvas.drawLine(50f, yPos, 545f, yPos, paintLineaFina)
                yPos += 14f

                if (pedido.items.isNotEmpty()) {
                    pedido.items.forEach { item ->
                        verificarNuevaPagina(18f)

                        canvas.drawText("${item.cantidad}x", 50f, yPos, paintItemCantidad)

                        val nombreCorto = if (item.prendaNombre.length > 30) {
                            item.prendaNombre.substring(0, 28) + "…"
                        } else item.prendaNombre
                        canvas.drawText(nombreCorto, 95f, yPos, paintItemTexto)

                        val variante = "${item.tallaSeleccionada} / ${item.colorSeleccionado}"
                        canvas.drawText(variante, 310f, yPos, paintItemDetalle)

                        canvas.drawText(
                            String.format(Locale.US, "S/ %.2f", item.prendaPrecio),
                            415f,
                            yPos,
                            paintItemTexto
                        )
                        canvas.drawText(
                            String.format(Locale.US, "S/ %.2f", item.subtotal),
                            485f,
                            yPos,
                            paintItemSubtotal
                        )

                        yPos += 16f
                    }
                } else {
                    canvas.drawText("1x", 50f, yPos, paintItemCantidad)
                    canvas.drawText("Prendas del pedido", 95f, yPos, paintItemTexto)
                    canvas.drawText("—", 310f, yPos, paintItemDetalle)
                    canvas.drawText(String.format(Locale.US, "S/ %.2f", pedido.total), 415f, yPos, paintItemTexto)
                    canvas.drawText(String.format(Locale.US, "S/ %.2f", pedido.total), 485f, yPos, paintItemSubtotal)
                    yPos += 16f
                }

                if (pedido.direccionEntrega.isNotBlank()) {
                    val dirCorta = if (pedido.direccionEntrega.length > 50) {
                        pedido.direccionEntrega.take(48) + "…"
                    } else pedido.direccionEntrega
                    canvas.drawText("Entrega en: $dirCorta", 50f, yPos, paintMeta)
                    yPos += 12f
                }

                sumaTotal += pedido.total

                yPos += 4f
                canvas.drawLine(40f, yPos, 555f, yPos, paintLinea)
                yPos += 16f
            }

            verificarNuevaPagina(50f)
            canvas.drawRoundRect(310f, yPos, 555f, yPos + 30f, 6f, 6f, paintTotalGeneralBg)
            canvas.drawText("TOTAL GENERAL:", 325f, yPos + 20f, paintTotalGeneralLabel)
            canvas.drawText(
                String.format(Locale.US, "S/ %.2f", sumaTotal),
                460f,
                yPos + 20f,
                paintTotalGeneralValor
            )
            yPos += 50f

            canvas.drawText(
                "ModaClick App © 2026 - Reporte emitido electrónicamente. | Página $paginaActual",
                40f,
                810f,
                paintPie
            )

            pdfDocument.finishPage(page)

            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val nombreArchivo = "Reporte_ModaClick_${timestamp}.pdf"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, nombreArchivo)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                    ?: throw Exception("No se pudo registrar el archivo en Descargas")

                context.contentResolver.openOutputStream(uri)?.use { output ->
                    pdfDocument.writeTo(output)
                } ?: throw Exception("No se pudo escribir en el archivo de Descargas")
            } else {
                val descargas = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val carpeta = if (descargas != null && (descargas.exists() || descargas.mkdirs())) {
                    descargas
                } else {
                    context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) ?: context.filesDir
                }
                val archivoFinal = File(carpeta, nombreArchivo)
                FileOutputStream(archivoFinal).use { output ->
                    pdfDocument.writeTo(output)
                }
            }

            pdfDocument.close()
            Result.success(nombreArchivo)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
