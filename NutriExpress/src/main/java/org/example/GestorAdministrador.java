package org.example;

import java.util.List;
import java.util.Scanner;

/**
 * Clase exclusiva para el manejo de operaciones administrativas.
 * Tiene permisos de escritura y modificacion profunda en la base de datos.
 */
public class GestorAdministrador {

    public void ejecutar(Scanner scanner, Usuario usuarioActual, SuplementoRepository suplementoRepo, SucursalRepository sucursalRepo, InventarioRepository inventarioRepo, EstadoRepository estadoRepo, MunicipioRepository municipioRepo) {
        boolean salir = false;

        while (!salir) {
            System.out.println("\n===========================================");
            System.out.println("           PANEL DE ADMINISTRADOR          ");
            System.out.println("          Admin activo: " + usuarioActual.getUsername());
            System.out.println("===========================================");
            System.out.println("1.- Dar de alta un nuevo suplemento");
            System.out.println("2.- Dar de baja un suplemento");
            System.out.println("3.- Modificar datos de un suplemento");
            System.out.println("4.- Gestionar ingreso de stock a sucursal");
            System.out.println("5.- Consultar catalogo global (Reporte)");
            System.out.println("6.- Cerrar Sesion");

            System.out.print("\nElija una opcion: ");

            try {
                int opcion = Integer.parseInt(scanner.nextLine());

                switch (opcion) {
                    case 1:
                        registrarNuevoSuplemento(scanner, suplementoRepo, sucursalRepo, inventarioRepo);
                        break;
                    case 2:
                        darDeBajaSuplemento(scanner, suplementoRepo);
                        break;
                    case 3:
                        modificarSuplemento(scanner, suplementoRepo);
                        break;
                    case 4:
                        gestionarInventarioSucursales(scanner, suplementoRepo, sucursalRepo, inventarioRepo, estadoRepo, municipioRepo);
                        break;
                    case 5:
                        consultarCatalogoGlobal(suplementoRepo, inventarioRepo);
                        break;
                    case 6:
                        System.out.println("\n[INFO] Cerrando panel de administrador...");
                        salir = true;
                        break;
                    default:
                        System.out.println("\n[ERROR] Opcion no valida. Intente de nuevo.");
                }

            } catch (NumberFormatException e) {

                System.out.println("\n[ERROR] Entrada invalida. Por favor, ingrese un numero entero.");
            }

        }

    }

    // ==========================================
    // METODOS DEL ADMINISTRADOR
    // ==========================================

