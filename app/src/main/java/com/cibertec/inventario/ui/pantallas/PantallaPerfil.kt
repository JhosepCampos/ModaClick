package com.cibertec.inventario.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cibertec.inventario.R
import com.cibertec.inventario.modelo.RolUsuario
import com.cibertec.inventario.modelo.Usuario
import com.cibertec.inventario.ui.componentes.BotonSecundario
import com.cibertec.inventario.ui.componentes.DialogoConfirmacion
import com.cibertec.inventario.ui.theme.BordeSutil
import com.cibertec.inventario.ui.theme.DoradoPrimario
import com.cibertec.inventario.ui.theme.ErrorRojo
import com.cibertec.inventario.ui.theme.ExitoVerde
import com.cibertec.inventario.ui.theme.FondoOscuro
import com.cibertec.inventario.ui.theme.SuperficieCard
import com.cibertec.inventario.ui.theme.SuperficieElevada
import com.cibertec.inventario.ui.theme.TextoBlanco
import com.cibertec.inventario.ui.theme.TextoGrisClaro
import com.cibertec.inventario.ui.theme.TextoMuted

@Composable
fun ItemInfoPerfil(
    icono: ImageVector,
    titulo: String,
    valor: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(SuperficieElevada),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = DoradoPrimario,
                modifier = Modifier.size(20.dp)
            )
        }
        Column(modifier = Modifier.padding(start = 14.dp)) {
            Text(
                text = titulo,
                color = TextoMuted,
                fontSize = 12.sp
            )
            Text(
                text = valor,
                color = TextoBlanco,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ItemInfoPerfilEditable(
    icono: ImageVector,
    titulo: String,
    valor: String,
    hint: String,
    tipoTeclado: KeyboardType = KeyboardType.Text,
    alGuardar: (String) -> Unit
) {
    var editando by remember { mutableStateOf(false) }
    var textoEditado by remember(valor, editando) { mutableStateOf(valor) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(SuperficieElevada),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = DoradoPrimario,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        if (!editando) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titulo,
                    color = TextoMuted,
                    fontSize = 12.sp
                )
                Text(
                    text = valor.ifBlank { stringResource(id = R.string.perfil_sin_datos) },
                    color = if (valor.isBlank()) TextoMuted else TextoBlanco,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            IconButton(
                onClick = {
                    textoEditado = valor
                    editando = true
                },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(id = R.string.perfil_editar_campo),
                    tint = DoradoPrimario,
                    modifier = Modifier.size(20.dp)
                )
            }
        } else {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titulo,
                    color = DoradoPrimario,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                        .background(SuperficieElevada, RoundedCornerShape(8.dp))
                        .border(
                            1.dp,
                            DoradoPrimario.copy(alpha = 0.6f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    if (textoEditado.isEmpty()) {
                        Text(
                            text = hint,
                            color = TextoMuted,
                            fontSize = 14.sp
                        )
                    }
                    BasicTextField(
                        value = textoEditado,
                        onValueChange = { textoEditado = it },
                        keyboardOptions = KeyboardOptions(keyboardType = tipoTeclado),
                        singleLine = true,
                        textStyle = TextStyle(
                            color = TextoBlanco,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            IconButton(
                onClick = {
                    textoEditado = valor
                    editando = false
                },
                modifier = Modifier.size(34.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(id = R.string.perfil_cancelar_campo),
                    tint = ErrorRojo,
                    modifier = Modifier.size(22.dp)
                )
            }

            IconButton(
                onClick = {
                    alGuardar(textoEditado)
                    editando = false
                },
                modifier = Modifier.size(34.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = stringResource(id = R.string.perfil_guardar_campo),
                    tint = ExitoVerde,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
fun PantallaPerfil(
    usuario: Usuario,
    alActualizarTelefono: (String) -> Unit = {},
    alActualizarDireccion: (String) -> Unit = {},
    alCerrarSesion: () -> Unit
) {
    var mostrarDialogoCerrarSesion by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoOscuro)
            .verticalScroll(scrollState)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(SuperficieCard),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = DoradoPrimario,
                modifier = Modifier.size(52.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = usuario.nombreCompleto,
            color = TextoBlanco,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = if (usuario.correo.isNotBlank()) usuario.correo else usuario.usuario,
            color = DoradoPrimario,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SuperficieCard),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BordeSutil))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(id = R.string.perfil_info_personal),
                    color = TextoBlanco,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                ItemInfoPerfil(
                    icono = Icons.Default.Email,
                    titulo = stringResource(id = R.string.perfil_correo),
                    valor = if (usuario.correo.isNotBlank()) usuario.correo else stringResource(id = R.string.perfil_sin_datos)
                )

                HorizontalDivider(color = BordeSutil, thickness = 1.dp)

                ItemInfoPerfilEditable(
                    icono = Icons.Default.Phone,
                    titulo = stringResource(id = R.string.perfil_telefono),
                    valor = usuario.telefono,
                    hint = stringResource(id = R.string.perfil_hint_telefono),
                    tipoTeclado = KeyboardType.Phone,
                    alGuardar = alActualizarTelefono
                )

                HorizontalDivider(color = BordeSutil, thickness = 1.dp)

                ItemInfoPerfilEditable(
                    icono = Icons.Default.Home,
                    titulo = stringResource(id = R.string.perfil_direccion),
                    valor = usuario.direccion,
                    hint = stringResource(id = R.string.perfil_hint_direccion),
                    tipoTeclado = KeyboardType.Text,
                    alGuardar = alActualizarDireccion
                )

                HorizontalDivider(color = BordeSutil, thickness = 1.dp)

                ItemInfoPerfil(
                    icono = Icons.Default.Security,
                    titulo = stringResource(id = R.string.perfil_rol),
                    valor = if (usuario.obtenerRolEnum() == RolUsuario.ADMIN) {
                        stringResource(id = R.string.rol_admin)
                    } else {
                        stringResource(id = R.string.rol_cliente)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        BotonSecundario(
            texto = stringResource(id = R.string.perfil_cerrar_sesion),
            onClick = { mostrarDialogoCerrarSesion = true },
            colorBorde = ErrorRojo.copy(alpha = 0.8f),
            colorTexto = ErrorRojo
        )

        Spacer(modifier = Modifier.height(30.dp))
    }

    if (mostrarDialogoCerrarSesion) {
        DialogoConfirmacion(
            titulo = stringResource(id = R.string.dialogo_cerrar_titulo),
            descripcion = stringResource(id = R.string.dialogo_cerrar_desc),
            alConfirmar = {
                mostrarDialogoCerrarSesion = false
                alCerrarSesion()
            },
            alDescartar = {
                mostrarDialogoCerrarSesion = false
            }
        )
    }
}
