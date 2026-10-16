package com.cibertec.inventario.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cibertec.inventario.R
import com.cibertec.inventario.modelo.ItemCarrito
import com.cibertec.inventario.ui.componentes.BarraSuperior
import com.cibertec.inventario.ui.componentes.BotonPrincipal
import com.cibertec.inventario.ui.componentes.DialogoConfirmacion
import com.cibertec.inventario.ui.theme.BordeSutil
import com.cibertec.inventario.ui.theme.DoradoClaro
import com.cibertec.inventario.ui.theme.DoradoPrimario
import com.cibertec.inventario.ui.theme.ErrorRojo
import com.cibertec.inventario.ui.theme.FondoOscuro
import com.cibertec.inventario.ui.theme.SuperficieCard
import com.cibertec.inventario.ui.theme.SuperficieElevada
import com.cibertec.inventario.ui.theme.TextoBlanco
import com.cibertec.inventario.ui.theme.TextoGrisClaro
import com.cibertec.inventario.ui.theme.TextoMuted

import com.cibertec.inventario.ui.componentes.CampoTexto

@Composable
fun PantallaRealizarPedido(
    items: List<ItemCarrito>,
    direccionInicial: String = "",
    estaCargando: Boolean = false,
    errorServidor: String? = null,
    alRegresar: () -> Unit,
    alConfirmarPedido: (String, String) -> Unit
) {
    val hayItemsSinStock = items.any { it.prendaStock <= 0 || it.cantidad > it.prendaStock }
    var direccion by remember(direccionInicial) { mutableStateOf(direccionInicial) }
    var errorDireccion by remember { mutableStateOf(false) }
    val metodoTarjeta = stringResource(id = R.string.pedido_tarjeta)
    val metodoEfectivo = stringResource(id = R.string.pedido_efectivo)
    val metodoYape = "Yape / Plin"

    var metodoPagoSeleccionado by remember { mutableStateOf(metodoTarjeta) }
    var mostrarDialogoConfirmacion by remember { mutableStateOf(false) }

    val subtotal = items.sumOf { it.subtotal }
    val envio = 0.0
    val total = subtotal + envio

    val metodosPago = listOf(
        Pair(metodoTarjeta, Icons.Default.CreditCard),
        Pair(metodoYape, Icons.Default.PhoneAndroid),
        Pair(metodoEfectivo, Icons.Default.LocalAtm)
    )

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoOscuro)
    ) {
        BarraSuperior(
            titulo = stringResource(id = R.string.pedido_titulo),
            puedeRegresar = true,
            alRegresar = alRegresar
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(20.dp)
        ) {
            Text(
                text = stringResource(id = R.string.pedido_datos_envio),
                color = TextoBlanco,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            CampoTexto(
                valor = direccion,
                alCambiar = {
                    direccion = it
                    errorDireccion = false
                },
                etiqueta = stringResource(id = R.string.pedido_direccion),
                iconoInicio = Icons.Default.LocationOn,
                esError = errorDireccion,
                mensajeError = if (errorDireccion) stringResource(id = R.string.error_campo_requerido) else null
            )

            if (direccionInicial.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stringResource(id = R.string.pedido_direccion_perfil_aviso),
                    color = TextoMuted,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(id = R.string.pedido_metodo_pago),
                color = TextoBlanco,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                metodosPago.forEach { (nombre, icono) ->
                    val seleccionado = metodoPagoSeleccionado == nombre
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (seleccionado) SuperficieElevada else SuperficieCard)
                            .border(
                                1.5.dp,
                                if (seleccionado) DoradoPrimario else BordeSutil,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { metodoPagoSeleccionado = nombre }
                            .padding(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = icono,
                                contentDescription = null,
                                tint = if (seleccionado) DoradoPrimario else TextoMuted,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = nombre,
                                color = if (seleccionado) TextoBlanco else TextoGrisClaro,
                                fontSize = 14.sp,
                                fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Resumen de Artículos (${items.size})",
                color = TextoBlanco,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SuperficieCard)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${item.cantidad}x ${item.prendaNombre}",
                                color = TextoGrisClaro,
                                fontSize = 13.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = stringResource(id = R.string.precio_formato, item.subtotal),
                                color = TextoBlanco,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    HorizontalDivider(color = BordeSutil)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = stringResource(id = R.string.carrito_envio), color = TextoMuted, fontSize = 13.sp)
                        Text(text = stringResource(id = R.string.carrito_gratis), color = DoradoClaro, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = stringResource(id = R.string.carrito_total), color = TextoBlanco, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = stringResource(id = R.string.precio_formato, total),
                            color = DoradoPrimario,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            colors = CardDefaults.cardColors(containerColor = SuperficieCard)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (hayItemsSinStock) {
                    Text(
                        text = stringResource(id = R.string.error_pedido_sin_stock),
                        color = ErrorRojo,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                } else if (!errorServidor.isNullOrBlank()) {
                    Text(
                        text = errorServidor,
                        color = ErrorRojo,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (estaCargando) {
                    CircularProgressIndicator(
                        color = DoradoPrimario,
                        modifier = Modifier.size(36.dp)
                    )
                } else {
                    BotonPrincipal(
                        texto = stringResource(id = R.string.pedido_confirmar),
                        habilitado = !hayItemsSinStock && items.isNotEmpty(),
                        onClick = {
                            if (direccion.isBlank()) {
                                errorDireccion = true
                            } else {
                                mostrarDialogoConfirmacion = true
                            }
                        }
                    )
                }
            }
        }
    }

    if (mostrarDialogoConfirmacion) {
        DialogoConfirmacion(
            titulo = stringResource(id = R.string.dialogo_pedido_titulo),
            descripcion = stringResource(id = R.string.dialogo_pedido_desc),
            alConfirmar = {
                mostrarDialogoConfirmacion = false
                alConfirmarPedido(direccion, metodoPagoSeleccionado)
            },
            alDescartar = {
                mostrarDialogoConfirmacion = false
            }
        )
    }
}
