package com.unrn.gpiv.views.admin;

import com.unrn.gpiv.model.Empresa;
import com.unrn.gpiv.service.EmpresaService;
import com.unrn.gpiv.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Informes de Empresas | SGPIV")
@Route(value = "admin/informes-empresas", layout = MainLayout.class)
public class InformesEmpresasView extends VerticalLayout implements BeforeEnterObserver {

    private final EmpresaService empresaService;
    private Grid<Empresa> grid = new Grid<>(Empresa.class, false);

    public InformesEmpresasView(EmpresaService empresaService) {
        this.empresaService = empresaService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);
        getStyle().set("background-color", "#f5f7fa");

        H2 titulo = new H2("Listado de Empresas Radicadas");

        configurarTabla();

        add(titulo, grid);
        actualizarTabla();
    }

    private void configurarTabla() {
        grid.setSizeFull();

        grid.addColumn(Empresa::getId).setHeader("ID").setSortable(true);
        grid.addColumn(Empresa::getRazonSocial).setHeader("Razón Social").setSortable(true);
        grid.addColumn(Empresa::getCuit).setHeader("CUIT");
        grid.addColumn(Empresa::getDireccion).setHeader("Dirección Legal");

        // Mostramos el Estado de Solicitud/Radicación usando un Badge simple
        grid.addColumn(empresa -> empresa.getEstado() != null ? empresa.getEstado().toString() : "PENDIENTE")
                .setHeader("Estado");

        // Botón "el ojito" para navegar al detalle usando el ID real
        grid.addComponentColumn(empresa -> {
            Button btnDetalle = new Button("Ver Detalle", VaadinIcon.EYE.create());
            btnDetalle.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            btnDetalle.addClickListener(e ->
                    getUI().ifPresent(ui -> ui.navigate(EmpresaDetalleView.class, empresa.getId()))
            );
            return btnDetalle;
        }).setHeader("Acciones");

        grid.getColumns().forEach(c -> c.setAutoWidth(true));
    }

    private void actualizarTabla() {
        grid.setItems(empresaService.obtenerTodasLasEmpresas());
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        actualizarTabla();
    }
}
/*
import com.unrn.gpiv.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

@PageTitle("Detalle de Empresa | SGPIV")
@Route(value = "admin/informe-detalle", layout = MainLayout.class)
public class InformesEmpresasView extends VerticalLayout {

    public InformesEmpresasView() {
        setPadding(true);
        setSpacing(true);
        getStyle().set("background-color", "#f5f7fa");

        // --- 1. CABECERA: LOGO + INFO GENERAL ---
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setPadding(true);
        header.getStyle().set("background-color", "white");
        header.getStyle().set("border-radius", "15px");
        header.setAlignItems(Alignment.CENTER);

        Image logoEmpresa = new Image("https://via.placeholder.com/100", "Logo Empresa");
        logoEmpresa.setWidth("100px");
        logoEmpresa.setHeight("100px");
        logoEmpresa.getStyle().set("border-radius", "10px");

        VerticalLayout infoGral = new VerticalLayout();
        infoGral.setSpacing(false);
        infoGral.setPadding(false);

        H2 nombre = new H2("Logística del Sur S.A.");
        nombre.addClassNames(LumoUtility.Margin.Vertical.NONE);

        Span rubro = new Span("RUBRO: Transporte y Almacenamiento Frío");
        rubro.getStyle().set("color", "#0063BE").set("font-weight", "bold");

        Paragraph desc = new Paragraph("Empresa dedicada a la distribución de productos perecederos en la Patagonia. " +
                "Cuenta con flota propia y depósito fiscal en el Lote 14.");
        desc.getStyle().set("color", "#666");

        infoGral.add(nombre, rubro, desc);
        header.add(logoEmpresa, infoGral);

        // --- 2. CUERPO: CONSUMOS (IZQ) Y RECURSOS (DER) ---
        HorizontalLayout cuerpo = new HorizontalLayout();
        cuerpo.setWidthFull();
        cuerpo.setSpacing(true);

        // --- SECCIÓN IZQUIERDA: CONSUMOS ANUALES ---
        VerticalLayout sectionConsumos = new VerticalLayout();
        sectionConsumos.setWidth("65%");
        sectionConsumos.getStyle().set("background-color", "white").set("border-radius", "15px").set("padding", "1.5em");

        HorizontalLayout navAnio = new HorizontalLayout();
        navAnio.setWidthFull();
        navAnio.setJustifyContentMode(JustifyContentMode.BETWEEN);

        Button btnAnt = new Button(VaadinIcon.ARROW_LEFT.create());
        H3 anioTitulo = new H3("Consumos Año 2025");
        Button btnSig = new Button(VaadinIcon.ARROW_RIGHT.create());

        navAnio.add(btnAnt, anioTitulo, btnSig);

        Grid<String[]> gridConsumos = new Grid<>();
        gridConsumos.addColumn(d -> d[0]).setHeader("Mes");
        gridConsumos.addColumn(d -> d[1]).setHeader("Luz (kWh)");
        gridConsumos.addColumn(d -> d[2]).setHeader("Agua (Lts)");
        gridConsumos.addColumn(d -> d[3]).setHeader("Gas (m3)");

        gridConsumos.setItems(new String[][]{
                {"Enero", "25.000", "5.000", "1.200"},
                {"Febrero", "28.500", "4.800", "1.100"},
                {"Marzo", "22.000", "5.200", "2.500"}
        });

        // Representación de Servicios Especiales
        HorizontalLayout otrosServicios = new HorizontalLayout();
        otrosServicios.add(
                crearBadgeServicio(VaadinIcon.DROP, "Cloaca: Conectado", "#009A3B"),
                crearBadgeServicio(VaadinIcon.SIGNAL, "Fibra Óptica: Activo", "#0063BE")
        );

        sectionConsumos.add(navAnio, gridConsumos, otrosServicios);

        // --- SECCIÓN DERECHA: EMPLEADOS Y VEHÍCULOS ---
        VerticalLayout sectionRecursos = new VerticalLayout();
        sectionRecursos.setWidth("35%");

        // Tabla Empleados
        VerticalLayout divEmp = new VerticalLayout();
        divEmp.getStyle().set("background-color", "white").set("border-radius", "15px");
        H4 tEmp = new H4("Personal (Total: 15)");
        Grid<String[]> gridEmp = new Grid<>();
        gridEmp.addColumn(d -> d[0]).setHeader("Nombre");
        gridEmp.addColumn(d -> d[1]).setHeader("Cargo");
        gridEmp.setItems(new String[][]{{"Juan Perez", "Gerente"}, {"Marta Gomez", "Operario"}});
        divEmp.add(tEmp, gridEmp);

        // Tabla Vehículos
        VerticalLayout divVeh = new VerticalLayout();
        divVeh.getStyle().set("background-color", "white").set("border-radius", "15px");
        H4 tVeh = new H4("Vehículos (Total: 4)");
        Grid<String[]> gridVeh = new Grid<>();
        gridVeh.addColumn(d -> d[0]).setHeader("Patente");
        gridVeh.addColumn(d -> d[1]).setHeader("Tipo");
        gridVeh.setItems(new String[][]{{"AF-123-JK", "Camión Scania"}, {"AD-444-OP", "Clinch"}});
        divVeh.add(tVeh, gridVeh);

        sectionRecursos.add(divEmp, divVeh);

        // --- 3. SECCIÓN DE AVANCES (HISTORIAL) ---
        VerticalLayout sectionAvances = new VerticalLayout();
        sectionAvances.setWidthFull();
        sectionAvances.getStyle().set("background-color", "white").set("border-radius", "15px").set("padding", "1.5em");

        H3 tAvance = new H3("Historial de Avances de Proyecto");

        VerticalLayout timeline = new VerticalLayout();
        timeline.add(crearItemAvance("02/05/2026", "Finalización de cerramiento perimetral", "Se terminaron de levantar las 4 paredes del galpón principal."));
        timeline.add(crearItemAvance("15/04/2026", "Conexión de servicios", "Se completó la instalación de gas industrial y medidores."));

        sectionAvances.add(tAvance, timeline);

        // Agregar todo a la vista
        cuerpo.add(sectionConsumos, sectionRecursos);
        add(header, cuerpo, sectionAvances);
    }

    private Span crearBadgeServicio(VaadinIcon icon, String texto, String color) {
        Span badge = new Span(icon.create(), new Span(texto));
        badge.addClassNames(LumoUtility.Display.FLEX, LumoUtility.AlignItems.CENTER, LumoUtility.Gap.SMALL);
        badge.getStyle().set("background-color", color + "22"); // 22 es transparencia
        badge.getStyle().set("color", color);
        badge.getStyle().set("padding", "5px 12px");
        badge.getStyle().set("border-radius", "20px");
        badge.getStyle().set("font-weight", "bold");
        badge.getStyle().set("font-size", "0.85em");
        return badge;
    }

    private VerticalLayout crearItemAvance(String fecha, String titulo, String desc) {
        VerticalLayout item = new VerticalLayout();
        item.setSpacing(false);
        item.setPadding(false);
        item.getStyle().set("border-left", "3px solid #0063BE");
        item.getStyle().set("padding-left", "15px");
        item.getStyle().set("margin-bottom", "10px");

        Span txtFecha = new Span(fecha);
        txtFecha.getStyle().set("font-size", "0.8em").set("color", "#999");
        H5 txtTit = new H5(titulo);
        Paragraph txtDesc = new Paragraph(desc);
        txtDesc.getStyle().set("font-size", "0.9em");

        item.add(txtFecha, txtTit, txtDesc);
        return item;
    }
}
 */