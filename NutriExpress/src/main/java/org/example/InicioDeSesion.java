package org.example;

import java.util.Optional;
import java.util.Scanner;

/**
 * Clase exclusiva para manejar la autenticación de usuarios.
 */
public class InicioDeSesion {

    public Usuario ejecutar(Scanner scanner, UsuarioRepository usuarioRepo) {
        boolean sesionActiva = false;
        Usuario usuarioAutenticado = null;

        System.out.println("\n===========================================");
        System.out.println("    NUTRIEXPRESS - ACCESO AL SISTEMA   ");
        System.out.println("===========================================");

        while (!sesionActiva) {
            System.out.println("\n--- INGRESE SUS CREDENCIALES ---");
            System.out.print("Usuario (o escriba 'salir' para apagar el sistema): ");
            String inputUser = scanner.nextLine();

            // Salida de seguridad del sistema.
            if (inputUser.equalsIgnoreCase("salir")) {
                System.out.println("\nApagando el sistema NutriExpress. Hasta pronto.");
                System.exit(0);
            }

            System.out.print("Contraseña: ");
            String inputPass = scanner.nextLine();

            Optional<Usuario> usuarioDb = usuarioRepo.findByUsername(inputUser);

            if (usuarioDb.isPresent()) {
                Usuario u = usuarioDb.get();

                if (u.getPassword().equals(inputPass)) {
                    System.out.println("\nInicio de Sesión Exitoso.");
                    System.out.println("Bienvenido, " + u.getUsername());
                    usuarioAutenticado = u;
                    sesionActiva = true;

                } else {
                    System.out.println("\nError: Contraseña incorrecta. Intente de nuevo.");

                }

            } else {
                System.out.println("\nError: El usuario '" + inputUser + "' no fue encontrado.");

            }

        }

        return usuarioAutenticado;

    }

}