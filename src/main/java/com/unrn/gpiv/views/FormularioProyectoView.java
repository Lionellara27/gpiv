package com.unrn.gpiv.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route("formulario-proyecto")
public class FormularioProyectoView extends VerticalLayout {

    public FormularioProyectoView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        getStyle().set("background-color", "#f5f7fa");

        VerticalLayout formCard = new VerticalLayout();
        formCard.setWidth("700px");
        formCard.getStyle().set("background-color", "white");
        formCard.getStyle().set("padding", "2.5em");
        formCard.getStyle().set("border-radius", "15px");
        formCard.getStyle().set("box-shadow", "0 10px 30px rgba(0,0,0,0.1)");
        formCard.setAlignItems(Alignment.STRETCH);

        // LOGO Y BANDERA ADENTRO DE LA TARJETA
        Image logo = new Image("images/logo-parque.png", "Logo");
        logo.setHeight("70px");
        logo.getStyle().set("margin", "0 auto");

        Div lineasBandera = new Div();
        lineasBandera.setWidth("100px");
        lineasBandera.setHeight("4px");
        lineasBandera.getStyle().set("background", "linear-gradient(to right, #0063BE 33%, #FFFFFF 33%, #FFFFFF 66%, #009A3B 66%)");
        lineasBandera.getStyle().set("margin", "0 auto 1.5em auto");

        H2 titulo = new H2("Proyecto Productivo");
        titulo.getStyle().set("text-align", "center");
        titulo.getStyle().set("color", "#0063BE");
        titulo.getStyle().set("margin-top", "0");

        Paragraph subtitulo = new Paragraph("Paso 2: Detallá la actividad que vas a realizar para que la Administración evalúe qué lote se adapta mejor a tus necesidades.");
        subtitulo.getStyle().set("text-align", "center");
        subtitulo.getStyle().set("color", "#666");

        TextField txtNombreProyecto = new TextField("Nombre del Proyecto");
        txtNombreProyecto.setPlaceholder("Ej: Planta de ensamblaje metalúrgico");

        TextArea txtActividad = new TextArea("Descripción de la Actividad Principal");
        txtActividad.setPlaceholder("Describí en detalle qué se va a producir o qué servicio se va a brindar...");
        txtActividad.setMinHeight("100px");

        HorizontalLayout filaMedidas = new HorizontalLayout();
        filaMedidas.setWidthFull();

        NumberField txtSuperficie = new NumberField("Superficie requerida (m²)");
        txtSuperficie.setPlaceholder("Ej: 2500");
        txtSuperficie.setWidth("50%");

        NumberField txtEmpleados = new NumberField("Empleados estimados");
        txtEmpleados.setPlaceholder("Ej: 15");
        txtEmpleados.setWidth("50%");

        filaMedidas.add(txtSuperficie, txtEmpleados);

        CheckboxGroup<String> chkServicios = new CheckboxGroup<>();
        chkServicios.setLabel("Servicios de Infraestructura necesarios");
        chkServicios.setItems("Agua Potable", "Energía Eléctrica (Trifásica)", "Gas Industrial", "Internet / Fibra Óptica");
        chkServicios.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);

        TextArea txtImpacto = new TextArea("Impacto Ambiental y Residuos");
        txtImpacto.setPlaceholder("¿Genera efluentes líquidos, gases o ruidos molestos? Detallar.");
        txtImpacto.setMinHeight("80px");

        HorizontalLayout botonera = new HorizontalLayout();
        botonera.getStyle().set("margin-top", "2em");
        botonera.setJustifyContentMode(JustifyContentMode.END);

        Button btnCancelar = new Button("Cancelar", e -> {
            getUI().ifPresent(ui -> ui.navigate(""));
        });
        btnCancelar.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        Button btnEnviar = new Button("ENVIAR SOLICITUD", e -> {
            // Próximamente guardamos
        });
        btnEnviar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnEnviar.getStyle().set("background-color", "#009A3B");

        botonera.add(btnCancelar, btnEnviar);

        // ACÁ SE AGREGA TODO, INCLUIDO EL LOGO
        formCard.add(logo, lineasBandera, titulo, subtitulo, txtNombreProyecto, txtActividad, filaMedidas, chkServicios, txtImpacto, botonera);

        add(formCard);
    }
}