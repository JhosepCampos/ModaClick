package com.cibertec.inventario.navegacion

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cibertec.inventario.R
import com.cibertec.inventario.modelo.Categoria
import com.cibertec.inventario.modelo.EstadoPedido
import com.cibertec.inventario.modelo.FiltroColeccion
import com.cibertec.inventario.modelo.RolUsuario
import com.cibertec.inventario.modelo.Usuario
import com.cibertec.inventario.ui.componentes.BarraNavegacionAdmin
import com.cibertec.inventario.ui.componentes.BarraNavegacionCliente
import com.cibertec.inventario.ui.componentes.BarraSuperior
import com.cibertec.inventario.ui.componentes.DialogoSinConexion
import com.cibertec.inventario.ui.pantallas.PantallaAdminCatalogo
import com.cibertec.inventario.ui.pantallas.PantallaAdminPedidos
import com.cibertec.inventario.ui.pantallas.PantallaCarrito
import com.cibertec.inventario.ui.pantallas.PantallaCatalogo
import com.cibertec.inventario.ui.pantallas.PantallaDetallePrenda
import com.cibertec.inventario.ui.pantallas.PantallaHistorialPedidos
import com.cibertec.inventario.ui.pantallas.PantallaInicio
import com.cibertec.inventario.ui.pantallas.PantallaLogin
import com.cibertec.inventario.ui.pantallas.PantallaPerfil
import com.cibertec.inventario.ui.pantallas.PantallaPerfilAdmin
import com.cibertec.inventario.ui.pantallas.PantallaRealizarPedido
import com.cibertec.inventario.ui.pantallas.PantallaRecuperarClave
import com.cibertec.inventario.ui.pantallas.PantallaRegistro
import com.cibertec.inventario.ui.pantallas.PantallaSplash
import com.cibertec.inventario.ui.theme.FondoOscuro
import com.cibertec.inventario.viewmodel.AdminViewModel
import com.cibertec.inventario.viewmodel.AuthViewModel
import com.cibertec.inventario.viewmodel.CarritoViewModel
import com.cibertec.inventario.viewmodel.CatalogoViewModel
import com.cibertec.inventario.viewmodel.ConexionViewModel
import com.cibertec.inventario.viewmodel.PedidosViewModel
import kotlinx.coroutines.launch

