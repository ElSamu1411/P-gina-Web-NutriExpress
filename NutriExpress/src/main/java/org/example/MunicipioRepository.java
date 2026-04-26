package org.example;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MunicipioRepository extends JpaRepository<Municipio, Integer> {

    /**
     * Equivalente en SQL a: SELECT * FROM municipio WHERE id_estado = ?
     */
    List<Municipio> findByIdEstado(Integer idEstado);
}