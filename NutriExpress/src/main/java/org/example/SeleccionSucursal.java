package org.example;

import java.util.List;
import java.util.Scanner;

/**
 * Clase exclusiva para aislar la logica de seleccion geografica.
 * Asegura que el cliente elija una ubicacion valida antes de acceder a los productos.
 */
public class SeleccionSucursal {

    /**
     * @return Municipio - Retorna el objeto completo del municipio elegido para que el
     * Main sepa de que sucursal descontar el inventario posteriormente.
     */
    public Municipio ejecutar(Scanner scanner, EstadoRepository estadoRepo, MunicipioRepository municipioRepo) {
        System.out.println("\n========================================");
        System.out.println("        SELECCION DE SUCURSAL        ");
        System.out.println("========================================");

        // --- PASO 1: SELECCION DE ESTADO ---
        // Consultamos la base de datos para obtener todos los estados disponibles
        List<Estado> estados = estadoRepo.findAll();
        Estado estadoElegido = null;

        // Bucle de validacion: No avanza hasta que se elija un estado valido
        while (estadoElegido == null) {
            System.out.println("\nEstados disponibles:");
            for (int i = 0; i < estados.size(); i++) {
                System.out.println((i + 1) + ".- " + estados.get(i).getNombre());
            }

            System.out.print("\nIngresa el numero de tu Estado: ");

            try {
                int indexEstado = Integer.parseInt(scanner.nextLine());
                if (indexEstado >= 1 && indexEstado <= estados.size()) {
                    // Restamos 1 porque los indices de las listas en Java comienzan en 0
                    estadoElegido = estados.get(indexEstado - 1);
                } else {
                    System.out.println("[ERROR] Numero fuera de rango. Elige una opcion valida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("[ERROR] Entrada invalida. Por favor, ingresa solo numeros.");
            }
        }

        // --- PASO 2: SELECCION DE MUNICIPIO ---
        // Filtramos los municipios basandonos en el ID del estado seleccionado arriba
        List<Municipio> municipios = municipioRepo.findByIdEstado(estadoElegido.getIdEstado());
        Municipio municipioElegido = null;

        if (municipios.isEmpty()) {
            System.out.println("\n[ERROR]Lo sentimos, no hay sucursales en ese Estado aun.");
        } else {
            // Bucle de validacion: No avanza hasta que se elija un municipio valido
            while (municipioElegido == null) {
                System.out.println("\nMunicipios disponibles en " + estadoElegido.getNombre() + ":");
                for (int i = 0; i < municipios.size(); i++) {
                    System.out.println((i + 1) + ".- " + municipios.get(i).getNombre());
                }

                System.out.print("\nIngresa el numero de tu Municipio: ");
                try {
                    int indexMunicipio = Integer.parseInt(scanner.nextLine());
                    if (indexMunicipio >= 1 && indexMunicipio <= municipios.size()) {
                        municipioElegido = municipios.get(indexMunicipio - 1);
                    } else {
                        System.out.println("\n[ERROR] Numero fuera de rango. Elige una opcion valida.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("\n[ERROR] Entrada invalida. Por favor, ingresa solo numeros.");
                }
            }

            System.out.println("\n[OK] Ubicacion guardada: " + municipioElegido.getNombre() + ", " + estadoElegido.getNombre());
            System.out.println("\n[INFO] Buscando inventario local...");
        }

        // Retornamos el resultado para que el director (Main) lo administre
        return municipioElegido;
    }
}