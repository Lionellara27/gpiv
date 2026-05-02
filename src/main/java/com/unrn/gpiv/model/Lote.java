package com.unrn.gpiv.model;

import com.unrn.gpiv.common.EstadoLote;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "lotes")
public class Lote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ubicacion;

    private Double superficie; // m2

    @Enumerated(EnumType.STRING)
    private EstadoLote estado;

    private boolean disponibilidad;

    @Column(columnDefinition = "TEXT")
    private String caracteristica;

    // --- LA RELACIÓN QUE HACÍA FALTA ---
    // El nombre 'servicios' tiene que coincidir con el mappedBy de la clase Servicio
    @ManyToMany
    @JoinTable(
            name = "lote_servicio",
            joinColumns = @JoinColumn(name = "lote_id"),
            inverseJoinColumns = @JoinColumn(name = "servicio_id")
    )
    private List<Servicio> servicios;

    @OneToOne
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;

    // Constructor vacío (obligatorio)
    public Lote() {
    }

    // --- GETTERS Y SETTERS ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public Double getSuperficie() {
        return superficie;
    }

    public void setSuperficie(Double superficie) {
        this.superficie = superficie;
    }

    public EstadoLote getEstado() {
        return estado;
    }

    public void setEstado(EstadoLote estado) {
        this.estado = estado;
    }

    public boolean isDisponibilidad() {
        return disponibilidad;
    }

    public void setDisponibilidad(boolean disponibilidad) {
        this.disponibilidad = disponibilidad;
    }

    public String getCaracteristica() {
        return caracteristica;
    }

    public void setCaracteristica(String caracteristica) {
        this.caracteristica = caracteristica;
    }

    public List<Servicio> getServicios() {
        return servicios;
    }

    public void setServicios(List<Servicio> servicios) {
        this.servicios = servicios;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }
}