package com.cibertec.inventario.ui.componentes

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cibertec.inventario.R
import com.cibertec.inventario.ui.theme.DoradoPrimario
import com.cibertec.inventario.ui.theme.FondoOscuro
import com.cibertec.inventario.ui.theme.SuperficieCard
import com.cibertec.inventario.ui.theme.TextoBlanco
import com.cibertec.inventario.ui.theme.TextoGrisClaro

@Composable
fun DialogoConfirmacion(
    titulo: String,
    descripcion: String,
    alConfirmar: () -> Unit,
    alDescartar: () -> Unit,
    textoConfirmar: String = stringResource(id = R.string.aceptar),
    textoCancelar: String = stringResource(id = R.string.cancelar),
    mostrarCancelar: Boolean = true
) {
    AlertDialog(
        onDismissRequest = alDescartar,
        shape = RoundedCornerShape(16.dp),
        containerColor = SuperficieCard,
        title = {
            Text(
                text = titulo,
                color = TextoBlanco,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Text(
                text = descripcion,
                color = TextoGrisClaro,
                fontSize = 14.sp
            )
        },
        confirmButton = {
            Button(
                onClick = alConfirmar,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DoradoPrimario,
                    contentColor = FondoOscuro
                )
            ) {
                Text(text = textoConfirmar, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = if (mostrarCancelar) {
            {
                OutlinedButton(
                    onClick = alDescartar,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextoGrisClaro)
                ) {
                    Text(text = textoCancelar)
                }
            }
        } else null
    )
}
