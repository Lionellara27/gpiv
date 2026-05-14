package com.unrn.gpiv.views.admin;

import com.unrn.gpiv.views.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
//cosas new
import com.unrn.gpiv.service.EmpresaService; // <--- 1. IMPORTANTE: Importá tu Service
import org.springframework.beans.factory.annotation.Autowired; // <--- 2. Para la inyección

@PageTitle("Dashboard Administrador | SGPIV")
@Route(value = "admin/dashboard", layout = MainLayout.class)
public class AdminDashboardView extends VerticalLayout {
    private final EmpresaService empresaService; // <--- 3. Variable para el Service

    public AdminDashboardView(@Autowired EmpresaService empresaService) {
        this.empresaService = empresaService;
        setPadding(true);
        setSpacing(true);
        setSizeFull();

        H2 header = new H2("Tablero de Gestión Municipal");

// --- TARJETAS KPI (LIMPIO) ---
        HorizontalLayout kpiLayout = new HorizontalLayout();
        kpiLayout.setWidthFull();
        kpiLayout.setSpacing(true);

// 1. La tarjeta dinámica que acabamos de arreglar
        long numPendientes = empresaService.contarSolicitudesPendientes();
        String textoPendientes = numPendientes + (numPendientes == 1 ? " PENDIENTE" : " PENDIENTES");

        VerticalLayout cardSolicitudes = crearTarjetaKPI("SOLICITUDES", textoPendientes, VaadinIcon.FILE_TEXT, "#0063BE");
        cardSolicitudes.getStyle().set("cursor", "pointer");
        cardSolicitudes.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("admin/evaluar")));

// 2. Agregamos UNA SOLA VEZ cada tarjeta al layout
        kpiLayout.add(
                cardSolicitudes, // La azul primero (o donde más te guste)
                crearTarjetaKPI("LOTES", "12 LIBRES", VaadinIcon.MAP_MARKER, "#009A3B"),
                crearTarjetaKPI("EMPRESAS", "45 RADICADAS", VaadinIcon.FACTORY, "#666"),
                crearTarjetaKPI("PRÉSTAMOS", "2 ACTIVOS", VaadinIcon.TOOLS, "#E67E22")
        );

        // --- CONTENEDOR DE TABLAS (MITAD Y MITAD) ---
        HorizontalLayout tablasLayout = new HorizontalLayout();
        tablasLayout.setWidthFull();
        tablasLayout.setSpacing(true);

        // 1. TABLA INVENTARIO (Izquierda)
        VerticalLayout inventorySection = new VerticalLayout();
        inventorySection.setWidth("50%");
        inventorySection.getStyle().set("background-color", "white");
        inventorySection.getStyle().set("border-radius", "15px");
        inventorySection.getStyle().set("padding", "1.5em");
        inventorySection.getStyle().set("box-shadow", "0 4px 15px rgba(0,0,0,0.05)");

        H3 invTitle = new H3("Estado de Recursos y Maquinaria");

        Grid<String[]> gridInv = new Grid<>();
        gridInv.addColumn(data -> data[0]).setHeader("Recurso");
        gridInv.addColumn(data -> data[1]).setHeader("Estado");
        gridInv.addColumn(data -> data[2]).setHeader("Poseedor / Origen");
        gridInv.setItems(new String[][]{
                {"Soldadora Inverter", "PRESTADO", "Metalúrgica Viedma SRL"},
                {"Llaves Maestras Galpón A", "PRESTADO", "Logística Patagonia"},
                {"Retroexcavadora", "A DEVOLVER", "Municipalidad de Viedma"} // <- ¡El detalle que sumaste!
        });

        // Totales de Inventario
        HorizontalLayout totalesInvLayout = new HorizontalLayout();
        totalesInvLayout.setWidthFull();
        totalesInvLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);

        Span totalPrestados = new Span("📤 Prestados a empresas: 2");
        totalPrestados.getStyle().set("font-weight", "bold").set("color", "#E67E22"); // Naranja

        Span totalAdevolver = new Span("📥 Elementos a devolver: 1");
        totalAdevolver.getStyle().set("font-weight", "bold").set("color", "#0063BE"); // Azul

        totalesInvLayout.add(totalPrestados, totalAdevolver);
        inventorySection.add(invTitle, gridInv, totalesInvLayout);

        // 2. TABLA CONSUMOS (Derecha)
        VerticalLayout consumosSection = new VerticalLayout();
        consumosSection.setWidth("50%");
        consumosSection.getStyle().set("background-color", "white");
        consumosSection.getStyle().set("border-radius", "15px");
        consumosSection.getStyle().set("padding", "1.5em");
        consumosSection.getStyle().set("box-shadow", "0 4px 15px rgba(0,0,0,0.05)");

        H3 consumosTitle = new H3("Top Consumos del Mes");

        Grid<String[]> gridConsumos = new Grid<>();
        gridConsumos.addColumn(data -> data[0]).setHeader("Empresa");
        gridConsumos.addColumn(data -> data[1]).setHeader("Energía (kWh)");
        gridConsumos.addColumn(data -> data[2]).setHeader("Agua (Lts)");
        gridConsumos.setItems(new String[][]{
                {"Logística del Sur", "300.000", "50.000"},
                {"PepeCrew", "180.000", "40.000"},
                {"Maderera Comarca", "95.000", "15.000"}
        });

        // Totales de Consumo
        HorizontalLayout totalesConsumoLayout = new HorizontalLayout();
        totalesConsumoLayout.setWidthFull();
        totalesConsumoLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);

        Span totalEnergia = new Span("⚡ Total Energía: 1.250.000 kWh");
        totalEnergia.getStyle().set("font-weight", "bold").set("color", "#d9534f"); // Rojo

        Span totalAgua = new Span("💧 Total Agua: 450.000 Lts");
        totalAgua.getStyle().set("font-weight", "bold").set("color", "#0063BE"); // Azul

        totalesConsumoLayout.add(totalEnergia, totalAgua);
        consumosSection.add(consumosTitle, gridConsumos, totalesConsumoLayout);

        // Agregamos las dos tablas al contenedor horizontal
        tablasLayout.add(inventorySection, consumosSection);

        add(header, kpiLayout, tablasLayout);

    }

    private VerticalLayout crearTarjetaKPI(String titulo, String valor, VaadinIcon icono, String color) {
        VerticalLayout card = new VerticalLayout();
        card.setWidth("250px");
        card.getStyle().set("background-color", "white");
        card.getStyle().set("border-radius", "10px");
        card.getStyle().set("box-shadow", "0 4px 10px rgba(0,0,0,0.05)");
        card.getStyle().set("border-left", "5px solid " + color);
        card.setAlignItems(Alignment.CENTER);

        Span txtTitulo = new Span(titulo);
        txtTitulo.getStyle().set("font-size", "0.8em").set("color", "#999").set("font-weight", "bold");

        Span txtValor = new Span(valor);
        txtValor.getStyle().set("font-size", "1.5em").set("font-weight", "bold").set("color", color);

        card.add(icono.create(), txtTitulo, txtValor);
        return card;
    }
}

