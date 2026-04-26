// ==========================================
// 1. SELECCIÓN DE ELEMENTOS DEL DOM
// ==========================================
const formRegistro = document.getElementById('formRegistro');
const formVerificacion = document.getElementById('formVerificacion');

const seccionRegistro = document.getElementById('seccion-registro');
const seccionVerificacion = document.getElementById('seccion-verificacion');

const mensajeErrorReg = document.getElementById('mensajeErrorReg');
const mensajeErrorVer = document.getElementById('mensajeErrorVer');
const textoCorreoEnviado = document.getElementById('correo-enviado-texto');

// Variable global para recordar el correo entre el paso 1 y el paso 2
let correoUsuarioTemporal = '';

// ==========================================
// 2. LÓGICA DEL PASO 1: ENVIAR CÓDIGO
// ==========================================
formRegistro.addEventListener('submit', function(evento) {
    evento.preventDefault(); // Evita que la página recargue

    // Obtener los valores de los inputs
    const correo = document.getElementById('reg-correo').value;
    const password = document.getElementById('reg-password').value;
    const passwordConf = document.getElementById('reg-password-conf').value;

    // Validación rápida: ¿Las contraseñas son iguales?
    if (password !== passwordConf) {
        mostrarError(mensajeErrorReg, "Las contraseñas no coinciden. Inténtalo de nuevo.");
        return;
    }

    // Limpiamos errores anteriores y mostramos estado de carga
    mensajeErrorReg.style.display = 'none';
    const boton = formRegistro.querySelector('button');
    boton.innerText = "ENVIANDO...";
    boton.disabled = true;

    // Hacemos la petición POST a tu servidor Java
    fetch('http://localhost:8080/api/auth/enviar-codigo', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ correo: correo, contrasena: password })
    })
    .then(respuesta => respuesta.json())
    .then(datos => {
        // NUEVO: Verificamos qué nos respondió Java
        if (datos.status === 'error') {
            // Si el correo ya existe, mostramos el error y reactivamos el botón
            mostrarError(mensajeErrorReg, datos.mensaje);
            boton.innerText = "ENVIAR CÓDIGO";
            boton.disabled = false;
        } else {
            // ¡Éxito! El correo es nuevo, guardamos el temporal y cambiamos de pantalla
            correoUsuarioTemporal = correo;
            textoCorreoEnviado.innerText = correo; 

            seccionRegistro.classList.add('oculto');
            seccionVerificacion.classList.remove('oculto');
        }

    })
    
    .catch(error => {
        console.error("Error:", error);
        mostrarError(mensajeErrorReg, "Error al conectar con el servidor. Verifica que Java esté corriendo.");
        boton.innerText = "ENVIAR CÓDIGO";
        boton.disabled = false;
    });
});

// ==========================================
// 3. LÓGICA DEL PASO 2: VERIFICAR CÓDIGO
// ==========================================
formVerificacion.addEventListener('submit', function(evento) {
    evento.preventDefault();

    const codigo = document.getElementById('reg-codigo').value;
    mensajeErrorVer.style.display = 'none';

    // Hacemos la petición POST para verificar
    fetch('http://localhost:8080/api/auth/verificar', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ correo: correoUsuarioTemporal, codigo: codigo })
    })
    .then(respuesta => respuesta.json())
    .then(datos => {
        if (datos.status === 'exito') {
            // Si el código es correcto, el servidor ya mandó el correo de bienvenida
            alert("¡Cuenta verificada con éxito! Bienvenido a NutriExpress.");
            
            // Redirección directa al catálogo del cliente
            window.location.href = 'catalogo.html';
        } else {
            // Si el código es incorrecto (error)
            mostrarError(mensajeErrorVer, datos.mensaje);
        }
    })
    .catch(error => {
        console.error("Error:", error);
        mostrarError(mensajeErrorVer, "Error al conectar con el servidor.");
    });
});

// ==========================================
// FUNCIÓN AUXILIAR PARA MOSTRAR ERRORES
// ==========================================
function mostrarError(elemento, mensaje) {
    elemento.innerText = mensaje;
    elemento.style.display = 'block';
}