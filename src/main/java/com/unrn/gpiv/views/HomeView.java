package com.unrn.gpiv.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

/**
 * Esta es la página principal del sistema (Ruta vacía "")
 */
@Route("")
public class HomeView extends VerticalLayout {

    public HomeView() {
        // 1. Configuración básica del layout (centrado como en JavaFX)
        setSpacing(true);
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setSizeFull(); // Que ocupe toda la pantalla

        // 2. Título principal
        H1 titulo = new H1("Bienvenido al SGPIV");
        titulo.getStyle().set("color", "#00468b"); // Azul institucional

        // 3. Bajada de texto
        Paragraph descripcion = new Paragraph(
                "Sistema de Gestión del Parque Industrial de Viedma. " +
                        "Centralizando la información para una mejor trazabilidad.");

        // 4. Un botón de acción (similar a un EventHandler en JavaFX)
        Button btnEmpezar = new Button("Explorar Lotes", e -> {
            Notification.show("¡Próximamente: Listado de Lotes!");
        });

        // Estilo al botón para que se vea moderno (Lumo theme)
        btnEmpezar.getElement().setAttribute("theme", "primary");

        // 5. Agregamos todo al "Stage" (Layout principal)
        add(titulo, descripcion, btnEmpezar);
    }
}