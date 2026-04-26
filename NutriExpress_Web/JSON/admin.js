// ==========================================
// 1. LÓGICA DE LA TABLA (CARGAR CATÁLOGO)
// ==========================================
document.addEventListener('DOMContentLoaded', () => {
    cargarTablaAdmin();
});

let catalogoGlobal = []; // Memoria para poder editar productos sin volver a consultar a Java
let idAEliminar = null;  // Memoria para saber a quién vamos a borrar

function cargarTablaAdmin() {
    const cuerpoTabla = document.getElementById('cuerpo-tabla');

    fetch('http://localhost:8080/api/catalogo')
        .then(respuesta => respuesta.json())
        .then(suplementos => {
            catalogoGlobal = suplementos; // Guardamos los datos en memoria
            cuerpoTabla.innerHTML = '';

            suplementos.forEach(producto => {
                const precio = new Intl.NumberFormat('es-MX', { style: 'currency', currency: 'MXN' }).format(producto.precio);
                const badge = producto.activo 
                    ? '<span class="badge-activo">ACTIVO</span>'
                    : '<span class="badge-activo" style="background-color: rgba(220,53,69,0.2); color:#dc3545;">INACTIVO</span>';

                // Agregamos los eventos onclick a los iconos
                const fila = `
                    <tr>
                        <td>#${producto.idSuplemento}</td>
                        <td><img src="${producto.urlImagen}" class="img-miniatura" alt="img"></td>
                        <td>${producto.nombre}</td>
                        <td>${producto.marca}</td>
                        <td>${precio}</td>
                        <td>${badge}</td>
                        <td>
                            <span class="icono-accion" title="Editar" onclick="prepararEdicion(${producto.idSuplemento})">✏️</span>
                            <span class="icono-accion" title="Eliminar" onclick="abrirModal(${producto.idSuplemento}, '${producto.nombre}')">🗑️</span>
                        </td>
                    </tr>
                `;
                cuerpoTabla.innerHTML += fila;
            });
        });
}

// === LÓGICA DE EDICIÓN ===
function prepararEdicion(id) {
    // Redirigimos a la nueva página pasando el ID por la URL
    window.location.href = `editar-producto.html?id=${id}`;
}

// === LÓGICA DE ELIMINACIÓN ===
function abrirModal(id, nombre) {
    idAEliminar = id;
    document.getElementById('nombre-eliminar-texto').innerText = nombre;
    document.getElementById('modal-eliminar').classList.remove('oculto');
}

function cerrarModal() {
    idAEliminar = null;
    document.getElementById('modal-eliminar').classList.add('oculto');
}

function ejecutarEliminacion(tipoBaja) {
    // Le enviamos la petición a Java pasándole el tipo de baja (Global o Sucursal)
    fetch(`http://localhost:8080/api/admin/suplementos/${idAEliminar}?tipo=${tipoBaja}`, {
        method: 'DELETE'
    })
    .then(res => res.json())
    .then(datos => {
        if (datos.status === 'exito') {
            alert(datos.mensaje);
            cerrarModal();
            cargarTablaAdmin(); // Recargamos para ver el cambio
        } else {
            alert("Error: " + datos.mensaje);
        }
    });
}

// ==========================================
// 2. LÓGICA DEL DRAG & DROP (IMÁGENES)
// ==========================================
const zonaDrop = document.getElementById('zona-drop');
const inputArchivo = document.getElementById('input-archivo');
let archivoSeleccionado = null; // Aquí guardaremos la foto real para mandarla a Java después

// Si hacen clic en el cuadro, abrimos el explorador de archivos
zonaDrop.addEventListener('click', () => {
    inputArchivo.click();
});

// Si seleccionan un archivo desde el explorador
inputArchivo.addEventListener('change', (evento) => {
    if (evento.target.files.length > 0) {
        procesarArchivo(evento.target.files[0]);
    }
});

// Eventos de arrastrar y soltar (Evitamos que el navegador abra la foto en otra pestaña)
zonaDrop.addEventListener('dragover', (evento) => {
    evento.preventDefault();
    zonaDrop.classList.add('arrastrando'); // Cambia el color del borde (CSS)
});

