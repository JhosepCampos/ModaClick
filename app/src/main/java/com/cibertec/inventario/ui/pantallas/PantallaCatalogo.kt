package com.cibertec.inventario.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cibertec.inventario.R
import com.cibertec.inventario.modelo.Categoria
import com.cibertec.inventario.modelo.FiltroColeccion
import com.cibertec.inventario.modelo.Prenda
import com.cibertec.inventario.ui.componentes.TarjetaPrenda
import com.cibertec.inventario.ui.theme.BordeSutil
import com.cibertec.inventario.ui.theme.DoradoPrimario
import com.cibertec.inventario.ui.theme.FondoOscuro
import com.cibertec.inventario.ui.theme.SuperficieCard
import com.cibertec.inventario.ui.theme.TextoBlanco
import com.cibertec.inventario.ui.theme.TextoGrisClaro
import com.cibertec.inventario.ui.theme.TextoMuted

@Composable
fun PantallaCatalogo(
    prendas: List<Prenda>,
    textoBusqueda: String,
    alCambiarBusqueda: (String) -> Unit,
    categoriaSeleccionada: Categoria,
    alSeleccionarCategoria: (Categoria) -> Unit,
    coleccionSeleccionada: FiltroColeccion = FiltroColeccion.TODOS,
    alSeleccionarColeccion: (FiltroColeccion) -> Unit = {},
    alSeleccionarPrenda: (String) -> Unit,
    alAgregarAlCarrito: (Prenda) -> Unit
) {
    val todasLasCategorias = listOf(
        Categoria.TODOS,
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
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            OutlinedTextField(
                value = textoBusqueda,
                onValueChange = alCambiarBusqueda,
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.catalogo_buscar),
                        color = TextoMuted,
                        fontSize = 14.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = DoradoPrimario
                    )
                },
                trailingIcon = {
                    if (textoBusqueda.isNotEmpty()) {
                        IconButton(onClick = { alCambiarBusqueda("") }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Limpiar",
                                tint = TextoMuted
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SuperficieCard,
                    unfocusedContainerColor = SuperficieCard,
                    focusedBorderColor = DoradoPrimario,
                    unfocusedBorderColor = BordeSutil,
                    focusedTextColor = TextoBlanco,
                    unfocusedTextColor = TextoBlanco,
                    cursorColor = DoradoPrimario
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 2.dp, bottom = 4.dp)
        ) {
            items(FiltroColeccion.values()) { col ->
                val seleccionada = col == coleccionSeleccionada
                FilterChip(
                    selected = seleccionada,
                    onClick = { alSeleccionarColeccion(col) },
                    label = {
                        Text(
                            text = stringResource(id = col.etiquetaRes),
                            fontSize = 13.sp,
                            fontWeight = if (seleccionada) FontWeight.Bold else FontWeight.Medium
                        )
                    },
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
                        selected = seleccionada
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 6.dp)
        ) {
            items(todasLasCategorias) { cat ->
                val seleccionada = cat == categoriaSeleccionada
                FilterChip(
                    selected = seleccionada,
                    onClick = { alSeleccionarCategoria(cat) },
                    label = {
                        Text(
                            text = stringResource(id = cat.nombreRes),
                            fontSize = 13.sp,
                            fontWeight = if (seleccionada) FontWeight.Bold else FontWeight.Normal
                        )
                    },
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
                        selected = seleccionada
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        if (prendas.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(id = R.string.catalogo_sin_resultados),
                        color = TextoBlanco,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Intenta buscar con otra palabra clave o categoría",
                        color = TextoMuted,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(prendas, key = { it.id }) { prenda ->
                    TarjetaPrenda(
                        prenda = prenda,
                        alHacerClic = { alSeleccionarPrenda(prenda.id) },
                        alAgregarCarrito = { alAgregarAlCarrito(prenda) }
                    )
                }
            }
        }
    }
}