    private void registrarNuevoSuplemento(Scanner scanner, SuplementoRepository suplementoRepo, SucursalRepository sucursalRepo, InventarioRepository inventarioRepo) {
        System.out.println("\n--- ALTA DE NUEVO SUPLEMENTO ---");
        System.out.println("[INFO] Ingrese los datos del nuevo producto. Escriba 'cancelar' en el nombre para abortar.");

        // 1. Validacion de Nombre
        String nombre = "";
        while (nombre.isEmpty()) {
            System.out.print("Nombre del suplemento: ");
            nombre = scanner.nextLine().trim();
            if (nombre.equalsIgnoreCase("cancelar")) {
                System.out.println("\n[INFO] Alta cancelada.");
                return;
            }
            if (nombre.isEmpty()) {
                System.out.println("\n[ERROR] El nombre no puede estar vacio.");
            }
        }

        // 2. Validacion de Marca
        String marca = "";

        while (marca.isEmpty()) {
            System.out.print("Marca (Ej. Muscletech): ");
            marca = scanner.nextLine().trim();
            if (marca.isEmpty()) System.out.println("\n[ERROR] La marca no puede estar vacia.");

        }

        // 3. Validacion de Categoria
        String categoria = "";
        while (categoria.isEmpty()) {
            System.out.print("Categoria (Ej. Proteina, Creatina): ");
            categoria = scanner.nextLine().trim();
            if (categoria.isEmpty()) System.out.println("\n[ERROR] La categoria no puede estar vacia.");

        }

        // 4. Validacion de Precio
        double precio = -1;
        while (precio <= 0) {
            System.out.print("Precio de venta: $");
            try {
                precio = Double.parseDouble(scanner.nextLine());
                if (precio <= 0) {
                    System.out.println("\n[ERROR] El precio debe ser mayor a $0.00");
                }
            } catch (NumberFormatException e) {
                System.out.println("\n[ERROR] Entrada invalida. Ingrese un numero decimal (Ej. 599.50).");
            }

        }

        // 5. Insercion de Imagen (Opcional, puede quedar en blanco)
        System.out.print("URL de la imagen (Presione Enter para omitir): ");
        String urlImagen = scanner.nextLine().trim();
        if (urlImagen.isEmpty()) {
            urlImagen = "sin_imagen.jpg"; // Valor por defecto
        }

        // --- FASE TRANSACCIONAL ---
        try {

            // Paso A: Guardar el nuevo suplemento en el catalogo global
            Suplemento nuevoSuplemento = new Suplemento();
            nuevoSuplemento.setNombre(nombre);
            nuevoSuplemento.setMarca(marca);
            nuevoSuplemento.setCategoria(categoria);
            nuevoSuplemento.setPrecio(precio);
            nuevoSuplemento.setUrlImagen(urlImagen);
            nuevoSuplemento.setActivo(true); // Siempre activo al crearse.

            Suplemento suplementoGuardado = suplementoRepo.save(nuevoSuplemento);

            // Paso B: Generar el inventario inicial (0 unidades) en TODAS las sucursales fisicas
            List<Sucursal> sucursales = sucursalRepo.findAll();
            for (Sucursal sucursal : sucursales) {
                Inventario nuevoInventario = new Inventario();
                nuevoInventario.setIdSucursal(sucursal.getIdSucursal());
                nuevoInventario.setIdSuplemento(suplementoGuardado.getIdSuplemento());
                nuevoInventario.setStock(0); // Comienza sin stock hasta que el admin le asigne mercancia

                inventarioRepo.save(nuevoInventario);
            }

            System.out.println("\n[OK] Producto registrado exitosamente con el ID: " + suplementoGuardado.getIdSuplemento());
            System.out.println("[OK] Registros de inventario inicializados en " + sucursales.size() + " sucursales (Stock 0).");

        } catch (Exception e) {
            System.out.println("\n[ERROR] Ocurrio un problema al guardar en la base de datos: " + e.getMessage());
        }

    }

