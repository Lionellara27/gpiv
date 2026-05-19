package com.unrn.gpiv.views.empresa;
import com.unrn.gpiv.views.MainLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route(value = "medicion-consumos", layout = MainLayout.class)
public class MedicionConsumosView extends VerticalLayout {
    public MedicionConsumosView() { add(new H2("Módulo: Historial de Consumos Mensuales (Luz, Agua, Gas)")); }
}