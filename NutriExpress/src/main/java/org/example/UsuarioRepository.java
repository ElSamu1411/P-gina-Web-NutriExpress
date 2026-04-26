package org.example;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

// Indica que esta interfaz maneja el acceso a datos
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    /**
     * Método personalizado para buscar por nombre de usuario.
     * Optional ayuda a evitar errores si el usuario no existe.
     */
    Optional<Usuario> findByUsername(String username);
}