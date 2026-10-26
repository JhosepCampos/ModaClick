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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cibertec.inventario.R
import com.cibertec.inventario.modelo.Prenda
import com.cibertec.inventario.ui.componentes.BarraSuperior
import com.cibertec.inventario.ui.componentes.BotonPrincipal
import com.cibertec.inventario.ui.componentes.ImagenPrenda
import com.cibertec.inventario.ui.componentes.iconoPorCategoria
import com.cibertec.inventario.ui.theme.BordeSutil
import com.cibertec.inventario.ui.theme.DoradoClaro
import com.cibertec.inventario.ui.theme.DoradoPrimario
import com.cibertec.inventario.ui.theme.ErrorRojo
import com.cibertec.inventario.ui.theme.FondoOscuro
import com.cibertec.inventario.ui.theme.SuperficieCard
import com.cibertec.inventario.ui.theme.SuperficieElevada
import com.cibertec.inventario.ui.theme.SuperficieOscura
import com.cibertec.inventario.ui.theme.TextoBlanco
import com.cibertec.inventario.ui.theme.TextoGrisClaro
import com.cibertec.inventario.ui.theme.TextoMuted

@Composable
fun PantallaDetallePrenda(
    prenda: Prenda?,
    alRegresar: () -> Unit,
    alAgregarAlCarrito: (Prenda, String, String, Int) -> Unit
) {
    if (prenda == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(FondoOscuro),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Prenda no encontrada", color = TextoBlanco)
        }
        return
    }

    val catEnum = prenda.obtenerCategoriaEnum()
    val sinStock = prenda.stock <= 0
    var tallaSeleccionada by remember { mutableStateOf(prenda.tallasDisponibles.firstOrNull() ?: "M") }
    var colorSeleccionado by remember { mutableStateOf(prenda.coloresDisponibles.firstOrNull() ?: "Negro") }
    var cantidad by remember(prenda.stock) { mutableIntStateOf(if (sinStock) 0 else 1) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoOscuro)
    ) {
        BarraSuperior(
            titulo = stringResource(id = R.string.detalle_titulo),
            puedeRegresar = true,
            alRegresar = alRegresar
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp),
                contentAlignment = Alignment.Center
            ) {
                ImagenPrenda(
                    imagenUrl = prenda.imagenUrl,
                    categoria = catEnum,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    contentScale = ContentScale.Fit,
                    tamanoIconoFallback = 110.dp,
                    descripcion = prenda.nombre
                )

                /*Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(FondoOscuro.copy(alpha = 0.85f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = DoradoPrimario,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${prenda.calificacion} (Valoración)",
                            color = TextoBlanco,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }*/
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = stringResource(id = catEnum.nombreRes),
                    color = DoradoClaro,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = prenda.nombre,
                    color = TextoBlanco,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(id = R.string.precio_formato, prenda.precio),
                    color = DoradoPrimario,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = BordeSutil)
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(id = R.string.detalle_descripcion),
                    color = TextoBlanco,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = prenda.descripcion,
                    color = TextoGrisClaro,
                    fontSize = 14.sp,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = stringResource(id = R.string.detalle_talla),
                    color = TextoBlanco,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(prenda.tallasDisponibles) { talla ->
                        val seleccionada = talla == tallaSeleccionada
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (seleccionada) DoradoPrimario else SuperficieCard)
                                .border(
                                    1.dp,
                                    if (seleccionada) DoradoPrimario else BordeSutil,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { tallaSeleccionada = talla },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = talla,
                                color = if (seleccionada) FondoOscuro else TextoBlanco,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = stringResource(id = R.string.detalle_color),
                    color = TextoBlanco,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(prenda.coloresDisponibles) { color ->
                        val seleccionado = color == colorSeleccionado
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (seleccionado) DoradoPrimario else SuperficieCard)
                                .border(
                                    1.dp,
                                    if (seleccionado) DoradoPrimario else BordeSutil,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { colorSeleccionado = color }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = color,
                                color = if (seleccionado) FondoOscuro else TextoBlanco,
                                fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = stringResource(id = R.string.detalle_cantidad),
                    color = TextoBlanco,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(SuperficieCard)
                            .clickable(enabled = !sinStock && cantidad > 1) { cantidad-- },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Disminuir",
                            tint = if (!sinStock && cantidad > 1) TextoBlanco else TextoMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Text(
                        text = cantidad.toString(),
                        color = if (sinStock) TextoMuted else TextoBlanco,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(SuperficieCard)
                            .clickable(enabled = !sinStock && cantidad < prenda.stock) { cantidad++ },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Aumentar",
                            tint = if (!sinStock && cantidad < prenda.stock) TextoBlanco else TextoMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = if (sinStock) {
                            stringResource(id = R.string.prenda_sin_stock)
                        } else {
                            stringResource(id = R.string.prenda_disponibles, prenda.stock)
                        },
                        color = if (sinStock) ErrorRojo else TextoMuted,
                        fontSize = 13.sp,
                        fontWeight = if (sinStock) FontWeight.Bold else FontWeight.Normal
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            colors = CardDefaults.cardColors(containerColor = SuperficieCard)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(0.45f)) {
                    Text(
                        text = "Subtotal",
                        color = TextoMuted,
                        fontSize = 12.sp
                    )
                    Text(
                        text = stringResource(id = R.string.precio_formato, if (sinStock) 0.0 else prenda.precio * cantidad),
                        color = DoradoPrimario,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                BotonPrincipal(
                    texto = if (!sinStock) {
                        stringResource(id = R.string.detalle_agregar)
                    } else {
                        stringResource(id = R.string.prenda_sin_stock)
                    },
                    habilitado = !sinStock && cantidad > 0,
                    onClick = {
                        if (!sinStock && cantidad > 0) {
                            alAgregarAlCarrito(prenda, tallaSeleccionada, colorSeleccionado, cantidad)
                        }
                    },
                    modifier = Modifier.weight(0.55f)
                )
            }
        }
    }
}
