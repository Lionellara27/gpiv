package com.unrn.gpiv.views.empresa;

import com.unrn.gpiv.common.EstadoSolicitud;
import com.unrn.gpiv.model.Empresa;
import com.unrn.gpiv.model.ProyectoProductivo;
import com.unrn.gpiv.model.RepresentanteEmpresa;
import com.unrn.gpiv.service.EmpresaService;
import com.unrn.gpiv.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Collectors;

import static com.unrn.gpiv.common.EstadoProyecto.APROBADO;
import static com.unrn.gpiv.common.EstadoProyecto.RECHAZADO;

@PageTitle("Mi Proyecto | SGPIV")
@Route(value = "mi-proyecto", layout = MainLayout.class)
public class MiProyectoView extends VerticalLayout {

    private final EmpresaService empresaService;

    public MiProyectoView(@Autowired EmpresaService empresaService) {
        this.empresaService = empresaService;

        setPadding(true);
        setSpacing(true);

        // 1. RECUPERAR DATOS DE SESIÓN
        RepresentanteEmpresa logueado = (RepresentanteEmpresa) VaadinSession.getCurrent().getAttribute("usuarioLogueado");

        if (logueado == null) {
            add(new H2("Por favor, inicie sesión para ver su proyecto."));
            return;
        }

        // 2. BUSCAR LA EMPRESA Y EL PROYECTO REAL
        Empresa empresa = empresaService.obtenerEmpresaPorRepresentante(logueado);

        if (empresa == null || empresa.getProyecto() == null) {
            add(new H2("Aún no has presentado ningún proyecto."));
            Button btnIrForm = new Button("Cargar Solicitud", e -> getUI().ifPresent(ui -> ui.navigate("formulario-proyecto")));
            add(btnIrForm);
            return;
        }

        ProyectoProductivo proyecto = empresa.getProyecto();
        EstadoSolicitud estado = empresa.getEstado();

        // --- UI ---
        H2 titulo = new H2("Mi Proyecto Productivo");

        // --- TARJETA DE ESTADO ---
        VerticalLayout statusCard = new VerticalLayout();
        statusCard.setWidthFull();
        statusCard.getStyle().set("background-color", "#f8f9fa");
        statusCard.getStyle().set("border-radius", "15px");
        statusCard.setPadding(true);

        H3 sub = new H3("Estado de la Solicitud");

        Span badgeEstado = new Span(estado.name());
        configurarEstiloBadge(badgeEstado, estado);

        Paragraph infoEstado = new Paragraph(obtenerMensajeEstado(estado));
        infoEstado.getStyle().set("color", "#666");

        // --- BOTONES DE ACCIÓN ---
        HorizontalLayout acciones = new HorizontalLayout();

        Button btnModificar = new Button("Modificar Solicitud", VaadinIcon.EDIT.create());
        btnModificar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        // REGLA DE ORO: Solo se modifica si está en PENDIENTE
        if (estado != EstadoSolicitud.PENDIENTE) {
            btnModificar.setEnabled(false);
            btnModificar.setTooltipText("No se puede modificar un proyecto que ya está siendo evaluado o aprobado.");
        }

        btnModificar.addClickListener(e -> {
            getUI().ifPresent(ui -> ui.navigate("formulario-proyecto"));
        });

        Button btnVerPDF = new Button("Ver PDF Solicitud", VaadinIcon.FILE.create());
        btnVerPDF.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        // Aquí podrías agregar la lógica para descargar el byte[] del PDF que ya tenés en el modelo

        acciones.add(btnModificar, btnVerPDF);
        statusCard.add(sub, badgeEstado, infoEstado, acciones);

        // --- RESUMEN DE DATOS REALES ---
        VerticalLayout datosProyecto = new VerticalLayout();
        datosProyecto.add(new H3("Resumen del Proyecto"));
        datosProyecto.add(new Paragraph("Nombre/Razón Social: " + empresa.getRazonSocial()));
        datosProyecto.add(new Paragraph("Actividad: " + proyecto.getActividadPrincipal()));
        datosProyecto.add(new Paragraph("Superficie Requerida: " + proyecto.getSuperficieRequerida()));

        // Convertimos el Set de servicios a un String lindo
        String servicios = proyecto.getServiciosNecesarios().stream()
                .map(Enum::name)
                .collect(Collectors.joining(", "));
        datosProyecto.add(new Paragraph("Servicios solicitados: " + (servicios.isEmpty() ? "Ninguno" : servicios)));

        add(titulo, statusCard, datosProyecto);
    }

    private void configurarEstiloBadge(Span badge, EstadoSolicitud estado) {
        // Estilos base para todos los estados
        badge.getStyle().set("padding", "0.5em 1em")
                .set("border-radius", "20px")
                .set("font-weight", "bold")
                .set("color", "white");

        // El Switch tiene que ser EXHAUSTIVO (cubrir los 4 estados)
        switch (estado) {
            case PENDIENTE -> badge.getStyle().set("background-color", "#6c757d"); // Gris

            case EN_EVALUACION -> badge.getStyle().set("background-color", "#0063BE"); // Azul institucional

            case APROBADA -> badge.getStyle().set("background-color", "#009A3B"); // Verde éxito

            case RECHAZADA -> badge.getStyle().set("background-color", "#d9534f"); // Rojo error
        }
    }

