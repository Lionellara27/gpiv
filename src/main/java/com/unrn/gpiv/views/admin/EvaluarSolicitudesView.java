package com.unrn.gpiv.views.admin;

import com.unrn.gpiv.common.EstadoSolicitud;
import com.unrn.gpiv.model.SolicitudRadicacion;
import com.unrn.gpiv.repository.SolicitudRadicacionRepository;
import com.unrn.gpiv.service.EmpresaService;
import com.unrn.gpiv.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.util.List;

@PageTitle("Evaluar Solicitudes | SGPIV")
@Route(value = "admin/evaluar", layout = MainLayout.class)
public class EvaluarSolicitudesView extends VerticalLayout {

    private final SolicitudRadicacionRepository solicitudRepo;
    private final EmpresaService empresaService;
    private final Grid<SolicitudRadicacion> grid = new Grid<>(SolicitudRadicacion.class, false);
    private String estadoActualFiltrado = "PENDIENTE";

    public EvaluarSolicitudesView(@Autowired SolicitudRadicacionRepository solicitudRepo,
                                  @Autowired EmpresaService empresaService) {
        this.solicitudRepo = solicitudRepo;
        this.empresaService = empresaService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);
        getStyle().set("background-color", "#f5f7fa");

        H2 titulo = new H2("Evaluación de Proyectos Productivos");
        titulo.getStyle().set("margin-bottom", "0");
        Paragraph subtitulo = new Paragraph("Bandeja de entrada del Administrador. Seleccioná una solicitud para evaluarla.");
        subtitulo.getStyle().set("color", "#666").set("margin-top", "0");

        VerticalLayout card = new VerticalLayout();
        card.setWidthFull();
        card.getStyle().set("background-color", "white").set("border-radius", "15px").set("padding", "2em");

        Tab tabPendientes = new Tab("Pendientes / Recibidas");
        Tab tabAprobadas = new Tab("Aprobadas");
        Tab tabRechazadas = new Tab("Rechazadas");
        Tabs tabs = new Tabs(tabPendientes, tabAprobadas, tabRechazadas);
        tabs.setWidthFull();

        tabs.addSelectedChangeListener(event -> {
            if (event.getSelectedTab() == tabPendientes) {
                estadoActualFiltrado = "PENDIENTE";
            } else if (event.getSelectedTab() == tabAprobadas) {
                estadoActualFiltrado = "APROBADA";
            } else {
                estadoActualFiltrado = "RECHAZADA";
            }
            filtrarGrilla();
        });

        grid.addColumn(SolicitudRadicacion::getRazonSocialPretendida).setHeader("Razón Social").setSortable(true).setAutoWidth(true);
        grid.addColumn(s -> s.getProyecto() != null ? s.getProyecto().getActividadPrincipal() : "Sin especificar").setHeader("Actividad Principal").setAutoWidth(true);
        grid.addComponentColumn(this::crearBadgeEstado).setHeader("Estado Actual").setAutoWidth(true);
        grid.addComponentColumn(this::crearBotoneraAcciones).setHeader("Acciones de Control").setAutoWidth(true);

        filtrarGrilla();

