package com.unrn.gpiv.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route("registro")
public class RegistroView extends VerticalLayout {

    public RegistroView() {
        // Mismo fondo gris clarito para que resalte la tarjeta
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        getStyle().set("background-color", "#f5f7fa");

        // --- TARJETA DE REGISTRO ---
        VerticalLayout registroCard = new VerticalLayout();
        registroCard.setWidth("500px"); // Un poco más ancha que el login
        registroCard.getStyle().set("background-color", "white");
        registroCard.getStyle().set("padding", "2.5em");
        registroCard.getStyle().set("border-radius", "15px");
        registroCard.getStyle().set("box-shadow", "0 10px 30px rgba(0,0,0,0.1)");
        registroCard.setAlignItems(Alignment.STRETCH);

        // Títulos y bienvenida
        H2 titulo = new H2("Solicitud de Lote");
        titulo.getStyle().set("text-align", "center");
        titulo.getStyle().set("color", "#0063BE"); // Azul bandera
        titulo.getStyle().set("margin-top", "0");
        titulo.getStyle().set("margin-bottom", "0.2em");

        Paragraph subtitulo = new Paragraph("¡Bienvenido! Para poder seleccionar un lote e iniciar tu proyecto, primero necesitamos crear tu cuenta en el sistema.");
        subtitulo.getStyle().set("text-align", "center");
        subtitulo.getStyle().set("color", "#666");
        subtitulo.getStyle().set("font-size", "0.95em");

        // --- SELECTOR DE TIPO DE ENTIDAD ---
        RadioButtonGroup<String> tipoEntidad = new RadioButtonGroup<>();
        tipoEntidad.setLabel("¿Bajo qué figura te vas a registrar?");
        tipoEntidad.setItems("Persona Física (Monotributo/Autónomo)", "Persona Jurídica (SRL, SA, etc.)");
        tipoEntidad.setValue("Persona Física (Monotributo/Autónomo)"); // Opción por defecto
        tipoEntidad.getStyle().set("margin-top", "1em");
        tipoEntidad.getStyle().set("font-weight", "bold");

        // --- CAMPOS DE FORMULARIO ---
        TextField txtNombre = new TextField("Nombre o Razón Social");
        txtNombre.setPlaceholder("Ej: Pedro Pérez / Metalúrgica Viedma SRL");

        TextField txtCuit = new TextField("CUIT");
        txtCuit.setPlaceholder("Sin guiones");

        EmailField txtCorreo = new EmailField("Correo electrónico");
        txtCorreo.setPlaceholder("Este será tu usuario");

        PasswordField txtPassword = new PasswordField("Contraseña");
        txtPassword.setPlaceholder("Mínimo 8 caracteres");

        // --- BOTÓN PRINCIPAL ---
        Button btnCrearCuenta = new Button("CREAR CUENTA Y CONTINUAR");
        btnCrearCuenta.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnCrearCuenta.getStyle().set("background-color", "#009A3B"); // Verde bandera
        btnCrearCuenta.getStyle().set("font-weight", "bold");
        btnCrearCuenta.getStyle().set("margin-top", "1.5em");
        btnCrearCuenta.addClickListener(e -> {
            // TODO: Guardar en base de datos y mandar al paso 2 (el formulario de proyecto/lote)
        });

        // --- VOLVER AL LOGIN ---
        VerticalLayout linksLayout = new VerticalLayout();
        linksLayout.setAlignItems(Alignment.CENTER);
        linksLayout.setPadding(false);
        linksLayout.getStyle().set("margin-top", "1em");

        Span txtYaTengo = new Span("¿Ya tenés cuenta en el Parque?");
        txtYaTengo.getStyle().set("font-size", "0.9em");
        txtYaTengo.getStyle().set("color", "#666");

        Button btnVolverLogin = new Button("Ingresar acá", e -> {
            getUI().ifPresent(ui -> ui.navigate("login"));
        });
        btnVolverLogin.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        btnVolverLogin.getStyle().set("font-weight", "bold");
        btnVolverLogin.getStyle().set("color", "#0063BE");

        linksLayout.add(txtYaTengo, btnVolverLogin);

        // --- ARMAMOS EL ROMPECABEZAS ---
        registroCard.add(titulo, subtitulo, tipoEntidad, txtNombre, txtCuit, txtCorreo, txtPassword, btnCrearCuenta, linksLayout);

        add(registroCard);
    }
}
