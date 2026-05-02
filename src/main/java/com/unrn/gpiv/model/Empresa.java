package com.unrn.gpiv.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "empresas")
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String razonSocial;
    private String cuit; // CUIT de la entidad (30-xxx, etc.)
    private String direccion;
    private boolean titulada; // HU 11: Si ya tiene escritura/título

    // RELACIONES

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "proyecto_id", referencedColumnName = "id")
    private ProyectoProductivo proyecto;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "representante_id", referencedColumnName = "id")
    private RepresentanteEmpresa representante;

    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InformeAvance> informesDeAvance = new ArrayList<>();

    // Constructores
    public Empresa() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }

    public String getCuit() { return cuit; }
    public void setCuit(String cuit) { this.cuit = cuit; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public boolean isTitulada() { return titulada; }
    public void setTitulada(boolean titulada) { this.titulada = titulada; }

    public ProyectoProductivo getProyecto() { return proyecto; }
    public void setProyecto(ProyectoProductivo proyecto) { this.proyecto = proyecto; }

    public RepresentanteEmpresa getRepresentante() { return representante; }
    public void setRepresentante(RepresentanteEmpresa representante) { this.representante = representante; }

    public List<InformeAvance> getInformesDeAvance() { return informesDeAvance; }
    public void setInformesDeAvance(List<InformeAvance> informes) { this.informesDeAvance = informes; }
}