        card.add(tabs, grid);
        add(titulo, subtitulo, card);
    }

    private Span crearBadgeEstado(SolicitudRadicacion solicitud) {
        Span badge = new Span(solicitud.getEstado().name());
        badge.getStyle().set("padding", "5px 12px").set("border-radius", "20px")
                .set("font-weight", "bold").set("font-size", "0.85em").set("color", "white");

        // FIX: Agregamos el color para EN_EVALUACION
        switch (solicitud.getEstado()) {
            case PENDIENTE -> badge.getStyle().set("background-color", "#E67E22");
            case EN_EVALUACION -> badge.getStyle().set("background-color", "#0063BE"); // Azul
            case APROBADA -> badge.getStyle().set("background-color", "#009A3B");
            case RECHAZADA -> badge.getStyle().set("background-color", "#d9534f");
        }
        return badge;
    }

    private HorizontalLayout crearBotoneraAcciones(SolicitudRadicacion solicitud) {
        HorizontalLayout layout = new HorizontalLayout();

        // --- BOTÓN DE VER DETALLES (El ojo) ---
        Button btnVerDetalles = new Button(VaadinIcon.EYE.create());
        btnVerDetalles.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

        // MODIFICACIÓN ACÁ: Cambiamos estado al abrir
        btnVerDetalles.addClickListener(e -> {
            empresaService.marcarComoEnEvaluacion(solicitud.getId());
            filtrarGrilla();
            abrirDetallesSolicitud(solicitud); // Sigue abriendo el diálogo como siempre
        });

        layout.add(btnVerDetalles);

        // --- DESCARGA PDF ---
        if (solicitud.getProyecto() != null && solicitud.getProyecto().getPdfProyecto() != null) {
            StreamResource resource = new StreamResource(
                    solicitud.getProyecto().getNombreArchivoPdf() != null ? solicitud.getProyecto().getNombreArchivoPdf() : "proyecto.pdf",
                    () -> new ByteArrayInputStream(solicitud.getProyecto().getPdfProyecto())
            );
            Anchor linkDescarga = new Anchor(resource, "");
            linkDescarga.getElement().setAttribute("download", true);

            Button btnPdf = new Button(VaadinIcon.DOWNLOAD.create());
            btnPdf.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

            // MODIFICACIÓN ACÁ: También bloqueamos al descargar
            btnPdf.addClickListener(e -> {
                empresaService.marcarComoEnEvaluacion(solicitud.getId());
                filtrarGrilla();
            });

            linkDescarga.add(btnPdf);
            layout.add(linkDescarga);
        }

        // Acciones de Aprobación/Rechazo (Solo si está PENDIENTE o EN_EVALUACION)
        if (solicitud.getEstado() == EstadoSolicitud.PENDIENTE || solicitud.getEstado() == EstadoSolicitud.EN_EVALUACION) {

            Button btnAprobar = new Button(VaadinIcon.CHECK.create(), e -> {
                try {
                    empresaService.aprobarRadicacion(solicitud.getId());
                    Notification.show("¡Solicitud Aprobada!", 3000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    filtrarGrilla();
                } catch (Exception ex) {
                    Notification.show("Error: " + ex.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            });
            btnAprobar.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);

            Button btnRechazar = new Button(VaadinIcon.CLOSE.create(), e -> {
                solicitud.setEstado(EstadoSolicitud.RECHAZADA);
                solicitudRepo.save(solicitud);
                Notification.show("Solicitud rechazada.");
                filtrarGrilla();
            });
            btnRechazar.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

            layout.add(btnAprobar, btnRechazar);
        }

        return layout;
    }

    private void abrirDetallesSolicitud(SolicitudRadicacion solicitud) {
        com.vaadin.flow.component.dialog.Dialog dialog = new com.vaadin.flow.component.dialog.Dialog();
        dialog.setHeaderTitle("Detalles Técnicos: " + solicitud.getRazonSocialPretendida());
        dialog.setWidth("500px"); // Mantenemos el ancho para que no se vea choto

        VerticalLayout content = new VerticalLayout();
        content.setSpacing(false);
        content.setPadding(false);

        if (solicitud.getProyecto() != null) {
            var p = solicitud.getProyecto();

            // El estilo "belleza" para las etiquetas
            String labelStyle = "font-weight: bold; color: #666; margin-top: 10px;";

            // Agregamos la info con el formato lindo
            content.add(
                    crearEtiquetaLina("Superficie Requerida:", labelStyle),
                    new Span(p.getSuperficieRequerida() != null ? p.getSuperficieRequerida() : "No especificada"),

                    crearEtiquetaLina("Cantidad de Empleados:", labelStyle),
                    new Span(String.valueOf(p.getCantidadEmpleados())),

                    crearEtiquetaLina("Servicios Necesarios:", labelStyle),
                    crearListaServicios(p.getServiciosNecesarios()), // <--- Tus badges vuelven a brillar

                    crearEtiquetaLina("Impacto Ambiental y Residuos:", labelStyle),
                    new Paragraph(p.getImpactoAmbiental() != null ? p.getImpactoAmbiental() : "Sin descripción de impacto.")
            );
        }

        // LA LÓGICA QUE ANDA DE LUJO: El botón en el footer
        Button btnCerrar = new Button("Cerrar", e -> dialog.close());
        btnCerrar.addThemeVariants(ButtonVariant.LUMO_PRIMARY); // Le damos un toque de color al botón
        dialog.getFooter().add(btnCerrar);

        dialog.add(content);
        dialog.open();
    }

    // Un método auxiliar chiquito para que el código de arriba no sea un choclo
    private Span crearEtiquetaLina(String texto, String estilo) {
        Span label = new Span(texto);
        label.getElement().setAttribute("style", estilo);
        return label;
    }
    // Este es el famoso método que "no existe" todavía
    private HorizontalLayout crearListaServicios(java.util.Set<com.unrn.gpiv.common.TipoServicio> servicios) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSpacing(true);
        layout.getStyle().set("margin-top", "5px").set("margin-bottom", "10px");

        if (servicios == null || servicios.isEmpty()) {
            layout.add(new Span("Ninguno"));
        } else {
            servicios.forEach(s -> {
                // Creamos un Span para cada servicio
                Span badge = new Span(s.name());

                // Usamos los estilos predefinidos de Vaadin (Badge Pill)
                badge.getElement().getThemeList().add("badge pill small");

                // Le damos el color azul que combina con tu sistema
                badge.getStyle()
                        .set("background-color", "#eef4ff")
                        .set("color", "#0063BE")
                        .set("font-weight", "600");

                layout.add(badge);
            });
        }
        return layout;
    }

    /* este anda pero se ve re choto este boton! abrirdetalle
    private void abrirDetallesSolicitud(SolicitudRadicacion solicitud) {
        // ... (Este método queda igual que el tuyo, funciona perfecto) ...
        com.vaadin.flow.component.dialog.Dialog dialog = new com.vaadin.flow.component.dialog.Dialog();
        dialog.setHeaderTitle("Detalles Técnicos: " + solicitud.getRazonSocialPretendida());

        VerticalLayout content = new VerticalLayout();
        if (solicitud.getProyecto() != null) {
            var p = solicitud.getProyecto();
            content.add(new Span("Superficie: " + p.getSuperficieRequerida()),
                    new Span("Empleados: " + p.getCantidadEmpleados()),
                    new Span("Impacto: " + p.getImpactoAmbiental()));
        }

        Button btnCerrar = new Button("Cerrar", e -> dialog.close());
        dialog.getFooter().add(btnCerrar);
        dialog.add(content);
        dialog.open();
    }*/

    private void filtrarGrilla() {
        EstadoSolicitud estadoEnum = EstadoSolicitud.valueOf(estadoActualFiltrado);
        List<SolicitudRadicacion> resultado = solicitudRepo.findAll().stream()
                .filter(s -> s.getEstado() == estadoEnum ||
                        (estadoActualFiltrado.equals("PENDIENTE") && s.getEstado() == EstadoSolicitud.EN_EVALUACION))
                .toList();
        grid.setItems(resultado);
    }
}

