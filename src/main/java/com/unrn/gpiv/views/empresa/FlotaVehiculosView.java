package com.unrn.gpiv.views.empresa;

import com.unrn.gpiv.model.Empresa;
import com.unrn.gpiv.model.Vehiculo;
import com.unrn.gpiv.service.EmpresaService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

public class FlotaVehiculosView extends VerticalLayout {

    private final EmpresaService empresaService;
    private Empresa empresaActual;
    private Grid<Vehiculo> gridVehiculos = new Grid<>(Vehiculo.class, false);

    // Campos para agregar nuevo vehículo
    private TextField txtPatente = new TextField("Patente");
    private TextField txtTipo = new TextField("Tipo (Ej: Camión Scania)");
    private Button btnAgregar = new Button("Agregar", VaadinIcon.PLUS.create());

    public FlotaVehiculosView(Empresa empresaActual, EmpresaService empresaService) {
        this.empresaActual = empresaActual;
        this.empresaService = empresaService;

        setWidthFull();
        setPadding(false); // Para que se alinee bien con la otra tabla

        H3 titulo = new H3("Flota de Vehículos");

        // 1. Configurar la Grilla
        gridVehiculos.addColumn(Vehiculo::getPatente).setHeader("Patente").setSortable(true);
        gridVehiculos.addColumn(Vehiculo::getTipo).setHeader("Tipo");

        // Botón Eliminar en la grilla
        gridVehiculos.addComponentColumn(vehiculo -> {
            Button btnEliminar = new Button(VaadinIcon.TRASH.create());
            btnEliminar.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
            btnEliminar.addClickListener(e -> eliminarVehiculo(vehiculo));
            return btnEliminar;
        }).setHeader("Acciones").setAutoWidth(true);

        actualizarGrilla();

        // 2. Configurar el Formulario de carga
        HorizontalLayout formCarga = new HorizontalLayout(txtPatente, txtTipo, btnAgregar);
        formCarga.setDefaultVerticalComponentAlignment(Alignment.BASELINE);

        btnAgregar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnAgregar.addClickListener(e -> agregarVehiculo());

        add(titulo, formCarga, gridVehiculos);
    }

    private void agregarVehiculo() {
        if (txtPatente.isEmpty() || txtTipo.isEmpty()) {
            Notification.show("Por favor, completá ambos campos");
            return;
        }

        Vehiculo nuevoVehiculo = new Vehiculo();
        nuevoVehiculo.setPatente(txtPatente.getValue().toUpperCase()); // Pasamos la patente a mayúsculas para que quede prolijo
        nuevoVehiculo.setTipo(txtTipo.getValue());
        nuevoVehiculo.setEmpresa(empresaActual);

        // Agregamos a la lista de la empresa
        empresaActual.getVehiculos().add(nuevoVehiculo);

        // Guardamos la empresa
        empresaService.actualizarEmpresa(empresaActual);

        // Limpiamos y actualizamos
        txtPatente.clear();
        txtTipo.clear();
        actualizarGrilla();
        Notification.show("Vehículo agregado a la flota");
    }

    private void eliminarVehiculo(Vehiculo vehiculo) {
        empresaActual.getVehiculos().remove(vehiculo);
        empresaService.actualizarEmpresa(empresaActual);
        actualizarGrilla();
        Notification.show("Vehículo eliminado");
    }

    private void actualizarGrilla() {
        gridVehiculos.setItems(empresaActual.getVehiculos());
    }
}