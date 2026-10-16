package com.cibertec.inventario.ui.pantallas

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cibertec.inventario.R
import com.cibertec.inventario.modelo.EstadoPedido
import com.cibertec.inventario.modelo.Pedido
import com.cibertec.inventario.modelo.Usuario
import com.cibertec.inventario.ui.componentes.BotonSecundario
import com.cibertec.inventario.ui.componentes.DialogoConfirmacion
import com.cibertec.inventario.ui.theme.AlertaNaranja
import com.cibertec.inventario.ui.theme.AzulInfo
import com.cibertec.inventario.ui.theme.BordeSutil
import com.cibertec.inventario.ui.theme.DoradoPrimario
import com.cibertec.inventario.ui.theme.ErrorRojo
import com.cibertec.inventario.ui.theme.ExitoVerde
import com.cibertec.inventario.ui.theme.FondoOscuro
import com.cibertec.inventario.ui.theme.SuperficieCard
import com.cibertec.inventario.ui.theme.SuperficieElevada
import com.cibertec.inventario.ui.theme.TextoBlanco
import com.cibertec.inventario.ui.theme.TextoMuted

@Composable
fun TarjetaMetricaAdmin(
    titulo: String,
    valor: String,
    colorValor: androidx.compose.ui.graphics.Color = DoradoPrimario,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SuperficieElevada),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BordeSutil))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = titulo,
                color = TextoMuted,
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = valor,
                color = colorValor,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PantallaPerfilAdmin(
    admin: Usuario,
    pedidos: List<Pedido>,
    alActualizarTelefono: (String) -> Unit = {},
    alActualizarDireccion: (String) -> Unit = {},
    alCerrarSesion: () -> Unit
) {
    var mostrarDialogoCerrarSesion by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    val totalPedidos = pedidos.size
    val pedidosPendientes = pedidos.count { it.obtenerEstadoEnum() == EstadoPedido.PENDIENTE }
    val pedidosEnCurso = pedidos.count { it.obtenerEstadoEnum() == EstadoPedido.EN_CURSO }
    val pedidosEntregados = pedidos.count { it.obtenerEstadoEnum() == EstadoPedido.ENTREGADO }
    val totalVentas = pedidos.sumOf { it.total }

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
                imageVector = Icons.Default.AdminPanelSettings,
                contentDescription = null,
                tint = DoradoPrimario,
                modifier = Modifier.size(52.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = admin.nombreCompleto,
            color = TextoBlanco,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stringResource(id = R.string.admin_subtitulo_perfil),
            color = DoradoPrimario,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(id = R.string.admin_metricas_generales),
            color = TextoBlanco,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TarjetaMetricaAdmin(
                titulo = stringResource(id = R.string.admin_total_pedidos),
                valor = totalPedidos.toString(),
                modifier = Modifier.weight(1f)
            )
            TarjetaMetricaAdmin(
                titulo = stringResource(id = R.string.admin_ingresos_totales),
                valor = stringResource(id = R.string.precio_formato, totalVentas),
                colorValor = DoradoPrimario,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TarjetaMetricaAdmin(
                titulo = stringResource(id = R.string.admin_pendientes),
                valor = pedidosPendientes.toString(),
                colorValor = AlertaNaranja,
                modifier = Modifier.weight(1f)
            )
            TarjetaMetricaAdmin(
                titulo = stringResource(id = R.string.admin_en_curso),
                valor = pedidosEnCurso.toString(),
                colorValor = AzulInfo,
                modifier = Modifier.weight(1f)
            )
            TarjetaMetricaAdmin(
                titulo = stringResource(id = R.string.admin_entregados),
                valor = pedidosEntregados.toString(),
                colorValor = ExitoVerde,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SuperficieCard),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BordeSutil))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(id = R.string.admin_cuenta_titulo),
                    color = TextoBlanco,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                ItemInfoPerfil(
                    icono = Icons.Default.Email,
                    titulo = stringResource(id = R.string.perfil_correo),
                    valor = admin.correo
                )

                HorizontalDivider(color = BordeSutil, thickness = 1.dp)

                ItemInfoPerfilEditable(
                    icono = Icons.Default.Phone,
                    titulo = stringResource(id = R.string.perfil_telefono),
                    valor = admin.telefono,
                    hint = stringResource(id = R.string.perfil_hint_telefono),
                    tipoTeclado = KeyboardType.Phone,
                    alGuardar = alActualizarTelefono
                )

                HorizontalDivider(color = BordeSutil, thickness = 1.dp)

                ItemInfoPerfilEditable(
                    icono = Icons.Default.Home,
                    titulo = stringResource(id = R.string.perfil_direccion),
                    valor = admin.direccion,
                    hint = stringResource(id = R.string.perfil_hint_direccion),
                    tipoTeclado = KeyboardType.Text,
                    alGuardar = alActualizarDireccion
                )

                HorizontalDivider(color = BordeSutil, thickness = 1.dp)

                ItemInfoPerfil(
                    icono = Icons.Default.Security,
                    titulo = stringResource(id = R.string.perfil_rol),
                    valor = stringResource(id = R.string.rol_admin)
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
