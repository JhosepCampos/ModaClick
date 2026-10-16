package com.cibertec.inventario.modelo

data class ItemCarrito(
    val prendaId: String = "",
    val prendaNombre: String = "",
    val prendaPrecio: Double = 0.0,
    val prendaCategoria: String = "",
    val tallaSeleccionada: String = "M",
    val colorSeleccionado: String = "Negro",
    var cantidad: Int = 1,
    val prendaImagenUrl: String = "",
    val prendaStock: Int = 10
) {
    constructor(
        prenda: Prenda,
        tallaSeleccionada: String,
        colorSeleccionado: String,
        cantidad: Int = 1
    ) : this(
        prendaId = prenda.id,
        prendaNombre = prenda.nombre,
        prendaPrecio = prenda.precio,
        prendaCategoria = prenda.categoria,
        tallaSeleccionada = tallaSeleccionada,
        colorSeleccionado = colorSeleccionado,
        cantidad = cantidad,
        prendaImagenUrl = prenda.imagenUrl,
        prendaStock = prenda.stock
    )

    val subtotal: Double
        get() = prendaPrecio * cantidad

    val sinStock: Boolean
        get() = prendaStock <= 0

    val superaStock: Boolean
        get() = cantidad > prendaStock
}
