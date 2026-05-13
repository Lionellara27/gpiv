package com.unrn.gpiv.model;

import com.unrn.gpiv.common.EstadoLote;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "lotes")
public class Lote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String manzana;

    @Column(nullable = false)
    private String nroLote;

    private String ubicacion; // Descripción adicional (ej: "Esquina Norte")

    @Column(nullable = false)
    private Double superficie; // m2

    @Enumerated(EnumType.STRING)
    private EstadoLote estado = EstadoLote.LIBRE;

    @Column(columnDefinition = "TEXT")
    private String caracteristicas; // Detalles como tipo de suelo, nivelación, etc.

    // --- RELACIONES ---

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "lote_servicio",
            joinColumns = @JoinColumn(name = "lote_id"),
            inverseJoinColumns = @JoinColumn(name = "servicio_id")
    )
    private Set<Servicio> servicios = new HashSet<>();

    // Muchos lotes pueden pertenecer a una sola empresa (escalable)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;

    public Lote() {
        
    }


    // --- MÉTODOS DE CONVENIENCIA ---

    public boolean estaDisponible() {
        return this.empresa == null && this.estado == EstadoLote.LIBRE;
    }

    // --- GETTERS Y SETTERS ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getManzana() { return manzana; }
    public void setManzana(String manzana) { this.manzana = manzana; }

    public String getNroLote() { return nroLote; }
    public void setNroLote(String nroLote) { this.nroLote = nroLote; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public Double getSuperficie() { return superficie; }
    public void setSuperficie(Double superficie) { this.superficie = superficie; }

    public EstadoLote getEstado() { return estado; }
    public void setEstado(EstadoLote estado) { this.estado = estado; }

    public String getCaracteristicas() { return caracteristicas; }
    public void setCaracteristicas(String caracteristicas) { this.caracteristicas = caracteristicas; }

    public Set<Servicio> getServicios() { return servicios; }
    public void setServicios(Set<Servicio> servicios) { this.servicios = servicios; }

    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }
}

/*package com.unrn.gpiv.model;

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
}*/