@Composable
fun NavegacionApp(
    controladorNavegacion: NavHostController = rememberNavController(),
    conexionViewModel: ConexionViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel(),
    catalogoViewModel: CatalogoViewModel = viewModel(),
    carritoViewModel: CarritoViewModel = viewModel(),
    pedidosViewModel: PedidosViewModel = viewModel(),
    adminViewModel: AdminViewModel = viewModel(),
    onFacebookLogin: () -> Unit = {}
) {
    val context = LocalContext.current
    val navBackStackEntry by controladorNavegacion.currentBackStackEntryAsState()
    val rutaActual = navBackStackEntry?.destination?.route ?: Rutas.Splash.ruta

    val hayInternet by conexionViewModel.hayInternet.collectAsState()

    val authEstado by authViewModel.estado.collectAsState()
    val todasLasPrendas by catalogoViewModel.todasLasPrendas.collectAsState()
    val prendasFiltradas by catalogoViewModel.prendasFiltradas.collectAsState()
    val prendasDestacadas by catalogoViewModel.prendasDestacadas.collectAsState()
    val prendasNovedades by catalogoViewModel.prendasNovedades.collectAsState()
    val textoBusqueda by catalogoViewModel.textoBusqueda.collectAsState()
    val categoriaSeleccionada by catalogoViewModel.categoriaSeleccionada.collectAsState()
    val coleccionSeleccionada by catalogoViewModel.coleccionSeleccionada.collectAsState()
    val catalogoEstaCargando by catalogoViewModel.estaCargando.collectAsState()

    val carritoItems by carritoViewModel.items.collectAsState()
    val pedidosCliente by pedidosViewModel.pedidosCliente.collectAsState()
    val todosLosPedidos by adminViewModel.todosLosPedidos.collectAsState()

    val usuarioActual = authEstado.usuarioAutenticado ?: Usuario()

    LaunchedEffect(authEstado.usuarioAutenticado) {
        val user = authEstado.usuarioAutenticado
        if (user != null && user.id.isNotBlank()) {
            pedidosViewModel.cargarPedidosDeUsuario(user)
        } else {
            pedidosViewModel.limpiarPedidos()
        }
    }

    LaunchedEffect(todasLasPrendas) {
        if (todasLasPrendas.isNotEmpty()) {
            carritoViewModel.sincronizarStock(todasLasPrendas)
        }
    }

    LaunchedEffect(authEstado.usuarioAutenticado, rutaActual) {
        val user = authEstado.usuarioAutenticado
        if (rutaActual == Rutas.Login.ruta && user != null && user.id.isNotBlank()) {
            if (user.obtenerRolEnum() == RolUsuario.ADMIN) {
                controladorNavegacion.navigate(Rutas.AdminPedidos.ruta) {
                    popUpTo(Rutas.Login.ruta) { inclusive = true }
                }
            } else {
                pedidosViewModel.cargarPedidosDeUsuario(user)
                controladorNavegacion.navigate(Rutas.Inicio.ruta) {
                    popUpTo(Rutas.Login.ruta) { inclusive = true }
                }
            }
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val mensajeAgregado = stringResource(id = R.string.snackbar_agregado)
    val mensajeEstadoActualizado = stringResource(id = R.string.snackbar_estado_actualizado)
    val mensajeReporteExito = stringResource(id = R.string.reporte_descargado_descargas)
    val mensajePerfilActualizado = stringResource(id = R.string.snackbar_perfil_actualizado)
    val mensajePrendaGuardada = stringResource(id = R.string.admin_prenda_guardada)
    val mensajePrendaEliminada = stringResource(id = R.string.admin_prenda_eliminada)
    val mensajeSinStock = stringResource(id = R.string.snackbar_sin_stock)

    val rutasPrincipalesCliente = listOf(
        Rutas.Inicio.ruta,
        Rutas.Catalogo.ruta,
        Rutas.Carrito.ruta,
        Rutas.HistorialPedidos.ruta,
        Rutas.Perfil.ruta
    )

    val rutasPrincipalesAdmin = listOf(
        Rutas.AdminPedidos.ruta,
        Rutas.AdminCatalogo.ruta,
        Rutas.PerfilAdmin.ruta
    )

    val mostrarBarraCliente = rutaActual in rutasPrincipalesCliente
    val mostrarBarraAdmin = rutaActual in rutasPrincipalesAdmin

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = FondoOscuro,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                if (mostrarBarraCliente) {
                    val titulo = when (rutaActual) {
                        Rutas.Inicio.ruta -> stringResource(id = R.string.app_name)
                        Rutas.Catalogo.ruta -> stringResource(id = R.string.catalogo_titulo)
                        Rutas.Carrito.ruta -> stringResource(id = R.string.carrito_titulo)
                        Rutas.HistorialPedidos.ruta -> stringResource(id = R.string.historial_titulo)
                        Rutas.Perfil.ruta -> stringResource(id = R.string.perfil_titulo)
                        else -> stringResource(id = R.string.app_name)
                    }

                    BarraSuperior(
                        titulo = titulo,
                        puedeRegresar = false,
                        mostrarCarrito = rutaActual != Rutas.Carrito.ruta,
                        cantidadCarrito = carritoItems.sumOf { it.cantidad },
                        alClicCarrito = {
                            controladorNavegacion.navigate(Rutas.Carrito.ruta)
                        }
                    )
                } else if (mostrarBarraAdmin) {
                    val titulo = when (rutaActual) {
                        Rutas.AdminPedidos.ruta -> stringResource(id = R.string.admin_pedidos_titulo)
                        Rutas.AdminCatalogo.ruta -> stringResource(id = R.string.admin_catalogo_titulo)
                        Rutas.PerfilAdmin.ruta -> stringResource(id = R.string.admin_perfil)
                        else -> stringResource(id = R.string.app_name)
                    }

                    BarraSuperior(
                        titulo = titulo,
                        puedeRegresar = false
                    )
                }
            },
            bottomBar = {
                if (mostrarBarraCliente) {
                    BarraNavegacionCliente(
                        rutaActual = rutaActual,
                        cantidadCarrito = carritoItems.sumOf { it.cantidad },
                        alNavegar = { destino ->
                            if (destino != rutaActual) {
                                controladorNavegacion.navigate(destino) {
                                    if (destino == Rutas.Inicio.ruta) {
                                        popUpTo(Rutas.Inicio.ruta) { inclusive = false }
                                    }
                                    launchSingleTop = true
                                }
                            }
                        }
                    )
                } else if (mostrarBarraAdmin) {
                    BarraNavegacionAdmin(
                        rutaActual = rutaActual,
                        alNavegar = { destino ->
                            if (destino != rutaActual) {
                                controladorNavegacion.navigate(destino) {
                                    if (destino == Rutas.AdminPedidos.ruta) {
                                        popUpTo(Rutas.AdminPedidos.ruta) { inclusive = false }
                                    }
                                    launchSingleTop = true
                                }
                            }
                        }
                    )
                }
            }
        ) { paddingValores ->
            NavHost(
                navController = controladorNavegacion,
                startDestination = Rutas.Splash.ruta,
                modifier = Modifier.padding(paddingValores)
            ) {
                composable(Rutas.Splash.ruta) {
                    PantallaSplash(
                        alTerminar = {
                            val usuario = authEstado.usuarioAutenticado
                            if (usuario != null && usuario.id.isNotBlank()) {
                                if (usuario.obtenerRolEnum() == RolUsuario.ADMIN) {
                                    controladorNavegacion.navigate(Rutas.AdminPedidos.ruta) {
                                        popUpTo(Rutas.Splash.ruta) { inclusive = true }
                                    }
                                } else {
                                    pedidosViewModel.cargarPedidosDeUsuario(usuario)
                                    controladorNavegacion.navigate(Rutas.Inicio.ruta) {
                                        popUpTo(Rutas.Splash.ruta) { inclusive = true }
                                    }
                                }
                            } else {
                                authViewModel.verificarSesionExistente { usuarioSesion ->
                                    if (usuarioSesion != null && usuarioSesion.id.isNotBlank()) {
                                        if (usuarioSesion.obtenerRolEnum() == RolUsuario.ADMIN) {
                                            controladorNavegacion.navigate(Rutas.AdminPedidos.ruta) {
                                                popUpTo(Rutas.Splash.ruta) { inclusive = true }
                                            }
                                        } else {
                                            pedidosViewModel.cargarPedidosDeUsuario(usuarioSesion)
                                            controladorNavegacion.navigate(Rutas.Inicio.ruta) {
                                                popUpTo(Rutas.Splash.ruta) { inclusive = true }
                                            }
                                        }
                                    } else {
                                        controladorNavegacion.navigate(Rutas.Login.ruta) {
                                            popUpTo(Rutas.Splash.ruta) { inclusive = true }
                                        }
                                    }
                                }
                            }
                        }
                    )
                }

                composable(Rutas.Login.ruta) {
                    PantallaLogin(
                        estaCargando = authEstado.estaCargando,
                        errorServidor = authEstado.error,
                        alIniciarSesion = { correo, pwd ->
                            authViewModel.iniciarSesion(correo, pwd) { rol ->
                                if (rol == RolUsuario.ADMIN) {
                                    controladorNavegacion.navigate(Rutas.AdminPedidos.ruta) {
                                        popUpTo(Rutas.Login.ruta) { inclusive = true }
                                    }
                                } else {
                                    pedidosViewModel.cargarPedidosDeUsuario(correo)
                                    controladorNavegacion.navigate(Rutas.Inicio.ruta) {
                                        popUpTo(Rutas.Login.ruta) { inclusive = true }
                                    }
                                }
                            }
                        },
                        alIniciarFacebook = onFacebookLogin,
                        alIrARegistro = {
                            authViewModel.limpiarErrores()
                            controladorNavegacion.navigate(Rutas.Registro.ruta)
                        },
                        alIrARecuperarClave = {
                            authViewModel.limpiarErrores()
                            controladorNavegacion.navigate(Rutas.RecuperarClave.ruta)
                        }
                    )
                }

                composable(Rutas.Registro.ruta) {
                    PantallaRegistro(
                        estaCargando = authEstado.estaCargando,
                        errorServidor = authEstado.error,
                        alRegistrar = { nombre, correo, pwd ->
                            authViewModel.registrar(nombre, correo, pwd, pwd) {
                                controladorNavegacion.navigate(Rutas.Login.ruta) {
                                    popUpTo(Rutas.Registro.ruta) { inclusive = true }
                                }
                            }
                        },
                        alIrALogin = {
                            controladorNavegacion.popBackStack()
                        }
                    )
                }

                composable(Rutas.RecuperarClave.ruta) {
                    PantallaRecuperarClave(
                        estaCargando = authEstado.estaCargando,
                        errorServidor = authEstado.error,
                        errorResId = authEstado.errorResId,
                        recuperacionExitosa = authEstado.recuperacionExitosa,
                        alRecuperar = { correo ->
                            authViewModel.recuperarContrasena(correo)
                        },
                        alReiniciarEstado = {
                            authViewModel.reiniciarEstadoRecuperacion()
                        },
                        alVolver = {
                            authViewModel.reiniciarEstadoRecuperacion()
                            controladorNavegacion.popBackStack()
                        }
                    )
                }

                composable(Rutas.Inicio.ruta) {
                    PantallaInicio(
                        nombreUsuario = usuarioActual.nombreCompleto.split(" ").firstOrNull() ?: "Usuario",
                        prendasDestacadas = prendasDestacadas,
                        prendasNovedades = prendasNovedades,
                        alIrACatalogo = {
                            catalogoViewModel.seleccionarCategoria(Categoria.TODOS)
                            catalogoViewModel.seleccionarColeccion(FiltroColeccion.TODOS)
                            controladorNavegacion.navigate(Rutas.Catalogo.ruta)
                        },
                        alIrATemporada = {
                            catalogoViewModel.seleccionarCategoria(Categoria.TODOS)
                            catalogoViewModel.seleccionarColeccion(FiltroColeccion.NOVEDADES)
                            controladorNavegacion.navigate(Rutas.Catalogo.ruta)
                        },
                        alIrADestacados = {
                            catalogoViewModel.seleccionarCategoria(Categoria.TODOS)
                            catalogoViewModel.seleccionarColeccion(FiltroColeccion.DESTACADOS)
                            controladorNavegacion.navigate(Rutas.Catalogo.ruta)
                        },
                        alIrANovedades = {
                            catalogoViewModel.seleccionarCategoria(Categoria.TODOS)
                            catalogoViewModel.seleccionarColeccion(FiltroColeccion.NOVEDADES)
                            controladorNavegacion.navigate(Rutas.Catalogo.ruta)
                        },
                        alSeleccionarCategoria = { cat ->
                            catalogoViewModel.seleccionarCategoria(cat)
                            catalogoViewModel.seleccionarColeccion(FiltroColeccion.TODOS)
                            controladorNavegacion.navigate(Rutas.Catalogo.ruta)
                        },
                        alSeleccionarPrenda = { prendaId ->
                            controladorNavegacion.navigate(Rutas.DetallePrenda.crearRuta(prendaId))
                        },
                        alAgregarAlCarrito = { prenda ->
                            if (prenda.stock > 0) {
                                val agregado = carritoViewModel.agregarPrenda(
                                    prenda = prenda,
                                    talla = prenda.tallasDisponibles.firstOrNull() ?: "M",
                                    color = prenda.coloresDisponibles.firstOrNull() ?: "Negro",
                                    cantidad = 1
                                )
                                coroutineScope.launch {
                                    if (agregado) {
                                        snackbarHostState.showSnackbar(mensajeAgregado)
                                    } else {
                                        snackbarHostState.showSnackbar(mensajeSinStock)
                                    }
                                }
                            } else {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(mensajeSinStock)
                                }
                            }
                        }
                    )
                }

                composable(Rutas.Catalogo.ruta) {
                    PantallaCatalogo(
                        prendas = prendasFiltradas,
                        textoBusqueda = textoBusqueda,
                        alCambiarBusqueda = { catalogoViewModel.actualizarBusqueda(it) },
                        categoriaSeleccionada = categoriaSeleccionada,
                        alSeleccionarCategoria = { catalogoViewModel.seleccionarCategoria(it) },
                        coleccionSeleccionada = coleccionSeleccionada,
                        alSeleccionarColeccion = { catalogoViewModel.seleccionarColeccion(it) },
                        alSeleccionarPrenda = { prendaId ->
                            controladorNavegacion.navigate(Rutas.DetallePrenda.crearRuta(prendaId))
                        },
                        alAgregarAlCarrito = { prenda ->
                            if (prenda.stock > 0) {
                                val agregado = carritoViewModel.agregarPrenda(
                                    prenda = prenda,
                                    talla = prenda.tallasDisponibles.firstOrNull() ?: "M",
                                    color = prenda.coloresDisponibles.firstOrNull() ?: "Negro",
                                    cantidad = 1
                                )
                                coroutineScope.launch {
                                    if (agregado) {
                                        snackbarHostState.showSnackbar(mensajeAgregado)
                                    } else {
                                        snackbarHostState.showSnackbar(mensajeSinStock)
                                    }
                                }
                            } else {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(mensajeSinStock)
                                }
                            }
                        }
                    )
                }

                composable(
                    route = Rutas.DetallePrenda.ruta,
                    arguments = listOf(navArgument("prendaId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val prendaId = backStackEntry.arguments?.getString("prendaId") ?: ""
                    val prenda = catalogoViewModel.obtenerPrendaPorId(prendaId)
                    PantallaDetallePrenda(
                        prenda = prenda,
                        alRegresar = {
                            controladorNavegacion.popBackStack()
                        },
                        alAgregarAlCarrito = { prendaSel, talla, color, cantidad ->
                            if (prendaSel.stock > 0) {
                                val agregado = carritoViewModel.agregarPrenda(prendaSel, talla, color, cantidad)
                                coroutineScope.launch {
                                    if (agregado) {
                                        snackbarHostState.showSnackbar(mensajeAgregado)
                                    } else {
                                        snackbarHostState.showSnackbar(mensajeSinStock)
                                    }
                                }
                                if (agregado) {
                                    controladorNavegacion.popBackStack()
                                }
                            } else {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(mensajeSinStock)
                                }
                            }
                        }
                    )
                }

                composable(Rutas.Carrito.ruta) {
                    PantallaCarrito(
                        items = carritoItems,
                        alModificarCantidad = { item, nuevaCantidad ->
                            carritoViewModel.modificarCantidad(item, nuevaCantidad)
                        },
                        alEliminarItem = { item ->
                            carritoViewModel.eliminarItem(item)
                        },
                        alProcederPago = {
                            controladorNavegacion.navigate(Rutas.RealizarPedido.ruta)
                        },
                        alSeguirComprando = {
                            controladorNavegacion.navigate(Rutas.Catalogo.ruta)
                        }
                    )
                }

                composable(Rutas.RealizarPedido.ruta) {
                    val pedidosEstaCargando by pedidosViewModel.estaCargando.collectAsState()
                    var errorPedido by remember { mutableStateOf<String?>(null) }

                    PantallaRealizarPedido(
                        items = carritoItems,
                        direccionInicial = usuarioActual.direccion,
                        estaCargando = pedidosEstaCargando,
                        errorServidor = errorPedido,
                        alRegresar = {
                            errorPedido = null
                            controladorNavegacion.popBackStack()
                        },
                        alConfirmarPedido = { direccion, metodoPago ->
                            errorPedido = null
                            pedidosViewModel.realizarPedido(
                                usuario = usuarioActual,
                                direccion = direccion,
                                metodoPago = metodoPago,
                                items = carritoItems,
                                alExito = {
                                    carritoViewModel.vaciarCarrito()
                                    controladorNavegacion.navigate(Rutas.HistorialPedidos.ruta) {
                                        popUpTo(Rutas.Inicio.ruta)
                                    }
                                },
                                alError = { err ->
                                    errorPedido = err
                                }
                            )
                        }
                    )
                }

                composable(Rutas.HistorialPedidos.ruta) {
                    PantallaHistorialPedidos(
                        pedidos = pedidosCliente,
                        alDescargarReporteGeneral = { pedidosFiltrados, tituloReporte ->
                            pedidosViewModel.descargarReporte(
                                context = context,
                                usuario = usuarioActual,
                                pedidos = pedidosFiltrados,
                                titulo = tituloReporte
                            ) { exito ->
                                if (exito) {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(mensajeReporteExito)
                                    }
                                }
                            }
                        },
                        alDescargarReporteIndividual = { pedido ->
                            pedidosViewModel.descargarReportePedidoIndividual(context, usuarioActual, pedido) {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(mensajeReporteExito)
                                }
                            }
                        }
                    )
                }

                composable(Rutas.Perfil.ruta) {
                    PantallaPerfil(
                        usuario = usuarioActual,
                        alActualizarTelefono = { nuevoTel ->
                            authViewModel.actualizarTelefono(nuevoTel) {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(mensajePerfilActualizado)
                                }
                            }
                        },
                        alActualizarDireccion = { nuevaDir ->
                            authViewModel.actualizarDireccion(nuevaDir) {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(mensajePerfilActualizado)
                                }
                            }
                        },
                        alCerrarSesion = {
                            pedidosViewModel.limpiarPedidos()
                            carritoViewModel.vaciarCarrito()
                            authViewModel.cerrarSesion {
                                controladorNavegacion.navigate(Rutas.Login.ruta) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                    )
                }

                composable(Rutas.AdminPedidos.ruta) {
                    PantallaAdminPedidos(
                        pedidos = todosLosPedidos,
                        alActualizarEstadoPedido = { pedidoId, nuevoEstado ->
                            adminViewModel.actualizarEstadoPedido(pedidoId, nuevoEstado) {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(mensajeEstadoActualizado)
                                }
                            }
                        },
                        alDescargarReporte = {
                            adminViewModel.descargarReporteGeneral(context, usuarioActual) {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(mensajeReporteExito)
                                }
                            }
                        }
                    )
                }

                composable(Rutas.AdminCatalogo.ruta) {
                    PantallaAdminCatalogo(
                        prendas = todasLasPrendas,
                        estaCargando = catalogoEstaCargando,
                        textoBusqueda = textoBusqueda,
                        alCambiarBusqueda = { catalogoViewModel.actualizarBusqueda(it) },
                        alGuardarPrenda = { prenda ->
                            catalogoViewModel.guardarPrenda(prenda) { exito ->
                                if (exito) {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(mensajePrendaGuardada)
                                    }
                                }
                            }
                        },
                        alEliminarPrenda = { prendaId ->
                            catalogoViewModel.eliminarPrenda(prendaId) { exito ->
                                if (exito) {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(mensajePrendaEliminada)
                                    }
                                }
                            }
                        }
                    )
                }

                composable(Rutas.PerfilAdmin.ruta) {
                    PantallaPerfilAdmin(
                        admin = usuarioActual,
                        pedidos = todosLosPedidos,
                        alActualizarTelefono = { nuevoTel ->
                            authViewModel.actualizarTelefono(nuevoTel) {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(mensajePerfilActualizado)
                                }
                            }
                        },
                        alActualizarDireccion = { nuevaDir ->
                            authViewModel.actualizarDireccion(nuevaDir) {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(mensajePerfilActualizado)
                                }
                            }
                        },
                        alCerrarSesion = {
                            pedidosViewModel.limpiarPedidos()
                            carritoViewModel.vaciarCarrito()
                            authViewModel.cerrarSesion {
                                controladorNavegacion.navigate(Rutas.Login.ruta) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                    )
                }
            }
        }

        if (!hayInternet) {
            DialogoSinConexion(
                alReintentar = {
                    conexionViewModel.reintentarConexion()
                }
            )
        }
    }
}
