package org.example;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // IMPORTANTE: Agregamos este import
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SuplementoRepository extends JpaRepository<Suplemento, Integer> {

    // El que arreglamos en el paso anterior
    List<Suplemento> findByActivoTrue();

    // 1. Buscar por nombre (LIKE %busqueda%) ignorando mayúsculas
    List<Suplemento> findByNombreContainingIgnoreCaseAndActivoTrue(String busqueda);

    // 2. Buscar por categoría específica
    List<Suplemento> findByCategoriaIgnoreCaseAndActivoTrue(String categoria);

    // 3. Buscar por marca específica
    List<Suplemento> findByMarcaIgnoreCaseAndActivoTrue(String marca);

    // 4. Ordenar catálogo por precio (Menor a Mayor y Mayor a Menor)
    List<Suplemento> findByActivoTrueOrderByPrecioAsc();
    List<Suplemento> findByActivoTrueOrderByPrecioDesc();

    // ========================================================================
    // CONSULTAS PERSONALIZADAS (Custom Queries)
    // Para obtener solo una lista de palabras sin repetir (ej. "Proteínas", "Creatinas")
    // ========================================================================

    @Query("SELECT DISTINCT s.categoria FROM Suplemento s WHERE s.activo = true AND s.categoria IS NOT NULL")
    List<String> findCategoriasActivas();

    @Query("SELECT DISTINCT s.marca FROM Suplemento s WHERE s.activo = true AND s.marca IS NOT NULL")
    List<String> findMarcasActivas();
}