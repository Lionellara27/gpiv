package com.unrn.gpiv.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

// ¡Ruta vacía y SIN layout! 100% independiente
@Route("")
public class HomeView extends VerticalLayout {

    public HomeView() {
        // Configuración general: fondo blanco y sin espacios raros
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle().set("background-color", "#FFFFFF");

        // --- 1. HEADER INTEGRADO DIRECTAMENTE EN LA VISTA ---
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setPadding(true);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setAlignItems(Alignment.CENTER);

        Image logo = new Image("images/Enrepavi.png", "Logo del Parque Industrial");
        logo.setHeight("180px"); // Logo grande de presentación

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

        H1 titulo = new H1("SGPIV");
        titulo.getStyle().set("font-size", "6em");
        titulo.getStyle().set("font-weight", "900");
        titulo.getStyle().set("color", "#000000");
        titulo.getStyle().set("margin", "0");

        Span subtitulo = new Span("Sistema de Gestión del Parque Industrial de Viedma");
        subtitulo.getStyle().set("font-size", "1.5em");
        subtitulo.getStyle().set("letter-spacing", "5px");
        subtitulo.getStyle().set("color", "#0063BE");
        subtitulo.getStyle().set("font-weight", "bold");

        Paragraph desc = new Paragraph("Parque Industrial de Viedma - Motor Productivo de la Comarca");
        desc.getStyle().set("color", "#666");
        desc.getStyle().set("margin-top", "1em");

        // --- SECCIÓN DE BOTONES ---
        HorizontalLayout botonera = new HorizontalLayout();
        botonera.getStyle().set("margin-top", "2em");
        botonera.setSpacing(true);

        Button btnEntrar = new Button("INGRESAR AL SISTEMA", e -> {
            getUI().ifPresent(ui -> ui.navigate("login"));
        });
        btnEntrar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnEntrar.getStyle().set("background-color", "#009A3B");
        btnEntrar.getStyle().set("padding", "1.5em 2.5em");
        btnEntrar.getStyle().set("font-weight", "bold");
        btnEntrar.getStyle().set("border-radius", "50px");
        btnEntrar.getStyle().set("box-shadow", "0 4px 15px rgba(0, 154, 59, 0.4)");
        btnEntrar.getStyle().set("cursor", "pointer");

        Button btnSolicitar = new Button("SOLICITAR LOTE (NUEVA EMPRESA)", e -> {
            getUI().ifPresent(ui -> ui.navigate("registro"));
        });
        btnSolicitar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnSolicitar.getStyle().set("background-color", "#0063BE");
        btnSolicitar.getStyle().set("padding", "1.5em 2.5em");
        btnSolicitar.getStyle().set("font-weight", "bold");
        btnSolicitar.getStyle().set("border-radius", "50px");
        btnSolicitar.getStyle().set("box-shadow", "0 4px 15px rgba(0, 99, 190, 0.4)");
        btnSolicitar.getStyle().set("cursor", "pointer");

        botonera.add(btnEntrar, btnSolicitar);
        mainContent.add(titulo, subtitulo, desc, botonera);

        // --- 3. FOOTER ---
        Div footer = new Div();
        footer.setWidthFull();
        footer.setHeight("10px");
        footer.getStyle().set("background-color", "#000000");

        // Sumamos TODO a la pantalla (incluido el header con el logo)
        add(header, mainContent, footer);
    }
}