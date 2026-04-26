package org.example;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Scanner;

/**
 * Clase principal que gestiona todas las operaciones que un cliente puede realizar.
 * Incluye consultas de catalogo y la logica transaccional del carrito de compras.
 */
public class GestorCliente {

    // Memoria temporal del carrito.
    // Llave (Key): ID del Suplemento. Valor (Value): Cantidad de unidades a comprar.
    private Map<Integer, Integer> carrito = new HashMap<>();

    /**
     * Motor principal del menu. Inyecta todas las dependencias necesarias para operar.
     */
    public void ejecutar(Scanner scanner, Usuario usuarioActual, Integer idSucursalActual, SuplementoRepository suplementoRepo, InventarioRepository inventarioRepo) {
        boolean salir = false;

        // Bucle infinito que mantiene al cliente en su panel hasta que decide cerrar sesion
        while (!salir) {
            System.out.println("\n===========================================");
            System.out.println("              PANEL DE CLIENTE             ");
            System.out.println("           Usuario activo: " + usuarioActual.getUsername());
            System.out.println("===========================================");
            System.out.println("1.- Buscar suplemento por nombre");
            System.out.println("2.- Buscar suplemento por categoria");
            System.out.println("3.- Buscar suplemento por marca");
            System.out.println("4.- Ordenar catalogo (Ascendente y Descendente)");
            System.out.println("5.- Consultar detalle de suplemento");
            System.out.println("6.- Consultar catalogo completo");
            System.out.println("7.- Carrito de compras y Checkout");
            System.out.println("8.- Regresar al Inicio de Sesion");

            System.out.print("\nElija una opcion: ");

            try {
                int opcion = Integer.parseInt(scanner.nextLine());

                // Delegacion de responsabilidades a metodos especificos segun la opcion
                switch (opcion) {
                    case 1: buscarPorNombre(scanner, suplementoRepo); break;
                    case 2: buscarPorCategoria(scanner, suplementoRepo); break;
                    case 3: buscarPorMarca(scanner, suplementoRepo); break;
                    case 4: ordenarCatalogo(scanner, suplementoRepo); break;
                    case 5: consultarDetalle(scanner, suplementoRepo); break;
                    case 6: consultarCatalogo(suplementoRepo); break;
                    case 7: gestionarCarrito(scanner, usuarioActual, idSucursalActual, suplementoRepo, inventarioRepo); break;
                    case 8:
                        System.out.println("\n[INFO] Cerrando panel del cliente...");
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
    // METODOS DE CONSULTA DE CATALOGO
    // ==========================================

    private void buscarPorNombre(Scanner scanner, SuplementoRepository suplementoRepo) {
        System.out.println("\n--- BUSQUEDA POR NOMBRE ---");
        System.out.print("Ingrese el nombre o palabra clave del suplemento: ");
        String busqueda = scanner.nextLine().trim();

        // Validacion para evitar busquedas en blanco que sobrecarguen la BD
        if (busqueda.isEmpty()) {
            System.out.println("[ERROR] El termino de busqueda no puede estar vacio.");
            return;
        }

        // Ejecuta consulta LIKE '%busqueda%' ignorando mayusculas
        List<Suplemento> resultados = suplementoRepo.findByNombreContainingIgnoreCaseAndActivoTrue(busqueda);

        if (resultados.isEmpty()) {
            System.out.println("\n[INFO] No se encontraron suplementos que coincidan con: '" + busqueda + "'");
        } else {
            System.out.println("\n[OK] Resultados encontrados (" + resultados.size() + "):");
            System.out.println("--------------------------------------------------------------");
            for (Suplemento sup : resultados) {
                System.out.printf("ID: %-3d | Nombre: %-28s | Marca: %-15s | Precio: $%.2f\n",
                        sup.getIdSuplemento(), sup.getNombre(), sup.getMarca(), sup.getPrecio());
            }
            System.out.println("--------------------------------------------------------------");
        }
    }

    private void buscarPorCategoria(Scanner scanner, SuplementoRepository suplementoRepo) {
        System.out.println("\n--- BUSQUEDA POR CATEGORIA ---");

        // Obtenemos categorias unicas (DISTINCT) para construir un menu dinamico
        List<String> categoriasDisponibles = suplementoRepo.findCategoriasActivas();

        if (categoriasDisponibles.isEmpty()) {
            System.out.println("\n[ERROR] Actualmente no hay categorias con suplementos disponibles.");
            return;
        }

        String categoriaElegida = null;

        while (categoriaElegida == null) {
            System.out.println("Categorias disponibles en catalogo:");
            for (int i = 0; i < categoriasDisponibles.size(); i++) {
                System.out.println((i + 1) + ".- " + categoriasDisponibles.get(i));
            }
            int opcionSalir = categoriasDisponibles.size() + 1;
            System.out.println(opcionSalir + ".- Regresar al menu principal");
            System.out.print("\nElija el numero de la categoria: ");

            try {
                int indexCategoria = Integer.parseInt(scanner.nextLine());
                if (indexCategoria >= 1 && indexCategoria <= categoriasDisponibles.size()) {
                    categoriaElegida = categoriasDisponibles.get(indexCategoria - 1);
                } else if (indexCategoria == opcionSalir) {
                    System.out.println("\n[INFO] Operacion cancelada. Regresando...");
                    return;
                } else {
                    System.out.println("\n[ERROR] Numero fuera de rango. Seleccione una opcion valida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("\n[ERROR] Entrada invalida. Por favor, ingrese solo numeros.");
            }
        }

        // Filtramos el catalogo por la categoria exacta elegida
        List<Suplemento> resultados = suplementoRepo.findByCategoriaIgnoreCaseAndActivoTrue(categoriaElegida);
        System.out.println("\n[OK] Resultados en la categoria '" + categoriaElegida + "' (" + resultados.size() + "):");
        System.out.println("--------------------------------------------------------------");
        for (Suplemento sup : resultados) {
            System.out.printf("ID: %-3d | Nombre: %-28s | Marca: %-15s | Precio: $%.2f\n",
                    sup.getIdSuplemento(), sup.getNombre(), sup.getMarca(), sup.getPrecio());
        }
        System.out.println("--------------------------------------------------------------");
    }

    private void buscarPorMarca(Scanner scanner, SuplementoRepository suplementoRepo) {
        System.out.println("\n--- BUSQUEDA POR MARCA ---");

        // Menu dinamico basado en las marcas actualmente registradas y activas
        List<String> marcasDisponibles = suplementoRepo.findMarcasActivas();

        if (marcasDisponibles.isEmpty()) {
            System.out.println("\n[ERROR] No hay marcas con productos disponibles actualmente.");
            return;
        }

        String marcaElegida = null;

        while (marcaElegida == null) {
            System.out.println("\nMarcas disponibles:");
            for (int i = 0; i < marcasDisponibles.size(); i++) {
                System.out.println((i + 1) + ".- " + marcasDisponibles.get(i));
            }
            int opcionSalir = marcasDisponibles.size() + 1;
            System.out.println(opcionSalir + ".- Regresar al menu principal");
            System.out.print("\nElija el numero de la marca: ");

            try {
                int indexMarca = Integer.parseInt(scanner.nextLine());
                if (indexMarca >= 1 && indexMarca <= marcasDisponibles.size()) {
                    marcaElegida = marcasDisponibles.get(indexMarca - 1);
                } else if (indexMarca == opcionSalir) {
                    System.out.println("[INFO] Operacion cancelada.");
                    return;
                } else {
                    System.out.println("\n[ERROR] Opcion fuera de rango.");
                }
            } catch (NumberFormatException e) {
                System.out.println("\n[ERROR] Entrada invalida. Ingrese solo numeros.");
            }
        }

        List<Suplemento> resultados = suplementoRepo.findByMarcaIgnoreCaseAndActivoTrue(marcaElegida);
        System.out.println("\n[OK] Resultados para la marca '" + marcaElegida + "' (" + resultados.size() + "):");
        System.out.println("--------------------------------------------------------------");
        for (Suplemento sup : resultados) {
            System.out.printf("ID: %-3d | Nombre: %-28s | Categoria: %-15s | Precio: $%.2f\n",
                    sup.getIdSuplemento(), sup.getNombre(), sup.getCategoria(), sup.getPrecio());
        }
        System.out.println("--------------------------------------------------------------");
    }

    private void ordenarCatalogo(Scanner scanner, SuplementoRepository suplementoRepo) {
        boolean opcionValida = false;
        List<Suplemento> resultados = null;
        String tipoOrden = "";

        while (!opcionValida) {
            System.out.println("\n--- ORDENAR CATALOGO POR PRECIO ---");
            System.out.println("1.- Menor a mayor precio (Ascendente)");
            System.out.println("2.- Mayor a menor precio (Descendente)");
            System.out.println("3.- Regresar al menu principal");
            System.out.print("\nElija una opcion de ordenamiento: ");

            try {
                int opcion = Integer.parseInt(scanner.nextLine());
                // Utilizamos las consultas auto-generadas de Spring Data (OrderBy)
                if (opcion == 1) {
                    resultados = suplementoRepo.findByActivoTrueOrderByPrecioAsc();
                    tipoOrden = "Menor a Mayor";
                    opcionValida = true;
                } else if (opcion == 2) {
                    resultados = suplementoRepo.findByActivoTrueOrderByPrecioDesc();
                    tipoOrden = "Mayor a Menor";
                    opcionValida = true;
                } else if (opcion == 3) {
                    System.out.println("[INFO] Operacion cancelada. Regresando...");
                    return;
                } else {
                    System.out.println("\n[ERROR] Opcion fuera de rango. Seleccione 1, 2 o 3.");
                }
            } catch (NumberFormatException e) {
                System.out.println("\n[ERROR] Entrada invalida. Por favor, ingrese solo numeros.");
            }
        }

        if (resultados != null && !resultados.isEmpty()) {
            System.out.println("\n[OK] Catalogo ordenado de " + tipoOrden + " (" + resultados.size() + " productos):");
            System.out.println("--------------------------------------------------------------");
            for (Suplemento sup : resultados) {
                System.out.printf("ID: %-3d | Nombre: %-28s | Categoria: %-15s | Precio: $%.2f\n",
                        sup.getIdSuplemento(), sup.getNombre(), sup.getCategoria(), sup.getPrecio());
            }
            System.out.println("--------------------------------------------------------------");
        } else {
            System.out.println("[INFO] El catalogo esta vacio o no hay productos activos para mostrar.");
        }
    }

    private void consultarDetalle(Scanner scanner, SuplementoRepository suplementoRepo) {
        System.out.println("\n--- CONSULTAR DETALLE DE SUPLEMENTO ---");
        List<Suplemento> disponibles = suplementoRepo.findByActivoTrue();

        if (disponibles.isEmpty()) {
            System.out.println("[INFO] Actualmente no hay productos disponibles en el catalogo.");
            return;
        }

        // Imprime un listado rapido para que el usuario no adivine los IDs
        System.out.println("\nProductos disponibles para consulta:");
        for (Suplemento sup : disponibles) {
            System.out.printf("ID: %-3d | %s\n", sup.getIdSuplemento(), sup.getNombre());
        }

        boolean idValido = false;

        while (!idValido) {
            System.out.print("\nIngrese el ID del suplemento (o escriba '0' para cancelar): ");
            try {
                int idBusqueda = Integer.parseInt(scanner.nextLine());
                if (idBusqueda == 0) {
                    System.out.println("[INFO] Operacion cancelada. Regresando al menu principal...");
                    return;
                }

                // Uso de Optional para evitar NullPointerExceptions si el ID no existe
                java.util.Optional<Suplemento> suplementoOpt = suplementoRepo.findById(idBusqueda);

                if (suplementoOpt.isPresent()) {
                    Suplemento suplemento = suplementoOpt.get();
                    // Solo mostramos detalles si la bandera logica 'activo' es verdadera
                    if (Boolean.TRUE.equals(suplemento.getActivo())) {
                        System.out.println("\nFICHA TECNICA DEL PRODUCTO");
                        System.out.println("-------------------------------------------");
                        System.out.println("ID:          " + suplemento.getIdSuplemento());
                        System.out.println("Nombre:      " + suplemento.getNombre());
                        System.out.println("Marca:       " + suplemento.getMarca());
                        System.out.println("Categoria:   " + suplemento.getCategoria());
                        System.out.println("Precio:      $" + String.format("%.2f", suplemento.getPrecio()));
                        System.out.println("Imagen Ref:  " + suplemento.getUrlImagen());
                        System.out.println("-------------------------------------------");
                        idValido = true;
                    } else {
                        System.out.println("[INFO] El producto con ID " + idBusqueda + " esta inactivo. Intente con otro.");
                    }
                } else {
                    System.out.println("\n[ERROR] ID " + idBusqueda + " no encontrado. Verifique la lista de arriba.");
                }
            } catch (NumberFormatException e) {
                System.out.println("\n[ERROR] Entrada invalida. Por favor, ingrese un numero entero.");
            }
        }
    }

    private void consultarCatalogo(SuplementoRepository suplementoRepo) {
        System.out.println("\n--- CATALOGO COMPLETO DE SUPLEMENTOS ---");
        List<Suplemento> catalogo = suplementoRepo.findByActivoTrue();

        if (catalogo.isEmpty()) {
            System.out.println("[INFO] El catalogo se encuentra vacio o sin productos activos en este momento.");
        } else {
            System.out.println("[OK] Mostrando " + catalogo.size() + " productos disponibles:");
            System.out.println("---------------------------------------------------------------------------------");
            for (Suplemento sup : catalogo) {
                System.out.printf("ID: %-3d | Nombre: %-28s | Marca: %-15s | Precio: $%.2f\n",
                        sup.getIdSuplemento(), sup.getNombre(), sup.getMarca(), sup.getPrecio());
            }
            System.out.println("---------------------------------------------------------------------------------");
            System.out.println("[INFO] Nota: Utilice la opcion 5 para ver los detalles tecnicos de un producto especifico.");
        }
    }

    // ==========================================
    // MODULO: CARRITO DE COMPRAS Y CHECKOUT
    // ==========================================

    private void gestionarCarrito(Scanner scanner, Usuario usuarioActual, Integer idSucursalActual, SuplementoRepository suplementoRepo, InventarioRepository inventarioRepo) {
        boolean salirCarrito = false;

        // Sub-menu especifico para la gestion del carrito
        while (!salirCarrito) {
            System.out.println("\n===========================================");
            System.out.println("            MI CARRITO DE COMPRAS         ");
            System.out.println("===========================================");
            System.out.println("Articulos en carrito: " + carrito.size());
            System.out.println("1.- Agregar o modificar articulos");
            System.out.println("2.- Ver y Modificar mi carrito ");
            System.out.println("3.- Realizar compra (Checkout)");
            System.out.println("4.- Regresar al menu principal");
            System.out.print("Elija una opcion: ");

            try {
                int opcion = Integer.parseInt(scanner.nextLine());
                switch (opcion) {
                    case 1:
                        agregarArticuloCarrito(scanner, idSucursalActual, suplementoRepo, inventarioRepo);
                        break;
                    case 2:
                        // Actualizamos para enviar las herramientas necesarias para modificar el carrito
                        verCarrito(scanner, idSucursalActual, suplementoRepo, inventarioRepo);
                        break;
                    case 3:
                        // Proteccion para no realizar checkout de carritos vacios
                        if (carrito.isEmpty()) {
                            System.out.println("\n[ERROR] Su carrito esta vacio. Agregue productos antes de comprar.");
                        } else {
                            realizarCompra(scanner, usuarioActual, idSucursalActual, suplementoRepo, inventarioRepo);
                        }
                        break;
                    case 4:
                        System.out.println("\n[INFO] Regresando al menu principal...");
                        salirCarrito = true;
                        break;
                    default:
                        System.out.println("\n[ERROR] Opcion fuera de rango.");
                }
            } catch (NumberFormatException e) {
                System.out.println("\n[ERROR] Entrada invalida.");
            }
        }
    }

    private void agregarArticuloCarrito(Scanner scanner, Integer idSucursalActual, SuplementoRepository suplementoRepo, InventarioRepository inventarioRepo) {
        System.out.println("\n--- AGREGAR PRODUCTOS AL CARRITO ---");

        List<Suplemento> disponibles = suplementoRepo.findByActivoTrue();

        System.out.println("Productos disponibles en esta sucursal:");
        System.out.println("-------------------------------------------------------------------------");
        for (Suplemento sup : disponibles) {
            // Consultamos el stock de cada producto en la sucursal actual
            int stockLocal = 0;
            java.util.Optional<Inventario> invOpt = inventarioRepo.findByIdSucursalAndIdSuplemento(idSucursalActual, sup.getIdSuplemento());

            if (invOpt.isPresent()) {
                stockLocal = invOpt.get().getStock();
            }

            // Imprimimos incluyendo la columna de Stock
            System.out.printf("ID: %-3d | Precio: $%-7.2f | Stock: %-3d | %s\n",
                    sup.getIdSuplemento(), sup.getPrecio(), stockLocal, sup.getNombre());
        }
        System.out.println("-------------------------------------------------------------------------");

        System.out.print("\nIngrese el ID del producto que desea agregar (o '0' para cancelar): ");

        try {
            int idBusqueda = Integer.parseInt(scanner.nextLine());
            if (idBusqueda == 0) return;

            // 1. Verificamos que el producto exista en el catalogo general
            suplementoRepo.findById(idBusqueda).ifPresentOrElse(suplemento -> {
                if (Boolean.TRUE.equals(suplemento.getActivo())) {

                    // 2. Verificamos el stock ESPECIFICO en la sucursal donde esta el cliente
                    java.util.Optional<Inventario> invOpt = inventarioRepo.findByIdSucursalAndIdSuplemento(idSucursalActual, idBusqueda);

                    if (invOpt.isPresent() && invOpt.get().getStock() > 0) {
                        int stockDisponible = invOpt.get().getStock();

                        // 3. Logica de insercion en el Map
                        if (carrito.containsKey(idBusqueda)) {
                            System.out.println("\n[INFO] Producto ya en carrito. Vaya al checkout o elimine el producto desde el menu.");
                        } else {
                            System.out.println("[INFO] Stock disponible en esta sucursal: " + stockDisponible + " unidades.");
                            System.out.print("Cuantas unidades deseas agregar?: ");
                            int cantidad = Integer.parseInt(scanner.nextLine());

                            // Validamos que no pida mas de lo que la tienda fisica tiene
                            if (cantidad > 0 && cantidad <= stockDisponible) {
                                carrito.put(idBusqueda, cantidad);
                                System.out.println("\n[OK] Producto agregado al carrito con exito.");
                            } else if (cantidad > stockDisponible) {
                                System.out.println("\n[ERROR] No hay suficiente stock. Maximo disponible: " + stockDisponible);
                            } else {
                                System.out.println("\n[ERROR] Cantidad invalida.");
                            }
                        }
                    } else {
                        System.out.println("\n[ERROR] El producto '" + suplemento.getNombre() + "' esta AGOTADO en esta sucursal.");
                    }
                }
            }, () -> System.out.println("\n[ERROR] ID de producto no encontrado."));
        } catch (NumberFormatException e) {
            System.out.println("\n[ERROR] Entrada invalida.");
        }
    }

    private void verCarrito(Scanner scanner, Integer idSucursalActual, SuplementoRepository suplementoRepo, InventarioRepository inventarioRepo) {
        System.out.println("\n--- LISTA DE MI CARRITO ---");
        if (carrito.isEmpty()) {
            System.out.println("\n[INFO] Su carrito esta vacio.");
            return;
        }

        System.out.println("-------------------------------------------------------------------------");
        for (Map.Entry<Integer, Integer> entry : carrito.entrySet()) {
            int id = entry.getKey();
            int cantidad = entry.getValue();

            suplementoRepo.findById(id).ifPresent(sup -> {
                double subtotalProducto = sup.getPrecio() * cantidad;
                System.out.printf("ID: %-3d | Cant: %-3d | %-25s | P. Unitario: $%-7.2f | Subtotal: $%.2f\n",
                        id, cantidad, sup.getNombre(), sup.getPrecio(), subtotalProducto);
            });
        }
        System.out.println("-------------------------------------------------------------------------");

        // --- NUEVA LOGICA DE MODIFICACION ---
        System.out.print("\nIngrese el ID del producto que desea modificar (o '0' para regresar): ");
        try {
            int idModificar = Integer.parseInt(scanner.nextLine());
            if (idModificar == 0) return;

            if (!carrito.containsKey(idModificar)) {
                System.out.println("\n[ERROR] El ID ingresado no se encuentra en su carrito.");
                return;
            }

            int cantidadActual = carrito.get(idModificar);
            System.out.println("\nQue desea hacer con este producto?");
            System.out.println("1.- Agregar unidades");
            System.out.println("2.- Quitar unidades");
            System.out.println("3.- Eliminar por completo del carrito");
            System.out.println("4.- Cancelar");
            System.out.print("\nOpcion: ");

            int accion = Integer.parseInt(scanner.nextLine());

            if (accion == 1) {
                System.out.print("\nCuantas unidades EXTRA desea agregar?: ");
                int extra = Integer.parseInt(scanner.nextLine());

                // Validamos el stock disponible en la sucursal
                java.util.Optional<Inventario> invOpt = inventarioRepo.findByIdSucursalAndIdSuplemento(idSucursalActual, idModificar);
                if (invOpt.isPresent()) {
                    int stockDisponible = invOpt.get().getStock();
                    if (cantidadActual + extra <= stockDisponible) {
                        carrito.put(idModificar, cantidadActual + extra);
                        System.out.println("[OK] Carrito actualizado con exito.");
                    } else {
                        System.out.println("\n[ERROR] Stock insuficiente. Solo quedan " + stockDisponible + " unidades en la sucursal.");
                    }
                }
            } else if (accion == 2) {
                System.out.print("\nCuantas unidades desea QUITAR?: ");
                int menos = Integer.parseInt(scanner.nextLine());

                if (cantidadActual - menos < 1) {
                    System.out.println("\n[ERROR] No puede reducir a menos de 1 unidad. Si desea eliminarlo, use la opcion 3.");
                } else {
                    carrito.put(idModificar, cantidadActual - menos);
                    System.out.println("\n[OK] Carrito actualizado con exito.");
                }
            } else if (accion == 3) {
                carrito.remove(idModificar);
                System.out.println("\n[OK] Producto eliminado del carrito.");

            } else if (accion == 4) {
                System.out.println("\n[INFO] Operacion cancelada.");

            } else {
                System.out.println("\n[ERROR] Opción no valida.");
            }

        } catch (NumberFormatException e) {
            System.out.println("\n[ERROR] Entrada invalida. Ingrese solo numeros.");
        }
    }

    private void realizarCompra(Scanner scanner, Usuario usuarioActual, Integer idSucursalActual, SuplementoRepository suplementoRepo, InventarioRepository inventarioRepo) {
        System.out.println("\n===========================================");
        System.out.println("             PANTALLA DE PAGO              ");
        System.out.println("===========================================");

        double subtotalEnvio = 99.00;
        double totalProductos = 0.0;

        System.out.println("RESUMEN DE COMPRA:");
        System.out.println("Cliente: " + usuarioActual.getUsername());
        System.out.println("-------------------------------------------");

        // Calculo de totales antes de procesar el pago
        for (Map.Entry<Integer, Integer> entry : carrito.entrySet()) {
            Suplemento sup = suplementoRepo.findById(entry.getKey()).orElse(null);
            if (sup != null) {
                double subtotal = sup.getPrecio() * entry.getValue();
                totalProductos += subtotal;
                System.out.printf("%d x %s ($%.2f c/u) = $%.2f\n", entry.getValue(), sup.getNombre(), sup.getPrecio(), subtotal);
            }
        }

        double totalPagar = totalProductos + subtotalEnvio;

        System.out.println("-------------------------------------------");
        System.out.printf("Subtotal Productos:  $%.2f\n", totalProductos);
        System.out.printf("Costo de Envio:      $%.2f\n", subtotalEnvio);
        System.out.printf("TOTAL A PAGAR:       $%.2f\n", totalPagar);
        System.out.println("===========================================");

        System.out.println("\nDesea confirmar su compra?");
        System.out.println("1.- SI, Autorizar pago");
        System.out.println("2.- NO, Cancelar y regresar");
        System.out.print("\nOpcion: ");

        try {
            int confirmacion = Integer.parseInt(scanner.nextLine());
            if (confirmacion == 1) {
                System.out.println("\n[OK] Procesando pago y descontando stock local...");

                // FASE TRANSACCIONAL: Se actualiza la base de datos real
                for (Map.Entry<Integer, Integer> entry : carrito.entrySet()) {
                    int idProducto = entry.getKey();
                    int cantidadComprada = entry.getValue();

                    // Se descuenta unicamente del inventario vinculado a la sucursal activa
                    inventarioRepo.findByIdSucursalAndIdSuplemento(idSucursalActual, idProducto).ifPresent(inv -> {
                        inv.setStock(inv.getStock() - cantidadComprada);
                        inventarioRepo.save(inv);
                    });
                }

                System.out.println("\n===========================================");
                System.out.println("               FACTURA GENERADA            ");
                System.out.println("===========================================");
                System.out.println("Comercio: NutriExpress Oficial");
                System.out.println("Cliente: " + usuarioActual.getUsername());
                System.out.println("Estado: PAGADO");
                System.out.println("Total: $" + String.format("%.2f", totalPagar));
                System.out.println("-------------------------------------------");
                System.out.println("Gracias por su preferencia. Su pedido sera");
                System.out.println("empaquetado y enviado a la brevedad.");
                System.out.println("===========================================");

                // Se vacia la memoria temporal para prepararla para la siguiente transaccion
                carrito.clear();
            } else {
                System.out.println("\n[INFO] Compra cancelada. Sus productos siguen en el carrito.");
            }
        } catch (NumberFormatException e) {
            System.out.println("\n[ERROR] Opcion invalida. Compra cancelada por seguridad.");
        }
    }
}