package com.unrn.gpiv.views.empresa;

import com.unrn.gpiv.common.EstadoSolicitud;
import com.unrn.gpiv.model.*;
import com.unrn.gpiv.service.EmpresaService;
import com.unrn.gpiv.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.util.stream.Collectors;

@PageTitle("Mi Proyecto | SGPIV")
@Route(value = "mi-proyecto", layout = MainLayout.class)
public class MiProyectoView extends VerticalLayout {

    private final EmpresaService empresaService;

    public MiProyectoView(@Autowired EmpresaService empresaService) {
        this.empresaService = empresaService;

        setPadding(true);
        setSpacing(true);

        // 1. RECUPERAR USUARIO DE SESIÓN
        Usuario usuarioLogueado = (Usuario) VaadinSession.getCurrent().getAttribute("usuarioLogueado");

        // Usamos el instanceof para validar y castear al mismo tiempo
        if (!(usuarioLogueado instanceof RepresentanteEmpresa logueado)) {
            add(new H2("Acceso denegado. Esta vista es para representantes de empresas."));
            return;
        }

        // 2. BUSCAR SOLICITUD REAL EN LA BD
        SolicitudRadicacion solicitud = empresaService.obtenerUltimaSolicitud(logueado);

        if (solicitud == null) {
            renderizarVistaSinProyecto();
            return;
        }

        // 3. EXTRAER DATOS
        ProyectoProductivo proyecto = solicitud.getProyecto();
        EstadoSolicitud estado = solicitud.getEstado();

        // --- UI PRINCIPAL ---
        H2 titulo = new H2("Mi Proyecto Productivo");

        // --- TARJETA DE ESTADO ---
        VerticalLayout statusCard = new VerticalLayout();
        statusCard.setWidthFull();
        statusCard.getStyle().set("background-color", "#f8f9fa").set("border-radius", "15px");
        statusCard.setPadding(true);

        H3 sub = new H3("Estado de la Solicitud");
        Span badgeEstado = new Span(estado.name());
        configurarEstiloBadge(badgeEstado, estado);

        Paragraph infoEstado = new Paragraph(obtenerMensajeEstado(estado));
        infoEstado.getStyle().set("color", "#666");

        // --- BOTONES DE ACCIÓN ---
        HorizontalLayout acciones = new HorizontalLayout();

        // BOTÓN MODIFICAR (Envía el ID para que el formulario se auto-llene)
        Button btnModificar = new Button("Modificar Solicitud", VaadinIcon.EDIT.create());
        btnModificar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        if (estado != EstadoSolicitud.PENDIENTE) {
            btnModificar.setEnabled(false);
            btnModificar.setTooltipText("No se puede editar: el proyecto ya está en evaluación.");
        }

        btnModificar.addClickListener(e -> {
            getUI().ifPresent(ui -> ui.navigate("formulario-proyecto/" + solicitud.getId()));
        });

        // BOTÓN VER PDF (Configurado con Anchor para descarga real)
        acciones.add(btnModificar);
        configurarBotonPDF(acciones, proyecto, logueado);

        statusCard.add(sub, badgeEstado, infoEstado, acciones);

        // --- RESUMEN DE DATOS ---
        VerticalLayout datosProyecto = new VerticalLayout();
        datosProyecto.add(new H3("Resumen del Proyecto"));

        datosProyecto.add(new Paragraph("Nombre del proyecto: " + solicitud.getRazonSocialPretendida()));

        // FIX "null": Si el nombreProyecto es nulo en la BD, mostramos un aviso
        /*String nombreProyecto = (proyecto.getNombreProyecto() != null) ? proyecto.getNombreProyecto() : "Nombre no asignado";
        datosProyecto.add(new Paragraph("Proyecto: " + nombreProyecto))*/;

        datosProyecto.add(new Paragraph("Superficie Requerida: " + proyecto.getSuperficieRequerida() + " m²"));

        String servicios = proyecto.getServiciosNecesarios().stream()
                .map(Enum::name)
                .collect(Collectors.joining(", "));
        datosProyecto.add(new Paragraph("Servicios: " + (servicios.isEmpty() ? "Ninguno" : servicios)));

        add(titulo, statusCard, datosProyecto);
    }

    private void renderizarVistaSinProyecto() {
        add(new H2("Aún no has presentado ningún proyecto."));
        Button btnIrForm = new Button("Cargar Solicitud", e -> getUI().ifPresent(ui -> ui.navigate("formulario-proyecto")));
        btnIrForm.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        add(btnIrForm);
    }

