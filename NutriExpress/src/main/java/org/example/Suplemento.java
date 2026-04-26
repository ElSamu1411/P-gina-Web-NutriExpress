package org.example;

import jakarta.persistence.*;

/**
 * Entidad que representa el catalogo global de suplementos.
 */
@Entity
@Table(name = "suplemento")
public class Suplemento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_suplemento")
    private Integer idSuplemento;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "marca")
    private String marca;

    @Column(name = "categoria")
    private String categoria;

    @Column(name = "precio")
    private Double precio;

    @Column(name = "url_imagen")
    private String urlImagen;

    // Bandera logica para dar de baja un producto a nivel global
    @Column(name = "activo")
    private Boolean activo = true;

    public Suplemento() {}

    // Getters y Setters
    public Integer getIdSuplemento() { return idSuplemento; }
    public void setIdSuplemento(Integer idSuplemento) { this.idSuplemento = idSuplemento; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }

    public String getUrlImagen() { return urlImagen; }
    public void setUrlImagen(String urlImagen) { this.urlImagen = urlImagen; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }



}
