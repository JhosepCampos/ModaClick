package com.cibertec.inventario.datos

import com.cibertec.inventario.modelo.EstadoPedido
import com.cibertec.inventario.modelo.ItemCarrito
import com.cibertec.inventario.modelo.Pedido
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull

class StockInsuficienteException(mensaje: String) : Exception(mensaje)

class PedidosRepository {

    private val firestore by lazy {
        try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun crearPedido(pedido: Pedido): Result<String> {
        return try {
            val db = firestore ?: return Result.failure(Exception("Base de datos no disponible"))
            val id = if (pedido.id.isNotBlank()) pedido.id else "VR-${1000 + (1..9999).random()}"
            val pedidoConId = pedido.copy(
                id = id,
                clienteCorreo = pedido.clienteCorreo.trim().lowercase()
            )

            withTimeoutOrNull(10000L) {
                db.runTransaction { transaction ->
                    val actualizacionesStock = mutableListOf<Pair<DocumentReference, Int>>()

                    val cantidadesPorPrenda = mutableMapOf<String, Int>()
                    for (item in pedido.items) {
                        cantidadesPorPrenda[item.prendaId] = (cantidadesPorPrenda[item.prendaId] ?: 0) + item.cantidad
                    }

                    for ((prendaId, cantidadTotal) in cantidadesPorPrenda) {
                        val prendaDocRef = db.collection("prendas").document(prendaId)
                        val snapshot = transaction.get(prendaDocRef)
                        if (!snapshot.exists()) {
                            throw Exception("Uno de los productos seleccionados ya no se encuentra disponible")
                        }
                        val stockActual = snapshot.getLong("stock")?.toInt() ?: 0
                        val prendaNombre = snapshot.getString("nombre") ?: "Producto"

                        if (stockActual < cantidadTotal) {
                            val detalle = if (stockActual <= 0) "se encuentra sin stock" else "solo tiene $stockActual disponibles"
                            throw StockInsuficienteException("El producto '$prendaNombre' $detalle")
                        }

                        val nuevoStock = (stockActual - cantidadTotal).coerceAtLeast(0)
                        actualizacionesStock.add(Pair(prendaDocRef, nuevoStock))
                    }

                    for ((docRef, nuevoStock) in actualizacionesStock) {
                        transaction.update(docRef, "stock", nuevoStock)
                    }

                    val pedidoDocRef = db.collection("pedidos").document(id)
                    transaction.set(pedidoDocRef, pedidoConId)
                }.await()
            } ?: return Result.failure(Exception("Tiempo de espera agotado al procesar el pedido"))

            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun obtenerPedidosPorCliente(clienteId: String, clienteCorreo: String = ""): Flow<List<Pedido>> = callbackFlow {
        val db = firestore
        val idLimpio = clienteId.trim()
        val correoLimpio = clienteCorreo.trim().lowercase()

        if (db == null || (idLimpio.isBlank() && correoLimpio.isBlank())) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = db.collection("pedidos")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val pedidos = snapshot.toObjects(Pedido::class.java)
                    val pedidosDelCliente = pedidos.filter {
                        (idLimpio.isNotBlank() && it.clienteId == idLimpio) ||
                        (correoLimpio.isNotBlank() && it.clienteCorreo.trim().equals(correoLimpio, ignoreCase = true))
                    }
                    trySend(pedidosDelCliente)
                } else {
                    trySend(emptyList())
                }
            }

        awaitClose { listener.remove() }
    }

    fun obtenerTodosLosPedidos(): Flow<List<Pedido>> = callbackFlow {
        val db = firestore

        if (db == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = db.collection("pedidos")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val pedidos = snapshot.toObjects(Pedido::class.java)
                    trySend(pedidos)
                } else {
                    trySend(emptyList())
                }
            }

        awaitClose { listener.remove() }
    }

    suspend fun actualizarEstadoPedido(pedidoId: String, nuevoEstado: EstadoPedido): Result<Unit> {
        return try {
            val db = firestore ?: return Result.failure(Exception("Base de datos no disponible"))
            withTimeoutOrNull(4000L) {
                db.collection("pedidos").document(pedidoId)
                    .update("estado", nuevoEstado.name)
                    .await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
