package com.unrn.gpiv.model;

import com.unrn.gpiv.common.*; // Para traer los Enums
import jakarta.persistence.*;

@Entity
@Table(name = "proyectos_productivos")
public class ProyectoProductivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ingresoBruto;

    @Column(nullable = false) // "rellenar siosi"
    private String actividadPrincipal;

    private String actividadSecundaria; // opcional

    private String rubro;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    private float potenciaSimultanea;
    private String destinoProduccion;

    @Enumerated(EnumType.STRING)
    private Servicio servicio; // El que ya tenés en .common

    private String tipoResiduos;

    @Enumerated(EnumType.STRING)
    private Emplazamiento emplazamientoActual; // (Propio, Alquilado)

    private String materiaPrima;

    // --- PERSONAL OCUPADO (Categorías y Cantidades) ---
    private int cantJerarquico;
    private int cantProduccion;
    private int cantAdministrativo;

    @Enumerated(EnumType.STRING)
    private TensionAlimentacion tensionAlimentacion; // (Media, Baja)

    @OneToOne(mappedBy = "proyecto")
    private Empresa empresa;

    public ProyectoProductivo() {}

    // --- GETTERS Y SETTERS ---
    // (Generalos todos con Alt+Insert en IntelliJ)
}