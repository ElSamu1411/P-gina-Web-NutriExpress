package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private SuplementoRepository suplementoRepository;

    private final String RUTA_CARPETA_IMG = "E:/Proyecto NutriExpress/NutriExpress/IMG/";

    // =================================================================
    // 1. EL MÉTODO PARA CREAR Y EDITAR (EL QUE SE QUEDA)
    // =================================================================
    @PostMapping("/suplementos")
    public Map<String, String> crearOEditarSuplemento(
            @RequestParam(value = "id", required = false) Integer id,
            @RequestParam("nombre") String nombre,
            @RequestParam("marca") String marca,
            @RequestParam("categoria") String categoria,
            @RequestParam("precio") Double precio,
            @RequestParam(value = "imagen", required = false) MultipartFile archivoImagen) {

        Map<String, String> respuesta = new HashMap<>();

        try {
            Suplemento suplemento;

            if (id != null) {
                suplemento = suplementoRepository.findById(id).orElse(new Suplemento());
            } else {
                suplemento = new Suplemento();
                suplemento.setActivo(true);
            }

            suplemento.setNombre(nombre);
            suplemento.setMarca(marca);
            suplemento.setCategoria(categoria);
            suplemento.setPrecio(precio);

            if (archivoImagen != null && !archivoImagen.isEmpty()) {
                File directorio = new File(RUTA_CARPETA_IMG);
                if (!directorio.exists()) directorio.mkdirs();

                String nombreArchivo = System.currentTimeMillis() + "_" + archivoImagen.getOriginalFilename();
                Files.write(Paths.get(RUTA_CARPETA_IMG + nombreArchivo), archivoImagen.getBytes());
                suplemento.setUrlImagen("../IMG/" + nombreArchivo);
            } else if (id == null) {
                respuesta.put("status", "error");
                respuesta.put("mensaje", "Debes incluir una imagen para un producto nuevo.");
                return respuesta;
            }

            suplementoRepository.save(suplemento);
            respuesta.put("status", "exito");
            respuesta.put("mensaje", "Producto guardado correctamente.");

        } catch (IOException e) {
            respuesta.put("status", "error");
            respuesta.put("mensaje", "Error al procesar: " + e.getMessage());
        }

        return respuesta;
    }

    // =================================================================
    // 2. EL MÉTODO PARA BORRAR
    // =================================================================
    @DeleteMapping("/suplementos/{id}")
    public Map<String, String> eliminarSuplemento(@PathVariable Integer id, @RequestParam("tipo") String tipoBaja) {
        Map<String, String> respuesta = new HashMap<>();

        try {
            if ("global".equals(tipoBaja)) {
                Suplemento suplemento = suplementoRepository.findById(id).orElse(null);
                if (suplemento != null) {
                    suplemento.setActivo(false);
                    suplementoRepository.save(suplemento);

                    respuesta.put("status", "exito");
                    respuesta.put("mensaje", "Baja global exitosa. El producto ya no aparecerá en el catálogo.");
                } else {
                    respuesta.put("status", "error");
                    respuesta.put("mensaje", "Suplemento no encontrado.");
                }
            } else {
                respuesta.put("status", "exito");
                respuesta.put("mensaje", "Baja en sucursal exitosa (Base de datos de inventario pendiente de conectar).");
            }
        } catch (Exception e) {
            respuesta.put("status", "error");
            respuesta.put("mensaje", "Ocurrió un error: " + e.getMessage());
        }

        return respuesta;
    }

    // =================================================================
    // 3. NUEVO ENDPOINT PARA BUSCAR UN SOLO PRODUCTO POR SU ID
    // =================================================================
    @GetMapping("/suplementos/{id}")
    public Suplemento obtenerSuplementoPorId(@PathVariable Integer id) {
        // Busca el producto en la base de datos y lo devuelve. Si no existe, devuelve nulo.
        return suplementoRepository.findById(id).orElse(null);

    }

}