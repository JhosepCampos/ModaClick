package com.cibertec.inventario.datos

import com.cibertec.inventario.modelo.RolUsuario
import com.cibertec.inventario.modelo.Usuario
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull

class CorreoNoRegistradoException(mensaje: String = "CORREO_NO_REGISTRADO") : Exception(mensaje)

class AuthRepository {

    private val auth by lazy {
        try {
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            null
        }
    }

    private val firestore by lazy {
        try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun obtenerUsuarioActual(): Usuario? {
        val fbAuth = auth ?: return null
        val firebaseUser = fbAuth.currentUser ?: return null
        val uid = firebaseUser.uid
        val db = firestore
        var usuarioFirestore: Usuario? = null
        if (db != null) {
            try {
                usuarioFirestore = withTimeoutOrNull(3500L) {
                    val doc = db.collection("usuarios").document(uid).get().await()
                    doc.toObject(Usuario::class.java)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return usuarioFirestore ?: Usuario(
            id = uid,
            nombreCompleto = firebaseUser.displayName ?: firebaseUser.email?.substringBefore("@") ?: "Usuario",
            correo = firebaseUser.email ?: "",
            usuario = firebaseUser.email?.substringBefore("@") ?: "",
            rol = if (firebaseUser.email?.contains("admin", ignoreCase = true) == true) RolUsuario.ADMIN.name else RolUsuario.CLIENTE.name
        )
    }

    suspend fun registrarUsuario(
        nombreCompleto: String,
        correo: String,
        contrasena: String
    ): Result<Usuario> {
        return try {
            val fbAuth = auth ?: return Result.failure(Exception("Servicio de autenticación no disponible"))
            val db = firestore

            val resultado = fbAuth.createUserWithEmailAndPassword(correo, contrasena).await()
            val uid = resultado.user?.uid ?: throw Exception("Error al obtener identificador de usuario")

            val nuevoUsuario = Usuario(
                id = uid,
                nombreCompleto = nombreCompleto,
                correo = correo,
                usuario = correo.substringBefore("@"),
                rol = if (correo.contains("admin", ignoreCase = true)) RolUsuario.ADMIN.name else RolUsuario.CLIENTE.name,
                telefono = "",
                direccion = "",
                creadoEn = System.currentTimeMillis()
            )

            if (db != null) {
                try {
                    withTimeoutOrNull(3500L) {
                        db.collection("usuarios").document(uid).set(nuevoUsuario).await()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            Result.success(nuevoUsuario)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun iniciarSesion(
        correo: String,
        contrasena: String
    ): Result<Usuario> {
        return try {
            val fbAuth = auth ?: return Result.failure(Exception("Servicio de autenticación no disponible"))
            val db = firestore

            val authResult = fbAuth.signInWithEmailAndPassword(correo, contrasena).await()
            val uid = authResult.user?.uid ?: throw Exception("Usuario no encontrado")

            var usuarioFirestore: Usuario? = null
            if (db != null) {
                try {
                    usuarioFirestore = withTimeoutOrNull(3500L) {
                        val docUsuario = db.collection("usuarios").document(uid).get().await()
                        docUsuario.toObject(Usuario::class.java)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            if (usuarioFirestore != null) {
                Result.success(usuarioFirestore)
            } else {
                val usuarioNuevo = Usuario(
                    id = uid,
                    nombreCompleto = authResult.user?.displayName ?: correo.substringBefore("@"),
                    correo = correo,
                    usuario = correo.substringBefore("@"),
                    rol = if (correo.contains("admin", ignoreCase = true)) RolUsuario.ADMIN.name else RolUsuario.CLIENTE.name,
                    creadoEn = System.currentTimeMillis()
                )
                if (db != null) {
                    try {
                        withTimeoutOrNull(2500L) {
                            db.collection("usuarios").document(uid).set(usuarioNuevo).await()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                Result.success(usuarioNuevo)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun recuperarContrasena(correo: String): Result<Unit> {
        return try {
            val correoLimpio = correo.trim()
            val db = firestore ?: return Result.failure(Exception("Base de datos no disponible"))
            val fbAuth = auth ?: return Result.failure(Exception("Servicio de autenticación no disponible"))

            val consulta = withTimeoutOrNull(5000L) {
                db.collection("usuarios")
                    .whereEqualTo("correo", correoLimpio)
                    .limit(1)
                    .get()
                    .await()
            } ?: return Result.failure(Exception("Error de conexión al consultar la base de datos"))

            val existe = if (!consulta.isEmpty) {
                true
            } else {
                val correoMinuscula = correoLimpio.lowercase()
                if (correoMinuscula != correoLimpio) {
                    val consultaMinuscula = withTimeoutOrNull(5000L) {
                        db.collection("usuarios")
                            .whereEqualTo("correo", correoMinuscula)
                            .limit(1)
                            .get()
                            .await()
                    }
                    consultaMinuscula != null && !consultaMinuscula.isEmpty
                } else {
                    false
                }
            }

            if (!existe) {
                return Result.failure(CorreoNoRegistradoException())
            }

            fbAuth.sendPasswordResetEmail(correoLimpio).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun actualizarPerfilUsuario(
        usuarioId: String,
        telefono: String,
        direccion: String
    ): Result<Unit> {
        return try {
            val db = firestore ?: return Result.failure(Exception("Base de datos no disponible"))
            val updates = mapOf(
                "telefono" to telefono,
                "direccion" to direccion
            )
            withTimeoutOrNull(3500L) {
                db.collection("usuarios").document(usuarioId)
                    .set(updates, com.google.firebase.firestore.SetOptions.merge())
                    .await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun iniciarSesionConFacebook(token: AccessToken): Result<Usuario> {
        return try {
            val fbAuth = auth ?: return Result.failure(Exception("Servicio de autenticación no disponible"))
            val db = firestore
            val credential = FacebookAuthProvider.getCredential(token.token)
            val authResult = fbAuth.signInWithCredential(credential).await()
            val uid = authResult.user?.uid ?: throw Exception("Usuario no encontrado")

            var usuarioFirestore: Usuario? = null
            if (db != null) {
                try {
                    usuarioFirestore = withTimeoutOrNull(3500L) {
                        val docUsuario = db.collection("usuarios").document(uid).get().await()
                        docUsuario.toObject(Usuario::class.java)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            if (usuarioFirestore != null) {
                Result.success(usuarioFirestore)
            } else {
                val fbUser = authResult.user
                val emailFb = fbUser?.email ?: ""
                val nombreFb = fbUser?.displayName ?: (if (emailFb.isNotBlank()) emailFb.substringBefore("@") else "Usuario")
                val usuarioNuevo = Usuario(
                    id = uid,
                    nombreCompleto = nombreFb,
                    correo = emailFb,
                    usuario = if (emailFb.isNotBlank()) emailFb.substringBefore("@") else uid.take(8),
                    rol = if (emailFb.contains("admin", ignoreCase = true)) RolUsuario.ADMIN.name else RolUsuario.CLIENTE.name,
                    telefono = "",
                    direccion = "",
                    creadoEn = System.currentTimeMillis()
                )
                if (db != null) {
                    try {
                        withTimeoutOrNull(2500L) {
                            db.collection("usuarios").document(uid).set(usuarioNuevo).await()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                Result.success(usuarioNuevo)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun cerrarSesion() {
        try {
            auth?.signOut()
            LoginManager.getInstance().logOut()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