    private void configurarBotonPDF(HorizontalLayout layout, ProyectoProductivo proyecto, RepresentanteEmpresa rep) {
        if (proyecto.getPdfProyecto() != null) {
            // Usamos nombreCompleto ya que no tenés "apellido"
            String nombreLimpio = rep.getNombreCompleto().replace(" ", "_");

            StreamResource resource = new StreamResource("Solicitud_" + nombreLimpio + ".pdf",
                    () -> new ByteArrayInputStream(proyecto.getPdfProyecto()));

            Anchor downloadLink = new Anchor(resource, "");
            downloadLink.getElement().setAttribute("download", true);

            Button btnVerPDF = new Button("Ver PDF Solicitud", VaadinIcon.FILE.create());
            btnVerPDF.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

            downloadLink.add(btnVerPDF);
            layout.add(downloadLink);
        } else {
            Button btnNoPDF = new Button("Ver PDF Solicitud", VaadinIcon.FILE.create());
            btnNoPDF.setEnabled(false);
            btnNoPDF.setTooltipText("No hay PDF disponible.");
            layout.add(btnNoPDF);
        }
    }

    private void configurarEstiloBadge(Span badge, EstadoSolicitud estado) {
        badge.getStyle().set("padding", "0.5em 1em").set("border-radius", "20px")
                .set("font-weight", "bold").set("color", "white");

        switch (estado) {
            case PENDIENTE -> badge.getStyle().set("background-color", "#6c757d");
            case EN_EVALUACION -> badge.getStyle().set("background-color", "#0063BE");
            case APROBADA -> badge.getStyle().set("background-color", "#009A3B");
            case RECHAZADA -> badge.getStyle().set("background-color", "#d9534f");
        }
    }

    private String obtenerMensajeEstado(EstadoSolicitud estado) {
        return switch (estado) {
            case PENDIENTE -> "Tu solicitud fue recibida. Podés editarla hasta que comience la evaluación.";
            case EN_EVALUACION -> "El Directorio está analizando tu proyecto. Ya no es posible modificarlo.";
            case APROBADA -> "¡Felicidades! Tu proyecto fue aprobado. El Parque se contactará para la asignación.";
            case RECHAZADA -> "Tu solicitud ha sido rechazada. Por favor, revisá tu correo para ver las observaciones.";
        };
    }
}

/*version que anda pero inestable
package com.unrn.gpiv.views.empresa;
import com.unrn.gpiv.common.EstadoSolicitud;
import com.unrn.gpiv.model.*;
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

        // 1. RECUPERAR DATOS DE SESIÓN (Como Usuario para ser más seguro)
        Usuario usuarioLogueado = (Usuario) VaadinSession.getCurrent().getAttribute("usuarioLogueado");

        if (!(usuarioLogueado instanceof RepresentanteEmpresa logueado)) {
            add(new H2("Acceso denegado. Esta vista es para empresas."));
            return;
        }

        // 2. EL CAMBIO CLAVE: Buscamos la SOLICITUD, no la Empresa
        // (Asegurate de haber agregado este método al EmpresaService)
        SolicitudRadicacion solicitud = empresaService.obtenerUltimaSolicitud(logueado);

        if (solicitud == null) {
            add(new H2("Aún no has presentado ningún proyecto."));
            Button btnIrForm = new Button("Cargar Solicitud", e -> getUI().ifPresent(ui -> ui.navigate("formulario-proyecto")));
            btnIrForm.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            add(btnIrForm);
            return;
        }

        // 3. Extraemos los datos de la solicitud
        ProyectoProductivo proyecto = solicitud.getProyecto();
        EstadoSolicitud estado = solicitud.getEstado();

        // --- UI ---
        H2 titulo = new H2("Mi Proyecto Productivo");

        // --- TARJETA DE ESTADO ---
        VerticalLayout statusCard = new VerticalLayout();
        statusCard.setWidthFull();
        statusCard.getStyle().set("background-color", "#f8f9fa").set("border-radius", "15px");
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

        // Regla: Solo se modifica si está en PENDIENTE
        if (estado != EstadoSolicitud.PENDIENTE) {
            btnModificar.setEnabled(false);
            btnModificar.setTooltipText("No se puede modificar un proyecto en evaluación.");
        }

        btnModificar.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("formulario-proyecto")));

        Button btnVerPDF = new Button("Ver PDF Solicitud", VaadinIcon.FILE.create());
        btnVerPDF.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        // TODO: Aquí conectarás el StreamResource para descargar el PDF de proyecto.getPdfProyecto()

        acciones.add(btnModificar, btnVerPDF);
        statusCard.add(sub, badgeEstado, infoEstado, acciones);

        // --- RESUMEN DE DATOS ---
        VerticalLayout datosProyecto = new VerticalLayout();
        datosProyecto.add(new H3("Resumen del Proyecto"));
        // Usamos los datos de la solicitud
        datosProyecto.add(new Paragraph("Razón Social: " + solicitud.getRazonSocialPretendida()));
        datosProyecto.add(new Paragraph("Proyecto: " + proyecto.getNombreProyecto()));
        datosProyecto.add(new Paragraph("Superficie Requerida: " + proyecto.getSuperficieRequerida() + " m²"));

        String servicios = proyecto.getServiciosNecesarios().stream()
                .map(Enum::name)
                .collect(Collectors.joining(", "));
        datosProyecto.add(new Paragraph("Servicios: " + (servicios.isEmpty() ? "Ninguno" : servicios)));

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
*/

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