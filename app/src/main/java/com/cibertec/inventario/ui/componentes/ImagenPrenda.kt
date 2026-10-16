package com.cibertec.inventario.ui.componentes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cibertec.inventario.modelo.Categoria
import com.cibertec.inventario.ui.theme.DoradoPrimario
import com.cibertec.inventario.ui.theme.SuperficieCard
import com.cibertec.inventario.ui.theme.SuperficieElevada
import com.cibertec.inventario.util.ImageUtils

@Composable
fun ImagenPrenda(
    imagenUrl: String,
    categoria: Categoria = Categoria.TODOS,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    tamanoIconoFallback: Dp = 48.dp,
    descripcion: String? = null
) {
    val imageBitmap = remember(imagenUrl) {
        ImageUtils.base64AImageBitmap(imagenUrl)
    }

    if (imageBitmap != null) {
        Box(
            modifier = modifier.background(
                Brush.verticalGradient(
                    listOf(SuperficieElevada, SuperficieCard)
                )
            ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                bitmap = imageBitmap,
                contentDescription = descripcion,
                contentScale = contentScale,
                modifier = Modifier.fillMaxSize()
            )
        }
    } else {
        Box(
            modifier = modifier.background(
                Brush.verticalGradient(
                    listOf(SuperficieElevada, SuperficieCard)
                )
            ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = iconoPorCategoria(categoria),
                contentDescription = descripcion,
                tint = DoradoPrimario.copy(alpha = 0.85f),
                modifier = Modifier.size(tamanoIconoFallback)
            )
        }
    }
}
