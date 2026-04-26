package org.example;

import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final EmailService emailService;

    // 1. Memoria temporal para los códigos
    private final Map<String, String> codigosDeVerificacion = new HashMap<>();

    // NUEVO: 2. Memoria para "sostener" la contraseña mientras el usuario se verifica
    private final Map<String, String> contrasenasPendientes = new HashMap<>();

    // NUEVO: 3. Nuestra Base de Datos de Usuarios Oficiales (Correo -> Contraseña)
    private final Map<String, String> usuariosRegistrados = new HashMap<>();

    // 4. Libreta temporal para los códigos de recuperación
    private final Map<String, String> codigosDeRecuperacion = new HashMap<>();

    public AuthController(EmailService emailService) {
        this.emailService = emailService;

        // NUEVO: Agregamos a los usuarios por defecto para que no pierdas el acceso de prueba
        usuariosRegistrados.put("administrador", "admin123");
        usuariosRegistrados.put("cliente", "cliente123");
    }

    static class SolicitudRegistro {
        public String correo;
        public String contrasena;
    }

    static class SolicitudVerificacion {
        public String correo;
        public String codigo;
    }

    static class SolicitudReset {
        public String correo;
        public String codigo;
        public String nuevaContrasena;
    }

    @PostMapping("/enviar-codigo")
    public Map<String, String> enviarCodigo(@RequestBody SolicitudRegistro solicitud) {
        Map<String, String> respuesta = new HashMap<>();

        // NUEVO GUARDIA: ¿El correo ya existe en nuestra base de datos?
        if (usuariosRegistrados.containsKey(solicitud.correo)) {
            respuesta.put("status", "error");
            respuesta.put("mensaje", "Este correo ya está registrado. Por favor, inicia sesión.");
            return respuesta; // Detenemos el proceso aquí mismo

        }

        // Si el correo es nuevo, procedemos normalmente
        String codigoGenerado = String.format("%06d", new Random().nextInt(1000000));

        codigosDeVerificacion.put(solicitud.correo, codigoGenerado);
        contrasenasPendientes.put(solicitud.correo, solicitud.contrasena);

        emailService.enviarCodigoVerificacion(solicitud.correo, codigoGenerado);

        respuesta.put("status", "exito");
        respuesta.put("mensaje", "Código de 6 dígitos enviado al correo: " + solicitud.correo);
        return respuesta;

    }

    @PostMapping("/verificar")
    public Map<String, String> verificarCodigo(@RequestBody SolicitudVerificacion solicitud) {
        Map<String, String> respuesta = new HashMap<>();
        String codigoGuardado = codigosDeVerificacion.get(solicitud.correo);

        if (codigoGuardado != null && codigoGuardado.equals(solicitud.codigo)) {
            emailService.enviarBienvenida(solicitud.correo);

            // NUEVO: ¡El usuario es válido! Lo pasamos a la base de datos oficial
            String passwordConfirmado = contrasenasPendientes.get(solicitud.correo);
            usuariosRegistrados.put(solicitud.correo, passwordConfirmado);

            // Limpiamos la memoria temporal
            codigosDeVerificacion.remove(solicitud.correo);
            contrasenasPendientes.remove(solicitud.correo);

            respuesta.put("status", "exito");
            respuesta.put("mensaje", "Cuenta creada y verificada.");
        } else {
            respuesta.put("status", "error");
            respuesta.put("mensaje", "Código incorrecto.");
        }
        return respuesta;
    }

    // ====================================================================
    // NUEVO ENDPOINT: Para iniciar sesión desde el index.html
    // ====================================================================
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody SolicitudRegistro solicitud) {
        Map<String, String> respuesta = new HashMap<>();

        // Buscamos si el correo existe en nuestra lista de registrados
        String passwordGuardado = usuariosRegistrados.get(solicitud.correo);

        // Si existe y la contraseña coincide...
        if (passwordGuardado != null && passwordGuardado.equals(solicitud.contrasena)) {
            respuesta.put("status", "exito");

            // Un pequeño truco: si el correo es "administrador", le decimos al frontend que es rol admin
            if (solicitud.correo.equals("administrador")) {
                respuesta.put("rol", "admin");
            } else {
                respuesta.put("rol", "cliente");
            }
        } else {
            respuesta.put("status", "error");
            respuesta.put("mensaje", "Usuario o contraseña incorrectos.");
        }

        return respuesta;
    }

    // ====================================================================
    // ENDPOINT: Solicitar recuperación de contraseña
    // ====================================================================
    @PostMapping("/olvide-password")
    public Map<String, String> olvidePassword(@RequestBody SolicitudRegistro solicitud) {
        Map<String, String> respuesta = new HashMap<>();

        // Verificamos si el usuario realmente existe en nuestra "Base de Datos"
        if (usuariosRegistrados.containsKey(solicitud.correo)) {
            String codigoGenerado = String.format("%06d", new Random().nextInt(1000000));
            codigosDeRecuperacion.put(solicitud.correo, codigoGenerado);
            emailService.enviarCodigoRecuperacion(solicitud.correo, codigoGenerado);

            respuesta.put("status", "exito");
            respuesta.put("mensaje", "Código de recuperación enviado.");
        } else {
            respuesta.put("status", "error");
            respuesta.put("mensaje", "No existe una cuenta con este correo.");
        }
        return respuesta;
    }

    // ====================================================================
    // ENDPOINT: Validar código y cambiar la contraseña
    // ====================================================================
    @PostMapping("/reset-password")
    public Map<String, String> resetPassword(@RequestBody SolicitudReset solicitud) {
        Map<String, String> respuesta = new HashMap<>();
        String codigoGuardado = codigosDeRecuperacion.get(solicitud.correo);

        if (codigoGuardado != null && codigoGuardado.equals(solicitud.codigo)) {
            // Actualizamos la contraseña en nuestra base de datos
            usuariosRegistrados.put(solicitud.correo, solicitud.nuevaContrasena);

            // Limpiamos la memoria
            codigosDeRecuperacion.remove(solicitud.correo);

            respuesta.put("status", "exito");
            respuesta.put("mensaje", "Contraseña actualizada correctamente.");
        } else {
            respuesta.put("status", "error");
            respuesta.put("mensaje", "El código es incorrecto o ha expirado.");
        }
        return respuesta;

    }

}