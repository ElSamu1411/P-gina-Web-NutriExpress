package org.example;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Integer> {

    // Obtiene todo el catalogo disponible en una sucursal especifica.
    List<Inventario> findByIdSucursal(Integer idSucursal);

    // Busca el stock especifico de un producto en una tienda particular.
    Optional<Inventario> findByIdSucursalAndIdSuplemento(Integer idSucursal, Integer idSuplemento);

    // Busca todos los registros de inventario de un suplemento especifico
    List<Inventario> findByIdSuplemento(Integer idSuplemento);

}
