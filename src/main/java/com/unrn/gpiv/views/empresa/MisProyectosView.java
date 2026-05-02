package com.unrn.gpiv.views.empresa;

import com.unrn.gpiv.views.MainLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route(value = "mis-proyectos", layout = MainLayout.class)
public class MisProyectosView extends VerticalLayout {
    public MisProyectosView() { add(new H2("Mis Proyectos")); }

}
