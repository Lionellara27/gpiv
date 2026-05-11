package com.unrn.gpiv.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

// Importamos Montserrat desde Google Fonts
@StyleSheet("https://fonts.googleapis.com/css2?family=Montserrat:wght@400;700&display=swap")
@Route("")
public class HomeView extends VerticalLayout {

	public HomeView() {
		// 1. Configuración del contenedor principal (Toda la pantalla)
		setSizeFull();
		setPadding(false);
		setSpacing(false);
		getStyle().set("background-color", "#F1E6D2"); // El beige de fondo
		getStyle().set("font-family", "Montserrat, sans-serif");

		// --- HEADER (Franja Verde Superior) ---
		HorizontalLayout header = new HorizontalLayout();
		header.setWidthFull();
		header.setHeight("80px");
		header.getStyle().set("background-color", "#1F3E2F"); // Verde Musgo
		header.setPadding(true);
		header.setAlignItems(Alignment.CENTER);
		header.setJustifyContentMode(JustifyContentMode.BETWEEN);

		// Logo Izquierdo
        Image logoImg = new Image("images/enrepavi.png", "Logo ENREPAVI");
        logoImg.setHeight("60px");
        header.add(logoImg);

		Span tituloHeader = new Span("Ente para la Reconversión del Parque Industrial de Viedma");
		tituloHeader.getStyle().set("color", "white");
		tituloHeader.getStyle().set("font-weight", "bold");

		// Bandera/Imagen Derecha
		Image banderaImg = new Image("images/bandera-RioNegro.jpg", "Bandera");
		banderaImg.setHeight("50px");
		header.add(banderaImg);

		header.add(logoImg, tituloHeader, banderaImg);


		// --- BODY (Contenido Central) ---
		VerticalLayout body = new VerticalLayout();
		body.setSizeFull();
		body.setJustifyContentMode(JustifyContentMode.CENTER);
		body.setAlignItems(Alignment.CENTER);

		H1 logoPrincipal = new H1("SGPIV");
		logoPrincipal.getStyle().set("font-size", "120px");
		logoPrincipal.getStyle().set("color", "#1F3E2F");
		logoPrincipal.getStyle().set("margin", "0");

		Span subtitulo = new Span("Sistema de Gestión de Proyectos");
		subtitulo.getStyle().set("font-size", "20px");
		subtitulo.getStyle().set("color", "#1F3E2F");
		subtitulo.getStyle().set("margin-bottom", "40px");

		// Contenedor de Botones
		HorizontalLayout layoutBotones = new HorizontalLayout();
		layoutBotones.setSpacing(true);

		Button btnIngresar = crearBotonEstilizado("INGRESAR");
		Button btnPresentar = crearBotonEstilizado("PRESENTAR PROYECTO");

		// Aquí podés agregar la navegación luego
		// btnIngresar.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("login")));

		layoutBotones.add(btnIngresar, btnPresentar);
		body.add(logoPrincipal, subtitulo, layoutBotones);


		// --- FOOTER (Franja Verde Inferior) ---
		HorizontalLayout footer = new HorizontalLayout();
		footer.setWidthFull();
		footer.setHeight("60px");
		footer.getStyle().set("background-color", "#1F3E2F");
		footer.setJustifyContentMode(JustifyContentMode.BETWEEN);
		footer.setAlignItems(Alignment.CENTER);
		footer.setPadding(true);
		footer.getStyle().set("color", "white");
		footer.getStyle().set("font-size", "14px");

		footer.add(new Span("Ruta 1 km 6.5"));
		footer.add(new Span("entrepavi@gmail.com"));
		footer.add(new Span("+54 9 2920 213078"));


		// Agregamos todo a la vista principal
		add(header, body, footer);
		expand(body); // Esto hace que el body ocupe todo el espacio sobrante
	}

	// Método auxiliar para no repetir código de botones
	private Button crearBotonEstilizado(String texto) {
		Button btn = new Button(texto);
		btn.getStyle().set("background-color", "#1F3E2F");
		btn.getStyle().set("color", "white");
		btn.getStyle().set("border-radius", "25px"); // Los hace ovalados
		btn.getStyle().set("padding", "25px 40px");
		btn.getStyle().set("font-weight", "bold");
		btn.getStyle().set("cursor", "pointer");
		return btn;
	}
}