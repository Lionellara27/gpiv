package com.unrn.gpiv.views.empresa;
import com.unrn.gpiv.views.MainLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route(value = "avances-obra", layout = MainLayout.class)
public class AvancesObraView extends VerticalLayout {
    public AvancesObraView() { add(new H2("Módulo: Presentar Avances de Proyecto (HU 04)")); }
}