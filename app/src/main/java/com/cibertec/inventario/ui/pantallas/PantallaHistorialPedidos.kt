package com.cibertec.inventario.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cibertec.inventario.R
import com.cibertec.inventario.modelo.EstadoPedido
import com.cibertec.inventario.modelo.Pedido
import com.cibertec.inventario.ui.componentes.DialogoConfirmacion
import com.cibertec.inventario.ui.componentes.TarjetaPedido
import com.cibertec.inventario.ui.theme.BordeSutil
import com.cibertec.inventario.ui.theme.DoradoPrimario
import com.cibertec.inventario.ui.theme.FondoOscuro
import com.cibertec.inventario.ui.theme.SuperficieCard
import com.cibertec.inventario.ui.theme.TextoBlanco
import com.cibertec.inventario.ui.theme.TextoGrisClaro
import com.cibertec.inventario.ui.theme.TextoMuted

@Composable
fun PantallaHistorialPedidos(
    pedidos: List<Pedido>,
    alDescargarReporteGeneral: (List<Pedido>, String) -> Unit,
    alDescargarReporteIndividual: (Pedido) -> Unit = {}
) {
    var filtroEstado by remember { mutableStateOf<EstadoPedido?>(null) }
    var mostrarDialogoReporte by remember { mutableStateOf(false) }
    var mostrarDialogoSinPedidos by remember { mutableStateOf(false) }
    var mensajeSinPedidos by remember { mutableStateOf("") }

    val msgSinPedidos = stringResource(id = R.string.dialogo_reporte_sin_pedidos_desc)
    val msgSinPedidosFiltro = stringResource(id = R.string.dialogo_reporte_sin_pedidos_filtro_desc)

    val pedidosFiltrados = remember(pedidos, filtroEstado) {
        if (filtroEstado == null) pedidos else pedidos.filter { it.obtenerEstadoEnum() == filtroEstado }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoOscuro)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = stringResource(id = R.string.historial_titulo),
                    color = TextoBlanco,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${pedidosFiltrados.size} pedidos registrados",
                    color = TextoMuted,
                    fontSize = 12.sp
                )
            }

            Button(
                onClick = {
                    if (pedidos.isEmpty()) {
                        mensajeSinPedidos = msgSinPedidos
                        mostrarDialogoSinPedidos = true
                    } else if (pedidosFiltrados.isEmpty()) {
                        mensajeSinPedidos = msgSinPedidosFiltro
                        mostrarDialogoSinPedidos = true
                    } else {
                        val subtitulo = if (filtroEstado != null) {
                            "Historial de Compras - Filtro: ${filtroEstado!!.name}"
                        } else {
                            "Historial de Compras del Cliente"
                        }
                        mostrarDialogoReporte = true
                        alDescargarReporteGeneral(pedidosFiltrados, subtitulo)
                    }
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DoradoPrimario,
                    contentColor = FondoOscuro
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.size(6.dp))
                Text(
                    text = "Reporte PDF",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            item {
                FilterChip(
                    selected = filtroEstado == null,
                    onClick = { filtroEstado = null },
                    label = { Text(text = stringResource(id = R.string.cat_todos), fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = SuperficieCard,
                        labelColor = TextoGrisClaro,
                        selectedContainerColor = DoradoPrimario,
                        selectedLabelColor = FondoOscuro
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = BordeSutil,
                        selectedBorderColor = DoradoPrimario,
                        enabled = true,
                        selected = filtroEstado == null
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
            }

            items(EstadoPedido.values()) { estado ->
                val seleccionado = filtroEstado == estado
                FilterChip(
                    selected = seleccionado,
                    onClick = { filtroEstado = estado },
                    label = { Text(stringResource(id = estado.etiquetaRes), fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = SuperficieCard,
                        labelColor = TextoGrisClaro,
                        selectedContainerColor = DoradoPrimario,
                        selectedLabelColor = FondoOscuro
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = BordeSutil,
                        selectedBorderColor = DoradoPrimario,
                        enabled = true,
                        selected = seleccionado
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }

        if (pedidosFiltrados.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(SuperficieCard),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AssignmentTurnedIn,
                            contentDescription = null,
                            tint = DoradoPrimario,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(id = R.string.historial_vacio_titulo),
                        color = TextoBlanco,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = stringResource(id = R.string.historial_vacio_desc),
                        color = TextoMuted,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(pedidosFiltrados, key = { it.id }) { pedido ->
                    TarjetaPedido(
                        pedido = pedido,
                        esAdmin = false,
                        alDescargarReporte = {
                            mostrarDialogoReporte = true
                            alDescargarReporteIndividual(pedido)
                        }
                    )
                }
            }
        }
    }

    if (mostrarDialogoSinPedidos) {
        DialogoConfirmacion(
            titulo = stringResource(id = R.string.dialogo_reporte_sin_pedidos_titulo),
            descripcion = mensajeSinPedidos,
            alConfirmar = { mostrarDialogoSinPedidos = false },
            alDescartar = { mostrarDialogoSinPedidos = false },
            mostrarCancelar = false
        )
    }

    if (mostrarDialogoReporte) {
        DialogoConfirmacion(
            titulo = stringResource(id = R.string.dialogo_reporte_titulo),
            descripcion = stringResource(id = R.string.dialogo_reporte_desc),
            alConfirmar = { mostrarDialogoReporte = false },
            alDescartar = { mostrarDialogoReporte = false },
            mostrarCancelar = false
        )
    }
}
