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

    @Column(name = "nro_lote", nullable = false) // Usamos snake_case para la DB
    private String nroLote;

    private String ubicacion;

    @Column(nullable = false)
    private Double superficie;

    @Enumerated(EnumType.STRING)
    private EstadoLote estado = EstadoLote.LIBRE;

    @Column(columnDefinition = "TEXT")
    private String caracteristicas;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "lote_servicio",
            joinColumns = @JoinColumn(name = "lote_id"),
            inverseJoinColumns = @JoinColumn(name = "servicio_id")
    )
    private Set<Servicio> servicios = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;

    //Logica
    public boolean isDisponible() {
        return EstadoLote.LIBRE.equals(this.estado) && this.empresa == null;
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
