package com.cibertec.inventario.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RemoveShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cibertec.inventario.R
import com.cibertec.inventario.modelo.Categoria
import com.cibertec.inventario.modelo.ItemCarrito
import com.cibertec.inventario.ui.componentes.BotonPrincipal
import com.cibertec.inventario.ui.componentes.DialogoConfirmacion
import com.cibertec.inventario.ui.componentes.ImagenPrenda
import com.cibertec.inventario.ui.componentes.iconoPorCategoria
import com.cibertec.inventario.ui.theme.BordeSutil
import com.cibertec.inventario.ui.theme.DoradoPrimario
import com.cibertec.inventario.ui.theme.ErrorRojo
import com.cibertec.inventario.ui.theme.FondoOscuro
import com.cibertec.inventario.ui.theme.SuperficieCard
import com.cibertec.inventario.ui.theme.SuperficieElevada
import com.cibertec.inventario.ui.theme.TextoBlanco
import com.cibertec.inventario.ui.theme.TextoGrisClaro
import com.cibertec.inventario.ui.theme.TextoMuted

@Composable
fun PantallaCarrito(
    items: List<ItemCarrito>,
    alModificarCantidad: (ItemCarrito, Int) -> Unit,
    alEliminarItem: (ItemCarrito) -> Unit,
    alProcederPago: () -> Unit,
    alSeguirComprando: () -> Unit
) {
    var itemAEliminar by remember { mutableStateOf<ItemCarrito?>(null) }
    val total = items.sumOf { it.subtotal }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoOscuro)
    ) {
        if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(SuperficieCard),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.RemoveShoppingCart,
                            contentDescription = null,
                            tint = DoradoPrimario,
                            modifier = Modifier.size(44.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = stringResource(id = R.string.carrito_vacio_titulo),
                        color = TextoBlanco,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = stringResource(id = R.string.carrito_vacio_desc),
                        color = TextoMuted,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    BotonPrincipal(
                        texto = "Explorar Catálogo",
                        onClick = alSeguirComprando,
                        modifier = Modifier.width(220.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items, key = { "${it.prendaId}_${it.tallaSeleccionada}_${it.colorSeleccionado}" }) { item ->
                    val catEnum = try {
                        Categoria.valueOf(item.prendaCategoria.uppercase())
                    } catch (e: Exception) {
                        Categoria.CAMISAS
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = SuperficieCard),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BordeSutil))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                ImagenPrenda(
                                    imagenUrl = item.prendaImagenUrl,
                                    categoria = catEnum,
                                    modifier = Modifier.fillMaxSize(),
                                    tamanoIconoFallback = 32.dp,
                                    descripcion = item.prendaNombre
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.prendaNombre,
                                    color = TextoBlanco,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = "Talla: ${item.tallaSeleccionada} | ${item.colorSeleccionado}",
                                    color = TextoMuted,
                                    fontSize = 12.sp
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = stringResource(id = R.string.precio_formato, item.subtotal),
                                    color = DoradoPrimario,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                if (item.prendaStock <= 0) {
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = stringResource(id = R.string.prenda_sin_stock),
                                        color = ErrorRojo,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                } else if (item.cantidad > item.prendaStock) {
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = stringResource(id = R.string.prenda_disponibles, item.prendaStock),
                                        color = ErrorRojo,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            Column(
                                horizontalAlignment = Alignment.End,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                IconButton(
                                    onClick = { itemAEliminar = item },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = "Eliminar",
                                        tint = ErrorRojo,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(26.dp)
                                            .clip(CircleShape)
                                            .background(SuperficieElevada)
                                            .clickable {
                                                if (item.cantidad > 1) {
                                                    alModificarCantidad(item, item.cantidad - 1)
                                                } else {
                                                    itemAEliminar = item
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Remove,
                                            contentDescription = "Restar",
                                            tint = TextoBlanco,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }

                                    Text(
                                        text = item.cantidad.toString(),
                                        color = TextoBlanco,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    val puedeAumentar = item.prendaStock > 0 && item.cantidad < item.prendaStock
                                    Box(
                                        modifier = Modifier
                                            .size(26.dp)
                                            .clip(CircleShape)
                                            .background(SuperficieElevada)
                                            .clickable(enabled = puedeAumentar) {
                                                if (puedeAumentar) {
                                                    alModificarCantidad(item, item.cantidad + 1)
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Sumar",
                                            tint = if (puedeAumentar) TextoBlanco else TextoMuted,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                colors = CardDefaults.cardColors(containerColor = SuperficieCard)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.carrito_total),
                            color = TextoGrisClaro,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = stringResource(id = R.string.precio_formato, total),
                            color = DoradoPrimario,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val hayItemsSinStock = items.any { it.prendaStock <= 0 || it.cantidad > it.prendaStock }

                    if (hayItemsSinStock) {
                        Text(
                            text = stringResource(id = R.string.error_pedido_sin_stock),
                            color = ErrorRojo,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    BotonPrincipal(
                        texto = stringResource(id = R.string.carrito_realizar_pedido),
                        habilitado = !hayItemsSinStock,
                        onClick = alProcederPago
                    )
                }
            }
        }
    }

    itemAEliminar?.let { item ->
        DialogoConfirmacion(
            titulo = stringResource(id = R.string.dialogo_eliminar_titulo),
            descripcion = stringResource(id = R.string.dialogo_eliminar_desc),
            alConfirmar = {
                alEliminarItem(item)
                itemAEliminar = null
            },
            alDescartar = {
                itemAEliminar = null
            }
        )
    }
}
