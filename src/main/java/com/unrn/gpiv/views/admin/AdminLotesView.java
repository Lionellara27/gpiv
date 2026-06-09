package com.unrn.gpiv.views.admin;

import com.unrn.gpiv.model.Lote;
import com.unrn.gpiv.common.EstadoLote;
import com.unrn.gpiv.service.EmpresaService;
import com.unrn.gpiv.service.HistorialService;
import com.unrn.gpiv.service.LoteService;
import com.unrn.gpiv.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Gestión de Lotes | SGPIV")
@Route(value = "admin/lotes", layout = MainLayout.class)
public class AdminLotesView extends VerticalLayout implements BeforeEnterObserver {

    private final HistorialService historialService;
    private final LoteService loteService;
    private final EmpresaService empresaService;
    private Grid<Lote> grid = new Grid<>(Lote.class, false);
    private Binder<Lote> binder = new BeanValidationBinder<>(Lote.class);

    private TextField manzana = new TextField("Manzana");
    private TextField nroLote = new TextField("Nro. Lote");
    private NumberField superficie = new NumberField("Superficie (m²)");
    private ComboBox<EstadoLote> estado = new ComboBox<>("Estado", EstadoLote.values());
    private TextField ubicacion = new TextField("Ubicación");
    private TextArea caracteristicas = new TextArea("Características");

    // ✅ CAMBIO: De ComboBox a Span (solo lectura, no editable)
    private Span empresaAsignada = new Span();

    private ComboBox<EstadoLote> filtroEstado = new ComboBox<>("Filtrar por Estado", EstadoLote.values());

    private Button guardar = new Button("Guardar");
    private Button cancelar = new Button("Cancelar");
    private Button editar = new Button("Editar Lote");
    private Button eliminar = new Button("Eliminar Lote");
    // ✅ CAMBIO: REMOVER botón desasignar - desadjudicación se hace desde EmpresaDetalleView

    private VerticalLayout panelEdicion;

    public AdminLotesView(LoteService loteService, EmpresaService empresaService, HistorialService historialService) {
        this.loteService = loteService;
        this.empresaService = empresaService;
        this.historialService = historialService;
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        H2 titulo = new H2("Gestión de Lotes");
        Button btnAgregar = new Button("Agregar Lote", e ->
                getUI().ifPresent(ui -> ui.navigate(RegistrarLotesView.class))
        );
        btnAgregar.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);

        filtroEstado.setPlaceholder("Todos los estados");
        filtroEstado.setClearButtonVisible(true);
        filtroEstado.addValueChangeListener(e -> filtrarLotes());

        HorizontalLayout barraHerramientas = new HorizontalLayout(filtroEstado, btnAgregar);
        barraHerramientas.setAlignItems(Alignment.BASELINE);
        barraHerramientas.setSpacing(true);

        HorizontalLayout header = new HorizontalLayout(titulo, barraHerramientas);
        header.setWidthFull();
        header.setFlexGrow(1, titulo);
        header.setVerticalComponentAlignment(Alignment.CENTER, barraHerramientas);

        tablaPrincipalLotes();
        this.panelEdicion = formularioDeEdicionLote();

        HorizontalLayout content = new HorizontalLayout(grid, panelEdicion);
        content.setSizeFull();
        content.setFlexGrow(1, grid);
        content.setFlexGrow(0, panelEdicion);

