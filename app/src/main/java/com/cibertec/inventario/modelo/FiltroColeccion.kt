package com.cibertec.inventario.modelo

import androidx.annotation.StringRes
import com.cibertec.inventario.R

enum class FiltroColeccion(
    @StringRes val etiquetaRes: Int
) {
    TODOS(R.string.coleccion_todos),
    DESTACADOS(R.string.coleccion_destacados),
    NOVEDADES(R.string.coleccion_novedades),
    BASICOS(R.string.coleccion_basicos)
}
