package com.cibertec.inventario.datos

import com.cibertec.inventario.modelo.Prenda
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull

class PrendasRepository {

    private val firestore by lazy {
        try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            null
        }
    }

    fun obtenerPrendas(): Flow<List<Prenda>> = callbackFlow {
        val db = firestore

        if (db == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = db.collection("prendas").addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(emptyList())
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val lista = snapshot.toObjects(Prenda::class.java)
                trySend(lista)
            } else {
                trySend(emptyList())
            }
        }

        awaitClose {
            listener.remove()
        }
    }

    suspend fun obtenerPrendaPorId(id: String): Prenda? {
        return try {
            val db = firestore ?: return null
            val doc = db.collection("prendas").document(id).get().await()
            doc.toObject(Prenda::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun guardarPrenda(prenda: Prenda): Result<String> {
        return try {
            val db = firestore ?: return Result.failure(Exception("Base de datos no disponible"))
            val id = if (prenda.id.isNotBlank()) prenda.id else "prenda_${System.currentTimeMillis()}"
            val prendaGuardar = prenda.copy(id = id)
            withTimeoutOrNull(5000L) {
                db.collection("prendas").document(id).set(prendaGuardar).await()
            }
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminarPrenda(id: String): Result<Unit> {
        return try {
            val db = firestore ?: return Result.failure(Exception("Base de datos no disponible"))
            withTimeoutOrNull(5000L) {
                db.collection("prendas").document(id).delete().await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
