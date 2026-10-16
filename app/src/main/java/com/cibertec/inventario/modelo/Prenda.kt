package com.cibertec.inventario.modelo

import android.content.Context
import androidx.annotation.StringRes
import com.cibertec.inventario.R

data class Prenda(
    val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val precio: Double = 0.0,
    val categoria: String = "TODOS",
    val tallasDisponibles: List<String> = listOf("S", "M", "L", "XL"),
    val coloresDisponibles: List<String> = listOf("Negro", "Blanco", "Azul"),
    val stock: Int = 10,
    val esDestacado: Boolean = false,
    val esNovedad: Boolean = false,
    val calificacion: Double = 4.8,
    val imagenUrl: String = ""
) {
    constructor(
        idNum: Int,
        @StringRes nombreRes: Int,
        @StringRes descripcionRes: Int,
        precio: Double,
        categoriaEnum: Categoria,
        tallasDisponibles: List<String>,
        coloresDisponibles: List<String>,
        stock: Int,
        esDestacado: Boolean,
        esNovedad: Boolean,
        calificacion: Double
    ) : this(
        id = idNum.toString(),
        nombre = "",
        descripcion = "",
        precio = precio,
        categoria = categoriaEnum.name,
        tallasDisponibles = tallasDisponibles,
        coloresDisponibles = coloresDisponibles,
        stock = stock,
        esDestacado = esDestacado,
        esNovedad = esNovedad,
        calificacion = calificacion
    )

    fun obtenerCategoriaEnum(): Categoria {
        return try {
            Categoria.valueOf(categoria.uppercase())
        } catch (e: Exception) {
            Categoria.TODOS
        }
    }
}
