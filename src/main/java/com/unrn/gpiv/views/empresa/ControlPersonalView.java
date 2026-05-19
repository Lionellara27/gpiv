package com.unrn.gpiv.views.empresa;
import com.unrn.gpiv.views.MainLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route(value = "control-personal", layout = MainLayout.class)
public class ControlPersonalView extends VerticalLayout {
    public ControlPersonalView() { add(new H2("Módulo: Control de Personal e Historial de Jerarquías")); }
}