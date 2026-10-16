package com.cibertec.inventario.viewmodel

import androidx.lifecycle.ViewModel
import com.cibertec.inventario.modelo.ItemCarrito
import com.cibertec.inventario.modelo.Prenda
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CarritoViewModel : ViewModel() {

    private val _items = MutableStateFlow<List<ItemCarrito>>(emptyList())
    val items: StateFlow<List<ItemCarrito>> = _items.asStateFlow()

    val total: Double
        get() = _items.value.sumOf { it.subtotal }

    val cantidadTotal: Int
        get() = _items.value.sumOf { it.cantidad }

    fun agregarPrenda(
        prenda: Prenda,
        talla: String,
        color: String,
        cantidad: Int = 1
    ): Boolean {
        if (prenda.stock <= 0) {
            return false
        }

        val listaActual = _items.value.toMutableList()
        val indexExistente = listaActual.indexOfFirst {
            it.prendaId == prenda.id && it.tallaSeleccionada == talla && it.colorSeleccionado == color
        }

        if (indexExistente != -1) {
            val itemExistente = listaActual[indexExistente]
            if (itemExistente.cantidad >= prenda.stock) {
                return false
            }
            val nuevaCantidad = minOf(itemExistente.cantidad + cantidad, prenda.stock)
            listaActual[indexExistente] = itemExistente.copy(
                cantidad = nuevaCantidad,
                prendaStock = prenda.stock
            )
        } else {
            val cantidadFinal = minOf(cantidad, prenda.stock)
            if (cantidadFinal <= 0) return false
            listaActual.add(
                ItemCarrito(
                    prenda = prenda,
                    tallaSeleccionada = talla,
                    colorSeleccionado = color,
                    cantidad = cantidadFinal
                )
            )
        }

        _items.value = listaActual
        return true
    }

    fun sincronizarStock(prendas: List<Prenda>) {
        if (_items.value.isEmpty()) return
        val mapStock = prendas.associate { it.id to it.stock }
        val listaActualizada = _items.value.map { item ->
            val stockActual = mapStock[item.prendaId] ?: item.prendaStock
            item.copy(prendaStock = stockActual)
        }
        _items.value = listaActualizada
    }

    fun modificarCantidad(item: ItemCarrito, nuevaCantidad: Int) {
        if (nuevaCantidad <= 0) {
            eliminarItem(item)
            return
        }

        if (nuevaCantidad > item.prendaStock) {
            return
        }

        val listaActual = _items.value.toMutableList()
        val index = listaActual.indexOf(item)
        if (index != -1) {
            listaActual[index] = item.copy(cantidad = nuevaCantidad)
            _items.value = listaActual
        }
    }

    fun eliminarItem(item: ItemCarrito) {
        val listaActual = _items.value.toMutableList()
        listaActual.remove(item)
        _items.value = listaActual
    }

    fun vaciarCarrito() {
        _items.value = emptyList()
    }
}