    private String obtenerMensajeEstado(EstadoSolicitud estado) {
        return switch (estado) {
            case PENDIENTE -> "Tu solicitud fue recibida. Podés editarla hasta que comience la evaluación.";

            case EN_EVALUACION ->
                    "El Directorio de ENREPAVI está analizando tu proyecto. Ya no es posible realizar modificaciones.";

            case APROBADA ->
                    "¡Felicidades! Tu proyecto fue aprobado. El Parque se contactará para la asignación del lote.";

            case RECHAZADA ->
                    "Tu solicitud no ha sido aprobada en esta instancia. Por favor, revisá las observaciones enviadas a tu mail.";
        };
    }
}

/*VIEJO SIN BASE DE DATOS
package com.unrn.gpiv.views.empresa;

import com.unrn.gpiv.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Mi Proyecto | SGPIV")
@Route(value = "mi-proyecto", layout = MainLayout.class)
public class MiProyectoView extends VerticalLayout {

    // Cambiá este valor ("PROPUESTO", "EN_EVALUACION", "APROBADO") para ver cómo cambia la vista
    private String estadoActual = "PROPUESTO";

    public MiProyectoView() {
        setPadding(true);
        setSpacing(true);

        H2 titulo = new H2("Mi Proyecto Productivo");

        // --- TARJETA DE ESTADO ---
        VerticalLayout statusCard = new VerticalLayout();
        statusCard.setWidthFull();
        statusCard.getStyle().set("background-color", "#f8f9fa");
        statusCard.getStyle().set("border-radius", "15px");
        statusCard.setPadding(true);

        H3 sub = new H3("Estado de la Solicitud");

        Span badgeEstado = new Span(estadoActual);
        configurarEstiloBadge(badgeEstado);

        Paragraph infoEstado = new Paragraph(obtenerMensajeEstado());
        infoEstado.getStyle().set("color", "#666");

        // --- BOTONES DE ACCIÓN ---
        HorizontalLayout acciones = new HorizontalLayout();
        acciones.setMargin(true);

        Button btnModificar = new Button("Modificar Solicitud", VaadinIcon.EDIT.create());
        btnModificar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        // Regla: Solo se modifica si está en PROPUESTO
        if (!estadoActual.equals("PROPUESTO")) {
            btnModificar.setEnabled(false);
            btnModificar.setTooltipText("No se puede modificar un proyecto que ya está siendo evaluado o aprobado.");
        }

        btnModificar.addClickListener(e -> {
            Notification.show("Redirigiendo al editor del formulario...");
        });

        Button btnVerDetalles = new Button("Ver PDF Solicitud", VaadinIcon.FILE.create());
        btnVerDetalles.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        acciones.add(btnModificar, btnVerDetalles);

        statusCard.add(sub, badgeEstado, infoEstado, acciones);

        // --- RESUMEN DE DATOS (MOCKUP) ---
        VerticalLayout datosProyecto = new VerticalLayout();
        datosProyecto.add(new H3("Resumen del Proyecto"));
        datosProyecto.add(new Paragraph("Nombre: Planta de Ensamblaje Metalúrgico Viedma"));
        datosProyecto.add(new Paragraph("Superficie Requerida: 2500 m²"));
        datosProyecto.add(new Paragraph("Servicios: Agua, Luz Trifásica, Gas."));

        add(titulo, statusCard, datosProyecto);
    }

    private void configurarEstiloBadge(Span badge) {
        badge.getStyle().set("padding", "0.5em 1em");
        badge.getStyle().set("border-radius", "20px");
        badge.getStyle().set("font-weight", "bold");
        badge.getStyle().set("color", "white");

        switch (estadoActual) {
            case "PROPUESTO":
                badge.getStyle().set("background-color", "#6c757d"); // Gris
                break;
            case "EN_EVALUACION":
                badge.getStyle().set("background-color", "#0063BE"); // Azul
                break;
            case "APROBADO":
                badge.getStyle().set("background-color", "#009A3B"); // Verde
                break;
            case "RECHAZADO":
                badge.getStyle().set("background-color", "#d9534f"); // Rojo
                break;
            default:
                badge.getStyle().set("background-color", "#000");
        }
    }

    private String obtenerMensajeEstado() {
        return switch (estadoActual) {
            case "PROPUESTO" -> "Tu solicitud fue recibida correctamente. Podés editarla hasta que el Directorio comience la evaluación.";
            case "EN_EVALUACION" -> "El Directorio de ENREPAVI está analizando tu proyecto. Ya no es posible realizar modificaciones.";
            case "APROBADO" -> "¡Felicidades! Tu proyecto fue aprobado. Próximamente se te asignará un lote.";
            case "RECHAZADO" -> "Tu solicitud no ha sido aprobada en esta instancia. Por favor, revisá las observaciones enviadas a tu mail.";
            default -> "Estado desconocido.";
        };
    }
}*/