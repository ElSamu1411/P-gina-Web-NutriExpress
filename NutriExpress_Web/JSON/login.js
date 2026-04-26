document.getElementById('formLogin').addEventListener('submit', function(evento) {
    evento.preventDefault(); 

    const usuario = document.getElementById('username').value;
    const contrasena = document.getElementById('password').value;
    const mensajeError = document.getElementById('mensajeError');

    // Limpiamos errores anteriores
    mensajeError.style.display = 'none';

    // Hacemos la petición a Java
    fetch('http://localhost:8080/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ correo: usuario, contrasena: contrasena })
    })
    .then(respuesta => respuesta.json())
    .then(datos => {
        if (datos.status === 'exito') {
            // Java dice que sí pasamos. Revisamos el rol para saber a qué pantalla mandarlo.
            if (datos.rol === 'admin') {
                window.location.href = 'admin.html'; // Próximamente la crearemos
            } else {
                window.location.href = 'catalogo.html';
            }
        } else {
            // Java dice que las credenciales están mal
            mensajeError.innerText = datos.mensaje;
            mensajeError.style.display = 'block';
        }
    })
    .catch(error => {
        console.error("Error de conexión:", error);
        mensajeError.innerText = "Error al conectar con el servidor.";
        mensajeError.style.display = 'block';
    });
});