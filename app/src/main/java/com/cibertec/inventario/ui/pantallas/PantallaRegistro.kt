package com.cibertec.inventario.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cibertec.inventario.R
import com.cibertec.inventario.ui.componentes.BarraSuperior
import com.cibertec.inventario.ui.componentes.BotonPrincipal
import com.cibertec.inventario.ui.componentes.CampoTexto
import com.cibertec.inventario.ui.theme.DoradoPrimario
import com.cibertec.inventario.ui.theme.ErrorRojo
import com.cibertec.inventario.ui.theme.FondoOscuro
import com.cibertec.inventario.ui.theme.TextoBlanco
import com.cibertec.inventario.ui.theme.TextoGrisClaro
import com.cibertec.inventario.ui.theme.TextoMuted

@Composable
fun PantallaRegistro(
    estaCargando: Boolean = false,
    errorServidor: String? = null,
    alRegistrar: (String, String, String) -> Unit,
    alIrALogin: () -> Unit
) {
    var nombreCompleto by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmarContrasena by remember { mutableStateOf("") }

    var errorNombre by remember { mutableStateOf(false) }
    var errorCorreo by remember { mutableStateOf(false) }
    var errorContrasena by remember { mutableStateOf(false) }
    var errorConfirmar by remember { mutableStateOf(false) }
    var errorConfirmarMensaje by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoOscuro)
    ) {
        BarraSuperior(
            titulo = stringResource(id = R.string.registro_titulo),
            puedeRegresar = true,
            alRegresar = alIrALogin
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(id = R.string.registro_titulo),
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = TextoBlanco,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = stringResource(id = R.string.registro_subtitulo),
                fontSize = 14.sp,
                color = TextoMuted,
                modifier = Modifier.fillMaxWidth()
            )

            if (!errorServidor.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = errorServidor,
                    color = ErrorRojo,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            CampoTexto(
                valor = nombreCompleto,
                alCambiar = {
                    nombreCompleto = it
                    errorNombre = false
                },
                etiqueta = stringResource(id = R.string.registro_nombre),
                iconoInicio = Icons.Default.Person,
                esError = errorNombre,
                mensajeError = if (errorNombre) stringResource(id = R.string.error_campo_requerido) else null
            )

            Spacer(modifier = Modifier.height(16.dp))

            CampoTexto(
                valor = correo,
                alCambiar = {
                    correo = it
                    errorCorreo = false
                },
                etiqueta = stringResource(id = R.string.registro_correo),
                iconoInicio = Icons.Default.Email,
                esError = errorCorreo,
                mensajeError = if (errorCorreo) stringResource(id = R.string.error_correo_invalido) else null
            )

            Spacer(modifier = Modifier.height(16.dp))

            CampoTexto(
                valor = contrasena,
                alCambiar = {
                    contrasena = it
                    errorContrasena = false
                },
                etiqueta = stringResource(id = R.string.registro_contrasena),
                iconoInicio = Icons.Default.Lock,
                esContrasena = true,
                esError = errorContrasena,
                mensajeError = if (errorContrasena) stringResource(id = R.string.error_contrasena_corta) else null
            )

            Spacer(modifier = Modifier.height(16.dp))

            CampoTexto(
                valor = confirmarContrasena,
                alCambiar = {
                    confirmarContrasena = it
                    errorConfirmar = false
                },
                etiqueta = stringResource(id = R.string.registro_confirmar),
                iconoInicio = Icons.Default.Lock,
                esContrasena = true,
                esError = errorConfirmar,
                mensajeError = if (errorConfirmar) errorConfirmarMensaje else null
            )

            Spacer(modifier = Modifier.height(28.dp))

            val msgNoCoincide = stringResource(id = R.string.error_contrasena_no_coincide)
            val msgRequerido = stringResource(id = R.string.error_campo_requerido)

            if (estaCargando) {
                CircularProgressIndicator(
                    color = DoradoPrimario,
                    modifier = Modifier.size(36.dp)
                )
            } else {
                BotonPrincipal(
                    texto = stringResource(id = R.string.registro_crear),
                    onClick = {
                        var valido = true
                        if (nombreCompleto.isBlank()) { errorNombre = true; valido = false }
                        if (correo.isBlank() || !correo.contains("@")) { errorCorreo = true; valido = false }
                        if (contrasena.length < 6) { errorContrasena = true; valido = false }
                        if (confirmarContrasena.isBlank()) {
                            errorConfirmar = true
                            errorConfirmarMensaje = msgRequerido
                            valido = false
                        } else if (contrasena != confirmarContrasena) {
                            errorConfirmar = true
                            errorConfirmarMensaje = msgNoCoincide
                            valido = false
                        }

                        if (valido) {
                            alRegistrar(nombreCompleto.trim(), correo.trim(), contrasena)
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.registro_ya_cuenta),
                    color = TextoGrisClaro,
                    fontSize = 14.sp
                )
                Text(
                    text = stringResource(id = R.string.registro_iniciar),
                    color = DoradoPrimario,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { alIrALogin() }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
