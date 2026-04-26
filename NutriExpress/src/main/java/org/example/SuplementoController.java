package org.example;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/suplementos") // Esta es la URL base
public class SuplementoController {

    private final SuplementoRepository suplementoRepo;

    // Inyectamos el repositorio que ya tenías creado
    public SuplementoController(SuplementoRepository suplementoRepo) {
        this.suplementoRepo = suplementoRepo;
    }

    // Cuando alguien entre a /api/suplementos con el navegador, se ejecuta esto:
    @GetMapping
    public List<Suplemento> obtenerCatalogoCliente() {
        // Reutilizamos el método exacto que creaste para el GestorCliente
        return suplementoRepo.findByActivoTrue();
    }
}