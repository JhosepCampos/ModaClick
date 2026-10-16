package com.cibertec.inventario.navegacion

sealed class Rutas(val ruta: String) {
    object Splash : Rutas("splash")
    object Login : Rutas("login")
    object Registro : Rutas("registro")
    object RecuperarClave : Rutas("recuperar_clave")

    object Inicio : Rutas("inicio")
    object Catalogo : Rutas("catalogo")
    object DetallePrenda : Rutas("detalle_prenda/{prendaId}") {
        fun crearRuta(prendaId: String) = "detalle_prenda/$prendaId"
    }
    object Carrito : Rutas("carrito")
    object RealizarPedido : Rutas("realizar_pedido")
    object HistorialPedidos : Rutas("historial_pedidos")
    object Perfil : Rutas("perfil")

    object AdminPedidos : Rutas("admin_pedidos")
    object AdminCatalogo : Rutas("admin_catalogo")
    object PerfilAdmin : Rutas("perfil_admin")
}
