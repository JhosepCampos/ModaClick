package com.cibertec.inventario

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.cibertec.inventario.navegacion.NavegacionApp
import com.cibertec.inventario.ui.theme.FondoOscuro
import com.cibertec.inventario.ui.theme.VentaRopaTheme
import com.cibertec.inventario.viewmodel.AuthViewModel
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult

class MainActivity : ComponentActivity() {

    private lateinit var callbackManager: CallbackManager
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        callbackManager = CallbackManager.Factory.create()
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        LoginManager.getInstance().registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    authViewModel.iniciarSesionConFacebook(result.accessToken)
                }

                override fun onCancel() {
                    authViewModel.establecerMensajeError(
                        getString(R.string.login_facebook_cancelado)
                    )
                }

                override fun onError(error: FacebookException) {
                    authViewModel.establecerMensajeError(
                        error.message ?: getString(R.string.login_facebook_error)
                    )
                }
            }
        )

        setContent {
            VentaRopaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = FondoOscuro
                ) {
                    NavegacionApp(
                        authViewModel = authViewModel,
                        onFacebookLogin = {
                            LoginManager.getInstance().logInWithReadPermissions(
                                this,
                                listOf("public_profile")
                            )
                        }
                    )
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
}