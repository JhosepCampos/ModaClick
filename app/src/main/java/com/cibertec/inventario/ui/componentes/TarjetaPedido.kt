package com.cibertec.inventario.ui.componentes

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cibertec.inventario.R
import com.cibertec.inventario.modelo.Categoria
import com.cibertec.inventario.modelo.EstadoPedido
import com.cibertec.inventario.modelo.Pedido
import com.cibertec.inventario.ui.theme.AlertaNaranja
import com.cibertec.inventario.ui.theme.AzulInfo
import com.cibertec.inventario.ui.theme.BordeSutil
import com.cibertec.inventario.ui.theme.DoradoPrimario
import com.cibertec.inventario.ui.theme.ExitoVerde
import com.cibertec.inventario.ui.theme.SuperficieCard
import com.cibertec.inventario.ui.theme.TextoBlanco
import com.cibertec.inventario.ui.theme.TextoGrisClaro
import com.cibertec.inventario.ui.theme.TextoMuted

@Composable
fun colorPorEstado(estado: EstadoPedido): Color {
    return when (estado) {
        EstadoPedido.PENDIENTE -> AlertaNaranja
        EstadoPedido.EN_CURSO -> AzulInfo
        EstadoPedido.ENTREGADO -> ExitoVerde
    }
}

@Composable
fun TarjetaPedido(
    pedido: Pedido,
    esAdmin: Boolean = false,
    alCambiarEstado: (() -> Unit)? = null,
    alDescargarReporte: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val estadoEnum = pedido.obtenerEstadoEnum()
    val colorEstado = colorPorEstado(estadoEnum)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SuperficieCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BordeSutil))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalShipping,
                        contentDescription = null,
                        tint = DoradoPrimario,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = stringResource(id = R.string.historial_numero, pedido.id),
                        color = TextoBlanco,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(colorEstado.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = stringResource(id = estadoEnum.etiquetaRes),
                        color = colorEstado,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = stringResource(id = R.string.historial_fecha, pedido.fecha),
                color = TextoMuted,
                fontSize = 13.sp
            )

            if (esAdmin && pedido.clienteNombre.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(id = R.string.admin_cliente, pedido.clienteNombre),
                    color = TextoGrisClaro,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = BordeSutil, thickness = 1.dp)
            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                pedido.items.forEach { item ->
                    val catEnum = try {
                        Categoria.valueOf(item.prendaCategoria.uppercase())
                    } catch (e: Exception) {
                        Categoria.TODOS
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            ImagenPrenda(
                                imagenUrl = item.prendaImagenUrl,
                                categoria = catEnum,
                                modifier = Modifier.fillMaxSize(),
                                tamanoIconoFallback = 24.dp,
                                descripcion = item.prendaNombre
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "${item.cantidad}x ${item.prendaNombre}",
                                color = TextoBlanco,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            val detalles = buildString {
                                append("Talla: ${item.tallaSeleccionada}")
                                if (item.colorSeleccionado.isNotBlank()) {
                                    append(" • ${item.colorSeleccionado}")
                                }
                            }
                            Text(
                                text = detalles,
                                color = TextoMuted,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = stringResource(id = R.string.precio_formato, item.subtotal),
                            color = DoradoPrimario,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.carrito_total),
                    color = TextoBlanco,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = stringResource(id = R.string.precio_formato, pedido.total),
                    color = DoradoPrimario,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (esAdmin && alCambiarEstado != null) {
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedButton(
                    onClick = alCambiarEstado,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DoradoPrimario),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(DoradoPrimario))
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = stringResource(id = R.string.admin_cambiar_estado),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            } else if (alDescargarReporte != null) {
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedButton(
                    onClick = alDescargarReporte,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DoradoPrimario),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(BordeSutil))
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = stringResource(id = R.string.historial_descargar),
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
