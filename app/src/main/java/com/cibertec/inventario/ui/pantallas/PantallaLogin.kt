package com.cibertec.inventario.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cibertec.inventario.R
import com.cibertec.inventario.ui.componentes.BotonPrincipal
import com.cibertec.inventario.ui.componentes.CampoTexto
import com.cibertec.inventario.ui.theme.DoradoPrimario
import com.cibertec.inventario.ui.theme.ErrorRojo
import com.cibertec.inventario.ui.theme.FondoOscuro
import com.cibertec.inventario.ui.theme.SuperficieCard
import com.cibertec.inventario.ui.theme.TextoBlanco
import com.cibertec.inventario.ui.theme.TextoGrisClaro
import com.cibertec.inventario.ui.theme.TextoMuted

@Composable
fun PantallaLogin(
    estaCargando: Boolean = false,
    errorServidor: String? = null,
    alIniciarSesion: (String, String) -> Unit,
    alIniciarFacebook: () -> Unit = {},
    alIrARegistro: () -> Unit,
    alIrARecuperarClave: () -> Unit
) {
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var errorCorreo by remember { mutableStateOf(false) }
    var mensajeErrorCorreo by remember { mutableStateOf("") }
    var errorContrasena by remember { mutableStateOf(false) }

    val msgCampoRequerido = stringResource(id = R.string.error_campo_requerido)
    val msgCorreoInvalido = stringResource(id = R.string.error_correo_invalido)

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoOscuro)
            .padding(horizontal = 24.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(SuperficieCard),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Checkroom,
                contentDescription = null,
                tint = DoradoPrimario,
                modifier = Modifier.size(46.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(id = R.string.login_bienvenido),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = TextoBlanco
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = stringResource(id = R.string.login_subtitulo),
            fontSize = 14.sp,
            color = TextoMuted
        )

        if (!errorServidor.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = errorServidor,
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
            },
            etiqueta = stringResource(id = R.string.login_correo),
            iconoInicio = Icons.Default.Email,
            esError = errorCorreo,
            mensajeError = if (errorCorreo) mensajeErrorCorreo else null
        )

        Spacer(modifier = Modifier.height(16.dp))

        CampoTexto(
            valor = contrasena,
            alCambiar = {
                contrasena = it
                errorContrasena = false
            },
            etiqueta = stringResource(id = R.string.login_contrasena),
            iconoInicio = Icons.Default.Lock,
            esContrasena = true,
            esError = errorContrasena,
            mensajeError = if (errorContrasena) msgCampoRequerido else null
        )

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                text = stringResource(id = R.string.login_recuperar),
                color = DoradoPrimario,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { alIrARecuperarClave() }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (estaCargando) {
            CircularProgressIndicator(
                color = DoradoPrimario,
                modifier = Modifier.size(36.dp)
            )
        } else {
            BotonPrincipal(
                texto = stringResource(id = R.string.login_iniciar),
                onClick = {
                    if (correo.isBlank()) {
                        errorCorreo = true
                        mensajeErrorCorreo = msgCampoRequerido
                    } else if (!correo.contains("@")) {
                        errorCorreo = true
                        mensajeErrorCorreo = msgCorreoInvalido
                    } else if (contrasena.isBlank()) {
                        errorContrasena = true
                    } else {
                        alIniciarSesion(correo.trim(), contrasena)
                    }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { alIniciarFacebook() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1877F2),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = stringResource(id = R.string.login_facebook),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.login_sin_cuenta),
                color = TextoGrisClaro,
                fontSize = 14.sp
            )
            Text(
                text = stringResource(id = R.string.login_registrarse),
                color = DoradoPrimario,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { alIrARegistro() }
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}
