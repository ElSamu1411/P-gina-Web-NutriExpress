package org.example;

import jakarta.persistence.*;

// Define esta clase como una entidad de base de datos
@Entity
// Vincula la clase con la tabla física 'usuario' creada en Docker
@Table(name = "usuario")
public class Usuario {

    // Define la llave primaria de la tabla
    @Id
    // Indica que el ID se autoincrementa en la BD (tipo SERIAL)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    // Mapea la columna de nombre de usuario; no permite nulos y es única
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    // Mapea la contraseña del usuario
    @Column(name = "password", nullable = false)
    private String password;

    // Define el rol (ADMIN o CLIENTE) para controlar los menús
    @Column(name = "rol", nullable = false)
    private String rol;

    // Constructor vacío: Indispensable para que Spring pueda instanciar la clase
    public Usuario() {
    }

    // Métodos Getter y Setter: Permiten el acceso controlado a los atributos
    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

}