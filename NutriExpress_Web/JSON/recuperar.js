const formSolicitud = document.getElementById('formSolicitud');
const formReset = document.getElementById('formReset');
const seccionSolicitud = document.getElementById('seccion-solicitud');
const seccionReset = document.getElementById('seccion-reset');

let correoParaReset = '';

// Paso 1: Pedir el código
formSolicitud.addEventListener('submit', (e) => {
    e.preventDefault();
    const correo = document.getElementById('reset-correo').value;
    
    fetch('http://localhost:8080/api/auth/olvide-password', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ correo: correo })
    })
    .then(res => res.json())
    .then(datos => {
        if(datos.status === 'exito') {
            correoParaReset = correo;
            seccionSolicitud.classList.add('oculto');
            seccionReset.classList.remove('oculto');
        } else {
            alert(datos.mensaje);
        }
    });
});

// Paso 2: Cambiar la contraseña
formReset.addEventListener('submit', (e) => {
    e.preventDefault();
    const codigo = document.getElementById('reset-codigo').value;
    const nuevaPass = document.getElementById('nueva-pass').value;

    fetch('http://localhost:8080/api/auth/reset-password', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ 
            correo: correoParaReset, 
            codigo: codigo, 
            nuevaContrasena: nuevaPass 
        })
    })
    .then(res => res.json())
    .then(datos => {
        if(datos.status === 'exito') {
            alert("Contraseña actualizada. Ahora puedes iniciar sesión.");
            window.location.href = 'index.html';
        } else {
            alert(datos.mensaje);
        }
    });
});