/* FUNCIONA PERFECTO pero si tocas el ojito y el descargar no cambian estado
package com.unrn.gpiv.views.admin;


import com.unrn.gpiv.common.EstadoSolicitud;
import com.unrn.gpiv.model.SolicitudRadicacion;
import com.unrn.gpiv.repository.SolicitudRadicacionRepository;
import com.unrn.gpiv.service.EmpresaService;
import com.unrn.gpiv.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.util.List;

@PageTitle("Evaluar Solicitudes | SGPIV")
@Route(value = "admin/evaluar", layout = MainLayout.class)
public class EvaluarSolicitudesView extends VerticalLayout {

    private final SolicitudRadicacionRepository solicitudRepo;
    private final EmpresaService empresaService;
    private final Grid<SolicitudRadicacion> grid = new Grid<>(SolicitudRadicacion.class, false);
    private String estadoActualFiltrado = "PENDIENTE"; // Mapea tus estados reales de la BD

    public EvaluarSolicitudesView(@Autowired SolicitudRadicacionRepository solicitudRepo,
                                  @Autowired EmpresaService empresaService) {
        this.solicitudRepo = solicitudRepo;
        this.empresaService = empresaService;

        // --- 1. CONFIGURACIÓN GENERAL ---
        setSizeFull();
        setPadding(true);
        setSpacing(true);
        getStyle().set("background-color", "#f5f7fa");

        // --- 2. ENCABEZADO ---
        H2 titulo = new H2("Evaluación de Proyectos Productivos");
        titulo.getStyle().set("margin-bottom", "0");
        Paragraph subtitulo = new Paragraph("Bandeja de entrada del Administrador. Seleccioná una solicitud para descargar el PDF y cambiar su estado.");
        subtitulo.getStyle().set("color", "#666").set("margin-top", "0");

        // --- 3. TARJETA PRINCIPAL ---
        VerticalLayout card = new VerticalLayout();
        card.setWidthFull();
        card.getStyle().set("background-color", "white");
        card.getStyle().set("border-radius", "15px");
        card.getStyle().set("padding", "2em");
        card.getStyle().set("box-shadow", "0 4px 15px rgba(0,0,0,0.05)");

        // --- 4. PESTAÑAS DE NAVEGACIÓN ---
        Tab tabPendientes = new Tab("Pendientes / Recibidas");
        Tab tabAprobadas = new Tab("Aprobadas");
        Tab tabRechazadas = new Tab("Rechazadas");
        Tabs tabs = new Tabs(tabPendientes, tabAprobadas, tabRechazadas);
        tabs.setWidthFull();

        tabs.addSelectedChangeListener(event -> {
            if (event.getSelectedTab() == tabPendientes) {
                estadoActualFiltrado = "PENDIENTE";
            } else if (event.getSelectedTab() == tabAprobadas) {
                estadoActualFiltrado = "APROBADA";
            } else {
                estadoActualFiltrado = "RECHAZADA";
            }
            filtrarGrilla();
        });

        // --- 5. GRILLA (TABLA DE DATOS CONECTADA) ---
        grid.addColumn(SolicitudRadicacion::getRazonSocialPretendida).setHeader("Razón Social").setSortable(true).setAutoWidth(true);
        grid.addColumn(s -> s.getProyecto() != null ? s.getProyecto().getActividadPrincipal() : "Sin especificar").setHeader("Actividad Principal").setAutoWidth(true);
        grid.addComponentColumn(this::crearBadgeEstado).setHeader("Estado Actual").setAutoWidth(true);
        grid.addComponentColumn(this::crearBotoneraAcciones).setHeader("Acciones de Control").setAutoWidth(true);

        // --- 6. CARGAR DATOS POSTA DE LA BD ---
        filtrarGrilla();

        card.add(tabs, grid);
        add(titulo, subtitulo, card);
    }

    // --- MÉTODOS VISUALES Y ACCIONES REALES ---

    private Span crearBadgeEstado(SolicitudRadicacion solicitud) {
        Span badge = new Span(solicitud.getEstado().name());
        badge.getStyle().set("padding", "5px 12px");
        badge.getStyle().set("border-radius", "20px");
        badge.getStyle().set("font-weight", "bold");
        badge.getStyle().set("font-size", "0.85em");
        badge.getStyle().set("color", "white");

        if (solicitud.getEstado() == EstadoSolicitud.PENDIENTE) {
            badge.getStyle().set("background-color", "#E67E22"); // Naranja para pendiente de revisión
        } else if (solicitud.getEstado() == EstadoSolicitud.APROBADA) {
            badge.getStyle().set("background-color", "#009A3B"); // Verde
        } else {
            badge.getStyle().set("background-color", "#d9534f"); // Rojo
        }
        return badge;
    }

    private HorizontalLayout crearBotoneraAcciones(SolicitudRadicacion solicitud) {
        HorizontalLayout layout = new HorizontalLayout();

        // --- BOTÓN DE VER DETALLES (El ojo) ---
        Button btnVerDetalles = new Button(VaadinIcon.EYE.create());
        btnVerDetalles.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        btnVerDetalles.setTooltipText("Ver detalles del proyecto");
        btnVerDetalles.addClickListener(e -> abrirDetallesSolicitud(solicitud));

        layout.add(btnVerDetalles);

        // 1. DESCARGA REAL DEL PDF ASOCIADO AL PROYECTO
        if (solicitud.getProyecto() != null && solicitud.getProyecto().getPdfProyecto() != null) {
            StreamResource resource = new StreamResource(
                    solicitud.getProyecto().getNombreArchivoPdf() != null ? solicitud.getProyecto().getNombreArchivoPdf() : "proyecto.pdf",
                    () -> new ByteArrayInputStream(solicitud.getProyecto().getPdfProyecto())
            );
            Anchor linkDescarga = new Anchor(resource, "");
            linkDescarga.getElement().setAttribute("download", true);

            Button btnPdf = new Button(VaadinIcon.DOWNLOAD.create());
            btnPdf.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            btnPdf.setTooltipText("Descargar PDF Original");

            linkDescarga.add(btnPdf);
            layout.add(linkDescarga);
        }

        // 2. ACCIONES DE APROBACIÓN / RECHAZO (Solo si está PENDIENTE)
        if (solicitud.getEstado() == EstadoSolicitud.PENDIENTE) {

            Button btnAprobar = new Button(VaadinIcon.CHECK.create());
            btnAprobar.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
            btnAprobar.setTooltipText("Aprobar e iniciar Radicación");
            btnAprobar.addClickListener(e -> {
                try {
                    // Llama a tu EmpresaService.aprobarRadicacion(Long id)
                    empresaService.aprobarRadicacion(solicitud.getId());
                    Notification.show("¡Solicitud Aprobada! Empresa creada en sistema.", 3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    filtrarGrilla();
                } catch (Exception ex) {
                    Notification.show("Error al aprobar: " + ex.getMessage(), 4000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            });

            Button btnRechazar = new Button(VaadinIcon.CLOSE.create());
            btnRechazar.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
            btnRechazar.setTooltipText("Rechazar Solicitud");
            btnRechazar.addClickListener(e -> {
                try {
                    // Marcamos rechazo directo sin borrar de la base de datos (como pide la HU)
                    solicitud.setEstado(EstadoSolicitud.RECHAZADA);
                    solicitudRepo.save(solicitud);
                    Notification.show("Solicitud rechazada.", 3000, Notification.Position.MIDDLE);
                    filtrarGrilla();
                } catch (Exception ex) {
                    Notification.show("Error: " + ex.getMessage());
                }
            });

            layout.add(btnAprobar, btnRechazar);
        }

        return layout;
    }

    private void abrirDetallesSolicitud(SolicitudRadicacion solicitud) {
        com.vaadin.flow.component.dialog.Dialog dialog = new com.vaadin.flow.component.dialog.Dialog();
        dialog.setHeaderTitle("Detalles Técnicos: " + solicitud.getRazonSocialPretendida());
        dialog.setWidth("500px");

        VerticalLayout content = new VerticalLayout();
        content.setSpacing(false);
        content.setPadding(false);

        if (solicitud.getProyecto() != null) {
            var p = solicitud.getProyecto();

            // Estilo para las etiquetas
            String labelStyle = "font-weight: bold; color: #666; margin-top: 10px;";

            content.add(
                    new Span("Superficie Requerida:"),
                    new Span(p.getSuperficieRequerida() != null ? p.getSuperficieRequerida() : "No especificada"),

                    new Span("Cantidad de Empleados:"),
                    new Span(String.valueOf(p.getCantidadEmpleados())),

                    new Span("Servicios Necesarios:"),
                    crearListaServicios(p.getServiciosNecesarios()),

                    new Span("Impacto Ambiental y Residuos:"),
                    new Paragraph(p.getImpactoAmbiental() != null ? p.getImpactoAmbiental() : "Sin descripción de impacto.")
            );

            // Aplicamos el estilo a todos los Spans que actúan como labels (los impares)
            content.getChildren().filter(c -> c instanceof Span).forEach(c -> {
                if (content.indexOf(c) % 2 == 0) c.getElement().setAttribute("style", labelStyle);
            });
        }

        Button btnCerrar = new Button("Cerrar", e -> dialog.close());
        dialog.getFooter().add(btnCerrar);

        dialog.add(content);
        dialog.open();
    }

    // Método auxiliar para mostrar los servicios como badges
    private HorizontalLayout crearListaServicios(java.util.Set<com.unrn.gpiv.common.TipoServicio> servicios) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSpacing(true);
        if (servicios == null || servicios.isEmpty()) {
            layout.add(new Span("Ninguno"));
        } else {
            servicios.forEach(s -> {
                Span badge = new Span(s.name());
                badge.getElement().getThemeList().add("badge pill small");
                layout.add(badge);
            });
        }
        return layout;
    }

    // --- FILTRADO DIRECTO A LA BASE DE DATOS ---
    private void filtrarGrilla() {
        EstadoSolicitud estadoEnum = EstadoSolicitud.valueOf(estadoActualFiltrado);

        // Buscamos en el repositorio usando Spring Data JPA real filtrado por el Enum de Estado
        List<SolicitudRadicacion> resultado = solicitudRepo.findAll().stream()
                .filter(s -> s.getEstado() == estadoEnum)
                .toList();

        grid.setItems(resultado);
    }
}


*/

