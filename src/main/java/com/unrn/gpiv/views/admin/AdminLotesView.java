package com.unrn.gpiv.views.admin;

import com.unrn.gpiv.views.MainLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Gestión de Lotes | SGPIV")
@Route(value = "admin/lotes", layout = MainLayout.class)
public class AdminLotesView extends VerticalLayout {

    public AdminLotesView() {
        add(new H2("Gestión de Lotes"));
        add(new Paragraph("Aquí el administrador podrá ver, registrar y actualizar los lotes (HU 3 y 5)."));

        // Luego acá pondremos la Grid (tabla) con los datos de la DB
    }
}