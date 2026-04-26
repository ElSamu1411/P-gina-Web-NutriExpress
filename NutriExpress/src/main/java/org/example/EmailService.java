package org.example;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // MÉTODO 1: El del código de 6 dígitos
    public void enviarCodigoVerificacion(String correoDestino, String codigoDe6Digitos) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(correoDestino);
        mensaje.setSubject("NutriExpress - Código de Verificación ⚡");
        mensaje.setText("¡Hola!\n\n"
                + "Gracias por registrarte en NutriExpress. Para completar la creación de tu cuenta, "
                + "por favor ingresa el siguiente código de seguridad en la página web:\n\n"
                + "CÓDIGO: " + codigoDe6Digitos + "\n\n"
                + "Si no solicitaste este correo, puedes ignorarlo con seguridad.\n\n"
                + "Atentamente,\n"
                + "El equipo de NutriExpress.");

        mailSender.send(mensaje);
        System.out.println("[SISTEMA] Correo de verificación enviado con éxito a: " + correoDestino);
    }

    // MÉTODO 2: El de Bienvenida (Este es el que estaba perdido)
    public void enviarBienvenida(String correoDestino) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(correoDestino);
        mensaje.setSubject("¡Bienvenido a la familia NutriExpress! 🌱");
        mensaje.setText("¡Felicidades!\n\n"
                + "Tu cuenta ha sido verificada con éxito. Ahora tienes acceso total a nuestro "
                + "catálogo de suplementos premium y ofertas exclusivas.\n\n"
                + "Estamos aquí para acompañarte en tu camino hacia el rendimiento máximo.\n\n"
                + "¡A darle con todo!\n\n"
                + "Atentamente,\n"
                + "El equipo de NutriExpress.");

        mailSender.send(mensaje);
        System.out.println("[SISTEMA] Correo de bienvenida enviado a: " + correoDestino);
    }

    // MÉTODO 3: Recuperación de contraseña
    public void enviarCodigoRecuperacion(String correoDestino, String codigoDe6Digitos) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(correoDestino);
        mensaje.setSubject("NutriExpress - Recuperación de Contraseña 🔐");
        mensaje.setText("¡Hola!\n\n"
                + "Hemos recibido una solicitud para restablecer la contraseña de tu cuenta.\n"
                + "Ingresa este código de seguridad para crear una nueva contraseña:\n\n"
                + "CÓDIGO: " + codigoDe6Digitos + "\n\n"
                + "Si no solicitaste este cambio, ignora este correo. Tu cuenta está segura.\n\n"
                + "Atentamente,\n"
                + "El equipo de NutriExpress.");

        mailSender.send(mensaje);
        System.out.println("[SISTEMA] Correo de recuperación enviado a: " + correoDestino);
    }
}