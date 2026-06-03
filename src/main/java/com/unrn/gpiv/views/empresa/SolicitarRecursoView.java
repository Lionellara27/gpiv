package com.unrn.gpiv.views.empresa;

import com.unrn.gpiv.common.EstadoEmpresa;
import com.unrn.gpiv.common.EstadoMovimientoRecurso;
import com.unrn.gpiv.model.*;
import com.unrn.gpiv.service.EmpresaService;
import com.unrn.gpiv.service.RecursoService;
import com.unrn.gpiv.service.SolicitudRecursoService;
import com.unrn.gpiv.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility;

@PageTitle("Herramientas y Recursos | SGPIV")
@Route(value = "empresa/herramientas-y-recursos", layout = MainLayout.class)
public class SolicitarRecursoView extends VerticalLayout {

    private final SolicitudRecursoService solicitudService;
    private final EmpresaService empresaService;
    private final RecursoService recursoService;

    private Empresa empresaLogueada;
    private Grid<Recurso> gridMisPrestamos = new Grid<>(Recurso.class, false);

    public SolicitarRecursoView(SolicitudRecursoService solicitudService, EmpresaService empresaService, RecursoService recursoService) {
        this.solicitudService = solicitudService;
        this.empresaService = empresaService;
        this.recursoService = recursoService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        getStyle().set("background-color", "var(--lumo-contrast-5pct)");

        H2 tituloGral = new H2("Herramientas y Recursos");
        tituloGral.addClassNames(
                LumoUtility.Margin.Bottom.NONE,
                LumoUtility.Margin.Top.SMALL,
                LumoUtility.TextColor.PRIMARY,
                LumoUtility.FontWeight.BOLD
        );
        add(tituloGral);

        if (!validarAccesoEmpresa()) {
            return;
        }

        HorizontalLayout layoutColumnas = new HorizontalLayout();
        layoutColumnas.setWidthFull();
        layoutColumnas.setSpacing(true);
        layoutColumnas.setAlignItems(Alignment.START);
        layoutColumnas.addClassNames(LumoUtility.Margin.Top.SMALL);

        // COLUMNA IZQUIERDA: TARJETA DE SOLICITUD
        VerticalLayout colIzquierda = new VerticalLayout();
        colIzquierda.setWidth("50%");
        colIzquierda.setSpacing(true);

        colIzquierda.setPadding(true);
        colIzquierda.addClassNames(
                LumoUtility.Background.BASE,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.Padding.MEDIUM
        );
        colIzquierda.getElement().getThemeList().add("shadow-sm");

        H3 subtituloIzquierdo = new H3("📝 Nueva Solicitud De Recurso");
        subtituloIzquierdo.addClassNames(LumoUtility.Margin.Top.NONE, LumoUtility.Margin.Bottom.SMALL, LumoUtility.Border.BOTTOM, LumoUtility.Padding.Bottom.SMALL);
        colIzquierda.add(subtituloIzquierdo);

        ComboBox<Item> cmbItems = new ComboBox<>("Seleccione el recurso que necesita");
        cmbItems.setItemLabelGenerator(Item::getNombre);
        cmbItems.setItems(recursoService.obtenerTodoElInventario().stream()
                .map(Recurso::getItem)
                .distinct()
                .toList());
        cmbItems.setWidthFull();

        IntegerField txtCantidad = new IntegerField("Cantidad");
        txtCantidad.setValue(1);
        txtCantidad.setMin(1);
        txtCantidad.setStepButtonsVisible(true);
        txtCantidad.setWidthFull();

        TextArea txtMotivo = new TextArea("Motivo de la solicitud *");
        txtMotivo.setPlaceholder("Describa brevemente para qué tarea requiere el recurso...");
        txtMotivo.setRequired(true);
        txtMotivo.setWidthFull();
        txtMotivo.setHeight("140px");

        Button btnEnviar = new Button("Enviar Pedido al Parque", VaadinIcon.PAPERPLANE.create(), e -> {
            if (cmbItems.getValue() == null || txtMotivo.getValue().trim().isEmpty()) {
                Notification.show("Por favor, complete los campos obligatorios.", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }
            try {
                solicitudService.crearSolicitud(empresaLogueada, cmbItems.getValue(), txtCantidad.getValue(), txtMotivo.getValue());
                Notification.show("Solicitud enviada con éxito. El Administrador ha sido notificado.", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                cmbItems.clear();
                txtMotivo.clear();
                txtCantidad.setValue(1);
            } catch (Exception ex) {
                Notification.show("Error: " + ex.getMessage(), 4000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        btnEnviar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button btnCancelar = new Button("Cancelar", e -> {
            cmbItems.clear();
            txtMotivo.clear();
            txtCantidad.setValue(1);
        });

        HorizontalLayout barraBotones = new HorizontalLayout(btnEnviar, btnCancelar);
        barraBotones.addClassNames(LumoUtility.Margin.Top.SMALL);
        colIzquierda.add(cmbItems, txtCantidad, txtMotivo, barraBotones);

        // COLUMNA DERECHA: TARJETA DE RECURSOS EN MI PODER
        VerticalLayout colDerecha = new VerticalLayout();
        colDerecha.setWidth("50%");
        colDerecha.setSpacing(true);

        colDerecha.setPadding(true);
        colDerecha.addClassNames(
                LumoUtility.Background.BASE,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.Padding.MEDIUM
        );
        colDerecha.getElement().getThemeList().add("shadow-sm");

        H3 subtituloDerecho = new H3("🛠️ Recursos/Herramientas en mi Poder");
        subtituloDerecho.addClassNames(LumoUtility.Margin.Top.NONE, LumoUtility.Margin.Bottom.NONE);

        Paragraph txtAyuda = new Paragraph("Avisá al Administrador cuando dejes de usar una herramienta para coordinar su devolución.");
        txtAyuda.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY, LumoUtility.Margin.Top.NONE, LumoUtility.Margin.Bottom.SMALL);

        colDerecha.add(subtituloDerecho, txtAyuda);

        gridMisPrestamos.setWidthFull();
        gridMisPrestamos.setHeight("355px");
        gridMisPrestamos.addThemeVariants();

        gridMisPrestamos.addColumn(recurso -> recurso.getItem().getNombre()).setHeader("Recurso").setSortable(true);
        gridMisPrestamos.addColumn(Recurso::getNumeroSerie).setHeader("Nro. Serie");
        gridMisPrestamos.addColumn(Recurso::getUbicacionFisica).setHeader("Ubicación");

        gridMisPrestamos.addComponentColumn(recurso -> {
            Button btnDevolver = new Button("Devolver", VaadinIcon.ARROW_BACKWARD.create(), e -> {
                try {
                    recurso.setEstadoMovimiento(EstadoMovimientoRecurso.A_DEVOLVER);
                    recursoService.guardarRecurso(recurso);

                    Notification.show("Aviso de devolución registrado", 4000, Notification.Position.TOP_CENTER)
                            .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                    actualizarTablaPrestamos();
                } catch (Exception ex) {
                    Notification.show("Error al devolver: " + ex.getMessage(), 4000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            });
            btnDevolver.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
            return btnDevolver;
        }).setHeader("Acción");

        colDerecha.add(gridMisPrestamos);

        layoutColumnas.add(colIzquierda, colDerecha);
        add(layoutColumnas);

        actualizarTablaPrestamos();
    }

    private void actualizarTablaPrestamos() {
        if (empresaLogueada != null) {
            // Buscamos solo lo que tiene esta empresa asignado en estado PRESTADO
            gridMisPrestamos.setItems(recursoService.listarRecursosPrestadosA(empresaLogueada));
        }
    }

    private boolean validarAccesoEmpresa() {
        Usuario usuario = (Usuario) VaadinSession.getCurrent().getAttribute("usuarioLogueado");
        if (!(usuario instanceof RepresentanteEmpresa logueado)) return false;

        empresaLogueada = empresaService.obtenerEmpresaPorRepresentante(logueado);
        if (empresaLogueada == null || (empresaLogueada.getEstadoEmpresa() != EstadoEmpresa.RADICADA && empresaLogueada.getEstadoEmpresa() != EstadoEmpresa.TITULADA)) {
            setSizeFull();
            setJustifyContentMode(JustifyContentMode.CENTER);
            setAlignItems(Alignment.CENTER);
            Span alerta = new Span("⚠️ Sección Exclusiva: Tu empresa debe estar Radicada o Titulada para operar con recursos.");
            alerta.addClassNames(LumoUtility.TextColor.ERROR, LumoUtility.FontWeight.BOLD);
            add(alerta);
            return false;
        }
        return true;
    }
}