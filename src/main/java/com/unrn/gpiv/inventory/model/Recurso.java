package com.unrn.gpiv.inventory.model;

import com.unrn.gpiv.model.Empresa; // Relación con el core
import jakarta.persistence.*;

@Entity
@Table(name = "inventario_recursos")
public class Recurso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private String descripcion;

    private String categoria; // Ejemplo: "Herramienta", "Maquinaria", "Llave"

    private String estadoConservacion; // Ejemplo: "Nuevo", "Dañado", "En Reparación"

    private boolean disponible = true;

    // Relación: Un recurso puede estar prestado a una empresa (HU 18)
    @ManyToOne
    @JoinColumn(name = "empresa_id")
    private Empresa prestadoA;

    public Recurso() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getEstadoConservacion() { return estadoConservacion; }
    public void setEstadoConservacion(String estado) { this.estadoConservacion = estado; }
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
    public Empresa getPrestadoA() { return prestadoA; }
    public void setPrestadoA(Empresa prestadoA) { this.prestadoA = prestadoA; }
}
