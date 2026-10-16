package com.cibertec.inventario.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cibertec.inventario.datos.PrendasRepository
import com.cibertec.inventario.modelo.Categoria
import com.cibertec.inventario.modelo.FiltroColeccion
import com.cibertec.inventario.modelo.Prenda
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CatalogoViewModel(
    private val prendasRepository: PrendasRepository = PrendasRepository()
) : ViewModel() {

    private val _todasLasPrendas = MutableStateFlow<List<Prenda>>(emptyList())
    val todasLasPrendas: StateFlow<List<Prenda>> = _todasLasPrendas.asStateFlow()

    private val _textoBusqueda = MutableStateFlow("")
    val textoBusqueda: StateFlow<String> = _textoBusqueda.asStateFlow()

    private val _categoriaSeleccionada = MutableStateFlow(Categoria.TODOS)
    val categoriaSeleccionada: StateFlow<Categoria> = _categoriaSeleccionada.asStateFlow()

    private val _coleccionSeleccionada = MutableStateFlow(FiltroColeccion.TODOS)
    val coleccionSeleccionada: StateFlow<FiltroColeccion> = _coleccionSeleccionada.asStateFlow()

    private val _estaCargando = MutableStateFlow(true)
    val estaCargando: StateFlow<Boolean> = _estaCargando.asStateFlow()

    init {
        viewModelScope.launch {
            prendasRepository.obtenerPrendas().collect { lista ->
                _todasLasPrendas.value = lista
                _estaCargando.value = false
            }
        }
    }

    val prendasFiltradas: StateFlow<List<Prenda>> = combine(
        _todasLasPrendas,
        _textoBusqueda,
        _categoriaSeleccionada,
        _coleccionSeleccionada
    ) { prendas, busqueda, categoria, coleccion ->
        prendas.filter { prenda ->
            val coincideCategoria = if (categoria == Categoria.TODOS) {
                true
            } else {
                prenda.categoria.equals(categoria.name, ignoreCase = true)
            }

            val coincideColeccion = when (coleccion) {
                FiltroColeccion.TODOS -> true
                FiltroColeccion.DESTACADOS -> prenda.esDestacado
                FiltroColeccion.NOVEDADES -> prenda.esNovedad
                FiltroColeccion.BASICOS -> !prenda.esDestacado && !prenda.esNovedad
            }

            val coincideBusqueda = if (busqueda.isBlank()) {
                true
            } else {
                prenda.nombre.contains(busqueda, ignoreCase = true) ||
                        prenda.descripcion.contains(busqueda, ignoreCase = true)
            }

            coincideCategoria && coincideColeccion && coincideBusqueda
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val prendasDestacadas: StateFlow<List<Prenda>> = combine(_todasLasPrendas) { prendas ->
        prendas[0].filter { it.esDestacado }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val prendasNovedades: StateFlow<List<Prenda>> = combine(_todasLasPrendas) { prendas ->
        prendas[0].filter { it.esNovedad }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun actualizarBusqueda(texto: String) {
        _textoBusqueda.value = texto
    }

    fun seleccionarCategoria(categoria: Categoria) {
        _categoriaSeleccionada.value = categoria
    }

    fun seleccionarColeccion(coleccion: FiltroColeccion) {
        _coleccionSeleccionada.value = coleccion
    }

    fun obtenerPrendaPorId(id: String): Prenda? {
        return _todasLasPrendas.value.find { it.id == id }
    }

    fun guardarPrenda(prenda: Prenda, alCompletar: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            _estaCargando.value = true
            val resultado = prendasRepository.guardarPrenda(prenda)
            _estaCargando.value = false
            alCompletar(resultado.isSuccess)
        }
    }

    fun eliminarPrenda(id: String, alCompletar: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            _estaCargando.value = true
            val resultado = prendasRepository.eliminarPrenda(id)
            _estaCargando.value = false
            alCompletar(resultado.isSuccess)
        }
    }
}
