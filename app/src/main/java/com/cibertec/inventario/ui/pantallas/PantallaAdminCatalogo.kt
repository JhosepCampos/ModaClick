package com.cibertec.inventario.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Surface
import com.cibertec.inventario.R
import com.cibertec.inventario.modelo.Categoria
import com.cibertec.inventario.modelo.FiltroColeccion
import com.cibertec.inventario.modelo.Prenda
import com.cibertec.inventario.ui.componentes.BarraSuperior
import com.cibertec.inventario.ui.componentes.BotonPrincipal
import com.cibertec.inventario.ui.componentes.BotonSecundario
import com.cibertec.inventario.ui.componentes.CampoTexto
import com.cibertec.inventario.ui.componentes.DialogoConfirmacion
import com.cibertec.inventario.ui.componentes.ImagenPrenda
import com.cibertec.inventario.util.ImageUtils
import com.cibertec.inventario.ui.theme.AlertaNaranja
import com.cibertec.inventario.ui.theme.BordeSutil
import com.cibertec.inventario.ui.theme.DoradoClaro
import com.cibertec.inventario.ui.theme.DoradoPrimario
import com.cibertec.inventario.ui.theme.ErrorRojo
import com.cibertec.inventario.ui.theme.ExitoVerde
import com.cibertec.inventario.ui.theme.FondoOscuro
import com.cibertec.inventario.ui.theme.SuperficieCard
import com.cibertec.inventario.ui.theme.SuperficieElevada
import com.cibertec.inventario.ui.theme.TextoBlanco
import com.cibertec.inventario.ui.theme.TextoGrisClaro
import com.cibertec.inventario.ui.theme.TextoMuted

