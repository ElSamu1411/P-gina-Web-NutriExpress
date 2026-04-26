package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/catalogo")
@CrossOrigin(origins = "*")
public class CatalogoController {

    // Inyectamos el repositorio oficial
    @Autowired
    private SuplementoRepository suplementoRepository;

    @GetMapping
    public List<Suplemento> obtenerCatalogo() {
        // Traemos todos los suplementos directamente de PostgreSQL
        return suplementoRepository.findAll();
    }
}