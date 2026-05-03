package com.unrn.gpiv.views.admin;

import com.unrn.gpiv.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Inventario | SGPIV")
@Route(value = "admin/inventario", layout = MainLayout.class)
public class InventarioView extends VerticalLayout {

    public InventarioView() {
        setSizeFull();
        setPadding(true);

        H2 title = new H2("Gestión de Inventario de Recursos");

        // --- BARRA DE BÚSQUEDA Y ACCIONES ---
        TextField filterText = new TextField();
        filterText.setPlaceholder("Buscar recurso...");
        filterText.setPrefixComponent(VaadinIcon.SEARCH.create());
        filterText.setClearButtonVisible(true);

        Button addBtn = new Button("Nuevo Recurso", VaadinIcon.PLUS.create());
        addBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addBtn.getStyle().set("background-color", "#009A3B");

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addBtn);

        // --- GRILLA PRINCIPAL ---
        Grid<String[]> grid = new Grid<>();
        grid.addColumn(data -> data[0]).setHeader("Nombre");
        grid.addColumn(data -> data[1]).setHeader("Categoría");
        grid.addColumn(data -> data[2]).setHeader("Estado");
        grid.addColumn(data -> data[3]).setHeader("Ubicación");

        grid.setItems(new String[][]{
                {"Soldadora Inverter", "Maquinaria", "En Uso", "Sector B - Lote 4"},
                {"Amoladora Angular", "Herramienta", "Disponible", "Depósito 1"},
                {"Llaves Sector Sur", "Equipamiento", "Extraviado", "-"}
        });

        add(title, toolbar, grid);
    }
}