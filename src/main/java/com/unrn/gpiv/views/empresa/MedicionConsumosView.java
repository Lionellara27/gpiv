package com.unrn.gpiv.views.empresa;

import com.unrn.gpiv.common.TipoServicio; // Asegurate de que importe tu Enum
import com.unrn.gpiv.model.ConsumoMensual;
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
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;

import java.util.Set;

public class MedicionConsumosView extends VerticalLayout {

    private final EmpresaService empresaService;
    private Empresa empresaActual;
    private Grid<ConsumoMensual> gridConsumos = new Grid<>(ConsumoMensual.class, false);

    // Campos para agregar nuevo consumo (Usamos NumberField para que solo acepten números)
    private TextField txtMes = new TextField("Mes/Año (Ej: 05/2026)");
    private NumberField numLuz = new NumberField("Luz (kWh)");
    private NumberField numAgua = new NumberField("Agua (Lts)");
    private NumberField numGas = new NumberField("Gas (m3)");
    private Button btnAgregar = new Button("Registrar", VaadinIcon.PLUS.create());

    public MedicionConsumosView(Empresa empresaActual, EmpresaService empresaService) {
        this.empresaActual = empresaActual;
        this.empresaService = empresaService;

        setWidthFull();
        setPadding(false);

        H3 titulo = new H3("Declaración de Consumos Mensuales");

        // 🚀 LÓGICA DE NEGOCIO: Bloquear lo que no pidieron en el proyecto
        if (empresaActual.getProyecto() != null && empresaActual.getProyecto().getServiciosNecesarios() != null) {
            Set<TipoServicio> servicios = empresaActual.getProyecto().getServiciosNecesarios();

            // Si el set NO contiene el servicio, deshabilitamos el campo (se pone gris)
            if (!servicios.contains(TipoServicio.LUZ)) {
                numLuz.setEnabled(false);
                numLuz.setPlaceholder("No solicitado");
            }
            if (!servicios.contains(TipoServicio.AGUA)) {
                numAgua.setEnabled(false);
                numAgua.setPlaceholder("No solicitado");
            }
            if (!servicios.contains(TipoServicio.GAS)) {
                numGas.setEnabled(false);
                numGas.setPlaceholder("No solicitado");
            }
        }

        // 1. Configurar la Grilla
        gridConsumos.addColumn(ConsumoMensual::getMesAnio).setHeader("Período").setSortable(true);
        gridConsumos.addColumn(c -> c.getConsumoLuz() != null ? c.getConsumoLuz() + " kWh" : "-").setHeader("Luz");
        gridConsumos.addColumn(c -> c.getConsumoAgua() != null ? c.getConsumoAgua() + " Lts" : "-").setHeader("Agua");
        gridConsumos.addColumn(c -> c.getConsumoGas() != null ? c.getConsumoGas() + " m3" : "-").setHeader("Gas");

        // Botón Eliminar
        gridConsumos.addComponentColumn(consumo -> {
            Button btnEliminar = new Button(VaadinIcon.TRASH.create());
            btnEliminar.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
            btnEliminar.addClickListener(e -> eliminarConsumo(consumo));
            return btnEliminar;
        }).setHeader("Acciones").setAutoWidth(true);

        actualizarGrilla();

        // 2. Configurar el Formulario de carga
        HorizontalLayout formCarga = new HorizontalLayout(txtMes, numLuz, numAgua, numGas, btnAgregar);
        formCarga.setDefaultVerticalComponentAlignment(Alignment.BASELINE);

        btnAgregar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnAgregar.addClickListener(e -> agregarConsumo());

        add(titulo, formCarga, gridConsumos);
    }

    private void agregarConsumo() {
        if (txtMes.isEmpty()) {
            Notification.show("El Mes/Año es obligatorio");
            return;
        }

        ConsumoMensual nuevoConsumo = new ConsumoMensual();
        nuevoConsumo.setMesAnio(txtMes.getValue());
        nuevoConsumo.setConsumoLuz(numLuz.getValue());
        nuevoConsumo.setConsumoAgua(numAgua.getValue());
        nuevoConsumo.setConsumoGas(numGas.getValue());
        nuevoConsumo.setEmpresa(empresaActual);

        empresaActual.getConsumosMensuales().add(nuevoConsumo);
        empresaService.actualizarEmpresa(empresaActual);

        txtMes.clear();
        numLuz.clear();
        numAgua.clear();
        numGas.clear();
        actualizarGrilla();
        Notification.show("Consumos registrados con éxito");
    }

    private void eliminarConsumo(ConsumoMensual consumo) {
        empresaActual.getConsumosMensuales().remove(consumo);
        empresaService.actualizarEmpresa(empresaActual);
        actualizarGrilla();
        Notification.show("Registro de consumo eliminado");
    }

    private void actualizarGrilla() {
        gridConsumos.setItems(empresaActual.getConsumosMensuales());
    }
}