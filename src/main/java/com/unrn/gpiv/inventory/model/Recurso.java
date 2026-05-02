package com.unrn.gpiv.inventory.model;

import com.unrn.gpiv.model.Empresa;
import jakarta.persistence.*;

@Entity
@Table(name = "recursos_fisicos")
public class Recurso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con el "molde" del catálogo (Ej: Hacha, Motosierra)
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    // Nombre del propietario (Útil para poner "Administración" o nombres genéricos)
    private String propietario;

    private String estadoConservacion; // "Nuevo", "Gastado", "Roto"

    private String numeroSerie; // Para diferenciar físicamente las unidades

    private boolean disponible = true;

    // CASO A: A qué empresa le prestamos el recurso hoy
    @ManyToOne
    @JoinColumn(name = "empresa_prestamo_id")
    private Empresa prestadoA;

    // CASO B: Si el dueño del recurso es una Empresa y no el Parque
    @ManyToOne
    @JoinColumn(name = "empresa_propietaria_id")
    private Empresa propietarioEmpresa;

    // Constructor vacío obligatorio para JPA
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

    public Empresa getPropietarioEmpresa() {
        return propietarioEmpresa;
    }

    public void setPropietarioEmpresa(Empresa propietarioEmpresa) {
        this.propietarioEmpresa = propietarioEmpresa;
    }
}