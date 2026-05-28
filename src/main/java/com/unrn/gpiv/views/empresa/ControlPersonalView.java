package com.unrn.gpiv.views.empresa;

import com.unrn.gpiv.model.Empleado;
import com.unrn.gpiv.model.Empresa;
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

public class ControlPersonalView extends VerticalLayout {

    private final EmpresaService empresaService;
    private Empresa empresaActual;
    private Grid<Empleado> gridEmpleados = new Grid<>(Empleado.class, false);

    // Campos para agregar nuevo personal
    private TextField txtNombre = new TextField("Nombre del Empleado");
    private TextField txtCargo = new TextField("Cargo / Puesto");
    private Button btnAgregar = new Button("Agregar", VaadinIcon.PLUS.create());

    public ControlPersonalView(Empresa empresaActual, EmpresaService empresaService) {
        this.empresaActual = empresaActual;
        this.empresaService = empresaService;

        setWidthFull();
        setPadding(false); // Sin padding extra porque ya está en un Tab

        H3 titulo = new H3("Nómina de Personal Activo");

        // 1. Configurar la Grilla
        gridEmpleados.addColumn(Empleado::getNombre).setHeader("Nombre").setSortable(true);
        gridEmpleados.addColumn(Empleado::getCargo).setHeader("Cargo");

        // Botón Eliminar en la grilla
        gridEmpleados.addComponentColumn(empleado -> {
            Button btnEliminar = new Button(VaadinIcon.TRASH.create());
            btnEliminar.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
            btnEliminar.addClickListener(e -> eliminarEmpleado(empleado));
            return btnEliminar;
        }).setHeader("Acciones").setAutoWidth(true);

        actualizarGrilla();

        // 2. Configurar el Formulario de carga
        HorizontalLayout formCarga = new HorizontalLayout(txtNombre, txtCargo, btnAgregar);
        formCarga.setDefaultVerticalComponentAlignment(Alignment.BASELINE);

        btnAgregar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnAgregar.addClickListener(e -> agregarEmpleado());

        add(titulo, formCarga, gridEmpleados);
    }

    private void agregarEmpleado() {
        if (txtNombre.isEmpty() || txtCargo.isEmpty()) {
            Notification.show("Por favor, completá ambos campos");
            return;
        }

        Empleado nuevoEmpleado = new Empleado();
        nuevoEmpleado.setNombre(txtNombre.getValue());
        nuevoEmpleado.setCargo(txtCargo.getValue());
        nuevoEmpleado.setEmpresa(empresaActual);

        // Agregamos a la lista de la empresa
        empresaActual.getEmpleados().add(nuevoEmpleado);

        // Guardamos la empresa entera (esto guarda al empleado por el CascadeType.ALL)
        empresaService.actualizarEmpresa(empresaActual);

        // Limpiamos y actualizamos
        txtNombre.clear();
        txtCargo.clear();
        actualizarGrilla();
        Notification.show("Empleado agregado a la nómina");
    }

    private void eliminarEmpleado(Empleado empleado) {
        empresaActual.getEmpleados().remove(empleado);
        empresaService.actualizarEmpresa(empresaActual);
        actualizarGrilla();
        Notification.show("Empleado eliminado");
    }

    private void actualizarGrilla() {
        gridEmpleados.setItems(empresaActual.getEmpleados());
    }
}