package com.cibertec.inventario.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cibertec.inventario.R
import com.cibertec.inventario.datos.AuthRepository
import com.cibertec.inventario.datos.CorreoNoRegistradoException
import com.cibertec.inventario.modelo.RolUsuario
import com.cibertec.inventario.modelo.Usuario
import com.facebook.AccessToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthEstado(
    val estaCargando: Boolean = false,
    val usuarioAutenticado: Usuario? = null,
    val error: String? = null,
    val errorResId: Int? = null,
    val registroExitoso: Boolean = false,
    val recuperacionExitosa: Boolean = false
)

class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _estado = MutableStateFlow(AuthEstado())
    val estado: StateFlow<AuthEstado> = _estado.asStateFlow()

    init {
        verificarSesionExistente()
    }

    fun verificarSesionExistente(alCompletar: (Usuario?) -> Unit = {}) {
        viewModelScope.launch {
            val usuarioActual = authRepository.obtenerUsuarioActual()
            if (usuarioActual != null) {
                _estado.value = _estado.value.copy(
                    usuarioAutenticado = usuarioActual
                )
            }
            alCompletar(usuarioActual)
        }
    }

    fun iniciarSesion(
        correo: String,
        contrasena: String,
        alCompletar: (RolUsuario) -> Unit
    ) {
        if (correo.isBlank() || contrasena.isBlank()) {
            _estado.value = _estado.value.copy(error = "Por favor completa todos los campos")
            return
        }

        if (!correo.contains("@")) {
            _estado.value = _estado.value.copy(error = "Ingresa un correo electrónico válido")
            return
        }

        _estado.value = _estado.value.copy(estaCargando = true, error = null)

        viewModelScope.launch {
            val resultado = authRepository.iniciarSesion(correo.trim(), contrasena)
            resultado.fold(
                onSuccess = { usuario ->
                    _estado.value = _estado.value.copy(
                        estaCargando = false,
                        usuarioAutenticado = usuario,
                        error = null
                    )
                    alCompletar(usuario.obtenerRolEnum())
                },
                onFailure = { excepcion ->
                    _estado.value = _estado.value.copy(
                        estaCargando = false,
                        error = excepcion.localizedMessage ?: "Correo o contraseña incorrectos"
                    )
                }
            )
        }
    }

    fun registrar(
        nombreCompleto: String,
        correo: String,
        contrasena: String,
        confirmarContrasena: String,
        alExito: () -> Unit
    ) {
        if (nombreCompleto.isBlank() || correo.isBlank() || contrasena.isBlank()) {
            _estado.value = _estado.value.copy(error = "Todos los campos son obligatorios")
            return
        }

        if (!correo.contains("@") || !correo.contains(".")) {
            _estado.value = _estado.value.copy(error = "Correo electrónico no válido")
            return
        }

        if (contrasena.length < 6) {
            _estado.value = _estado.value.copy(error = "La contraseña debe tener al menos 6 caracteres")
            return
        }

        if (contrasena != confirmarContrasena) {
            _estado.value = _estado.value.copy(error = "Las contraseñas no coinciden")
            return
        }

        _estado.value = _estado.value.copy(estaCargando = true, error = null)

        viewModelScope.launch {
            val resultado = authRepository.registrarUsuario(
                nombreCompleto = nombreCompleto.trim(),
                correo = correo.trim(),
                contrasena = contrasena
            )
            resultado.fold(
                onSuccess = { usuario ->
                    _estado.value = _estado.value.copy(
                        estaCargando = false,
                        registroExitoso = true,
                        error = null
                    )
                    alExito()
                },
                onFailure = { excepcion ->
                    _estado.value = _estado.value.copy(
                        estaCargando = false,
                        error = excepcion.localizedMessage ?: "Error al registrar la cuenta"
                    )
                }
            )
        }
    }

    fun recuperarContrasena(
        correo: String,
        alExito: () -> Unit = {}
    ) {
        val correoLimpio = correo.trim()
        if (correoLimpio.isBlank() || !correoLimpio.contains("@")) {
            _estado.value = _estado.value.copy(
                errorResId = R.string.error_correo_invalido,
                error = null,
                recuperacionExitosa = false
            )
            return
        }

        _estado.value = _estado.value.copy(
            estaCargando = true,
            error = null,
            errorResId = null,
            recuperacionExitosa = false
        )

        viewModelScope.launch {
            val resultado = authRepository.recuperarContrasena(correoLimpio)
            resultado.fold(
                onSuccess = {
                    _estado.value = _estado.value.copy(
                        estaCargando = false,
                        recuperacionExitosa = true,
                        error = null,
                        errorResId = null
                    )
                    alExito()
                },
                onFailure = { excepcion ->
                    val errorRes = when {
                        excepcion is CorreoNoRegistradoException ||
                        excepcion.message == "CORREO_NO_REGISTRADO" ||
                        excepcion.message?.contains("user-not-found", ignoreCase = true) == true ||
                        excepcion.message?.contains("no user record", ignoreCase = true) == true -> {
                            R.string.auth_error_correo_no_registrado
                        }
                        excepcion.message?.contains("network", ignoreCase = true) == true -> {
                            R.string.conexion_sin_internet_titulo
                        }
                        else -> {
                            R.string.auth_error_generico
                        }
                    }
                    _estado.value = _estado.value.copy(
                        estaCargando = false,
                        recuperacionExitosa = false,
                        errorResId = errorRes,
                        error = excepcion.localizedMessage
                    )
                }
            )
        }
    }

    fun reiniciarEstadoRecuperacion() {
        _estado.value = _estado.value.copy(
            recuperacionExitosa = false,
            error = null,
            errorResId = null
        )
    }

    fun limpiarErrores() {
        _estado.value = _estado.value.copy(error = null, errorResId = null)
    }

    fun actualizarTelefono(nuevoTelefono: String, alCompletar: (Boolean) -> Unit = {}) {
        val usuario = _estado.value.usuarioAutenticado ?: return
        val usuarioActualizado = usuario.copy(telefono = nuevoTelefono.trim())
        _estado.value = _estado.value.copy(usuarioAutenticado = usuarioActualizado)
        viewModelScope.launch {
            val res = authRepository.actualizarPerfilUsuario(
                usuarioId = usuario.id,
                telefono = nuevoTelefono.trim(),
                direccion = usuario.direccion
            )
            alCompletar(res.isSuccess)
        }
    }

    fun actualizarDireccion(nuevaDireccion: String, alCompletar: (Boolean) -> Unit = {}) {
        val usuario = _estado.value.usuarioAutenticado ?: return
        val usuarioActualizado = usuario.copy(direccion = nuevaDireccion.trim())
        _estado.value = _estado.value.copy(usuarioAutenticado = usuarioActualizado)
        viewModelScope.launch {
            val res = authRepository.actualizarPerfilUsuario(
                usuarioId = usuario.id,
                telefono = usuario.telefono,
                direccion = nuevaDireccion.trim()
            )
            alCompletar(res.isSuccess)
        }
    }

    fun iniciarSesionConFacebook(
        token: AccessToken,
        alCompletar: (RolUsuario) -> Unit = {}
    ) {
        _estado.value = _estado.value.copy(estaCargando = true, error = null)
        viewModelScope.launch {
            val resultado = authRepository.iniciarSesionConFacebook(token)
            resultado.fold(
                onSuccess = { usuario ->
                    _estado.value = _estado.value.copy(
                        estaCargando = false,
                        usuarioAutenticado = usuario,
                        error = null
                    )
                    alCompletar(usuario.obtenerRolEnum())
                },
                onFailure = { excepcion ->
                    _estado.value = _estado.value.copy(
                        estaCargando = false,
                        error = excepcion.localizedMessage ?: "Error con Facebook Login"
                    )
                }
            )
        }
    }

    fun establecerMensajeError(mensaje: String) {
        _estado.value = _estado.value.copy(
            estaCargando = false,
            error = mensaje
        )
    }

    fun cerrarSesion(alCompletar: () -> Unit) {
        authRepository.cerrarSesion()
        _estado.value = AuthEstado()
        alCompletar()
    }
}
