package com.unrn.gpiv.views.empresa;
import com.unrn.gpiv.views.MainLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route(value = "flota-vehiculos", layout = MainLayout.class)
public class FlotaVehiculosView extends VerticalLayout {
    public FlotaVehiculosView() { add(new H2("Módulo: Declaración de Flota Vehicular y Patentes")); }
}
