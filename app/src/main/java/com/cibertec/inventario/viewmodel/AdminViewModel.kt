package com.cibertec.inventario.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cibertec.inventario.datos.PedidosRepository
import com.cibertec.inventario.modelo.EstadoPedido
import com.cibertec.inventario.modelo.Pedido
import com.cibertec.inventario.modelo.Usuario
import com.cibertec.inventario.util.GeneradorReportesPdf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AdminViewModel(
    private val pedidosRepository: PedidosRepository = PedidosRepository()
) : ViewModel() {

    private val _todosLosPedidos = MutableStateFlow<List<Pedido>>(emptyList())
    val todosLosPedidos: StateFlow<List<Pedido>> = _todosLosPedidos.asStateFlow()

    private val _filtroEstado = MutableStateFlow<EstadoPedido?>(null)
    val filtroEstado: StateFlow<EstadoPedido?> = _filtroEstado.asStateFlow()

    private val _estaCargando = MutableStateFlow(true)
    val estaCargando: StateFlow<Boolean> = _estaCargando.asStateFlow()

    init {
        viewModelScope.launch {
            pedidosRepository.obtenerTodosLosPedidos().collect { lista ->
                _todosLosPedidos.value = lista
                _estaCargando.value = false
            }
        }
    }

    val pedidosFiltrados: StateFlow<List<Pedido>> = combine(
        _todosLosPedidos,
        _filtroEstado
    ) { lista, filtro ->
        if (filtro == null) lista else lista.filter { it.obtenerEstadoEnum() == filtro }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalPedidos: Int
        get() = _todosLosPedidos.value.size

    val pedidosPendientes: Int
        get() = _todosLosPedidos.value.count { it.obtenerEstadoEnum() == EstadoPedido.PENDIENTE }

    val pedidosEnCurso: Int
        get() = _todosLosPedidos.value.count { it.obtenerEstadoEnum() == EstadoPedido.EN_CURSO }

    val pedidosEntregados: Int
        get() = _todosLosPedidos.value.count { it.obtenerEstadoEnum() == EstadoPedido.ENTREGADO }

    val totalVentas: Double
        get() = _todosLosPedidos.value.sumOf { it.total }

    fun filtrarPorEstado(estado: EstadoPedido?) {
        _filtroEstado.value = estado
    }

    fun actualizarEstadoPedido(pedidoId: String, nuevoEstado: EstadoPedido, alExito: () -> Unit) {
        viewModelScope.launch {
            pedidosRepository.actualizarEstadoPedido(pedidoId, nuevoEstado)
            alExito()
        }
    }

    fun descargarReporteGeneral(context: Context, admin: Usuario, alFinalizar: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            val resultado = GeneradorReportesPdf.generarYDescargarReporte(
                context = context,
                tituloReporte = "Reporte Consolidado de Ventas y Pedidos",
                pedidos = _todosLosPedidos.value,
                nombreSolicitante = admin.nombreCompleto
            )
            alFinalizar(resultado.isSuccess)
        }
    }
}
