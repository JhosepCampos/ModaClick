package com.cibertec.inventario.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import com.cibertec.inventario.ui.componentes.BotonSecundario
import com.cibertec.inventario.ui.componentes.CampoTexto
import com.cibertec.inventario.ui.componentes.DialogoConfirmacion
import com.cibertec.inventario.ui.theme.DoradoPrimario
import com.cibertec.inventario.ui.theme.ErrorRojo
import com.cibertec.inventario.ui.theme.FondoOscuro
import com.cibertec.inventario.ui.theme.TextoBlanco
import com.cibertec.inventario.ui.theme.TextoMuted

@Composable
fun PantallaRecuperarClave(
    estaCargando: Boolean = false,
    errorServidor: String? = null,
    errorResId: Int? = null,
    recuperacionExitosa: Boolean = false,
    alRecuperar: ((String) -> Unit)? = null,
    alReiniciarEstado: (() -> Unit)? = null,
    alVolver: () -> Unit
) {
    var correo by remember { mutableStateOf("") }
    var errorCorreo by remember { mutableStateOf(false) }

    val mensajeError = errorResId?.let { stringResource(id = it) } ?: errorServidor

    DisposableEffect(Unit) {
        onDispose {
            alReiniciarEstado?.invoke()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoOscuro)
    ) {
        BarraSuperior(
            titulo = stringResource(id = R.string.recuperar_titulo),
            puedeRegresar = true,
            alRegresar = {
                alReiniciarEstado?.invoke()
                alVolver()
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(id = R.string.recuperar_titulo),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextoBlanco
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(id = R.string.recuperar_subtitulo),
                fontSize = 14.sp,
                color = TextoMuted,
                lineHeight = 20.sp
            )

            if (!mensajeError.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = mensajeError,
                    color = ErrorRojo,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            CampoTexto(
                valor = correo,
                alCambiar = {
                    correo = it
                    errorCorreo = false
                    if (mensajeError != null) {
                        alReiniciarEstado?.invoke()
                    }
                },
                etiqueta = stringResource(id = R.string.recuperar_correo),
                iconoInicio = Icons.Default.Email,
                esError = errorCorreo,
                mensajeError = if (errorCorreo) stringResource(id = R.string.error_correo_invalido) else null
            )

            Spacer(modifier = Modifier.height(28.dp))

            if (estaCargando) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = DoradoPrimario,
                        modifier = Modifier.size(36.dp)
                    )
                }
            } else {
                BotonPrincipal(
                    texto = stringResource(id = R.string.recuperar_enviar),
                    onClick = {
                        val correoLimpio = correo.trim()
                        if (correoLimpio.isBlank() || !correoLimpio.contains("@")) {
                            errorCorreo = true
                        } else {
                            errorCorreo = false
                            alRecuperar?.invoke(correoLimpio)
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            BotonSecundario(
                texto = stringResource(id = R.string.recuperar_volver),
                onClick = {
                    alReiniciarEstado?.invoke()
                    alVolver()
                }
            )
        }
    }

    if (recuperacionExitosa) {
        DialogoConfirmacion(
            titulo = stringResource(id = R.string.recuperar_titulo),
            descripcion = stringResource(id = R.string.recuperar_enviado),
            alConfirmar = {
                alReiniciarEstado?.invoke()
                alVolver()
            },
            alDescartar = {
                alReiniciarEstado?.invoke()
                alVolver()
            },
            mostrarCancelar = false
        )
    }
}
