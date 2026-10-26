package com.cibertec.inventario.ui.componentes

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.DryCleaning
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material.icons.filled.RollerShades
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cibertec.inventario.R
import com.cibertec.inventario.modelo.Categoria
import com.cibertec.inventario.modelo.Prenda
import com.cibertec.inventario.ui.theme.BordeSutil
import com.cibertec.inventario.ui.theme.DoradoClaro
import com.cibertec.inventario.ui.theme.DoradoPrimario
import com.cibertec.inventario.ui.theme.ErrorRojo
import com.cibertec.inventario.ui.theme.FondoOscuro
import com.cibertec.inventario.ui.theme.SuperficieCard
import com.cibertec.inventario.ui.theme.SuperficieElevada
import com.cibertec.inventario.ui.theme.TextoBlanco
import com.cibertec.inventario.ui.theme.TextoMuted

fun iconoPorCategoria(cat: Categoria): ImageVector {
    return when (cat) {
        Categoria.CAMISAS -> Icons.Default.Checkroom
        Categoria.PANTALONES -> Icons.Default.RollerShades
        Categoria.VESTIDOS -> Icons.Default.DryCleaning
        Categoria.ZAPATOS -> Icons.Default.LocalMall
        Categoria.ACCESORIOS -> Icons.Default.Diamond
        Categoria.CHAQUETAS -> Icons.Default.Checkroom
        else -> Icons.Default.Checkroom
    }
}

@Composable
fun TarjetaPrenda(
    prenda: Prenda,
    alHacerClic: () -> Unit,
    alAgregarCarrito: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val catEnum = prenda.obtenerCategoriaEnum()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = alHacerClic),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SuperficieCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BordeSutil, BordeSutil)))
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentAlignment = Alignment.Center
            ) {
                ImagenPrenda(
                    imagenUrl = prenda.imagenUrl,
                    categoria = catEnum,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(6.dp),
                    contentScale = ContentScale.Fit,
                    tamanoIconoFallback = 56.dp,
                    descripcion = prenda.nombre
                )

                /*Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(FondoOscuro.copy(alpha = 0.8f))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = DoradoPrimario,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = prenda.calificacion.toString(),
                            color = TextoBlanco,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }*/

                if (prenda.stock <= 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(ErrorRojo)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.prenda_sin_stock_badge),
                            color = TextoBlanco,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else if (prenda.esNovedad || prenda.esDestacado) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(DoradoPrimario)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (prenda.esNovedad) "NUEVO" else "TOP",
                            color = FondoOscuro,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = stringResource(id = catEnum.nombreRes),
                    color = DoradoClaro,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = prenda.nombre,
                    color = TextoBlanco,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.precio_formato, prenda.precio),
                        color = DoradoPrimario,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    /*if (alAgregarCarrito != null) {
                        val sinStock = prenda.stock <= 0
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(if (sinStock) SuperficieElevada else DoradoPrimario)
                                .clickable(enabled = !sinStock) { alAgregarCarrito() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddShoppingCart,
                                contentDescription = if (sinStock) stringResource(id = R.string.prenda_sin_stock) else "Añadir al carrito",
                                tint = if (sinStock) TextoMuted else FondoOscuro,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }*/
                }
            }
        }
    }
}
