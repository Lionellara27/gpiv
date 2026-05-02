package com.unrn.gpiv.inventory.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "items_inventario")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre; // Ej: "Motosierra Stihl", "Taladro Bosch"

    private String descripcion; // "Herramienta de corte a explosión..."

    // ¡ESTA ES LA VARIABLE MÁGICA PARA EL BUSCADOR!
    private String categoria; // Ej: "Herramienta", "Maquinaria pesada", "Acceso"

    // Relación: Un "molde" del catálogo tiene muchas herramientas físicas reales
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    private List<Recurso> existencias;

    // Constructor vacío obligatorio
    public Item() {
    }

    // --- GETTERS Y SETTERS ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public List<Recurso> getExistencias() {
        return existencias;
    }

    public void setExistencias(List<Recurso> existencias) {
        this.existencias = existencias;
    }
}