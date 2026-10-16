package com.cibertec.inventario.modelo

import androidx.annotation.StringRes
import com.cibertec.inventario.R

enum class EstadoPedido(
    @StringRes val etiquetaRes: Int
) {
    PENDIENTE(R.string.estado_pendiente),
    EN_CURSO(R.string.estado_en_curso),
    ENTREGADO(R.string.estado_entregado)
}

data class Pedido(
    val id: String = "",
    val clienteId: String = "",
    val clienteNombre: String = "",
    val clienteCorreo: String = "",
    val fecha: String = "",
    val total: Double = 0.0,
    val estado: String = EstadoPedido.PENDIENTE.name,
    val items: List<ItemCarrito> = emptyList(),
    val direccionEntrega: String = "",
    val metodoPago: String = "",
    val timestamp: Long = System.currentTimeMillis()
) {
    fun obtenerEstadoEnum(): EstadoPedido {
        return try {
            EstadoPedido.valueOf(estado.uppercase())
        } catch (e: Exception) {
            EstadoPedido.PENDIENTE
        }
    }
}
