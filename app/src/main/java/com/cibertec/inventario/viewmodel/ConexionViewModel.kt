package com.cibertec.inventario.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cibertec.inventario.util.MonitorRed
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ConexionViewModel(application: Application) : AndroidViewModel(application) {

    private val monitorRed = MonitorRed(application)

    private val _hayInternet = MutableStateFlow(true)
    val hayInternet: StateFlow<Boolean> = _hayInternet.asStateFlow()

    init {
        _hayInternet.value = monitorRed.hayConexion()
        viewModelScope.launch {
            monitorRed.estadoConexion.collect { conectividad ->
                _hayInternet.value = conectividad
            }
        }
    }

    fun reintentarConexion() {
        _hayInternet.value = monitorRed.hayConexion()
    }
}