        add(header, content);
        actualizarTabla();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        actualizarTabla();
    }

    private VerticalLayout formularioDeEdicionLote() {
        FormLayout formLayout = new FormLayout();
        // ✅ CAMBIO: No agregar empresaAsignada al formulario - solo lectura
        formLayout.add(manzana, nroLote, ubicacion, superficie, estado, caracteristicas);
        formLayout.setColspan(caracteristicas, 2);

        empresaAsignada.getStyle().set("font-weight", "500");

        formLayout.setWidthFull();
        binder.bindInstanceFields(this);

        guardar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        guardar.addClickListener(event -> accionGuardar());
        cancelar.addClickListener(event -> limpiarFormulario());

        editar.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        editar.addClickListener(event -> activarModoEdicion(true));

        eliminar.addThemeVariants(ButtonVariant.LUMO_ERROR);
        eliminar.addClickListener(event -> accionEliminar());

        // ✅ CAMBIO: Solo mostrar botones Editar y Eliminar
        HorizontalLayout accionesPrincipales = new HorizontalLayout(editar, eliminar);
        HorizontalLayout barraGuardado = new HorizontalLayout(guardar, cancelar);

        // ✅ NUEVA SECCIÓN: Información sobre empresa asignada
        H3 tituloEmpresa = new H3("Empresa Asignada");
        VerticalLayout infoEmpresa = new VerticalLayout(tituloEmpresa, empresaAsignada);
        infoEmpresa.setPadding(false);
        infoEmpresa.setSpacing(false);

        VerticalLayout panel = new VerticalLayout(
                new H2("Detalles del Lote"),
                formLayout,
                infoEmpresa,
                accionesPrincipales,
                barraGuardado
        );
        panel.setWidth("400px");
        panel.setMinWidth("350px");
        panel.setVisible(false);
        panel.setId("formulario-edicion");

        panel.getStyle().set("border-left", "1px solid #e5e5e5");
        panel.getStyle().set("background-color", "#fcfcfc");

        return panel;
    }

    private void activarModoEdicion(boolean editable) {
        manzana.setReadOnly(!editable);
        nroLote.setReadOnly(!editable);
        ubicacion.setReadOnly(!editable);
        superficie.setReadOnly(!editable);
        estado.setReadOnly(!editable);
        caracteristicas.setReadOnly(!editable);

        guardar.setVisible(editable);
        cancelar.setVisible(editable);
        editar.setVisible(!editable);
    }

    private void accionGuardar() {
        try {
            Lote lote = grid.asSingleSelect().getValue();
            if (lote == null) return;

            if (binder.writeBeanIfValid(lote)) {
                loteService.guardar(lote);
                Notification.show("Lote actualizado con éxito", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                actualizarTabla();
                limpiarFormulario();
            }
        } catch (Exception e) {
            Notification.show("Error al guardar: " + e.getMessage(), 5000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void accionEliminar() {
        Lote lote = grid.asSingleSelect().getValue();
        if (lote == null) return;

        if (lote.getEmpresa() != null) {
            Notification.show("No se puede eliminar un lote que tiene una empresa asignada", 4000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        try {
            loteService.eliminar(lote);
            Notification.show("Lote eliminado con éxito", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            actualizarTabla();
            limpiarFormulario();
        } catch (Exception e) {
            Notification.show("Error al eliminar: " + e.getMessage(), 5000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void editarLote(Lote lote) {
        if (lote == null) {
            limpiarFormulario();
        } else {
            binder.setBean(lote);
            // ✅ CAMBIO: Ya no cargamos empresas ni permitimos seleccionar

            // Bloqueamos los atributos estructurales por defecto al seleccionar de la tabla
            activarModoEdicion(false);

            // ✅ CAMBIO: Mostrar información de empresa asignada (solo lectura)
            if (lote.getEmpresa() != null) {
                empresaAsignada.setText("Empresa: " + lote.getEmpresa().getRazonSocial() + " (Adjudicado)");
                empresaAsignada.getStyle().set("color", "var(--color-text-info)");
                // Solo permitir editar si está LIBRE
                editar.setEnabled(lote.getEstado() == EstadoLote.LIBRE);
                editar.setTooltipText(lote.getEstado() == EstadoLote.LIBRE ?
                        "Editar propiedades del lote" :
                        "No se puede editar un lote asignado");
            } else {
                empresaAsignada.setText("Sin asignar - Adjudica desde 'Evaluar Solicitudes'");
                empresaAsignada.getStyle().set("color", "var(--color-text-warning)");
                editar.setEnabled(true);
                editar.setTooltipText("Editar propiedades del lote");
            }

            // ✅ CAMBIO: Botones guardar y desasignar SIEMPRE ocultos
            guardar.setVisible(false);
            cancelar.setVisible(true);
            // desasignar eliminado - se hace desde EmpresaDetalleView

            panelEdicion.setVisible(true);
        }
    }

    private void limpiarFormulario() {
        binder.setBean(null);
        grid.asSingleSelect().clear();
        panelEdicion.setVisible(false);
    }

    private void actualizarTabla() {
        if (filtroEstado != null && filtroEstado.getValue() != null) {
            filtrarLotes();
        } else {
            grid.setItems(loteService.listarTodos());
        }
    }

    private void filtrarLotes() {
        EstadoLote estadoSeleccionado = filtroEstado.getValue();
        if (estadoSeleccionado == null) {
            grid.setItems(loteService.listarTodos());
        } else {
            grid.setItems(loteService.listarTodos().stream().filter(l -> l.getEstado() == estadoSeleccionado).toList());
        }
    }

    private void tablaPrincipalLotes() {
        grid.setSizeFull();
        grid.addColumn(Lote::getManzana).setHeader("Manzana").setSortable(true);
        grid.addColumn(Lote::getNroLote).setHeader("Nro. Lote").setSortable(true);
        grid.addColumn(Lote::getUbicacion).setHeader("Ubicación");
        grid.addColumn(lote -> lote.getSuperficie() + " m²").setHeader("Superficie");
        grid.addColumn(lote -> lote.getEmpresa() != null ? lote.getEmpresa().getRazonSocial() : "Sin Asignar")
                .setHeader("Empresa Asignada").setSortable(true);
        grid.addColumn(Lote::getCaracteristicas).setHeader("Características");

        grid.addComponentColumn(lote -> {
            com.vaadin.flow.component.html.Span badge = new com.vaadin.flow.component.html.Span(lote.getEstado().toString());
            badge.getElement().getThemeList().add("badge");
            switch (lote.getEstado()) {
                case LIBRE: badge.getElement().getThemeList().add("badge success"); break;
                case RESERVADO: badge.getStyle().set("background-color", "#fff3e0").set("color", "#b78103"); break;
                case OCUPADO: badge.getStyle().set("background-color", "#e0f7fa").set("color", "#006064"); break;
                case OCIOSO: badge.getElement().getThemeList().add("badge error"); break;
            }
            return badge;
        }).setHeader("Estado").setSortable(true);

        grid.getColumns().forEach(c -> c.setAutoWidth(true));
        grid.asSingleSelect().addValueChangeListener(event -> editarLote(event.getValue()));
    }
}