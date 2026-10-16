package com.cibertec.inventario.ui.componentes

import android.app.Activity
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.cibertec.inventario.R
import com.cibertec.inventario.ui.theme.DoradoPrimario
import com.cibertec.inventario.ui.theme.ErrorRojo
import com.cibertec.inventario.ui.theme.FondoOscuro
import com.cibertec.inventario.ui.theme.SuperficieCard
import com.cibertec.inventario.ui.theme.TextoBlanco
import com.cibertec.inventario.ui.theme.TextoGrisClaro

@Composable
fun DialogoSinConexion(
    alReintentar: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = { /* Modal bloqueante */ },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        ),
        shape = RoundedCornerShape(16.dp),
        containerColor = SuperficieCard,
        title = {
            Text(
                text = stringResource(id = R.string.conexion_sin_internet_titulo),
                color = TextoBlanco,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Text(
                text = stringResource(id = R.string.conexion_sin_internet_desc),
                color = TextoGrisClaro,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        },
        confirmButton = {
            Button(
                onClick = alReintentar,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DoradoPrimario,
                    contentColor = FondoOscuro
                )
            ) {
                Text(
                    text = stringResource(id = R.string.conexion_reintentar),
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = {
                    (context as? Activity)?.finishAffinity()
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = ErrorRojo
                )
            ) {
                Text(
                    text = stringResource(id = R.string.conexion_salir),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    )
}
