package com.unrn.gpiv.model;

import com.unrn.gpiv.common.EstadoLote;
import jakarta.persistence.*;

@Entity
@Table(name = "lotes")
public class Lote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Usamos Long por estándar de JPA

    private String ubicacion;

    private Double superficie; // m2

    // Lo que pediste como disponibilidad, lo manejamos con el Enum para más detalle
    @Enumerated(EnumType.STRING)
    private EstadoLote estado;

    // Campo extra para cumplir con lo de 'disponibilidad' si querés un booleano simple
    private boolean disponibilidad;

    @Column(columnDefinition = "TEXT")
    private String caracteristica;

    // Relación con la Empresa: Un lote puede tener una empresa radicada
    @OneToOne
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;

    // Constructor vacío
    public Lote() {
    }

    // --- GETTERS Y SETTERS ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public Double getSuperficie() { return superficie; }
    public void setSuperficie(Double superficie) { this.superficie = superficie; }

    public EstadoLote getEstado() { return estado; }
    public void setEstado(EstadoLote estado) { this.estado = estado; }

    public boolean isDisponibilidad() { return disponibilidad; }
    public void setDisponibilidad(boolean disponibilidad) { this.disponibilidad = disponibilidad; }

    public String getCaracteristica() { return caracteristica; }
    public void setCaracteristica(String caracteristica) { this.caracteristica = caracteristica; }

    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }
}