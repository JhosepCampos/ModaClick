package com.cibertec.inventario.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cibertec.inventario.datos.PedidosRepository
import com.cibertec.inventario.modelo.EstadoPedido
import com.cibertec.inventario.modelo.ItemCarrito
import com.cibertec.inventario.modelo.Pedido
import com.cibertec.inventario.modelo.Usuario
import com.cibertec.inventario.util.GeneradorReportesPdf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PedidosViewModel(
    private val pedidosRepository: PedidosRepository = PedidosRepository()
) : ViewModel() {

    private val _pedidosCliente = MutableStateFlow<List<Pedido>>(emptyList())
    val pedidosCliente: StateFlow<List<Pedido>> = _pedidosCliente.asStateFlow()

    private val _estaCargando = MutableStateFlow(false)
    val estaCargando: StateFlow<Boolean> = _estaCargando.asStateFlow()

    fun cargarPedidosDeUsuario(usuario: Usuario) {
        viewModelScope.launch {
            _estaCargando.value = true
            pedidosRepository.obtenerPedidosPorCliente(usuario.id, usuario.correo).collect { lista ->
                _pedidosCliente.value = lista
                _estaCargando.value = false
            }
        }
    }

    fun cargarPedidosDeUsuario(clienteIdOCorreo: String) {
        viewModelScope.launch {
            _estaCargando.value = true
            pedidosRepository.obtenerPedidosPorCliente(clienteId = clienteIdOCorreo, clienteCorreo = clienteIdOCorreo).collect { lista ->
                _pedidosCliente.value = lista
                _estaCargando.value = false
            }
        }
    }

    fun limpiarPedidos() {
        _pedidosCliente.value = emptyList()
    }

    fun realizarPedido(
        usuario: Usuario,
        direccion: String,
        metodoPago: String,
        items: List<ItemCarrito>,
        alExito: (String) -> Unit,
        alError: ((String) -> Unit)? = null
    ) {
        if (items.isEmpty()) {
            alError?.invoke("El carrito está vacío")
            return
        }

        val itemsSinStock = items.filter { it.prendaStock <= 0 || it.cantidad > it.prendaStock }
        if (itemsSinStock.isNotEmpty()) {
            alError?.invoke("Uno o más productos no cuentan con stock suficiente para realizar el pedido")
            return
        }

        val fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val nuevoPedido = Pedido(
            id = "MC-${1000 + (1..9999).random()}",
            clienteId = usuario.id,
            clienteNombre = usuario.nombreCompleto,
            clienteCorreo = usuario.correo,
            fecha = fecha,
            total = items.sumOf { it.subtotal },
            estado = EstadoPedido.PENDIENTE.name,
            items = items,
            direccionEntrega = direccion,
            metodoPago = metodoPago,
            timestamp = System.currentTimeMillis()
        )

        viewModelScope.launch {
            _estaCargando.value = true
            val resultado = pedidosRepository.crearPedido(nuevoPedido)
            _estaCargando.value = false
            resultado.fold(
                onSuccess = { id -> alExito(id) },
                onFailure = { excepcion ->
                    alError?.invoke(excepcion.localizedMessage ?: "Error al procesar el pedido")
                }
            )
        }
    }

    fun descargarReporte(
        context: Context,
        usuario: Usuario,
        pedidos: List<Pedido> = _pedidosCliente.value,
        titulo: String = "Historial de Compras del Cliente",
        alFinalizar: (Boolean) -> Unit = {}
    ) {
        viewModelScope.launch {
            if (pedidos.isEmpty()) {
                alFinalizar(false)
                return@launch
            }
            val resultado = GeneradorReportesPdf.generarYDescargarReporte(
                context = context,
                tituloReporte = titulo,
                pedidos = pedidos,
                nombreSolicitante = usuario.nombreCompleto
            )
            alFinalizar(resultado.isSuccess)
        }
    }

    fun descargarReportePedidoIndividual(context: Context, usuario: Usuario, pedido: Pedido, alFinalizar: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            val resultado = GeneradorReportesPdf.generarYDescargarReporte(
                context = context,
                tituloReporte = "Comprobante de Pedido #${pedido.id}",
                pedidos = listOf(pedido),
                nombreSolicitante = usuario.nombreCompleto
            )
            alFinalizar(resultado.isSuccess)
        }
    }
}
