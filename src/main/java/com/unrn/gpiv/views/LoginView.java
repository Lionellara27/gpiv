package com.unrn.gpiv.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route("login")
public class LoginView extends VerticalLayout {

    public LoginView() {
        // Fondo de la pantalla un poco gris para que resalte la "tarjeta" blanca del login
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        getStyle().set("background-color", "#f5f7fa");

        // --- TARJETA DE LOGIN (El contenedor blanco en el centro) ---
        VerticalLayout loginCard = new VerticalLayout();
        loginCard.setWidth("400px");
        loginCard.getStyle().set("background-color", "white");
        loginCard.getStyle().set("padding", "2.5em");
        loginCard.getStyle().set("border-radius", "15px");
        loginCard.getStyle().set("box-shadow", "0 10px 30px rgba(0,0,0,0.1)");
        loginCard.setAlignItems(Alignment.STRETCH); // Estira los campos a los bordes de la tarjeta

        // Título
        H2 titulo = new H2("Iniciar Sesión");
        titulo.getStyle().set("text-align", "center");
        titulo.getStyle().set("color", "#0063BE"); // Tu Azul bandera
        titulo.getStyle().set("margin-top", "0");

        // Campos de texto
        TextField txtUsuario = new TextField("Correo electrónico o Usuario");
        txtUsuario.setPlaceholder("ejemplo@empresa.com");
        txtUsuario.setClearButtonVisible(true); // Agrega la crucecita para borrar rápido

        PasswordField txtPassword = new PasswordField("Contraseña");
        txtPassword.setPlaceholder("Ingresá tu contraseña");

        // Botón de Ingresar
        Button btnIngresar = new Button("INGRESAR");
        btnIngresar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnIngresar.getStyle().set("background-color", "#009A3B"); // Tu Verde bandera
        btnIngresar.getStyle().set("font-weight", "bold");
        btnIngresar.getStyle().set("margin-top", "1.5em");
        btnIngresar.addClickListener(e -> {
            // TODO: Acá después conectamos Spring Security y la Base de Datos
        });

        // --- SECCIÓN DE LINKS INFERIORES ---
        VerticalLayout linksLayout = new VerticalLayout();
        linksLayout.setAlignItems(Alignment.CENTER);
        linksLayout.setPadding(false);
        linksLayout.setSpacing(false);
        linksLayout.getStyle().set("margin-top", "1.5em");

        // Link de recuperar contraseña (Botón invisible que funciona como texto)
        Button btnOlvido = new Button("¿Problemas para iniciar sesión? Olvidaste tu contraseña");
        btnOlvido.addThemeVariants(ButtonVariant.LUMO_TERTIARY); // Le saca el fondo y los bordes
        btnOlvido.getStyle().set("font-size", "0.85em");
        btnOlvido.getStyle().set("color", "#666");

        // Separador visual
        Span txtSeparador = new Span("────────  o  ────────");
        txtSeparador.getStyle().set("color", "#ccc");
        txtSeparador.getStyle().set("margin", "1em 0");

        // Link para registrarse
        Span txtNoCuenta = new Span("¿No tenés cuenta?");
        txtNoCuenta.getStyle().set("font-size", "0.9em");
        txtNoCuenta.getStyle().set("color", "#666");

        Button btnRegistro = new Button("Registrate y solicitá un lote", e -> {
            getUI().ifPresent(ui -> ui.navigate("registro"));
        });
        btnRegistro.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        btnRegistro.getStyle().set("font-weight", "bold");
        btnRegistro.getStyle().set("color", "#0063BE");

        // Armamos el rompecabezas
        linksLayout.add(btnOlvido, txtSeparador, txtNoCuenta, btnRegistro);
        loginCard.add(titulo, txtUsuario, txtPassword, btnIngresar, linksLayout);

        // Agregamos la tarjeta a la vista principal
        add(loginCard);
    }
}