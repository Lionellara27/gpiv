package com.unrn.gpiv.views.admin;

import com.unrn.gpiv.model.Empresa;
import com.unrn.gpiv.model.Lote;
import com.unrn.gpiv.common.EstadoLote;
import com.unrn.gpiv.service.EmpresaService;
import com.unrn.gpiv.service.LoteService; // Tu servicio de Spring
import com.unrn.gpiv.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
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

import java.time.LocalDate;

@PageTitle("Gestión de Lotes | SGPIV")
@Route(value = "admin/lotes", layout = MainLayout.class)
public class AdminLotesView extends VerticalLayout implements BeforeEnterObserver {

    private final LoteService loteService; // Inyectado por Spring
    private final EmpresaService empresaService; //inyectado en el constructor
    private Grid<Lote> grid = new Grid<>(Lote.class, false);
    
    // El Binder conecta el objeto Lote con los inputs del formulario
    private Binder<Lote> binder = new BeanValidationBinder<>(Lote.class);

    private TextField manzana = new TextField("Manzana");
    private TextField nroLote = new TextField("Nro. Lote");
    private NumberField superficie = new NumberField("Superficie (m2)");
    private ComboBox<EstadoLote> estado = new ComboBox<>("Estado", EstadoLote.values());
    private TextField ubicacion = new TextField("Ubicación");
    private TextArea caracteristicas = new TextArea("Características");
    private ComboBox<Empresa> empresaAsignada = new ComboBox<>("Asignar Empresa/Productor"); // se habilita cuando el estado del lotes es LIBRE

    private Button guardar = new Button("Guardar");
    private Button cancelar = new Button("Cancelar");
    private VerticalLayout panelEdicion;

    // Constructor con Inyección de Dependencias
    public AdminLotesView(LoteService loteService, EmpresaService empresaService) {
        this.loteService = loteService;
        this.empresaService = empresaService;
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        H2 titulo = new H2("Gestión de Lotes");
        Button btnAgregar = new Button("Agregar Lote", e ->
                getUI().ifPresent(ui -> ui.navigate(RegistrarLotesView.class))
        );
        btnAgregar.addThemeNames("primary", "success");

        HorizontalLayout header = new HorizontalLayout(titulo, btnAgregar);
        header.setVerticalComponentAlignment(Alignment.CENTER, btnAgregar);
        header.setSpacing(true);

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
        // Esto se ejecuta CADA VEZ que alguien entra a la ruta "admin/lotes" (para refrescar la tabla y aparezcan los datos registrados)
        actualizarTabla();
    }

    private VerticalLayout formularioDeEdicionLote() {
        FormLayout formLayout = new FormLayout();
        formLayout.add(manzana, nroLote, ubicacion, superficie, estado, caracteristicas, empresaAsignada);

        // caracteristicas ocupa dos columnas del formulario
        formLayout.setColspan(caracteristicas, 2);

        empresaAsignada.setItemLabelGenerator(Empresa::getRazonSocial);

        formLayout.setWidthFull();
        binder.bindInstanceFields(this);

        guardar.addThemeNames("primary");
        guardar.addClickListener(event -> accionGuardar());
        cancelar.addClickListener(event -> limpiarFormulario());

        HorizontalLayout toolbar = new HorizontalLayout(guardar, cancelar);

        VerticalLayout panel = new VerticalLayout(new H2("Detalles del Lote"), formLayout, toolbar);
        panel.setWidth("400px");
        panel.setMinWidth("350px");
        panel.setVisible(false);
        panel.setId("formulario-edicion");

        // Estilo para que se vea como un panel lateral
        panel.getStyle().set("border-left", "1px solid #e5e5e5");
        panel.getStyle().set("background-color", "#fcfcfc");

        return panel;
    }

    private void accionGuardar() {
        try {
            Lote lote = binder.getBean(); // fila del lote seleccionado
            if (lote.getEstado() == EstadoLote.LIBRE && empresaAsignada.getValue() != null) {
                lote.setEmpresa(empresaAsignada.getValue());
                lote.setFechaAsignacion(LocalDate.now()); // Registra la fecha de asignacion
                lote.setEstado(EstadoLote.OCUPADO);
            }

            loteService.guardar(lote);

            Notification.show("Lote guardado con exito", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            actualizarTabla();
            limpiarFormulario();
            
        } catch (Exception e) {
            Notification.show("Error al guardar: " + e.getMessage())
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void editarLote(Lote lote) {
        if (lote == null) {
            limpiarFormulario();
        } else {
            binder.setBean(lote);

            if (lote.getEstado() == EstadoLote.LIBRE) {
                empresaAsignada.setVisible(true);
                empresaAsignada.setEnabled(true);
                // cargo solo las empresas aprobadas que no tengan lote
                empresaAsignada.setItems(empresaService.listarAprobadasSinLote());
            } else {
                // Si esta ocupado, muestra la empresa que ya lo tiene pero deshabilitado
                empresaAsignada.setVisible(lote.getEmpresa() != null);
                empresaAsignada.setEnabled(false);
            }

            panelEdicion.setVisible(true);
        }
    }

    private void limpiarFormulario() {
        binder.setBean(null);
        empresaAsignada.setValue(null);
        empresaAsignada.setEnabled(true); // Lo rehabilitamos para la próxima seleccion
        grid.asSingleSelect().clear();
        panelEdicion.setVisible(false);
    }

    private void actualizarTabla() {
        grid.setItems(loteService.listarTodos());
    }

    private void tablaPrincipalLotes() {
        grid.setSizeFull();

        grid.addColumn(Lote::getManzana).setHeader("Manzana").setSortable(true);
        grid.addColumn(Lote::getNroLote).setHeader("Nro. Lote").setSortable(true);
        grid.addColumn(Lote::getUbicacion).setHeader("Ubicación");
        grid.addColumn(lote -> lote.getSuperficie() + " m²").setHeader("Superficie");
        grid.addColumn(Lote::getCaracteristicas).setHeader("Características");
        
        grid.addComponentColumn(lote -> {
            com.vaadin.flow.component.html.Span badge = new com.vaadin.flow.component.html.Span(lote.getEstado().toString());
            badge.getElement().getThemeList().add("badge");

            switch (lote.getEstado()) {
                case LIBRE:
                    badge.getElement().getThemeList().add("success");
                    break;
                case RESERVADO:
                    badge.getElement().getThemeList().add("contrast");
                    break;
                case OCUPADO:
                    badge.getStyle().set("background-color", "#e0f7fa").set("color", "#006064");
                    break;
                case OCIOSO:
                    badge.getElement().getThemeList().add("error");
                    break;
            }
            return badge;
        }).setHeader("Estado").setSortable(true);

        grid.getColumns().forEach(c -> c.setAutoWidth(true));
        grid.asSingleSelect().addValueChangeListener(event -> editarLote(event.getValue()));
    }
}