package com.cibertec.inventario.ui.componentes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cibertec.inventario.ui.theme.BordeSutil
import com.cibertec.inventario.ui.theme.DoradoPrimario
import com.cibertec.inventario.ui.theme.ErrorRojo
import com.cibertec.inventario.ui.theme.SuperficieCard
import com.cibertec.inventario.ui.theme.TextoBlanco
import com.cibertec.inventario.ui.theme.TextoGrisClaro
import com.cibertec.inventario.ui.theme.TextoMuted

@Composable
fun CampoTexto(
    valor: String,
    alCambiar: (String) -> Unit,
    etiqueta: String,
    modifier: Modifier = Modifier,
    iconoInicio: ImageVector? = null,
    esContrasena: Boolean = false,
    esError: Boolean = false,
    mensajeError: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    lineasMax: Int = 1
) {
    var contrasenaVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = valor,
            onValueChange = alCambiar,
            label = { Text(text = etiqueta, fontSize = 14.sp) },
            leadingIcon = iconoInicio?.let {
                {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = if (esError) ErrorRojo else DoradoPrimario
                    )
                }
            },
            trailingIcon = if (esContrasena) {
                {
                    val icono = if (contrasenaVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                    IconButton(onClick = { contrasenaVisible = !contrasenaVisible }) {
                        Icon(
                            imageVector = icono,
                            contentDescription = "Alternar contraseña",
                            tint = TextoMuted
                        )
                    }
                }
            } else null,
            visualTransformation = if (esContrasena && !contrasenaVisible) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            isError = esError,
            singleLine = lineasMax == 1,
            maxLines = lineasMax,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SuperficieCard,
                unfocusedContainerColor = SuperficieCard,
                errorContainerColor = SuperficieCard,
                focusedBorderColor = DoradoPrimario,
                unfocusedBorderColor = BordeSutil,
                errorBorderColor = ErrorRojo,
                focusedTextColor = TextoBlanco,
                unfocusedTextColor = TextoBlanco,
                focusedLabelColor = DoradoPrimario,
                unfocusedLabelColor = TextoGrisClaro,
                cursorColor = DoradoPrimario
            ),
            modifier = Modifier.fillMaxWidth()
        )

        if (esError && !mensajeError.isNullOrEmpty()) {
            Text(
                text = mensajeError,
                color = ErrorRojo,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }
    }
}
