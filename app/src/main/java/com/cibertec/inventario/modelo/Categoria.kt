package com.cibertec.inventario.modelo

import androidx.annotation.StringRes
import com.cibertec.inventario.R

enum class Categoria(
    val id: String,
    @StringRes val nombreRes: Int
) {
    TODOS("todos", R.string.cat_todos),
    CAMISAS("camisas", R.string.cat_camisas),
    PANTALONES("pantalones", R.string.cat_pantalones),
    VESTIDOS("vestidos", R.string.cat_vestidos),
    ZAPATOS("zapatos", R.string.cat_zapatos),
    ACCESORIOS("accesorios", R.string.cat_accesorios),
    CHAQUETAS("chaquetas", R.string.cat_chaquetas)
}
