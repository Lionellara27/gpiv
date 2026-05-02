package com.unrn.gpiv.views.evaluadores;

import com.unrn.gpiv.views.MainLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route(value = "evaluar", layout = MainLayout.class)
public class EvaluarSolicitudesView extends VerticalLayout {
    public EvaluarSolicitudesView() { add(new H2("Evaluar Solicitudes")); }
}
