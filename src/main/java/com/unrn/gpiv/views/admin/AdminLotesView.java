package com.unrn.gpiv.views.admin;

import com.unrn.gpiv.model.Lote;
import com.unrn.gpiv.common.EstadoLote;
import com.unrn.gpiv.views.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;

@PageTitle("Gestión de Lotes | SGPIV")
@Route(value = "admin/lotes", layout = MainLayout.class)
public class AdminLotesView extends VerticalLayout {

    // Cambiamos el Grid para que use tu clase Lote del paquete model
    private Grid<Lote> grid = new Grid<>(Lote.class, false);

    public AdminLotesView() {
        setSizeFull();
        setSpacing(true);

        add(new H2("Gestión de Lotes"));

        configurarGrid();
        
        // Carga de datos de prueba (Luego lo conectarás a tu Repositorio/Servicio)
        actualizarDatos();

        add(grid);
    }

    private void configurarGrid() {
        grid.setSizeFull();

        // 1. Columna Manzana
        grid.addColumn(Lote::getManzana).setHeader("Manzana").setSortable(true);

        // 2. Columna Número de Lote
        grid.addColumn(Lote::getNroLote).setHeader("Nro. Lote").setSortable(true);

        // 3. Columna Superficie
        grid.addColumn(lote -> lote.getSuperficie() + " m²").setHeader("Superficie").setSortable(true);

        // 4. Columna de Situación Actual (EstadoLote) con colores
        grid.addColumn(new ComponentRenderer<>(lote -> {
            EstadoLote estado = lote.getEstado();
            Span badge = new Span(estado.toString());
            
            // Asignamos estilo según el Enum que definiste
            if (estado == EstadoLote.LIBRE) {
                badge.getElement().getThemeList().add("badge success");
            } else if (estado == EstadoLote.RESERVADO) {
                badge.getElement().getThemeList().add("badge contrast");
            } else {
                badge.getElement().getThemeList().add("badge error");
            }
            
            return badge;
        })).setHeader("Situación Actual").setSortable(true);

        // 5. Columna Empresa (Muestra el nombre si existe, sino "-" )
        //grid.addColumn(lote -> lote.getEmpresa() != null ? lote.getEmpresa().getNombre() : "-")
        //    .setHeader("Empresa Asignada");

        // Ajuste de columnas
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void actualizarDatos() {
        // Ejemplo de cómo se vería con datos reales
        List<Lote> listaEjemplo = new ArrayList<>();
        
        Lote l1 = new Lote();
        l1.setManzana("A");
        l1.setNroLote("01");
        l1.setSuperficie(1500.0);
        l1.setEstado(EstadoLote.LIBRE);
        
        listaEjemplo.add(l1);
        
        grid.setItems(listaEjemplo);
    }
}