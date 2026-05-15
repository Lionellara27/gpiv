package com.unrn.gpiv.views.admin;

import com.unrn.gpiv.model.Lote;
import com.unrn.gpiv.common.EstadoLote;
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
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Gestión de Lotes | SGPIV")
@Route(value = "admin/lotes", layout = MainLayout.class)
public class AdminLotesView extends VerticalLayout {

    private final LoteService loteService; // Inyectado por Spring
    private Grid<Lote> grid = new Grid<>(Lote.class, false);
    
    // El Binder conecta el objeto Lote con los inputs del formulario
    private Binder<Lote> binder = new BeanValidationBinder<>(Lote.class);

    private TextField manzana = new TextField("Manzana");
    private TextField nroLote = new TextField("Nro. Lote");
    private NumberField superficie = new NumberField("Superficie (m2)");
    private ComboBox<EstadoLote> estado = new ComboBox<>("Estado", EstadoLote.values());

    private Button guardar = new Button("Guardar");
    private Button cancelar = new Button("Cancelar");

    // Constructor con Inyección de Dependencias
    public AdminLotesView(LoteService loteService) {
        this.loteService = loteService;
        setSizeFull();

        // Creamos el encabezado con el título y el botón al lado
        H2 titulo = new H2("Gestión de Lotes");
        Button btnAgregar = new Button("Agregar Lote", e -> 
            getUI().ifPresent(ui -> ui.navigate(RegistrarLotesView.class))
        );
        btnAgregar.addThemeNames("primary", "success");

        HorizontalLayout header = new HorizontalLayout(titulo, btnAgregar);
        header.setVerticalComponentAlignment(Alignment.CENTER, btnAgregar); // Alinea el botón con el texto
        header.setSpacing(true);

        modificarGrillaLote();
        
        HorizontalLayout content = new HorizontalLayout(grid, crearFormulario());
        content.setSizeFull();

        add(header, content); // Agregamos el nuevo encabezado
        actualizarTabla();
    }

    private VerticalLayout crearFormulario() {
        FormLayout formLayout = new FormLayout(manzana, nroLote, superficie, estado);
        
        // Vinculamos automáticamente los campos por nombre
        binder.bindInstanceFields(this);

        guardar.addThemeNames("primary");
        guardar.addClickListener(event -> accionGuardar());
        
        cancelar.addClickListener(event -> limpiarFormulario());

        HorizontalLayout toolbar = new HorizontalLayout(guardar, cancelar);
        VerticalLayout panel = new VerticalLayout(new H2("Detalles del Lote"), formLayout, toolbar);
        panel.setWidth("350px");
        panel.setVisible(false); // Oculto hasta que se seleccione algo
        panel.setId("formulario-edicion");
        
        return panel;
    }

    private void accionGuardar() {
        try {
            Lote lote = binder.getBean(); // Obtenemos el lote que se está editando
            
            // 1. Guardar en la Base de Datos a través del servicio
            loteService.guardar(lote);
            
            // 2. Feedback al usuario
            Notification.show("Lote guardado con éxito")
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            
            // 3. Refrescar la interfaz
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
            binder.setBean(lote); // Mapea los datos del lote a los campos de texto
            guardar.getParent().get().getParent().get().setVisible(true); // Muestra el panel
        }
    }

    private void limpiarFormulario() {
        binder.setBean(null);
        grid.asSingleSelect().clear();
        guardar.getParent().get().getParent().get().setVisible(false);
    }

    private void actualizarTabla() {
        grid.setItems(loteService.listarTodos());
    }

    private void modificarGrillaLote() {
        grid.setSizeFull();
        grid.addColumn(Lote::getManzana).setHeader("Manzana");
        grid.addColumn(Lote::getNroLote).setHeader("Nro. Lote");
        grid.addColumn(Lote::getSuperficie).setHeader("Superficie");
        grid.addColumn(Lote::getEstado).setHeader("Estado");
        grid.getColumns().forEach(c -> c.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(event -> editarLote(event.getValue()));
    }
}