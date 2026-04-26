document.addEventListener('DOMContentLoaded', () => {
    // 1. Leer el ID de la URL (ej. editar-producto.html?id=5)
    const parametrosUrl = new URLSearchParams(window.location.search);
    const idProducto = parametrosUrl.get('id');

    if (!idProducto) {
        alert("No se proporcionó un ID válido.");
        window.location.href = 'admin.html';
        return;
    }

    // 2. Pedirle los datos de ese ID a Java
    fetch(`http://localhost:8080/api/admin/suplementos/${idProducto}`)
        .then(respuesta => respuesta.json())
        .then(producto => {
            if (!producto) {
                alert("El producto no existe.");
                window.location.href = 'admin.html';
                return;
            }

            // 3. Llenar el formulario con los datos
            document.getElementById('edit-id').value = producto.idSuplemento;
            document.getElementById('edit-nombre').value = producto.nombre;
            document.getElementById('edit-marca').value = producto.marca;
            document.getElementById('edit-categoria').value = producto.categoria;
            document.getElementById('edit-precio').value = producto.precio;

            // 4. Mostrar la imagen actual en el cuadro de Drag & Drop
            const zonaDrop = document.getElementById('edit-zona-drop');
            zonaDrop.innerHTML = `
                <img src="${producto.urlImagen}" style="max-height: 120px; border-radius: 8px; margin-bottom: 10px;">
                <p style="color: #d35400; font-weight: bold;">Imagen Actual</p>
                <p style="font-size: 12px; color: #a8abaf;">Haz clic o arrastra una nueva imagen para reemplazarla</p>
            `;
        })
        .catch(error => {
            console.error("Error:", error);
            alert("Error al cargar los datos del producto.");
        });
});

// === LÓGICA DE DRAG & DROP Y ENVÍO (Similar al admin.js) ===
const zonaDrop = document.getElementById('edit-zona-drop');
const inputArchivo = document.getElementById('edit-input-archivo');
let archivoNuevo = null;

zonaDrop.addEventListener('click', () => inputArchivo.click());

inputArchivo.addEventListener('change', (e) => {
    if (e.target.files.length > 0) procesarArchivo(e.target.files[0]);
});

zonaDrop.addEventListener('dragover', (e) => { e.preventDefault(); zonaDrop.style.borderColor = '#d35400'; });
zonaDrop.addEventListener('dragleave', () => { zonaDrop.style.borderColor = '#555'; });
zonaDrop.addEventListener('drop', (e) => {
    e.preventDefault();
    zonaDrop.style.borderColor = '#555';
    if (e.dataTransfer.files.length > 0) procesarArchivo(e.dataTransfer.files[0]);
});

function procesarArchivo(archivo) {
    archivoNuevo = archivo;
    const reader = new FileReader();
    reader.onload = (e) => {
        zonaDrop.innerHTML = `
            <img src="${e.target.result}" style="max-height: 120px; border-radius: 8px; margin-bottom: 10px;">
            <p style="color: #28a745; font-weight: bold;">¡Nueva imagen lista!</p>
            <p style="font-size: 12px; color: #a8abaf;">${archivo.name}</p>
        `;
    };
    reader.readAsDataURL(archivo);
}

// === ENVIAR ACTUALIZACIÓN A JAVA ===
document.getElementById('form-editar').addEventListener('submit', (e) => {
    e.preventDefault();

    const paquete = new FormData();
    paquete.append('id', document.getElementById('edit-id').value);
    paquete.append('nombre', document.getElementById('edit-nombre').value);
    paquete.append('marca', document.getElementById('edit-marca').value);
    paquete.append('categoria', document.getElementById('edit-categoria').value);
    paquete.append('precio', document.getElementById('edit-precio').value);
    
    // Solo si el usuario subió una nueva foto, la mandamos. Si no, Java mantiene la vieja.
    if (archivoNuevo) {
        paquete.append('imagen', archivoNuevo);
    }

    const btnGuardar = document.querySelector('button[type="submit"]');
    btnGuardar.innerText = "Guardando...";
    btnGuardar.disabled = true;

    fetch('http://localhost:8080/api/admin/suplementos', {
        method: 'POST',
        body: paquete
    })
    .then(res => res.json())
    .then(datos => {
        if (datos.status === 'exito') {
            alert("Producto actualizado correctamente.");
            window.location.href = 'admin.html'; // Lo regresamos al panel principal
        } else {
            alert("Error: " + datos.mensaje);
        }
    })
    .finally(() => {
        btnGuardar.innerText = "Guardar Cambios";
        btnGuardar.disabled = false;
    });
});