/*
package com.unrn.gpiv.views.admin;

import com.unrn.gpiv.views.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Dashboard Administrador | SGPIV")
@Route(value = "admin/dashboard", layout = MainLayout.class)
public class AdminDashboardView extends VerticalLayout {

    public AdminDashboardView() {
        setPadding(true);
        setSpacing(true);
        setSizeFull();

        H2 header = new H2("Tablero de Gestión Municipal");

        // --- TARJETAS KPI ---
        HorizontalLayout kpiLayout = new HorizontalLayout();
        kpiLayout.setWidthFull();
        kpiLayout.add(
                crearTarjetaKPI("SOLICITUDES", "4 PENDIENTES", VaadinIcon.FILE_TEXT, "#0063BE"),
                crearTarjetaKPI("LOTES", "12 LIBRES", VaadinIcon.MAP_MARKER, "#009A3B"),
                crearTarjetaKPI("EMPRESAS", "45 RADICADAS", VaadinIcon.FACTORY, "#666"),
                crearTarjetaKPI("PRÉSTAMOS", "2 ACTIVOS", VaadinIcon.TOOLS, "#E67E22")
        );

        // --- CONTENEDOR DE TABLAS (MITAD Y MITAD) ---
        HorizontalLayout tablasLayout = new HorizontalLayout();
        tablasLayout.setWidthFull();
        tablasLayout.setSpacing(true);

        // 1. TABLA INVENTARIO (Izquierda)
        VerticalLayout inventorySection = new VerticalLayout();
        inventorySection.setWidth("50%");
        inventorySection.getStyle().set("background-color", "white");
        inventorySection.getStyle().set("border-radius", "15px");
        inventorySection.getStyle().set("padding", "1.5em");
        inventorySection.getStyle().set("box-shadow", "0 4px 15px rgba(0,0,0,0.05)");

        H3 invTitle = new H3("Herramientas Prestadas");

        Grid<String[]> gridInv = new Grid<>();
        gridInv.addColumn(data -> data[0]).setHeader("Recurso");
        gridInv.addColumn(data -> data[1]).setHeader("Estado");
        gridInv.addColumn(data -> data[2]).setHeader("Poseedor Actual");
        gridInv.setItems(new String[][]{
                {"Soldadora Inverter", "PRESTADO", "Metalúrgica Viedma SRL"},
                {"Llaves Maestras Galpón A", "PRESTADO", "Logística Patagonia"}
        });
        inventorySection.add(invTitle, gridInv);

        // 2. TABLA CONSUMOS (Derecha)
        VerticalLayout consumosSection = new VerticalLayout();
        consumosSection.setWidth("50%");
        consumosSection.getStyle().set("background-color", "white");
        consumosSection.getStyle().set("border-radius", "15px");
        consumosSection.getStyle().set("padding", "1.5em");
        consumosSection.getStyle().set("box-shadow", "0 4px 15px rgba(0,0,0,0.05)");

        H3 consumosTitle = new H3("Top Consumos del Mes");

        Grid<String[]> gridConsumos = new Grid<>();
        gridConsumos.addColumn(data -> data[0]).setHeader("Empresa");
        gridConsumos.addColumn(data -> data[1]).setHeader("Energía (kWh)");
        gridConsumos.addColumn(data -> data[2]).setHeader("Agua (Lts)");
        gridConsumos.setItems(new String[][]{
                {"Logística del Sur", "300.000", "50.000"},
                {"PepeCrew", "180.000", "40.000"},
                {"Maderera Comarca", "95.000", "15.000"}
        });

        // Totales abajo de la tabla
        HorizontalLayout totalesLayout = new HorizontalLayout();
        totalesLayout.setWidthFull();
        totalesLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        Span totalEnergia = new Span("⚡ Total Energía Parque: 1.250.000 kWh");
        totalEnergia.getStyle().set("font-weight", "bold").set("color", "#d9534f"); // Rojo alerta
        Span totalAgua = new Span("💧 Total Agua Parque: 450.000 Lts");
        totalAgua.getStyle().set("font-weight", "bold").set("color", "#0063BE"); // Azul agua
        totalesLayout.add(totalEnergia, totalAgua);

        consumosSection.add(consumosTitle, gridConsumos, totalesLayout);

        // Agregamos las dos tablas al contenedor horizontal
        tablasLayout.add(inventorySection, consumosSection);

        add(header, kpiLayout, tablasLayout);
    }

    private VerticalLayout crearTarjetaKPI(String titulo, String valor, VaadinIcon icono, String color) {
        VerticalLayout card = new VerticalLayout();
        card.setWidth("250px");
        card.getStyle().set("background-color", "white");
        card.getStyle().set("border-radius", "10px");
        card.getStyle().set("box-shadow", "0 4px 10px rgba(0,0,0,0.05)");
        card.getStyle().set("border-left", "5px solid " + color);
        card.setAlignItems(Alignment.CENTER);

        Span txtTitulo = new Span(titulo);
        txtTitulo.getStyle().set("font-size", "0.8em").set("color", "#999").set("font-weight", "bold");

        Span txtValor = new Span(valor);
        txtValor.getStyle().set("font-size", "1.5em").set("font-weight", "bold").set("color", color);

        card.add(icono.create(), txtTitulo, txtValor);
        return card;
    }
}
*/
/*
VERSION 2 -> V2

package com.unrn.gpiv.views.admin;

import com.unrn.gpiv.views.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Dashboard Administrador | SGPIV")
@Route(value = "admin/dashboard", layout = MainLayout.class)
public class AdminDashboardView extends VerticalLayout {

    public AdminDashboardView() {
        setPadding(true);
        setSpacing(true);

        H2 header = new H2("Tablero de Gestión Municipal");

        // --- TARJETAS KPI (Ya las tenías) ---
        HorizontalLayout kpiLayout = new HorizontalLayout();
        kpiLayout.setWidthFull();
        kpiLayout.add(
                crearTarjetaKPI("SOLICITUDES", "4 PENDIENTES", VaadinIcon.FILE_TEXT, "#0063BE"),
                crearTarjetaKPI("LOTES", "12 LIBRES", VaadinIcon.MAP_MARKER, "#009A3B"),
                crearTarjetaKPI("EMPRESAS", "45 RADICADAS", VaadinIcon.FACTORY, "#666"),
                crearTarjetaKPI("PRÉSTAMOS", "2 ACTIVOS", VaadinIcon.TOOLS, "#E67E22")
        );

        // --- SECCIÓN INFERIOR: RESUMEN DE INVENTARIO ---
        VerticalLayout inventorySection = new VerticalLayout();
        inventorySection.getStyle().set("background-color", "white");
        inventorySection.getStyle().set("border-radius", "15px");
        inventorySection.getStyle().set("padding", "2em");
        inventorySection.getStyle().set("box-shadow", "0 4px 15px rgba(0,0,0,0.05)");

        H3 invTitle = new H3("Estado Crítico de Recursos (Herramientas en uso)");

        // Una grilla rápida para ver quién tiene qué (HU 18/19)
        Grid<String[]> gridInv = new Grid<>();
        gridInv.addColumn(data -> data[0]).setHeader("Recurso");
        gridInv.addColumn(data -> data[1]).setHeader("Estado");
        gridInv.addColumn(data -> data[2]).setHeader("Poseedor Actual");

        // Datos de ejemplo para el mockup
        gridInv.setItems(new String[][]{
                {"Soldadora Inverter", "PRESTADO", "Metalúrgica Viedma SRL"},
                {"Llaves Maestras Galpón A", "PRESTADO", "Logística Patagonia"},
                {"Grupo Electrógeno 50kva", "DISPONIBLE", "Depósito Central"}
        });

        inventorySection.add(invTitle, gridInv);
        add(header, kpiLayout, inventorySection);
    }

    private VerticalLayout crearTarjetaKPI(String titulo, String valor, VaadinIcon icono, String color) {
        VerticalLayout card = new VerticalLayout();
        card.setWidth("250px");
        card.getStyle().set("background-color", "white");
        card.getStyle().set("border-radius", "10px");
        card.getStyle().set("box-shadow", "0 4px 10px rgba(0,0,0,0.05)");
        card.getStyle().set("border-left", "5px solid " + color);
        card.setAlignItems(Alignment.CENTER);
        Span txtTitulo = new Span(titulo);
        txtTitulo.getStyle().set("font-size", "0.8em").set("color", "#999").set("font-weight", "bold");
        Span txtValor = new Span(valor);
        txtValor.getStyle().set("font-size", "1.5em").set("font-weight", "bold").set("color", color);
        card.add(icono.create(), txtTitulo, txtValor);
        return card;
    }
}
*/


