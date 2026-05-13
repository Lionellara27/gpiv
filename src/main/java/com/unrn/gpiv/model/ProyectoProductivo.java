package com.unrn.gpiv.model;

import com.unrn.gpiv.common.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "proyectos_productivos")
@Getter @Setter
public class ProyectoProductivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- AGREGÁ ESTA LÍNEA ---
    @Column(nullable = false)
    private String nombreProyecto;

    // --- NUEVO: Lo que agregamos para el formulario ---
    @Column(nullable = false)
    private String superficieRequerida;

    @jakarta.validation.constraints.Min(value = 1, message = "Debe tener al menos 1 empleado")
    @jakarta.validation.constraints.Max(value = 1500, message = "Número irreal para el Parque")
    @Column(nullable = false)
    private Integer cantidadEmpleados;
    // --------------------------------------------------

    private String ingresoBruto;

    @Column(nullable = false)
    private String actividadPrincipal;

    private String actividadSecundaria;
    private String rubro;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    private float potenciaSimultanea;
    private String destinoProduccion;

    // --- MODIFICADO: De Enum simple a Lista de Enums (para los Checkbox) ---
    @ElementCollection(targetClass = TipoServicio.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "proyecto_servicios", joinColumns = @JoinColumn(name = "proyecto_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "servicio")
    private Set<TipoServicio> serviciosNecesarios = new HashSet<>();
    // ------------------------------------------------------------------------

    @Column(columnDefinition = "TEXT")
    private String impactoAmbiental;

    private String tipoResiduos;

    @Enumerated(EnumType.STRING)
    private Emplazamiento emplazamientoActual;

    private String materiaPrima;

    @Column(name = "pdf_proyecto")
    private byte[] pdfProyecto;

    @Column(name = "nombre_archivo_pdf")
    private String nombreArchivoPdf;

    // Personal Ocupado (Dejamos los otros dos por si Martín los pide a futuro)
    private int cantJerarquico;
    private int cantAdministrativo;

    @Enumerated(EnumType.STRING)
    private TensionAlimentacion tensionAlimentacion;

    @OneToOne(mappedBy = "proyecto")
    private Empresa empresa;

    public ProyectoProductivo() {
    }
}

/*package com.unrn.gpiv.model;

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
    private TipoServicio tipoServicio;

    private String tipoResiduos;

    @Enumerated(EnumType.STRING)
    private Emplazamiento emplazamientoActual;

    private String materiaPrima;

    @Column(name = "pdf_proyecto")
    private byte[] pdfProyecto;

    @Column(name = "nombre_archivo_pdf")
    private String nombreArchivoPdf;

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
*/
