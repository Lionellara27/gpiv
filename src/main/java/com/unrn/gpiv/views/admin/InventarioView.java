package com.unrn.gpiv.views.admin;

import com.unrn.gpiv.common.EstadoConservacionRecurso;
import com.unrn.gpiv.common.EstadoMovimientoRecurso;
import com.unrn.gpiv.model.Empresa;
import com.unrn.gpiv.model.Item;
import com.unrn.gpiv.model.Recurso;
import com.unrn.gpiv.service.EmpresaService;
import com.unrn.gpiv.service.InventarioService;
import com.unrn.gpiv.service.RecursoService;
import com.unrn.gpiv.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.format.DateTimeFormatter; // 🟢 IMPORTANTE PARA LA FECHA

@PageTitle("Inventario | SGPIV")
@Route(value = "admin/inventario", layout = MainLayout.class)
public class InventarioView extends VerticalLayout {


    //para ver si puedo hacer anda el boton de modificar
    @Autowired
    private EmpresaService empresaService;
    //
    private final RecursoService recursoService;
    private final InventarioService inventarioService;
    private Grid<Recurso> grid;
    private TextField filterText = new TextField();

    public InventarioView(@Autowired RecursoService recursoService, @Autowired InventarioService inventarioService) {
        this.recursoService = recursoService;
        this.inventarioService = inventarioService;

        setSizeFull();
        setPadding(true);

        H2 title = new H2("Gestión de Inventario de Recursos");

        add(title, getToolbar());
        configureGrid();
        add(grid);

        actualizarLista();
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Buscar por categoría...");
        filterText.setPrefixComponent(VaadinIcon.SEARCH.create());
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> actualizarLista());