    private void darDeBajaSuplemento(Scanner scanner, SuplementoRepository suplementoRepo) {

        System.out.println("\n--- BAJA LÓGICA DE SUPLEMENTO ---");

        // 1. Mostrar solo los suplementos que actualmente estan activos
        List<Suplemento> activos = suplementoRepo.findByActivoTrue();

        if (activos.isEmpty()) {
            System.out.println("[INFO] No hay suplementos activos en el catalogo en este momento.");
            return;
        }

        System.out.println("Suplementos actualmente activos:");
        System.out.println("-------------------------------------------------------------------------");
        for (Suplemento sup : activos) {
            System.out.printf("ID: %-3d | %s\n", sup.getIdSuplemento(), sup.getNombre());
        }
        System.out.println("-------------------------------------------------------------------------");

        System.out.print("\nIngrese el ID del suplemento que desea dar de BAJA (o '0' para cancelar): ");

        try {

            int idBaja = Integer.parseInt(scanner.nextLine());

            if (idBaja == 0) {
                System.out.println("[INFO] Operacion cancelada. Regresando al menu...");
                return;
            }

            // 2. Buscamos el suplemento en la base de datos
            suplementoRepo.findById(idBaja).ifPresentOrElse(suplemento -> {

                // Verificamos si realmente esta activo
                if (Boolean.TRUE.equals(suplemento.getActivo())) {

                    // Doble confirmacion por seguridad
                    System.out.println("\nEsta a punto de dar de baja: '" + suplemento.getNombre() + "'");
                    System.out.println("Este producto dejara de ser visible para los clientes.");
                    System.out.print("¿Esta seguro? (1 = SI / 2 = NO): ");

                    try {
                        int confirmacion = Integer.parseInt(scanner.nextLine());
                        if (confirmacion == 1) {
                            // Aplicamos la Baja Logica
                            suplemento.setActivo(false);
                            suplementoRepo.save(suplemento); // Guardamos la actualizacion en la BD
                            System.out.println("\n[OK] El producto ha sido dado de baja exitosamente.");
                        } else {
                            System.out.println("\n[INFO] Operacion cancelada.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("\n[ERROR] Entrada invalida. Operacion abortada por seguridad.");
                    }

                } else {
                    System.out.println("\n[INFO] El suplemento seleccionado YA se encuentra dado de baja.");
                }

            }, () -> System.out.println("\n[ERROR] No se encontro ningun suplemento con el ID: " + idBaja));

        } catch (NumberFormatException e) {
            System.out.println("\n[ERROR] Entrada invalida. Por favor, ingrese un numero entero.");

        }

    }

    private void modificarSuplemento(Scanner scanner, SuplementoRepository suplementoRepo) {
        System.out.println("\n--- MODIFICAR DATOS DE SUPLEMENTO ---");

        // 1. Mostramos TODOS los productos (Activos e Inactivos) porque un admin podria querer corregir/reactivar uno dado de baja
        List<Suplemento> catalogoTotal = suplementoRepo.findAll();

        if (catalogoTotal.isEmpty()) {
            System.out.println("[INFO] El catalogo esta completamente vacio. No hay nada que modificar.");
            return;
        }

        System.out.println("Catalogo Global (Activos e Inactivos):");
        System.out.println("-------------------------------------------------------------------------");
        for (Suplemento sup : catalogoTotal) {
            String estado = sup.getActivo() ? "[ACTIVO]" : "[INACTIVO]";
            System.out.printf("ID: %-3d | %-10s | %s\n", sup.getIdSuplemento(), estado, sup.getNombre());
        }
        System.out.println("-------------------------------------------------------------------------");

        System.out.print("\nIngrese el ID del suplemento a modificar (o '0' para cancelar): ");

        try {
            int idModificar = Integer.parseInt(scanner.nextLine());

            if (idModificar == 0) {
                System.out.println("[INFO] Operacion cancelada. Regresando al menu...");
                return;
            }

            // 2. Buscamos el producto en la BD
            suplementoRepo.findById(idModificar).ifPresentOrElse(suplemento -> {

                boolean salirEdicion = false;

                // 3. Sub-menu de edicion (Se mantiene aqui hasta que decida guardar y salir)
                while (!salirEdicion) {
                    System.out.println("\n--- EDITANDO: " + suplemento.getNombre() + " ---");
                    System.out.println("1.- Modificar Nombre     (Actual: " + suplemento.getNombre() + ")");
                    System.out.println("2.- Modificar Marca      (Actual: " + suplemento.getMarca() + ")");
                    System.out.println("3.- Modificar Categoria  (Actual: " + suplemento.getCategoria() + ")");
                    System.out.println("4.- Modificar Precio     (Actual: $" + String.format("%.2f", suplemento.getPrecio()) + ")");
                    System.out.println("5.- Cambiar Estado       (Actual: " + (suplemento.getActivo() ? "ACTIVO" : "INACTIVO") + ")");
                    System.out.println("6.- Terminar edicion y regresar");
                    System.out.print("Elija el campo a modificar: ");

                    try {
                        int opcionEdicion = Integer.parseInt(scanner.nextLine());

                        switch (opcionEdicion) {
                            case 1:
                                System.out.print("Nuevo Nombre: ");
                                String nuevoNombre = scanner.nextLine().trim();
                                if (!nuevoNombre.isEmpty()) {
                                    suplemento.setNombre(nuevoNombre);
                                    System.out.println("[OK] Nombre actualizado en memoria.");
                                } else {
                                    System.out.println("\n[ERROR] El nombre no puede quedar vacio.");
                                }
                                break;
                            case 2:
                                System.out.print("Nueva Marca: ");
                                String nuevaMarca = scanner.nextLine().trim();
                                if (!nuevaMarca.isEmpty()) {
                                    suplemento.setMarca(nuevaMarca);
                                    System.out.println("[OK] Marca actualizada en memoria.");
                                } else {
                                    System.out.println("\n[ERROR] La marca no puede quedar vacia.");
                                }
                                break;
                            case 3:
                                System.out.print("Nueva Categoria: ");
                                String nuevaCat = scanner.nextLine().trim();
                                if (!nuevaCat.isEmpty()) {
                                    suplemento.setCategoria(nuevaCat);
                                    System.out.println("[OK] Categoria actualizada en memoria.");
                                } else {
                                    System.out.println("\n[ERROR] La categoria no puede quedar vacia.");
                                }
                                break;
                            case 4:
                                System.out.print("Nuevo Precio: $");
                                try {
                                    double nuevoPrecio = Double.parseDouble(scanner.nextLine());
                                    if (nuevoPrecio > 0) {
                                        suplemento.setPrecio(nuevoPrecio);
                                        System.out.println("[OK] Precio actualizado en memoria.");
                                    } else {
                                        System.out.println("\n[ERROR] El precio debe ser mayor a $0.00");
                                    }
                                } catch (NumberFormatException e) {
                                    System.out.println("\n[ERROR] Entrada invalida. Ingrese un numero decimal.");
                                }
                                break;
                            case 5:
                                // Logica de Toggle (Interruptor): Si es true lo hace false, y viceversa
                                suplemento.setActivo(!suplemento.getActivo());
                                System.out.println("[OK] Estado cambiado a: " + (suplemento.getActivo() ? "ACTIVO" : "INACTIVO"));
                                break;
                            case 6:
                                // Guardamos los cambios finales en la base de datos
                                suplementoRepo.save(suplemento);
                                System.out.println("\n[OK] Todos los cambios fueron guardados exitosamente en la base de datos.");
                                salirEdicion = true;
                                break;
                            default:
                                System.out.println("\n[ERROR] Opcion de edicion no valida.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("\n[ERROR] Entrada invalida. Ingrese un numero entero.");
                    }
                }

            }, () -> System.out.println("\n[ERROR] No se encontro ningun suplemento con el ID: " + idModificar));

        } catch (NumberFormatException e) {
            System.out.println("\n[ERROR] Entrada invalida. Por favor, ingrese un numero entero.");

        }

    }

    private void gestionarInventarioSucursales(Scanner scanner, SuplementoRepository suplementoRepo, SucursalRepository sucursalRepo, InventarioRepository inventarioRepo, EstadoRepository estadoRepo, MunicipioRepository municipioRepo) {
        System.out.println("\n--- INGRESO Y GESTION DE MERCANCIA ---");

        // --- MEJORA 1: FILTRO GEOGRAFICO REUTILIZANDO CODIGO ---
        // Instanciamos la clase que creaste para el cliente, ¡es perfectamente reutilizable!
        SeleccionSucursal seleccion = new SeleccionSucursal();
        Municipio municipioAdmin = seleccion.ejecutar(scanner, estadoRepo, municipioRepo);

        if (municipioAdmin == null) return;

        // Buscamos las sucursales especificas de ESE municipio
        List<Sucursal> sucursales = sucursalRepo.findByIdMunicipio(municipioAdmin.getIdMunicipio());

        if (sucursales.isEmpty()) {
            System.out.println("\n[ERROR] No hay tiendas fisicas construidas en " + municipioAdmin.getNombre() + " todavia.");
            return;
        }

        Sucursal sucursalElegida = null;

        // Si solo hay 1 sucursal en ese municipio, la selecciona en automatico para ahorrar tiempo
        if (sucursales.size() == 1) {
            sucursalElegida = sucursales.get(0);
            System.out.println("\n[INFO] Sucursal detectada: " + sucursalElegida.getNombreSucursal());
        } else {
            // Si hay mas de 1, le pregunta al admin cual de todas
            while (sucursalElegida == null) {
                System.out.println("\nSucursales en " + municipioAdmin.getNombre() + ":");
                for (int i = 0; i < sucursales.size(); i++) {
                    System.out.println((i + 1) + ".- " + sucursales.get(i).getNombreSucursal());
                }
                System.out.print("\nElija la sucursal: ");
                try {
                    int indexSuc = Integer.parseInt(scanner.nextLine());
                    if (indexSuc >= 1 && indexSuc <= sucursales.size()) {
                        sucursalElegida = sucursales.get(indexSuc - 1);
                    } else {
                        System.out.println("\n[ERROR] Opcion fuera de rango.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("\n[ERROR] Entrada invalida.");
                }
            }
        }

        // --- 2. SELECCION DE PRODUCTO ---
        List<Suplemento> catalogo = suplementoRepo.findAll();
        System.out.println("\nCatalogo Global de Productos:");
        System.out.println("-------------------------------------------------------------------------");
        for (Suplemento sup : catalogo) {
            System.out.printf("ID: %-3d | %-25s | Activo: %b\n", sup.getIdSuplemento(), sup.getNombre(), sup.getActivo());
        }
        System.out.println("-------------------------------------------------------------------------");

        Suplemento productoElegido = null;
        while (productoElegido == null) {
            System.out.print("\nIngrese el ID del producto a gestionar (o '0' para cancelar): ");
            try {
                int idProd = Integer.parseInt(scanner.nextLine());
                if (idProd == 0) return;

                productoElegido = catalogo.stream().filter(p -> p.getIdSuplemento() == idProd).findFirst().orElse(null);

                if (productoElegido == null) {
                    System.out.println("\n[ERROR] ID de producto no encontrado.");
                }
            } catch (NumberFormatException e) {
                System.out.println("\n[ERROR] Ingrese un numero valido.");
            }
        }

        // --- 3. EXTRACCION DE INVENTARIO ---
        java.util.Optional<Inventario> invOpt = inventarioRepo.findByIdSucursalAndIdSuplemento(sucursalElegida.getIdSucursal(), productoElegido.getIdSuplemento());
        Inventario inventarioActual;

        if (invOpt.isPresent()) {
            inventarioActual = invOpt.get();
        } else {
            inventarioActual = new Inventario();
            inventarioActual.setIdSucursal(sucursalElegida.getIdSucursal());
            inventarioActual.setIdSuplemento(productoElegido.getIdSuplemento());
            inventarioActual.setStock(0);
        }

        System.out.println("\n[INFO] Stock actual de '" + productoElegido.getNombre() + "' en " + sucursalElegida.getNombreSucursal() + ": " + inventarioActual.getStock() + " unidades.");

        // --- MEJORA 2: SUBMENU INTUITIVO DE SUMA/RESTA ---
        boolean guardado = false;
        while (!guardado) {
            System.out.println("\n¿Que accion desea realizar?");
            System.out.println("1.- Ingresar nueva mercancia (Sumar stock)");
            System.out.println("2.- Registrar merma o daño   (Restar stock)");
            System.out.println("3.- Cancelar operacion");
            System.out.print("\nOpcion: ");

            try {
                int accion = Integer.parseInt(scanner.nextLine());

                if (accion == 3) {
                    System.out.println("\n[INFO] Operacion cancelada. No se modifico el stock.");
                    guardado = true;
                } else if (accion == 1) {
                    System.out.print("Ingrese la cantidad de botes a SUMAR: ");
                    int suma = Integer.parseInt(scanner.nextLine());
                    if (suma > 0) {
                        inventarioActual.setStock(inventarioActual.getStock() + suma);
                        inventarioRepo.save(inventarioActual);
                        System.out.println("\n[OK] Ingreso registrado. Nuevo stock total: " + inventarioActual.getStock() + " unidades.");
                        guardado = true;
                    } else {
                        System.out.println("\n[ERROR] Debe ingresar un numero mayor a 0.");
                    }
                } else if (accion == 2) {
                    System.out.print("Ingrese la cantidad de botes a RESTAR: ");
                    int resta = Integer.parseInt(scanner.nextLine());
                    if (resta > 0) {
                        if (inventarioActual.getStock() - resta >= 0) {
                            inventarioActual.setStock(inventarioActual.getStock() - resta);
                            inventarioRepo.save(inventarioActual);
                            System.out.println("\n[OK] Merma registrada. Nuevo stock total: " + inventarioActual.getStock() + " unidades.");
                            guardado = true;
                        } else {
                            System.out.println("\n[ERROR] Operacion bloqueada. No puede restar mas de lo que hay en existencia (" + inventarioActual.getStock() + ").");
                        }
                    } else {
                        System.out.println("\n[ERROR] Debe ingresar un numero mayor a 0.");
                    }
                } else {
                    System.out.println("\n[ERROR] Opcion no valida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("\n[ERROR] Entrada invalida. Use solo numeros enteros.");
            }

        }

    }

    private void consultarCatalogoGlobal(SuplementoRepository suplementoRepo, InventarioRepository inventarioRepo) {
        System.out.println("\n--- REPORTE GLOBAL DE CATALOGO (VISTA ADMINISTRATIVA) ---");

        // 1. Extraemos absolutamente todos los registros de la base de datos
        List<Suplemento> catalogoTotal = suplementoRepo.findAll();

        if (catalogoTotal.isEmpty()) {
            System.out.println("[INFO] El catalogo de la base de datos esta completamente vacio.");
            return;
        }

        // Encabezado de la tabla
        System.out.println("-------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-4s | %-10s | %-35s | %-15s | %-10s | %-15s\n", "ID", "ESTADO", "NOMBRE DEL PRODUCTO", "MARCA", "PRECIO", "STOCK NACIONAL");
        System.out.println("-------------------------------------------------------------------------------------------------------------------");

        for (Suplemento sup : catalogoTotal) {
            // Convertimos el booleano en una etiqueta visual clara
            String estado = sup.getActivo() ? "[ACTIVO]" : "[INACTIVO]";

            // 2. Calculo del Stock Nacional
            // Buscamos todos los registros de inventario de este producto sin importar la sucursal
            List<Inventario> inventarios = inventarioRepo.findByIdSuplemento(sup.getIdSuplemento());

            // Usamos un Stream de Java para sumar rapidamente la columna "stock" de todos los registros encontrados
            int stockGlobal = inventarios.stream().mapToInt(Inventario::getStock).sum();

            // 3. Formateo de texto para evitar que nombres muy largos rompan la tabla
            String nombreFormateado = sup.getNombre();
            if (nombreFormateado.length() > 32) {
                nombreFormateado = nombreFormateado.substring(0, 29) + "...";
            }

            // Impresion de la fila
            System.out.printf("%-4d | %-10s | %-35s | %-15s | $%-9.2f | %-15d\n",
                    sup.getIdSuplemento(), estado, nombreFormateado, sup.getMarca(), sup.getPrecio(), stockGlobal);
        }

        System.out.println("-------------------------------------------------------------------------------------------------------------------");
        System.out.println("\nINFO] Total de productos registrados en el sistema: " + catalogoTotal.size());
    }

} // Cierre de la Clase.



