package com.cibertec.inventario.ui.componentes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cibertec.inventario.ui.theme.DoradoPrimario
import com.cibertec.inventario.ui.theme.FondoOscuro
import com.cibertec.inventario.ui.theme.TextoBlanco

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraSuperior(
    titulo: String,
    puedeRegresar: Boolean = false,
    alRegresar: (() -> Unit)? = null,
    mostrarCarrito: Boolean = false,
    cantidadCarrito: Int = 0,
    alClicCarrito: (() -> Unit)? = null,
    acciones: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                text = titulo,
                color = TextoBlanco,
                fontSize = 19.sp,
                fontWeight = FontWeight.SemiBold
            )
        },
        navigationIcon = {
            if (puedeRegresar && alRegresar != null) {
                IconButton(onClick = alRegresar) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Regresar",
                        tint = DoradoPrimario
                    )
                }
            }
        },
        actions = {
            if (mostrarCarrito && alClicCarrito != null) {
                IconButton(onClick = alClicCarrito) {
                    BadgedBox(
                        badge = {
                            if (cantidadCarrito > 0) {
                                Badge(
                                    containerColor = DoradoPrimario,
                                    contentColor = FondoOscuro
                                ) {
                                    Text(
                                        text = cantidadCarrito.toString(),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Carrito",
                            tint = DoradoPrimario,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
            acciones()
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = FondoOscuro,
            titleContentColor = TextoBlanco,
            navigationIconContentColor = DoradoPrimario
        )
    )
}