@Composable
fun PantallaAdminCatalogo(
    prendas: List<Prenda>,
    estaCargando: Boolean = false,
    textoBusqueda: String = "",
    alCambiarBusqueda: (String) -> Unit,
    alGuardarPrenda: (Prenda) -> Unit,
    alEliminarPrenda: (String) -> Unit
) {
    var prendaParaEditar by remember { mutableStateOf<Prenda?>(null) }
    var mostrarDialogoFormulario by remember { mutableStateOf(false) }
    var prendaParaEliminar by remember { mutableStateOf<Prenda?>(null) }
    var coleccionFiltro by remember { mutableStateOf(FiltroColeccion.TODOS) }
    var categoriaFiltro by remember { mutableStateOf(Categoria.TODOS) }

    val prendasFiltradas = prendas.filter { prenda ->
        val coincideCat = if (categoriaFiltro == Categoria.TODOS) true else prenda.categoria.equals(categoriaFiltro.name, ignoreCase = true)
        val coincideColeccion = when (coleccionFiltro) {
            FiltroColeccion.TODOS -> true
            FiltroColeccion.DESTACADOS -> prenda.esDestacado
            FiltroColeccion.NOVEDADES -> prenda.esNovedad
            FiltroColeccion.BASICOS -> !prenda.esDestacado && !prenda.esNovedad
        }
        val coincideBusq = if (textoBusqueda.isBlank()) true else prenda.nombre.contains(textoBusqueda, ignoreCase = true) || prenda.descripcion.contains(textoBusqueda, ignoreCase = true)
        coincideCat && coincideColeccion && coincideBusq
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    prendaParaEditar = null
                    mostrarDialogoFormulario = true
                },
                containerColor = DoradoPrimario,
                contentColor = FondoOscuro,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.admin_agregar_prenda),
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        containerColor = FondoOscuro
    ) { paddingValores ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValores)
        ) {
            OutlinedTextField(
                value = textoBusqueda,
                onValueChange = alCambiarBusqueda,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                placeholder = {
                    Text(text = stringResource(id = R.string.catalogo_buscar), color = TextoMuted)
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = DoradoPrimario)
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DoradoPrimario,
                    unfocusedBorderColor = BordeSutil,
                    focusedContainerColor = SuperficieCard,
                    unfocusedContainerColor = SuperficieCard,
                    focusedTextColor = TextoBlanco,
                    unfocusedTextColor = TextoBlanco
                ),
                singleLine = true
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 6.dp)
            ) {
                items(FiltroColeccion.values()) { col ->
                    val seleccionado = coleccionFiltro == col
                    FilterChip(
                        selected = seleccionado,
                        onClick = { coleccionFiltro = col },
                        label = {
                            Text(
                                text = stringResource(id = col.etiquetaRes),
                                fontSize = 12.sp,
                                fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Medium
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
                            selected = seleccionado
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 10.dp)
            ) {
                items(Categoria.values()) { cat ->
                    val seleccionado = categoriaFiltro == cat
                    FilterChip(
                        selected = seleccionado,
                        onClick = { categoriaFiltro = cat },
                        label = {
                            Text(
                                text = when (cat) {
                                    Categoria.TODOS -> stringResource(id = R.string.cat_todos)
                                    Categoria.CAMISAS -> stringResource(id = R.string.cat_camisas)
                                    Categoria.PANTALONES -> stringResource(id = R.string.cat_pantalones)
                                    Categoria.VESTIDOS -> stringResource(id = R.string.cat_vestidos)
                                    Categoria.ZAPATOS -> stringResource(id = R.string.cat_zapatos)
                                    Categoria.ACCESORIOS -> stringResource(id = R.string.cat_accesorios)
                                    Categoria.CHAQUETAS -> stringResource(id = R.string.cat_chaquetas)
                                },
                                fontSize = 12.sp
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
                            selected = seleccionado
                        )
                    )
                }
            }

            if (estaCargando) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = DoradoPrimario)
                }
            } else if (prendasFiltradas.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Checkroom,
                        contentDescription = null,
                        tint = TextoMuted,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = stringResource(id = R.string.admin_sin_prendas),
                        color = TextoBlanco,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = stringResource(id = R.string.admin_sin_prendas_desc),
                        color = TextoMuted,
                        fontSize = 13.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    BotonPrincipal(
                        texto = stringResource(id = R.string.admin_agregar_prenda),
                        onClick = {
                            prendaParaEditar = null
                            mostrarDialogoFormulario = true
                        }
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(prendasFiltradas, key = { it.id }) { prenda ->
                        TarjetaPrendaAdmin(
                            prenda = prenda,
                            alEditar = {
                                prendaParaEditar = prenda
                                mostrarDialogoFormulario = true
                            },
                            alEliminar = {
                                prendaParaEliminar = prenda
                            }
                        )
                    }
                }
            }
        }
    }

    if (mostrarDialogoFormulario) {
        DialogoFormularioPrenda(
            prendaExistente = prendaParaEditar,
            alGuardar = { prendaNueva ->
                alGuardarPrenda(prendaNueva)
                mostrarDialogoFormulario = false
                prendaParaEditar = null
            },
            alDescartar = {
                mostrarDialogoFormulario = false
                prendaParaEditar = null
            }
        )
    }

    prendaParaEliminar?.let { prenda ->
        DialogoConfirmacion(
            titulo = stringResource(id = R.string.admin_eliminar_prenda),
            descripcion = stringResource(id = R.string.admin_confirmar_eliminar) + " (${prenda.nombre})",
            alConfirmar = {
                alEliminarPrenda(prenda.id)
                prendaParaEliminar = null
            },
            alDescartar = {
                prendaParaEliminar = null
            }
        )
    }
}

