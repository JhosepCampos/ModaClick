package com.cibertec.inventario.ui.componentes

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cibertec.inventario.R
import com.cibertec.inventario.navegacion.Rutas
import com.cibertec.inventario.ui.theme.DoradoPrimario
import com.cibertec.inventario.ui.theme.FondoOscuro
import com.cibertec.inventario.ui.theme.SuperficieOscura
import com.cibertec.inventario.ui.theme.TextoBlanco
import com.cibertec.inventario.ui.theme.TextoMuted

data class ElementoNavegacion(
    val ruta: String,
    val tituloRes: Int,
    val icono: ImageVector,
    val tieneBadge: Boolean = false,
    val badgeContador: Int = 0
)

@Composable
fun BarraNavegacionCliente(
    rutaActual: String,
    cantidadCarrito: Int = 0,
    alNavegar: (String) -> Unit
) {
    val items = listOf(
        ElementoNavegacion(Rutas.Inicio.ruta, R.string.nav_inicio, Icons.Default.Home),
        ElementoNavegacion(Rutas.Catalogo.ruta, R.string.nav_catalogo, Icons.Default.GridView),
        ElementoNavegacion(
            Rutas.Carrito.ruta,
            R.string.nav_carrito,
            Icons.Default.ShoppingCart,
            tieneBadge = cantidadCarrito > 0,
            badgeContador = cantidadCarrito
        ),
        ElementoNavegacion(Rutas.HistorialPedidos.ruta, R.string.nav_pedidos, Icons.Default.Assignment),
        ElementoNavegacion(Rutas.Perfil.ruta, R.string.nav_perfil, Icons.Default.Person)
    )

    NavigationBar(
        containerColor = SuperficieOscura,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val seleccionado = rutaActual == item.ruta
            NavigationBarItem(
                selected = seleccionado,
                onClick = { alNavegar(item.ruta) },
                icon = {
                    if (item.tieneBadge) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = DoradoPrimario,
                                    contentColor = FondoOscuro
                                ) {
                                    Text(text = item.badgeContador.toString())
                                }
                            }
                        ) {
                            Icon(
                                imageVector = item.icono,
                                contentDescription = stringResource(id = item.tituloRes),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    } else {
                        Icon(
                            imageVector = item.icono,
                            contentDescription = stringResource(id = item.tituloRes),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                label = {
                    Text(
                        text = stringResource(id = item.tituloRes),
                        fontSize = 11.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = FondoOscuro,
                    selectedTextColor = DoradoPrimario,
                    indicatorColor = DoradoPrimario,
                    unselectedIconColor = TextoMuted,
                    unselectedTextColor = TextoMuted
                )
            )
        }
    }
}

@Composable
fun BarraNavegacionAdmin(
    rutaActual: String,
    alNavegar: (String) -> Unit
) {
    val items = listOf(
        ElementoNavegacion(Rutas.AdminPedidos.ruta, R.string.admin_pedidos, Icons.Default.Assignment),
        ElementoNavegacion(Rutas.AdminCatalogo.ruta, R.string.nav_catalogo, Icons.Default.GridView),
        ElementoNavegacion(Rutas.PerfilAdmin.ruta, R.string.admin_perfil, Icons.Default.Person)
    )

    NavigationBar(
        containerColor = SuperficieOscura,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val seleccionado = rutaActual == item.ruta
            NavigationBarItem(
                selected = seleccionado,
                onClick = { alNavegar(item.ruta) },
                icon = {
                    Icon(
                        imageVector = item.icono,
                        contentDescription = stringResource(id = item.tituloRes),
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = stringResource(id = item.tituloRes),
                        fontSize = 11.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = FondoOscuro,
                    selectedTextColor = DoradoPrimario,
                    indicatorColor = DoradoPrimario,
                    unselectedIconColor = TextoMuted,
                    unselectedTextColor = TextoMuted
                )
            )
        }
    }
}
