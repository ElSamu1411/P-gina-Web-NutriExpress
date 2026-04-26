document.addEventListener('DOMContentLoaded', () => {
    cargarProductos();
});

function cargarProductos() {
    const contenedor = document.getElementById('contenedor-productos');

    fetch('http://localhost:8080/api/catalogo')
        .then(respuesta => respuesta.json())
        .then(suplementos => {
            contenedor.innerHTML = '';

            suplementos.forEach(producto => {
                
                // Si el producto no está "activo" en la base de datos, lo saltamos y no lo dibujamos
                if (!producto.activo) return;

                const precioFormateado = new Intl.NumberFormat('es-MX', { 
                    style: 'currency', 
                    currency: 'MXN' 
                }).format(producto.precio);

                // Fíjate cómo usamos producto.urlImagen y producto.idSuplemento
                const tarjetaHTML = `
                    <article class="tarjeta-producto">
                        <img src="${producto.urlImagen}" alt="${producto.nombre}" class="imagen-producto">
                        <span class="marca-producto">${producto.marca}</span>
                        <h2 class="nombre-producto">${producto.nombre}</h2>
                        <span class="precio-producto">${precioFormateado}</span>
                        <button class="btn-carrito" onclick="agregarAlCarrito(${producto.idSuplemento})">Agregar al Carrito</button>
                    </article>
                `;

                contenedor.innerHTML += tarjetaHTML;
            });
        })
        .catch(error => {
            console.error("Error al cargar el catálogo:", error);
            contenedor.innerHTML = '<p style="color: red; text-align: center; width: 100%;">Error al conectar con el servidor.</p>';
        });
}

function agregarAlCarrito(idDelProducto) {
    console.log("Producto seleccionado ID:", idDelProducto);
    alert("Producto agregado al carrito. (Lógica en desarrollo)");
}