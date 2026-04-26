package org.example;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SucursalRepository extends JpaRepository<Sucursal, Integer> {

    // Encuentra las sucursales ubicadas en un municipio especifico
    List<Sucursal> findByIdMunicipio(Integer idMunicipio);

}
