package com.unrn.gpiv.views;

import com.unrn.gpiv.common.TipoServicio; // <-- IMPORTANTE PARA LOS CHECKBOX
import com.unrn.gpiv.model.ProyectoProductivo;
import com.unrn.gpiv.model.RepresentanteEmpresa;
import com.unrn.gpiv.service.EmpresaService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField; // <-- CAMBIADO A INTEGER
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;

@Route("formulario-proyecto")
public class FormularioProyectoView extends VerticalLayout {

    // Componentes que se vinculan al modelo
    private TextField txtNombreProyecto = new TextField("Razón Social / Nombre del Proyecto");
    private TextArea txtActividad = new TextArea("Descripción de la Actividad Principal");

    // --- 1. SUPERFICIE ---
    private com.vaadin.flow.component.radiobutton.RadioButtonGroup<String> selSuperficie = new com.vaadin.flow.component.radiobutton.RadioButtonGroup<>();

    // --- 2. EMPLEADOS (Ahora es IntegerField) ---
    private IntegerField txtEmpleados = new IntegerField("Cantidad aproximada de empleados");

    // --- 3. SERVICIOS (Conectado al Enum) ---
    private CheckboxGroup<TipoServicio> chkServicios = new CheckboxGroup<>();

    private TextArea txtImpacto = new TextArea("Impacto Ambiental y Residuos");

    // Componentes para el PDF
    private MemoryBuffer buffer = new MemoryBuffer();
    private Upload uploadPdf = new Upload(buffer);

    private Binder<ProyectoProductivo> binder = new Binder<>(ProyectoProductivo.class);

    public FormularioProyectoView(@Autowired EmpresaService empresaService) {
        // --- CONFIGURACIÓN DE LA VISTA ---
        setSizeFull();
        setPadding(true);
        setSpacing(true);
        setAlignItems(Alignment.CENTER);
        getStyle().set("overflow-y", "auto");
        getStyle().set("background-color", "#f5f7fa");

        configurarValidaciones();
        configurarUpload();

        VerticalLayout formCard = new VerticalLayout();
        formCard.setWidth("700px");
        formCard.getStyle().set("background-color", "white");
        formCard.getStyle().set("padding", "2.5em");
        formCard.getStyle().set("border-radius", "15px");
        formCard.getStyle().set("box-shadow", "0 10px 30px rgba(0,0,0,0.1)");
        formCard.getStyle().set("margin-top", "2em");
        formCard.getStyle().set("margin-bottom", "2em");
        formCard.setAlignItems(Alignment.STRETCH);

        // LOGO Y BANDERA
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

        // CAMPOS DE TEXTO
        txtNombreProyecto.setPlaceholder("Ej: Planta de ensamblaje metalúrgico");
        txtActividad.setPlaceholder("Describí en detalle qué se va a producir o qué servicio se va a brindar...");
        txtActividad.setMinHeight("100px");

        // --- SUPERFICIE ---
        selSuperficie.setLabel("Superficie requerida para desarrollar la actividad");
        selSuperficie.setItems("1200 m²", "2500 m²", "5000 m²", "Más de 5000 m²");
        selSuperficie.addThemeVariants(com.vaadin.flow.component.radiobutton.RadioGroupVariant.LUMO_VERTICAL);
        selSuperficie.getStyle().set("font-weight", "bold").set("margin-top", "1em");

        // --- EMPLEADOS ---
        txtEmpleados.setPlaceholder("Ej: 15");
        txtEmpleados.setMin(1);
        txtEmpleados.setMax(1500); // Límite lógico para el parque
        txtEmpleados.setStepButtonsVisible(true);
        txtEmpleados.getStyle().set("margin-top", "1em");

        // --- SERVICIOS ---
        chkServicios.setLabel("Servicios de Infraestructura necesarios");
        chkServicios.setItems(TipoServicio.values()); // Trae las opciones directo de la BD
        chkServicios.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);