@Composable
fun TarjetaPrendaAdmin(
    prenda: Prenda,
    alEditar: () -> Unit,
    alEliminar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SuperficieCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BordeSutil))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                ImagenPrenda(
                    imagenUrl = prenda.imagenUrl,
                    categoria = prenda.obtenerCategoriaEnum(),
                    modifier = Modifier.fillMaxSize(),
                    tamanoIconoFallback = 32.dp,
                    descripcion = prenda.nombre
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = prenda.nombre,
                        color = TextoBlanco,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (prenda.esDestacado) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = DoradoPrimario
                        ) {
                            Text(
                                text = "TOP",
                                color = FondoOscuro,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                    if (prenda.esNovedad) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = DoradoPrimario.copy(alpha = 0.25f)
                        ) {
                            Text(
                                text = "NUEVO",
                                color = DoradoPrimario,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = prenda.categoria,
                    color = DoradoClaro,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.precio_formato, prenda.precio),
                        color = DoradoPrimario,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = if (prenda.stock > 0) stringResource(id = R.string.admin_stock_formato, prenda.stock) else stringResource(id = R.string.prenda_sin_stock),
                        color = if (prenda.stock > 0) ExitoVerde else ErrorRojo,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = alEditar) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(id = R.string.admin_editar_prenda),
                        tint = DoradoPrimario,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(onClick = alEliminar) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(id = R.string.admin_eliminar_prenda),
                        tint = ErrorRojo,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DialogoFormularioPrenda(
    prendaExistente: Prenda?,
    alGuardar: (Prenda) -> Unit,
    alDescartar: () -> Unit
) {
    var nombre by remember { mutableStateOf(prendaExistente?.nombre ?: "") }
    var descripcion by remember { mutableStateOf(prendaExistente?.descripcion ?: "") }
    var precioTexto by remember { mutableStateOf(prendaExistente?.precio?.toString() ?: "") }
    var stockTexto by remember { mutableStateOf(prendaExistente?.stock?.toString() ?: "10") }
    var categoriaSeleccionada by remember {
        mutableStateOf(
            prendaExistente?.categoria ?: Categoria.CAMISAS.name
        )
    }
    var tallasTexto by remember {
        mutableStateOf(prendaExistente?.tallasDisponibles?.joinToString(", ") ?: "S, M, L, XL")
    }
    var coloresTexto by remember {
        mutableStateOf(prendaExistente?.coloresDisponibles?.joinToString(", ") ?: "Negro, Blanco")
    }
    var esDestacado by remember { mutableStateOf(prendaExistente?.esDestacado ?: false) }
    var esNovedad by remember { mutableStateOf(prendaExistente?.esNovedad ?: false) }

    var imagenUrl by remember { mutableStateOf(prendaExistente?.imagenUrl ?: "") }
    val context = LocalContext.current

    val launcherCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            imagenUrl = ImageUtils.bitmapABase64(it)
        }
    }

    val launcherGaleria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val bitmap = ImageUtils.uriABitmap(context, it)
            bitmap?.let { b ->
                imagenUrl = ImageUtils.bitmapABase64(b)
            }
        }
    }

    var errorNombre by remember { mutableStateOf(false) }
    var errorPrecio by remember { mutableStateOf(false) }
    var errorStock by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = alDescartar,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 24.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SuperficieCard),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BordeSutil))
        ) {
            Column(
                modifier = Modifier
                    .padding(22.dp)
                    .verticalScroll(scrollState)
            ) {
                Text(
                    text = if (prendaExistente == null) stringResource(id = R.string.admin_agregar_prenda) else stringResource(id = R.string.admin_editar_prenda),
                    color = TextoBlanco,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = stringResource(id = R.string.foto_titulo),
                    color = TextoMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(SuperficieElevada)
                        .border(1.dp, BordeSutil, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (imagenUrl.isNotBlank()) {
                        ImagenPrenda(
                            imagenUrl = imagenUrl,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(6.dp),
                            contentScale = ContentScale.Fit,
                            descripcion = "Foto de la prenda"
                        )
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(FondoOscuro.copy(alpha = 0.85f))
                                    .clickable { launcherCamara.launch(null) }
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CameraAlt,
                                        contentDescription = null,
                                        tint = DoradoPrimario,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = stringResource(id = R.string.foto_tomar),
                                        color = TextoBlanco,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(FondoOscuro.copy(alpha = 0.85f))
                                    .clickable { launcherGaleria.launch("image/*") }
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PhotoLibrary,
                                        contentDescription = null,
                                        tint = DoradoPrimario,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = stringResource(id = R.string.foto_galeria),
                                        color = TextoBlanco,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ErrorRojo.copy(alpha = 0.9f))
                                    .clickable { imagenUrl = "" }
                                    .padding(6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = stringResource(id = R.string.foto_quitar),
                                    tint = TextoBlanco,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = DoradoPrimario,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = stringResource(id = R.string.foto_toca_para_agregar),
                                color = TextoMuted,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(SuperficieCard)
                                        .border(1.dp, DoradoPrimario.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                        .clickable { launcherCamara.launch(null) }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CameraAlt,
                                            contentDescription = null,
                                            tint = DoradoPrimario,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = stringResource(id = R.string.foto_tomar),
                                            color = DoradoPrimario,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(SuperficieCard)
                                        .border(1.dp, BordeSutil, RoundedCornerShape(8.dp))
                                        .clickable { launcherGaleria.launch("image/*") }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PhotoLibrary,
                                            contentDescription = null,
                                            tint = TextoBlanco,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = stringResource(id = R.string.foto_galeria),
                                            color = TextoBlanco,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                CampoTexto(
                    valor = nombre,
                    alCambiar = {
                        nombre = it
                        errorNombre = false
                    },
                    etiqueta = stringResource(id = R.string.admin_prenda_nombre),
                    iconoInicio = Icons.Default.Checkroom,
                    esError = errorNombre,
                    mensajeError = if (errorNombre) stringResource(id = R.string.error_campo_requerido) else null
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(text = stringResource(id = R.string.admin_prenda_categoria), color = TextoMuted, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    val categoriasDisponibles = Categoria.values().filter { it != Categoria.TODOS }
                    items(categoriasDisponibles) { cat ->
                        val sel = categoriaSeleccionada.equals(cat.name, ignoreCase = true)
                        FilterChip(
                            selected = sel,
                            onClick = { categoriaSeleccionada = cat.name },
                            label = { Text(text = cat.name, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = SuperficieElevada,
                                labelColor = TextoGrisClaro,
                                selectedContainerColor = DoradoPrimario,
                                selectedLabelColor = FondoOscuro
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                borderColor = BordeSutil,
                                selectedBorderColor = DoradoPrimario,
                                enabled = true,
                                selected = sel
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        CampoTexto(
                            valor = precioTexto,
                            alCambiar = {
                                precioTexto = it
                                errorPrecio = false
                            },
                            etiqueta = stringResource(id = R.string.admin_prenda_precio),
                            iconoInicio = Icons.Default.MonetizationOn,
                            esError = errorPrecio,
                            mensajeError = if (errorPrecio) stringResource(id = R.string.error_campo_requerido) else null
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        CampoTexto(
                            valor = stockTexto,
                            alCambiar = {
                                stockTexto = it
                                errorStock = false
                            },
                            etiqueta = stringResource(id = R.string.admin_prenda_stock),
                            iconoInicio = Icons.Default.Inventory,
                            esError = errorStock,
                            mensajeError = if (errorStock) stringResource(id = R.string.error_campo_requerido) else null
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                CampoTexto(
                    valor = descripcion,
                    alCambiar = { descripcion = it },
                    etiqueta = stringResource(id = R.string.admin_prenda_descripcion),
                    iconoInicio = Icons.Default.Description
                )

                Spacer(modifier = Modifier.height(12.dp))

                CampoTexto(
                    valor = tallasTexto,
                    alCambiar = { tallasTexto = it },
                    etiqueta = stringResource(id = R.string.admin_prenda_tallas),
                    iconoInicio = Icons.Default.Checkroom
                )

                Spacer(modifier = Modifier.height(12.dp))

                CampoTexto(
                    valor = coloresTexto,
                    alCambiar = { coloresTexto = it },
                    etiqueta = stringResource(id = R.string.admin_prenda_colores),
                    iconoInicio = Icons.Default.Star
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(id = R.string.admin_prenda_destacado), color = TextoBlanco, fontSize = 13.sp)
                    Switch(
                        checked = esDestacado,
                        onCheckedChange = { esDestacado = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = FondoOscuro, checkedTrackColor = DoradoPrimario)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(id = R.string.admin_prenda_novedad), color = TextoBlanco, fontSize = 13.sp)
                    Switch(
                        checked = esNovedad,
                        onCheckedChange = { esNovedad = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = FondoOscuro, checkedTrackColor = DoradoPrimario)
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                BotonPrincipal(
                    texto = stringResource(id = R.string.guardar),
                    onClick = {
                        val precioNum = precioTexto.toDoubleOrNull()
                        val stockNum = stockTexto.toIntOrNull()

                        if (nombre.isBlank()) errorNombre = true
                        if (precioNum == null || precioNum <= 0) errorPrecio = true
                        if (stockNum == null || stockNum < 0) errorStock = true

                        if (nombre.isNotBlank() && precioNum != null && precioNum > 0 && stockNum != null && stockNum >= 0) {
                            val tallas = tallasTexto.split(",").map { it.trim() }.filter { it.isNotBlank() }
                            val colores = coloresTexto.split(",").map { it.trim() }.filter { it.isNotBlank() }

                            val prendaFinal = Prenda(
                                id = prendaExistente?.id ?: "prenda_${System.currentTimeMillis()}",
                                nombre = nombre.trim(),
                                descripcion = if (descripcion.isBlank()) "Prenda exclusiva de alta calidad" else descripcion.trim(),
                                precio = precioNum,
                                categoria = categoriaSeleccionada,
                                tallasDisponibles = tallas.ifEmpty { listOf("M") },
                                coloresDisponibles = colores.ifEmpty { listOf("Negro") },
                                stock = stockNum,
                                esDestacado = esDestacado,
                                esNovedad = esNovedad,
                                calificacion = prendaExistente?.calificacion ?: 4.8,
                                imagenUrl = imagenUrl
                            )
                            alGuardar(prendaFinal)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                BotonSecundario(
                    texto = stringResource(id = R.string.cancelar),
                    onClick = alDescartar
                )
            }
        }
    }
}
