package com.unrn.gpiv.model;

import com.unrn.gpiv.common.Rol;
import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // Crea una sola tabla 'usuario' con una columna 'dtype'
@DiscriminatorColumn(name = "tipo_usuario")
public abstract class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password; // ¡Acordate de encriptarla después!
    private String email;

    @Enumerated(EnumType.STRING) // Guarda el nombre del rol (ej: "ADMIN") en la DB
    private Rol rol;

    // Getters y Setters...
}