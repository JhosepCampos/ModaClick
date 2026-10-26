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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cibertec.inventario.R
import com.cibertec.inventario.modelo.Categoria
import com.cibertec.inventario.modelo.Prenda
import com.cibertec.inventario.ui.componentes.TarjetaPrenda
import com.cibertec.inventario.ui.componentes.iconoPorCategoria
import com.cibertec.inventario.ui.theme.DoradoOscuro
import com.cibertec.inventario.ui.theme.DoradoPrimario
import com.cibertec.inventario.ui.theme.FondoOscuro
import com.cibertec.inventario.ui.theme.SuperficieCard
import com.cibertec.inventario.ui.theme.SuperficieElevada
import com.cibertec.inventario.ui.theme.TextoBlanco
import com.cibertec.inventario.ui.theme.TextoGrisClaro
import com.cibertec.inventario.ui.theme.TextoMuted

@Composable
fun PantallaInicio(
    nombreUsuario: String = "Usuario",
    prendasDestacadas: List<Prenda> = emptyList(),
    prendasNovedades: List<Prenda> = emptyList(),
    alIrACatalogo: () -> Unit,
    alIrATemporada: () -> Unit = alIrACatalogo,
    alIrADestacados: () -> Unit = alIrACatalogo,
    alIrANovedades: () -> Unit = alIrACatalogo,
    alSeleccionarCategoria: (Categoria) -> Unit,
    alSeleccionarPrenda: (String) -> Unit,
    alAgregarAlCarrito: (Prenda) -> Unit
) {
    val scrollState = rememberScrollState()
    val categorias = listOf(
        Categoria.CAMISAS,
        Categoria.PANTALONES,
        Categoria.VESTIDOS,
        Categoria.ZAPATOS,
        Categoria.ACCESORIOS,
        Categoria.CHAQUETAS
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoOscuro)
            .verticalScroll(scrollState)
            .padding(bottom = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.inicio_hola, nombreUsuario),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextoBlanco
            )
            /*Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(id = R.string.inicio_subtitulo),
                fontSize = 14.sp,
                color = TextoMuted
            )*/
        }

        /*Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SuperficieCard)
                .clickable { alIrACatalogo() }
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = DoradoPrimario
                )
                Text(
                    text = stringResource(id = R.string.inicio_buscar),
                    color = TextoMuted,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))*/

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SuperficieElevada)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            listOf(DoradoOscuro.copy(alpha = 0.5f), SuperficieElevada)
                        )
                    )
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalOffer,
                                contentDescription = null,
                                tint = DoradoPrimario,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "NUEVA TEMPORADA",
                                color = DoradoPrimario,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Hasta 30% OFF en prendas seleccionadas",
                            color = TextoBlanco,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 22.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(DoradoPrimario)
                            .clickable { alIrATemporada() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Explorar",
                            tint = FondoOscuro,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.inicio_categorias),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextoBlanco
            )
            Text(
                text = stringResource(id = R.string.inicio_ver_todo),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = DoradoPrimario,
                modifier = Modifier.clickable { alIrACatalogo() }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(categorias) { cat ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { alSeleccionarCategoria(cat) }
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(SuperficieCard),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = iconoPorCategoria(cat),
                            contentDescription = null,
                            tint = DoradoPrimario,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = stringResource(id = cat.nombreRes),
                        fontSize = 12.sp,
                        color = TextoGrisClaro,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        if (prendasDestacadas.isNotEmpty()) {
            Spacer(modifier = Modifier.height(26.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.inicio_destacados),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoBlanco
                )
                Text(
                    text = stringResource(id = R.string.inicio_ver_todo),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DoradoPrimario,
                    modifier = Modifier.clickable { alIrADestacados() }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(prendasDestacadas, key = { it.id }) { prenda ->
                    TarjetaPrenda(
                        prenda = prenda,
                        alHacerClic = { alSeleccionarPrenda(prenda.id) },
                        alAgregarCarrito = { alAgregarAlCarrito(prenda) },
                        modifier = Modifier.width(170.dp)
                    )
                }
            }
        }

        if (prendasNovedades.isNotEmpty()) {
            Spacer(modifier = Modifier.height(26.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.inicio_novedades),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoBlanco
                )
                Text(
                    text = stringResource(id = R.string.inicio_ver_todo),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DoradoPrimario,
                    modifier = Modifier.clickable { alIrANovedades() }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(prendasNovedades, key = { it.id }) { prenda ->
                    TarjetaPrenda(
                        prenda = prenda,
                        alHacerClic = { alSeleccionarPrenda(prenda.id) },
                        alAgregarCarrito = { alAgregarAlCarrito(prenda) },
                        modifier = Modifier.width(170.dp)
                    )
                }
            }
        }
    }
}
