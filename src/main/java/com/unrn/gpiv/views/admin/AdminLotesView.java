package com.unrn.gpiv.views.admin;

import com.unrn.gpiv.model.Empresa;
import com.unrn.gpiv.model.Lote;
import com.unrn.gpiv.common.EstadoLote;
import com.unrn.gpiv.service.EmpresaService;
import com.unrn.gpiv.service.LoteService;
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
    private ComboBox<Empresa> empresaAsignada = new ComboBox<>("Asignar Empresa/Productor");
    private ComboBox<EstadoLote> filtroEstado = new ComboBox<>("Filtrar por Estado", EstadoLote.values());

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

        // Configuracion del filtro
        filtroEstado.setPlaceholder("Todos los estados");
        filtroEstado.setClearButtonVisible(true); // la 'X' para limpiar el filtro rapido
        filtroEstado.addValueChangeListener(e -> filtrarLotes()); // Filtra en tiempo real al cambiar

        // filtro al lado del boton de agregar
        HorizontalLayout barraHerramientas = new HorizontalLayout(filtroEstado, btnAgregar);
        barraHerramientas.setAlignItems(Alignment.BASELINE);
        barraHerramientas.setSpacing(true);

        HorizontalLayout header = new HorizontalLayout(titulo, barraHerramientas);
        header.setWidthFull();
        header.setFlexGrow(1, titulo); // el filtro y el boton a la derecha
        header.setVerticalComponentAlignment(Alignment.CENTER, barraHerramientas);

        tablaPrincipalLotes();
        this.panelEdicion = formularioDeEdicionLote();

        HorizontalLayout content = new HorizontalLayout(grid, panelEdicion);
        content.setSizeFull();

        content.setFlexGrow(1, grid);
        content.setFlexGrow(0, panelEdicion);

        add(header, content); // header completo con el filtro incluido
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

        // Panel lateral
        panel.getStyle().set("border-left", "1px solid #e5e5e5");
        panel.getStyle().set("background-color", "#fcfcfc");

        return panel;
    }

    private void accionGuardar() {
        try {
            Lote lote = binder.getBean(); // Fila del lote seleccionado o editado

            if (empresaAsignada.getValue() != null) {
                // Asignamos la empresa elegida (puede ser la primera o una nueva)
                lote.setEmpresa(empresaAsignada.getValue());

                // Si estaba libre, ahora pasa a estar ocupado y le clavamos la fecha de hoy
                if (lote.getEstado() == EstadoLote.LIBRE) {
                    lote.setEstado(EstadoLote.OCUPADO);
                    lote.setFechaAsignacion(LocalDate.now());
                }
            } else {
                // Si el combo está vacío, significa que desvincularon la empresa del lote
                lote.setEmpresa(null);
                lote.setFechaAsignacion(null);
                lote.setEstado(EstadoLote.LIBRE);
            }

            // Persistimos el lote de forma directa
            loteService.guardar(lote);

            Notification.show("Lote guardado con éxito", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            actualizarTabla();
            limpiarFormulario();

        } catch (Exception e) {
            Notification.show("Error al guardar: " + e.getMessage(), 5000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void editarLote(Lote lote) {
        if (lote == null) {
            limpiarFormulario();
        } else {
            binder.setBean(lote);

            // Cargamos siempre todas las empresas aprobadas del Parque
            empresaAsignada.setItems(empresaService.listarTodasLasAprobadas());

            switch (lote.getEstado()) {
                case LIBRE:
                    empresaAsignada.setValue(null);
                    empresaAsignada.setEnabled(true);
                    empresaAsignada.setHelperText("Seleccione una empresa");
                    break;

                case RESERVADO:
                    empresaAsignada.setValue(lote.getEmpresa());
                    empresaAsignada.setEnabled(true);
                    empresaAsignada.setHelperText("Reserva en tramite. Se puede modificar o remover la empresa si es necesario");
                    break;

                case OCUPADO:
                    empresaAsignada.setValue(lote.getEmpresa());
                    empresaAsignada.setEnabled(false);
                    empresaAsignada.setHelperText("Lote activo");
                    break;

                case OCIOSO:
                    empresaAsignada.setValue(lote.getEmpresa());
                    empresaAsignada.setEnabled(false);
                    empresaAsignada.setHelperText("⚠️ Lote bajo alerta por inactividad");
                    break;
            }
            panelEdicion.setVisible(true);
        }
    }

    private void limpiarFormulario() {
        binder.setBean(null);
        empresaAsignada.setValue(null);
        empresaAsignada.setEnabled(true);// Lo rehabilitamos para la próxima seleccion
        grid.asSingleSelect().clear();
        panelEdicion.setVisible(false);
    }

    private void actualizarTabla() {
//        grid.setItems(loteService.listarTodos());
        if (filtroEstado != null && filtroEstado.getValue() != null) {
            filtrarLotes();
        } else {
            grid.setItems(loteService.listarTodos());
        }
    }

    private void filtrarLotes() {
        EstadoLote estadoSeleccionado = filtroEstado.getValue();
        if (estadoSeleccionado == null) {
            // Si se limpió el combo, mostramos todos de forma directa sin volver a evaluar
            grid.setItems(loteService.listarTodos());
        } else {
            grid.setItems(loteService.buscarPorEstado(estadoSeleccionado));
        }
    }

    private void tablaPrincipalLotes() {
        grid.setSizeFull();

        grid.addColumn(Lote::getManzana).setHeader("Manzana").setSortable(true);
        grid.addColumn(Lote::getNroLote).setHeader("Nro. Lote").setSortable(true);
        grid.addColumn(Lote::getUbicacion).setHeader("Ubicación");
        grid.addColumn(lote -> lote.getSuperficie() + " m²").setHeader("Superficie");
        // empresa asignada al lote
        grid.addColumn(lote -> lote.getEmpresa() != null ? lote.getEmpresa().getRazonSocial() : "Sin Asignar")
                .setHeader("Empresa Asignada")
                .setSortable(true);
        grid.addColumn(Lote::getCaracteristicas).setHeader("Características");
        
        grid.addComponentColumn(lote -> {
            com.vaadin.flow.component.html.Span badge = new com.vaadin.flow.component.html.Span(lote.getEstado().toString());
            badge.getElement().getThemeList().add("badge");

            switch (lote.getEstado()) {
                case LIBRE:
                    badge.getElement().getThemeList().add("badge success");
                    break;
                case RESERVADO:
                    badge.getElement().getThemeList().add("badge");
                    badge.getStyle().set("background-color", "#fff3e0").set("color", "#b78103");
                    break;
                case OCUPADO:
                    badge.getStyle().set("background-color", "#e0f7fa").set("color", "#006064");
                    break;
                case OCIOSO:
                    badge.getElement().getThemeList().add("badge error");
                    break;
            }

            return badge;
        }).setHeader("Estado").setSortable(true);

        grid.getColumns().forEach(c -> c.setAutoWidth(true));
        grid.asSingleSelect().addValueChangeListener(event -> editarLote(event.getValue()));
    }
}