        Button addBtn = new Button("Nuevo Recurso", VaadinIcon.PLUS.create());
        addBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        addBtn.addClickListener(e -> abrirFormularioNuevo());

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addBtn);
        toolbar.setWidthFull();
        return toolbar;
    }

    private void configureGrid() {
        grid = new Grid<>(Recurso.class, false);
        grid.setSizeFull();

        // 1. FECHA
        grid.addColumn(r -> r.getFechaRegistro() != null ? r.getFechaRegistro().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "-")
                .setHeader("Fecha").setSortable(true).setWidth("120px").setFlexGrow(0);

        // 2. NOMBRE
        grid.addColumn(r -> r.getItem() != null ? r.getItem().getNombre() : "S/N")
                .setHeader("Nombre").setSortable(true);

        // 3. CATEGORÍA
        grid.addColumn(r -> r.getItem() != null ? r.getItem().getCategoria() : "Sin Cat")
                .setHeader("Categoría").setSortable(true);

        // 4. ESTADO FÍSICO (Badge)
        grid.addComponentColumn(this::createEstadoBadge).setHeader("Estado Físico").setWidth("150px").setFlexGrow(0);

        // 5. ESTADO MOVIMIENTO (Nuevo: Disponible/Prestado)
        // Acá podés poner un texto simple o un badge de color también si querés
        grid.addColumn(Recurso::getEstadoMovimiento).setHeader("Estado").setSortable(true);

        // 6. POSEEDOR (El nuevo campo clave)
        // Si prestadoA es null, significa que está en el parque/disponible
        grid.addColumn(r -> r.getPrestadoA() != null ? r.getPrestadoA().getRazonSocial() : "El Parque")
                .setHeader("Poseedor").setSortable(true);

        // 7. UBICACIÓN
        grid.addColumn(Recurso::getUbicacionFisica).setHeader("Ubicación").setSortable(true);

        // 8. ACCIONES (Lápiz y Tacho)
        grid.addComponentColumn(this::createActionButtons).setHeader("Acciones").setWidth("120px").setFlexGrow(0);
    }

    /* columna que se que SI ANDA
    private void configureGrid() {
        grid = new Grid<>(Recurso.class, false);
        grid.setSizeFull();

        // 🎯 1. LA COLUMNA FECHA (Exactamente donde la pediste)
        grid.addColumn(r -> r.getFechaRegistro() != null ? r.getFechaRegistro().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "-")
                .setHeader("Fecha").setSortable(true).setWidth("120px").setFlexGrow(0);

        // 2. Nombre
        grid.addColumn(r -> r.getItem() != null ? r.getItem().getNombre() : "S/N")
                .setHeader("Nombre").setSortable(true);

        // 3. Categoría
        grid.addColumn(r -> r.getItem() != null ? r.getItem().getCategoria() : "Sin Cat")
                .setHeader("Categoría").setSortable(true);

        // 4. Estado Físico
        grid.addComponentColumn(this::createEstadoBadge).setHeader("Estado").setWidth("150px").setFlexGrow(0);

        // 5. Ubicación
        grid.addColumn(Recurso::getUbicacionFisica).setHeader("Ubicación").setSortable(true);

        // 6. Acciones
        grid.addComponentColumn(this::createActionButtons).setHeader("Acciones").setWidth("120px").setFlexGrow(0);
    }
*/
    private Span createEstadoBadge(Recurso r) {
        String estado = r.getEstadoMovimiento().name();
        Span badge = new Span(estado);
        badge.getStyle().set("padding", "3px 8px").set("border-radius", "12px")
                .set("font-size", "0.85em").set("font-weight", "bold").set("color", "white");

        switch (r.getEstadoMovimiento()) {
            case DISPONIBLE -> badge.getStyle().set("background-color", "#10b981");
            case PRESTADO -> badge.getStyle().set("background-color", "#3b82f6");
            case A_DEVOLVER -> badge.getStyle().set("background-color", "#f59e0b");
            case EXTRAVIADO -> badge.getStyle().set("background-color", "#ef4444");
        }
        return badge;
    }

    private HorizontalLayout createActionButtons(Recurso r) {
        //boton de modificar
        Button btnEdit = new Button(VaadinIcon.PENCIL.create());
        btnEdit.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_TERTIARY);
        btnEdit.setTooltipText("Modificar estado/poseedor");
        btnEdit.addClickListener(e -> abrirEditorRecurso(r));

        //logica del boton para elimianr!
        Button btnDelete = new Button(VaadinIcon.TRASH.create());
        btnDelete.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_TERTIARY);
        btnDelete.setTooltipText("Eliminar Recurso");

        btnDelete.addClickListener(e -> {
            Dialog confirmDialog = new Dialog();
            confirmDialog.setHeaderTitle("Confirmar Eliminación");
            confirmDialog.add("¿Seguro que desea dar de baja este recurso? Esta acción no se puede deshacer.");

            Button btnConfirmar = new Button("Eliminar", click -> {
                try {
                    recursoService.darDeBaja(r.getId());
                    actualizarLista();
                    Notification.show("Recurso eliminado del inventario.").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    confirmDialog.close();
                } catch (Exception ex) {
                    Notification.show("Error: " + ex.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            });
            btnConfirmar.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
            Button btnCancelar = new Button("Cancelar", c -> confirmDialog.close());

            confirmDialog.getFooter().add(btnConfirmar, btnCancelar);
            confirmDialog.open();
        });

        return new HorizontalLayout(btnDelete, btnEdit);
    }

    // --- MODAL PRINCIPAL: ALTA DE RECURSO FÍSICO ---
    private void abrirFormularioNuevo() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Registrar Nueva Existencia");
        dialog.setMinWidth("450px");

        FormLayout form = new FormLayout();

        ComboBox<Item> itemCombo = new ComboBox<>("Ítem Base (Catálogo)");
        itemCombo.setItems(inventarioService.obtenerTodosLosItemsCatalogo());
        itemCombo.setItemLabelGenerator(Item::getNombre);
        itemCombo.setRequired(true);
        itemCombo.setWidthFull();

        Button btnNuevoMolde = new Button(VaadinIcon.PLUS.create());
        btnNuevoMolde.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
        btnNuevoMolde.setTooltipText("Crear nuevo ítem en el catálogo");
        btnNuevoMolde.addClickListener(e -> abrirFormularioNuevoMolde(itemCombo));

        HorizontalLayout comboLayout = new HorizontalLayout(itemCombo, btnNuevoMolde);
        comboLayout.setDefaultVerticalComponentAlignment(Alignment.BASELINE);

        TextField txtSerie = new TextField("Número de Serie / Código");
        TextField txtUbicacion = new TextField("Ubicación en Parque");

        ComboBox<EstadoConservacionRecurso> conservacionCombo = new ComboBox<>("Estado de Conservación");
        conservacionCombo.setItems(EstadoConservacionRecurso.values());
        conservacionCombo.setValue(EstadoConservacionRecurso.NUEVO);

        form.add(comboLayout, txtSerie, txtUbicacion, conservacionCombo);

        Button saveButton = new Button("Guardar Registro", e -> {
            // 🎯 AGREGAMOS TRY-CATCH: Si falla, ahora te avisa con un cartel rojo por qué falló
            try {
                if (itemCombo.isEmpty()) {
                    Notification.show("Debe seleccionar un Ítem del catálogo.").addThemeVariants(NotificationVariant.LUMO_ERROR);
                    return;
                }

                Recurso nuevo = new Recurso();
                nuevo.setItem(itemCombo.getValue());
                nuevo.setNumeroSerie(txtSerie.getValue());
                nuevo.setUbicacionFisica(txtUbicacion.getValue());
                nuevo.setEstadoConservacion(conservacionCombo.getValue());
                nuevo.setEstadoMovimiento(EstadoMovimientoRecurso.DISPONIBLE);

                recursoService.guardarRecurso(nuevo);

                actualizarLista();
                dialog.close();
                Notification.show("¡Recurso cargado con éxito!").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } catch (Exception ex) {
                // Si la base de datos chilla (ej: un campo null), te lo muestra en pantalla
                Notification.show("Error al guardar: " + ex.getMessage(), 5000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                ex.printStackTrace(); // Imprime el error completo en la consola de Spring
            }
        });

        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancelButton = new Button("Cancelar", e -> dialog.close());

        dialog.add(form);
        dialog.getFooter().add(saveButton, cancelButton);
        dialog.open();
    }

    // --- MODAL SECUNDARIO: CREAR MOLDE (ITEM) AL VUELO ---
    private void abrirFormularioNuevoMolde(ComboBox<Item> comboAActualizar) {
        Dialog dialogMolde = new Dialog();
        dialogMolde.setHeaderTitle("Nuevo Ítem de Catálogo");

        TextField txtNombre = new TextField("Nombre (Ej: Soldadora Inverter)");
        txtNombre.setRequired(true);
        txtNombre.setWidthFull();

        TextField txtCategoria = new TextField("Categoría (Ej: Maquinaria)");
        txtCategoria.setRequired(true);
        txtCategoria.setWidthFull();

        Button btnGuardarMolde = new Button("Crear", click -> {
            try {
                if (txtNombre.isEmpty() || txtCategoria.isEmpty()) {
                    Notification.show("Completá nombre y categoría.").addThemeVariants(NotificationVariant.LUMO_ERROR);
                    return;
                }

                Item nuevoItem = new Item();
                nuevoItem.setNombre(txtNombre.getValue());
                nuevoItem.setCategoria(txtCategoria.getValue());
                nuevoItem.setDescripcion("Generado automáticamente");

                inventarioService.guardarItem(nuevoItem);

                comboAActualizar.setItems(inventarioService.obtenerTodosLosItemsCatalogo());
                comboAActualizar.setValue(nuevoItem);

                dialogMolde.close();
                Notification.show("Ítem agregado al catálogo.").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } catch (Exception ex) {
                Notification.show("Error al crear molde: " + ex.getMessage())
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                ex.printStackTrace();
            }
        });
        btnGuardarMolde.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        VerticalLayout layout = new VerticalLayout(txtNombre, txtCategoria);
        layout.setPadding(false);

        dialogMolde.add(layout);
        dialogMolde.getFooter().add(btnGuardarMolde, new Button("Cancelar", c -> dialogMolde.close()));
        dialogMolde.open();
    }

    private void actualizarLista() {
        if (filterText.isEmpty()) {
            grid.setItems(recursoService.obtenerTodoElInventario());
        } else {
            grid.setItems(recursoService.buscarPorCategoria(filterText.getValue()));
        }
    }

    //DEMO LOGICA DEL BOTON DE EDITAR RECURSOS!
    private void abrirEditorRecurso(Recurso r) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Modificar Registro: " + r.getItem().getNombre());
        dialog.setMinWidth("400px");

        VerticalLayout layout = new VerticalLayout();

        // 1. Combo de estados (En uso, Disponible, etc.)
        ComboBox<EstadoMovimientoRecurso> estadoCombo = new ComboBox<>("Estado Actual");
        estadoCombo.setItems(EstadoMovimientoRecurso.values());
        estadoCombo.setValue(r.getEstadoMovimiento());
        estadoCombo.setWidthFull();

        // 2. Combo de Empresas (quién lo tiene ahora)
        ComboBox<Empresa> empresaCombo = new ComboBox<>("Prestado a / Poseedor");
        // Asumo que tenés un empresaService para listar las empresas
        empresaCombo.setItems(this.empresaService.obtenerTodasLasEmpresas());
        empresaCombo.setItemLabelGenerator(Empresa::getRazonSocial);
        empresaCombo.setValue(r.getPrestadoA());
        empresaCombo.setWidthFull();

        // 🎯 Lógica inteligente: Ocultamos el campo "Empresa" si está disponible
        empresaCombo.setVisible(r.getEstadoMovimiento() == EstadoMovimientoRecurso.PRESTADO ||
                r.getEstadoMovimiento() == EstadoMovimientoRecurso.A_DEVOLVER);

        estadoCombo.addValueChangeListener(e -> {
            boolean requierePoseedor = (e.getValue() == EstadoMovimientoRecurso.PRESTADO ||
                    e.getValue() == EstadoMovimientoRecurso.A_DEVOLVER);
            empresaCombo.setVisible(requierePoseedor);
        });

        layout.add(estadoCombo, empresaCombo);

        // 3. Botón Guardar
        Button btnGuardar = new Button("Guardar Cambios", e -> {
            try {
                r.setEstadoMovimiento(estadoCombo.getValue());
                // Si no requiere poseedor, guardamos null
                r.setPrestadoA(empresaCombo.isVisible() ? empresaCombo.getValue() : null);

                recursoService.guardarRecurso(r); // Actualizamos en BD
                grid.getDataProvider().refreshItem(r); // Refrescamos solo la fila, ¡mágico!

                dialog.close();
                Notification.show("Recurso actualizado con éxito").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } catch (Exception ex) {
                Notification.show("Error: " + ex.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        btnGuardar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        dialog.getFooter().add(btnGuardar, new Button("Cancelar", c -> dialog.close()));
        dialog.add(layout);
        dialog.open();
    }
}