        txtImpacto.setPlaceholder("¿Genera efluentes líquidos, gases o ruidos molestos? Detallar.");
        txtImpacto.setMinHeight("80px");

        // Carga de PDF
        Span etiquetaPdf = new Span("Adjuntar Análisis de Mercado y Objetivos (PDF)");
        etiquetaPdf.getStyle().set("font-weight", "bold").set("margin-top", "1em").set("color", "#444");

        HorizontalLayout botonera = new HorizontalLayout();
        botonera.getStyle().set("margin-top", "2em");
        botonera.setJustifyContentMode(JustifyContentMode.END);

        Button btnCancelar = new Button("Cancelar", e -> getUI().ifPresent(ui -> ui.navigate("")));
        btnCancelar.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        Button btnEnviar = new Button("ENVIAR SOLICITUD", e -> {
            try {
                ProyectoProductivo proyecto = new ProyectoProductivo();
                RepresentanteEmpresa rep = (RepresentanteEmpresa) VaadinSession.getCurrent().getAttribute("usuarioLogueado");

                if (rep == null) {
                    Notification.show("ERROR: No hay usuario en sesión. Volvé a registrarte.", 5000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
                    return;
                }

                if (binder.writeBeanIfValid(proyecto)) {

                    if (buffer.getFileName().isEmpty()) {
                        Notification.show("Debe subir el PDF del proyecto", 3000, Notification.Position.MIDDLE);
                        return;
                    }

                    proyecto.setPdfProyecto(buffer.getInputStream().readAllBytes());
                    proyecto.setNombreArchivoPdf(buffer.getFileName());

                    empresaService.recibirSolicitud(proyecto, rep, txtNombreProyecto.getValue());

                    Notification.show("Solicitud enviada correctamente", 3000, Notification.Position.MIDDLE);
                    getUI().ifPresent(ui -> ui.navigate(""));

                } else {
                    Notification.show("Error de validación: Revisá los campos rojos", 3000, Notification.Position.MIDDLE);
                    binder.validate();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                Notification.show("ERROR CRÍTICO: " + ex.getMessage(), 10000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        btnEnviar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnEnviar.getStyle().set("background-color", "#009A3B");

        botonera.add(btnCancelar, btnEnviar);

        // --- ARMADO DE LA TARJETA (Acá agregué todos en orden) ---
        formCard.add(logo, lineasBandera, titulo, subtitulo, txtNombreProyecto, txtActividad,
                selSuperficie, txtEmpleados, chkServicios, txtImpacto, etiquetaPdf, uploadPdf, botonera);

        add(formCard);
    }

    private void configurarUpload() {
        uploadPdf.setAcceptedFileTypes("application/pdf");
        uploadPdf.setMaxFiles(1);
        uploadPdf.setUploadButton(new Button("Seleccionar PDF"));
    }

    private void configurarValidaciones() {

        binder.forField(txtActividad)
                .asRequired("La descripción de la actividad es obligatoria")
                .bind(ProyectoProductivo::getActividadPrincipal, ProyectoProductivo::setActividadPrincipal);

        // NUEVA VALIDACIÓN: Superficie
        binder.forField(selSuperficie)
                .asRequired("Debe seleccionar una superficie")
                .bind(ProyectoProductivo::getSuperficieRequerida, ProyectoProductivo::setSuperficieRequerida);

        // NUEVA VALIDACIÓN: Empleados
        binder.forField(txtEmpleados)
                .asRequired("Indique la cantidad de empleados")
                .bind(ProyectoProductivo::getCantidadEmpleados, ProyectoProductivo::setCantidadEmpleados);

        // NUEVA VALIDACIÓN: Servicios
        binder.forField(chkServicios)
                .asRequired("Seleccione al menos un servicio")
                .bind(ProyectoProductivo::getServiciosNecesarios, ProyectoProductivo::setServiciosNecesarios);

        binder.bindInstanceFields(this);

        binder.forField(txtImpacto)
                .bind(ProyectoProductivo::getImpactoAmbiental, ProyectoProductivo::setImpactoAmbiental);

        binder.bindInstanceFields(this);
    }
}

/*package com.unrn.gpiv.views;

import com.unrn.gpiv.model.ProyectoProductivo;
import com.unrn.gpiv.model.RepresentanteEmpresa;
import com.unrn.gpiv.service.EmpresaService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

@Route("formulario-proyecto")
public class FormularioProyectoView extends VerticalLayout {

    // Componentes que se vinculan al modelo
    private TextField txtNombreProyecto = new TextField("Razón Social / Nombre del Proyecto");
    private TextArea txtActividad = new TextArea("Descripción de la Actividad Principal");
    //private NumberField txtSuperficie = new NumberField("Superficie requerida (m²)");
    private NumberField txtEmpleados = new NumberField("Empleados estimados");
    private TextArea txtImpacto = new TextArea("Impacto Ambiental y Residuos");
    //radio button
    private com.vaadin.flow.component.radiobutton.RadioButtonGroup<String> selSuperficie = new com.vaadin.flow.component.radiobutton.RadioButtonGroup<>();

    // Componentes para el PDF
    private MemoryBuffer buffer = new MemoryBuffer();
    private Upload uploadPdf = new Upload(buffer);

    private Binder<ProyectoProductivo> binder = new Binder<>(ProyectoProductivo.class);

    public FormularioProyectoView(@Autowired EmpresaService empresaService) {
        // --- CONFIGURACIÓN DE LA VISTA (Para que no se corte arriba) ---
        setSizeFull();
        setPadding(true);
        setSpacing(true);
        setAlignItems(Alignment.CENTER);
        getStyle().set("overflow-y", "auto"); // Permite scroll
        getStyle().set("background-color", "#f5f7fa");

        configurarValidaciones();
        configurarUpload();

        VerticalLayout formCard = new VerticalLayout();

        formCard.setWidth("700px");
        formCard.getStyle().set("background-color", "white");
        formCard.getStyle().set("padding", "2.5em");
        formCard.getStyle().set("border-radius", "15px");
        formCard.getStyle().set("box-shadow", "0 10px 30px rgba(0,0,0,0.1)");

        formCard.getStyle().set("margin-top", "2em");
        formCard.getStyle().set("margin-bottom", "2em");

        formCard.setAlignItems(Alignment.STRETCH);

        // LOGO Y BANDERA (Tu diseño original)
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

        // Campos de texto con tus placeholders originales
        txtNombreProyecto.setPlaceholder("Ej: Planta de ensamblaje metalúrgico");
        txtActividad.setPlaceholder("Describí en detalle qué se va a producir o qué servicio se va a brindar...");
        txtActividad.setMinHeight("100px");

        HorizontalLayout filaMedidas = new HorizontalLayout();
        filaMedidas.setWidthFull();
        //txtSuperficie.setPlaceholder("Ej: 2500");
        //txtSuperficie.setWidth("50%");
        txtEmpleados.setPlaceholder("Ej: 15");
        txtEmpleados.setWidth("50%");
        filaMedidas.add(txtEmpleados);

        selSuperficie.setLabel("Superficie requerida para desarrollar la actividad");
        selSuperficie.setItems("1200 m²", "2500 m²", "5000 m²", "Más de 5000 m²");
        selSuperficie.addThemeVariants(com.vaadin.flow.component.radiobutton.RadioGroupVariant.LUMO_VERTICAL);
        selSuperficie.getStyle().set("font-weight", "bold").set("margin-top", "1em");

        CheckboxGroup<String> chkServicios = new CheckboxGroup<>();
        chkServicios.setLabel("Servicios de Infraestructura necesarios");
        chkServicios.setItems("Agua Potable", "Energía Eléctrica (Trifásica)", "Gas Industrial", "Internet / Fibra Óptica");
        chkServicios.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);

        txtImpacto.setPlaceholder("¿Genera efluentes líquidos, gases o ruidos molestos? Detallar.");
        txtImpacto.setMinHeight("80px");

        //Carga de PDF--------------------
        Span etiquetaPdf = new Span("Adjuntar Análisis de Mercado y Objetivos (PDF)");
        etiquetaPdf.getStyle().set("font-weight", "bold").set("margin-top", "1em").set("color", "#444");

        HorizontalLayout botonera = new HorizontalLayout();
        botonera.getStyle().set("margin-top", "2em");
        botonera.setJustifyContentMode(JustifyContentMode.END);

        Button btnCancelar = new Button("Cancelar", e -> getUI().ifPresent(ui -> ui.navigate("")));
        btnCancelar.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        Button btnEnviar = new Button("ENVIAR SOLICITUD", e -> {
            try {
                ProyectoProductivo proyecto = new ProyectoProductivo();
                RepresentanteEmpresa rep = (RepresentanteEmpresa) VaadinSession.getCurrent().getAttribute("usuarioLogueado");

                // DEBUG 1: ¿Existe el usuario?
                if (rep == null) {
                    Notification.show("ERROR: No hay usuario en sesión. Volvé a registrarte.", 5000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
                    return;
                }

                // DEBUG 2: ¿Los datos del formulario son válidos?
                if (binder.writeBeanIfValid(proyecto)) {

                    if (buffer.getFileName().isEmpty()) {
                        Notification.show("Debe subir el PDF del proyecto", 3000, Notification.Position.MIDDLE);
                        return;
                    }

                    // Procesamos el archivo
                    proyecto.setPdfProyecto(buffer.getInputStream().readAllBytes());
                    proyecto.setNombreArchivoPdf(buffer.getFileName());

                    // DEBUG 3: Llamada al servicio
                    System.out.println("Enviando solicitud al servicio...");
                    empresaService.recibirSolicitud(proyecto, rep, txtNombreProyecto.getValue());
                    System.out.println("¡Servicio completado con éxito!");

                    Notification.show("Solicitud enviada correctamente", 3000, Notification.Position.MIDDLE);
                    getUI().ifPresent(ui -> ui.navigate(""));

                } else {
                    // Si entra acá, hay un campo mal cargado que el Binder detectó
                    Notification.show("Error de validación: Revisá los campos rojos", 3000, Notification.Position.MIDDLE);
                    binder.validate(); // Esto fuerza a que se pongan rojos los campos con error
                }
            } catch (Exception ex) {
                // ESTO ES CLAVE: Si hay un error de base de datos o de código, saltará acá
                ex.printStackTrace(); // Esto imprime el error detallado en la consola de IntelliJ
                Notification.show("ERROR CRÍTICO: " + ex.getMessage(), 10000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        btnEnviar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnEnviar.getStyle().set("background-color", "#009A3B");

        botonera.add(btnCancelar, btnEnviar);


        formCard.add(logo, lineasBandera, titulo, subtitulo, txtNombreProyecto, txtActividad,
                selSuperficie, chkServicios, txtImpacto, etiquetaPdf, uploadPdf, botonera);

        add(formCard);
    }

    private void configurarUpload() {
        uploadPdf.setAcceptedFileTypes("application/pdf");
        uploadPdf.setMaxFiles(1);
        uploadPdf.setUploadButton(new Button("Seleccionar PDF"));
    }

    private void configurarValidaciones() {
        binder.forField(txtActividad)
                .asRequired("La descripción de la actividad es obligatoria")
                .bind(ProyectoProductivo::getActividadPrincipal, ProyectoProductivo::setActividadPrincipal);

        binder.forField(txtEmpleados)
                .bind(p -> (double) p.getCantProduccion(),
                        (p, v) -> p.setCantProduccion(v.intValue()));

        binder.bindInstanceFields(this);
    }
}*/