/*package com.unrn.gpiv.views.admin;
VERSION 1111111111111111111111111111111 -> V1
import com.unrn.gpiv.views.MainLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Dashboard Administrador | SGPIV")
@Route(value = "admin/dashboard", layout = MainLayout.class) // Se mete adentro del MainLayout
public class AdminDashboardView extends VerticalLayout {

    public AdminDashboardView() {
        setPadding(true);
        setSpacing(true);

        H2 header = new H2("Tablero de Gestión Municipal");
        header.getStyle().set("margin-bottom", "1em");

        // --- FILA DE TARJETAS (KPIs) ---
        HorizontalLayout kpiLayout = new HorizontalLayout();
        kpiLayout.setWidthFull();
        kpiLayout.setSpacing(true);

        kpiLayout.add(
                crearTarjetaKPI("SOLICITUDES", "4 PENDIENTES", VaadinIcon.FILE_TEXT, "#0063BE"),
                crearTarjetaKPI("LOTES", "12 LIBRES", VaadinIcon.MAP_MARKER, "#009A3B"),
                crearTarjetaKPI("EMPRESAS", "45 RADICADAS", VaadinIcon.FACTORY, "#666"),
                crearTarjetaKPI("PRÉSTAMOS", "2 ACTIVOS", VaadinIcon.TOOLS, "#E67E22")
        );

        add(header, kpiLayout);
        // Acá abajo después podemos poner una tablita de "Últimos movimientos"
    }

    private VerticalLayout crearTarjetaKPI(String titulo, String valor, VaadinIcon icono, String color) {
        VerticalLayout card = new VerticalLayout();
        card.setWidth("250px");
        card.getStyle().set("background-color", "white");
        card.getStyle().set("border-radius", "10px");
        card.getStyle().set("box-shadow", "0 4px 10px rgba(0,0,0,0.05)");
        card.getStyle().set("border-left", "5px solid " + color);
        card.setAlignItems(Alignment.CENTER);

        Span txtTitulo = new Span(titulo);
        txtTitulo.getStyle().set("font-size", "0.8em").set("color", "#999").set("font-weight", "bold");

        Span txtValor = new Span(valor);
        txtValor.getStyle().set("font-size", "1.5em").set("font-weight", "bold").set("color", color);

        card.add(icono.create(), txtTitulo, txtValor);
        return card;
    }
}
*/