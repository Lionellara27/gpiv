package com.unrn.gpiv.model;

import com.unrn.gpiv.common.EstadoSolicitud;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "empresas")
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private EstadoSolicitud estado = EstadoSolicitud.PENDIENTE;

    @Column(nullable = false)
    private String razonSocial;

    @Column(nullable = false, unique = true)
    private String cuit;

    private String direccion; // Dirección legal/administrativa

    private boolean titulada; // Si ya tiene el título de propiedad del lote

    // --- RELACIONES CORE ---

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "proyecto_id", referencedColumnName = "id")
    private ProyectoProductivo proyecto;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "representante_id", referencedColumnName = "id")
    private RepresentanteEmpresa representante;

    // --- RELACIÓN CON EL ESPACIO FÍSICO ---

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "lote_id")                 TENGO QUE BORRAR ESTO
//    private Lote loteAsignado; // Asignado por el Admin al aprobar

    // cambio la relacion, para que una empresa pueda tener varios lotes
    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Lote> lotesAsignados = new ArrayList<>();

    private LocalDate fechaRadicacion; // Fecha efectiva de entrada al Parque

    // --- INFORMES Y SEGUIMIENTO ---

    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InformeAvance> informesDeAvance = new ArrayList<>();

    // --- RELACIONES DE INVENTARIO (Módulo Gerente) ---

    // Herramientas que el Parque le prestó a la empresa
    @OneToMany(mappedBy = "prestadoA")
    private List<Recurso> herramientasPrestadas = new ArrayList<>();

    // Herramientas que la empresa trajo al parque (su capital)
    @OneToMany(mappedBy = "propietarioEmpresa")
    private List<Recurso> herramientasAportadas = new ArrayList<>();

    // --- CONSTRUCTORES ---

    public Empresa() {}

    // --- GETTERS Y SETTERS ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public EstadoSolicitud getEstado() { return estado; }
    public void setEstado(EstadoSolicitud estado) { this.estado = estado; }

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

    public List<Lote> getLotesAsignados() { return lotesAsignados; }
    public void setLotesAsignados(List<Lote> lotesAsignados) { this.lotesAsignados = lotesAsignados; }

    public LocalDate getFechaRadicacion() { return fechaRadicacion; }
    public void setFechaRadicacion(LocalDate fechaRadicacion) { this.fechaRadicacion = fechaRadicacion; }

    public List<InformeAvance> getInformesDeAvance() { return informesDeAvance; }
    public void setInformesDeAvance(List<InformeAvance> informes) { this.informesDeAvance = informes; }

    public List<Recurso> getHerramientasPrestadas() { return herramientasPrestadas; }
    public void setHerramientasPrestadas(List<Recurso> herramientasPrestadas) { this.herramientasPrestadas = herramientasPrestadas; }

    public List<Recurso> getHerramientasAportadas() { return herramientasAportadas; }
    public void setHerramientasAportadas(List<Recurso> herramientasAportadas) { this.herramientasAportadas = herramientasAportadas; }
}

/*package com.unrn.gpiv.model;

import com.unrn.gpiv.common.EstadoSolicitud;
import com.unrn.gpiv.model.Recurso; // Importamos la clase del otro módulo
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "empresas")
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private EstadoSolicitud estado = EstadoSolicitud.PENDIENTE;

    private String razonSocial;
    private String cuit;
    private String direccion;
    private boolean titulada;

    // --- RELACIONES CORE ---

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "proyecto_id", referencedColumnName = "id")
    private ProyectoProductivo proyecto;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "representante_id", referencedColumnName = "id")
    private RepresentanteEmpresa representante;

    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InformeAvance> informesDeAvance = new ArrayList<>();

    // --- RELACIONES DE INVENTARIO (Lo nuevo para el Gerente) ---

    // Las herramientas que el Parque le prestó a esta empresa hoy
    @OneToMany(mappedBy = "prestadoA")
    private List<Recurso> herramientasPrestadas = new ArrayList<>();

    // Las herramientas que son propiedad de esta empresa (como el tractor del Lote 1)
    @OneToMany(mappedBy = "propietarioEmpresa")
    private List<Recurso> herramientasAportadas = new ArrayList<>();

    // Constructores
    public Empresa() {}

    // --- GETTERS Y SETTERS ---

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

    // Getters y Setters del Inventario
    public List<Recurso> getHerramientasPrestadas() { return herramientasPrestadas; }
    public void setHerramientasPrestadas(List<Recurso> lista) { this.herramientasPrestadas = lista; }

    public List<Recurso> getHerramientasAportadas() { return herramientasAportadas; }
    public void setHerramientasAportadas(List<Recurso> lista) { this.herramientasAportadas = lista; }

    public void setEstado(EstadoSolicitud estadoSolicitud) {
        this.estado = estadoSolicitud;
    }
}*/