/*VERSION 1 CHOTINGA
package com.unrn.gpiv.views.admin;


import com.unrn.gpiv.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;

@PageTitle("Evaluar Solicitudes | SGPIV")
@Route(value = "admin/evaluar", layout = MainLayout.class)
public class EvaluarSolicitudesView extends VerticalLayout {

    // Lista de prueba (Mock) para que la vista funcione sin base de datos
    private List<SolicitudMock> listaDatos = new ArrayList<>();
    private Grid<SolicitudMock> grid = new Grid<>();

    public EvaluarSolicitudesView() {
        // --- 1. CONFIGURACIÓN GENERAL ---
        setSizeFull();
        setPadding(true);
        setSpacing(true);
        getStyle().set("background-color", "#f5f7fa");

        // --- 2. ENCABEZADO ---
        H2 titulo = new H2("Evaluación de Proyectos Productivos");
        titulo.getStyle().set("margin-bottom", "0");
        Paragraph subtitulo = new Paragraph("Bandeja de entrada del Directorio. Seleccioná una solicitud para descargar el PDF y cambiar su estado.");
        subtitulo.getStyle().set("color", "#666").set("margin-top", "0");

        // --- 3. TARJETA PRINCIPAL (Mantiene el estilo aesthetic) ---
        VerticalLayout card = new VerticalLayout();
        card.setWidthFull();
        card.getStyle().set("background-color", "white");
        card.getStyle().set("border-radius", "15px");
        card.getStyle().set("padding", "2em");
        card.getStyle().set("box-shadow", "0 4px 15px rgba(0,0,0,0.05)");

        // --- 4. PESTAÑAS DE NAVEGACIÓN ---
        Tab tabRecibidas = new Tab("Nuevas Recibidas (2)");
        Tab tabEnEvaluacion = new Tab("En Evaluación (1)");
        Tab tabHistorial = new Tab("Historial Aprobadas/Rechazadas (2)");
        Tabs tabs = new Tabs(tabRecibidas, tabEnEvaluacion, tabHistorial);
        tabs.setWidthFull();

        // Lógica para filtrar la tabla cuando hacés clic en una pestaña
        tabs.addSelectedChangeListener(event -> {
            if (event.getSelectedTab() == tabRecibidas) {
                filtrarGrilla("RECIBIDA");
            } else if (event.getSelectedTab() == tabEnEvaluacion) {
                filtrarGrilla("EN_EVALUACION");
            } else {
                filtrarGrilla("HISTORIAL");
            }
        });

        // --- 5. GRILLA (TABLA DE DATOS) ---
        grid.addColumn(SolicitudMock::getEmpresa).setHeader("Razón Social").setSortable(true).setAutoWidth(true);
        grid.addColumn(SolicitudMock::getRubro).setHeader("Rubro / Actividad").setAutoWidth(true);
        grid.addComponentColumn(this::crearBadgeEstado).setHeader("Estado Actual").setAutoWidth(true);
        grid.addComponentColumn(this::crearBotoneraAcciones).setHeader("Acciones de Directorio").setAutoWidth(true);

        cargarDatosDePrueba();
        filtrarGrilla("RECIBIDA"); // Mostrar las nuevas por defecto

        card.add(tabs, grid);
        add(titulo, subtitulo, card);
    }

    // --- MÉTODOS VISUALES ---

    private Span crearBadgeEstado(SolicitudMock solicitud) {
        Span badge = new Span(solicitud.getEstado().replace("_", " "));
        badge.getStyle().set("padding", "5px 12px");
        badge.getStyle().set("border-radius", "20px");
        badge.getStyle().set("font-weight", "bold");
        badge.getStyle().set("font-size", "0.85em");
        badge.getStyle().set("color", "white");

        switch (solicitud.getEstado()) {
            case "RECIBIDA":
                badge.getStyle().set("background-color", "#6c757d"); // Gris
                break;
            case "EN_EVALUACION":
                badge.getStyle().set("background-color", "#E67E22"); // Naranja
                break;
            case "APROBADA":
                badge.getStyle().set("background-color", "#009A3B"); // Verde
                break;
            case "RECHAZADA":
                badge.getStyle().set("background-color", "#d9534f"); // Rojo
                break;
        }
        return badge;
    }

    private HorizontalLayout crearBotoneraAcciones(SolicitudMock solicitud) {
        HorizontalLayout layout = new HorizontalLayout();

        Button btnPdf = new Button(VaadinIcon.DOWNLOAD.create());
        btnPdf.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        btnPdf.setTooltipText("Descargar PDF del Proyecto");

        Button btnEvaluar = new Button("Revisar", VaadinIcon.EYE.create());
        btnEvaluar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button btnAprobar = new Button(VaadinIcon.CHECK.create());
        btnAprobar.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        btnAprobar.setTooltipText("Aprobar Radicación");

        Button btnRechazar = new Button(VaadinIcon.CLOSE.create());
        btnRechazar.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        btnRechazar.setTooltipText("Rechazar");

        // Dependiendo del estado, mostramos unos botones u otros
        if (solicitud.getEstado().equals("RECIBIDA")) {
            layout.add(btnPdf, btnEvaluar);
        } else if (solicitud.getEstado().equals("EN_EVALUACION")) {
            layout.add(btnPdf, btnAprobar, btnRechazar);
        } else {
            layout.add(btnPdf); // Si ya es historial, solo dejamos ver el PDF
        }

        return layout;
    }

    // --- MÉTODOS DE DATOS (MOCK) ---

    private void cargarDatosDePrueba() {
        listaDatos.add(new SolicitudMock("Metalúrgica Viedma", "Metalurgia pesada", "RECIBIDA"));
        listaDatos.add(new SolicitudMock("Maderera Comarca", "Carpintería", "RECIBIDA"));
        listaDatos.add(new SolicitudMock("Logística del Sur", "Logística y Transporte", "EN_EVALUACION"));
        listaDatos.add(new SolicitudMock("AgroPatagonia", "Agroquímicos", "APROBADA"));
        listaDatos.add(new SolicitudMock("TechSolutions", "Software", "RECHAZADA"));
    }

    private void filtrarGrilla(String filtroEstado) {
        if (filtroEstado.equals("HISTORIAL")) {
            grid.setItems(listaDatos.stream()
                    .filter(s -> s.getEstado().equals("APROBADA") || s.getEstado().equals("RECHAZADA"))
                    .toList());
        } else {
            grid.setItems(listaDatos.stream()
                    .filter(s -> s.getEstado().equals(filtroEstado))
                    .toList());
        }
    }

    // Clase interna falsa para la vista
    private static class SolicitudMock {
        private String empresa;
        private String rubro;
        private String estado;

        public SolicitudMock(String empresa, String rubro, String estado) {
            this.empresa = empresa;
            this.rubro = rubro;
            this.estado = estado;
        }
        public String getEmpresa() { return empresa; }
        public String getRubro() { return rubro; }
        public String getEstado() { return estado; }
    }
}*/