package com.unrn.gpiv.model;

import com.unrn.gpiv.common.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "proyectos_productivos")
@Getter @Setter
public class ProyectoProductivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ingresoBruto;

    @Column(nullable = false)
    private String actividadPrincipal;

    private String actividadSecundaria;
    private String rubro;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    private float potenciaSimultanea;
    private String destinoProduccion;

    @Enumerated(EnumType.STRING)
    private TipoServicio tipoServicio; // <--- USANDO TU ENUM EXISTENTE

    private String tipoResiduos;

    @Enumerated(EnumType.STRING)
    private Emplazamiento emplazamientoActual;

    private String materiaPrima;

    // Personal Ocupado
    private int cantJerarquico;
    private int cantProduccion;
    private int cantAdministrativo;

    @Enumerated(EnumType.STRING)
    private TensionAlimentacion tensionAlimentacion;

    @OneToOne(mappedBy = "proyecto")
    private Empresa empresa;

    public ProyectoProductivo() {
    }
}