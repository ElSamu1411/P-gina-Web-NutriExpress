package org.example;

import jakarta.persistence.*;

/**
 * Entidad puente que relaciona cuantos suplementos hay en cada sucursal.
 */
@Entity
@Table(name = "inventario")
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inventario")
    private Integer idInventario;

    @Column(name = "id_sucursal", nullable = false)
    private Integer idSucursal;

    @Column(name = "id_suplemento", nullable = false)
    private Integer idSuplemento;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    public Inventario() {}

    // Getters y Setters
    public Integer getIdInventario() { return idInventario; }
    public void setIdInventario(Integer idInventario) { this.idInventario = idInventario; }

    public Integer getIdSucursal() { return idSucursal; }
    public void setIdSucursal(Integer idSucursal) { this.idSucursal = idSucursal; }

    public Integer getIdSuplemento() { return idSuplemento; }
    public void setIdSuplemento(Integer idSuplemento) { this.idSuplemento = idSuplemento; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

}
