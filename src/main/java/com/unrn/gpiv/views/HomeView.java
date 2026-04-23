package com.unrn.gpiv.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("")
public class HomeView extends VerticalLayout {

    public HomeView() {
        // Configuración general: fondo blanco y sin espacios raros
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle().set("background-color", "#FFFFFF");

        // --- 1. HEADER CON LOGO (Esquina superior) ---
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setPadding(true);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setAlignItems(Alignment.CENTER);

        // Cargamos el logo (Asegurate de que esté en src/main/resources/META-INF/resources/images/)
        Image logo = new Image("images/logo-parque.png", "Logo");
        logo.setHeight("180px"); // Ajustá el tamaño a tu gusto

        // Franja decorativa de la bandera a la derecha del logo
        Div lineasBandera = new Div();
        lineasBandera.setWidth("150px");
        lineasBandera.setHeight("5px");
        lineasBandera.getStyle().set("background", "linear-gradient(to right, #0063BE 33%, #FFFFFF 33%, #FFFFFF 66%, #009A3B 66%)");
        lineasBandera.getStyle().set("border", "1px solid #eee");

        header.add(logo, lineasBandera);

        // --- 2. CUERPO CENTRAL ---
        VerticalLayout mainContent = new VerticalLayout();
        mainContent.setSizeFull();
        mainContent.setJustifyContentMode(JustifyContentMode.CENTER);
        mainContent.setAlignItems(Alignment.CENTER);

        // Título con el azul justicia
        H1 titulo = new H1("SGPIV");
        titulo.getStyle().set("font-size", "6em");
        titulo.getStyle().set("font-weight", "900");
        titulo.getStyle().set("color", "#000000"); // Negro río
        titulo.getStyle().set("margin", "0");

        Span subtitulo = new Span("Sistema de Gestión del Parque Industrial de Viedma");
        subtitulo.getStyle().set("font-size", "1.5em");
        subtitulo.getStyle().set("letter-spacing", "5px");
        subtitulo.getStyle().set("color", "#0063BE"); // Azul bandera
        subtitulo.getStyle().set("font-weight", "bold");

        Paragraph desc = new Paragraph("Parque Industrial de Viedma - Motor Productivo de la Comarca");
        desc.getStyle().set("color", "#666");
        desc.getStyle().set("margin-top", "1em");

        // Botón con el Verde Producción
        Button btnEntrar = new Button("INGRESAR AL SISTEMA", e -> {
            // Próximamente navegación
        });
        btnEntrar.getStyle().set("background-color", "#009A3B"); // Verde bandera
        btnEntrar.getStyle().set("color", "white");
        btnEntrar.getStyle().set("padding", "1.2em 2.5em");
        btnEntrar.getStyle().set("font-weight", "bold");
        btnEntrar.getStyle().set("border-radius", "50px"); // Redondeado fachero
        btnEntrar.getStyle().set("box-shadow", "0 4px 15px rgba(0, 154, 59, 0.3)");
        btnEntrar.getStyle().set("cursor", "pointer");
        btnEntrar.getStyle().set("margin-top", "2em");

        mainContent.add(titulo, subtitulo, desc, btnEntrar);

        // --- 3. FOOTER ---
        Div footer = new Div();
        footer.setWidthFull();
        footer.setHeight("10px");
        footer.getStyle().set("background-color", "#000000"); // Negro del río

        add(header, mainContent, footer);
    }
}

/*package com.unrn.gpiv.views;

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
/*
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
}*/
