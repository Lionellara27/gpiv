package com.unrn.gpiv.inventory.model;

import com.unrn.gpiv.model.Empresa;
import jakarta.persistence.*;

@Entity
@Table(name = "recursos_fisicos")
public class Recurso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con el "molde": Muchos recursos pueden ser del mismo Item (ej: muchas hachas)
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    private String propietario; // "Pepe", "Alberto", "Administración"

    private String estadoConservacion; // "Nuevo", "Gastado", "Roto"

    private String numeroSerie; // Para diferenciar físicamente la de Pepe de la de Alberto

    private boolean disponible = true;

    // Relación con Empresa: A quién se lo prestamos (HU 18)
    @ManyToOne
    @JoinColumn(name = "empresa_id")
    private Empresa prestadoA;

    // Constructor vacío (obligatorio para JPA)
    public Recurso() {
    }

    // --- GETTERS Y SETTERS ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public String getPropietario() {
        return propietario;
    }

    public void setPropietario(String propietario) {
        this.propietario = propietario;
    }

    public String getEstadoConservacion() {
        return estadoConservacion;
    }

    public void setEstadoConservacion(String estadoConservacion) {
        this.estadoConservacion = estadoConservacion;
    }

    public String getNumeroSerie() {
        return numeroSerie;
    }

    public void setNumeroSerie(String numeroSerie) {
        this.numeroSerie = numeroSerie;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public Empresa getPrestadoA() {
        return prestadoA;
    }

    public void setPrestadoA(Empresa prestadoA) {
        this.prestadoA = prestadoA;
    }
}