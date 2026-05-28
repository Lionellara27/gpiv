package com.unrn.gpiv.views.admin;

import com.unrn.gpiv.model.HistorialRadicacion;
import com.unrn.gpiv.service.HistorialService;
import com.unrn.gpiv.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Historial de Radicaciones | SGPIV")
@Route(value = "admin/historiales/radicaciones", layout = MainLayout.class)
public class HistorialRadicacionesView extends VerticalLayout {

    public HistorialRadicacionesView(HistorialService historialService) {
        setSizeFull();
        setPadding(true);

        Button btnVolver = new Button("Volver al Panel", VaadinIcon.ARROW_LEFT.create(), e ->
                getUI().ifPresent(ui -> ui.navigate(HistorialDashboardView.class))
        );

        HorizontalLayout header = new HorizontalLayout(new H2("Historial Cronológico de Tierras"), btnVolver);
        header.setWidthFull();
        header.setAlignItems(Alignment.CENTER);
        header.setFlexGrow(1, header.getChildren().findFirst().orElse(null));

        Grid<HistorialRadicacion> grid = new Grid<>(HistorialRadicacion.class, false);
        grid.setSizeFull();

        grid.addColumn(HistorialRadicacion::getRazonSocialEmpresa).setHeader("Empresa").setSortable(true);
        grid.addColumn(HistorialRadicacion::getCuitEmpresa).setHeader("CUIT");
        grid.addColumn(HistorialRadicacion::getNomenclaturaLote).setHeader("Espacio Físico").setSortable(true);
        grid.addColumn(HistorialRadicacion::getFechaAsignacion).setHeader("Fecha Entrada").setSortable(true);

        grid.addComponentColumn(reg -> {
            if (reg.getFechaDesasignacion() == null) {
                Span activo = new Span("Activo Actual");
                activo.getElement().getThemeList().add("badge success");
                return activo;
            } else {
                return new Span(reg.getFechaDesasignacion().toString());
            }
        }).setHeader("Fecha Salida / Liberación").setSortable(true);

        grid.setItems(historialService.listarTodo());
        grid.getColumns().forEach(c -> c.setAutoWidth(true));

        add(header, grid);
    }
}