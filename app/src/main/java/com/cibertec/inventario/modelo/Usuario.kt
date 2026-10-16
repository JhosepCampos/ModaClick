package com.cibertec.inventario.modelo

enum class RolUsuario {
    CLIENTE,
    ADMIN
}

data class Usuario(
    val id: String = "",
    val nombreCompleto: String = "",
    val correo: String = "",
    val usuario: String = "",
    val rol: String = RolUsuario.CLIENTE.name,
    val telefono: String = "",
    val direccion: String = "",
    val creadoEn: Long = System.currentTimeMillis()
) {
    fun obtenerRolEnum(): RolUsuario {
        return if (rol.equals(RolUsuario.ADMIN.name, ignoreCase = true)) {
            RolUsuario.ADMIN
        } else {
            RolUsuario.CLIENTE
        }
    }
}