zonaDrop.addEventListener('dragleave', () => {
    zonaDrop.classList.remove('arrastrando');
});

zonaDrop.addEventListener('drop', (evento) => {
    evento.preventDefault();
    zonaDrop.classList.remove('arrastrando');
    
    // Si soltaron un archivo, lo procesamos
    if (evento.dataTransfer.files.length > 0) {
        procesarArchivo(evento.dataTransfer.files[0]);
    }
});

// Función para mostrar la vista previa de la foto en el cuadro
function procesarArchivo(archivo) {
    // Validamos que sea una imagen
    if (!archivo.type.startsWith('image/')) {
        alert('Por favor, selecciona un archivo de imagen válido (PNG, JPG).');
        return;
    }

    archivoSeleccionado = archivo; // Guardamos el archivo en la memoria

    // Usamos FileReader para leer la foto y mostrarla en la pantalla
    const reader = new FileReader();
    reader.onload = (evento) => {
        zonaDrop.innerHTML = `
            <img src="${evento.target.result}" style="max-height: 120px; border-radius: 8px; margin-bottom: 10px;">
            <p style="color: #d35400; font-weight: bold;">${archivo.name}</p>
            <p style="font-size: 12px; color: #a8abaf;">Haz clic o arrastra otra imagen para cambiarla</p>
        `;
    };
    reader.readAsDataURL(archivo);
}

// ==========================================
// 3. ENVIAR EL NUEVO PRODUCTO A JAVA
// ==========================================
const formProducto = document.getElementById('form-producto');

formProducto.addEventListener('submit', (evento) => {
    evento.preventDefault(); 

    // 1. Obtenemos el ID oculto (Si está vacío, es un producto nuevo. Si tiene número, estamos editando)
    const idSuplemento = document.getElementById('suplemento-id').value;

    // GUARDIA CORREGIDO: Solo exigimos imagen si es un producto NUEVO
    if (!idSuplemento && !archivoSeleccionado) {
        alert("Por favor, arrastra una imagen para el nuevo producto.");
        return;
    }

    // 2. Recolectamos los textos
    const nombre = document.getElementById('nombre').value;
    const marca = document.getElementById('marca').value;
    const categoria = document.getElementById('categoria').value;
    const precio = document.getElementById('precio').value;

    // 3. Empaquetamos todo
    const paquete = new FormData();
    
    // Si estamos editando, metemos el ID a la caja para que Java lo sepa
    if (idSuplemento) {
        paquete.append('id', idSuplemento);
    }
    
    paquete.append('nombre', nombre);
    paquete.append('marca', marca);
    paquete.append('categoria', categoria);
    paquete.append('precio', precio);
    
    // Solo mandamos el archivo si el usuario arrastró uno nuevo
    if (archivoSeleccionado) {
        paquete.append('imagen', archivoSeleccionado); 
    }

    const btnGuardar = formProducto.querySelector('button[type="submit"]');
    btnGuardar.innerText = "Guardando...";
    btnGuardar.disabled = true;

    // 4. Enviamos a Java
    fetch('http://localhost:8080/api/admin/suplementos', {
        method: 'POST',
        body: paquete
    })
    .then(respuesta => respuesta.json())
    .then(datos => {
        if (datos.status === 'exito') {
            alert("Operación exitosa: " + nombre);
            
            // Limpiamos todo para que quede listo para el siguiente
            formProducto.reset(); 
            document.getElementById('suplemento-id').value = ''; // Vaciamos el ID oculto
            btnGuardar.innerText = "Guardar Producto"; // Restauramos el texto original
            zonaDrop.innerHTML = '<span style="font-size: 30px;">📸</span><p><strong>Arrastra y suelta</strong> la imagen aquí o haz clic para explorar.</p>';
            archivoSeleccionado = null;
            
            cargarTablaAdmin(); 
        } else {
            alert("Error: " + datos.mensaje);
        }
    })
    .catch(error => {
        console.error("Error al enviar:", error);
        alert("Error al conectar con el servidor.");
    })
    .finally(() => {
        btnGuardar.innerText = "Guardar Producto";
        btnGuardar.disabled = false;
    });
});