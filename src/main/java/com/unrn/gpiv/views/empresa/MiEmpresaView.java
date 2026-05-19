package com.unrn.gpiv.views.empresa;

import com.unrn.gpiv.model.RepresentanteEmpresa;
import com.unrn.gpiv.model.Usuario;
import com.unrn.gpiv.service.EmpresaService;
import com.unrn.gpiv.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;

@PageTitle("Mi Empresa | SGPIV")
@Route(value = "mi-empresa", layout = MainLayout.class)
public class MiEmpresaView extends VerticalLayout {

    private final EmpresaService empresaService;

    public MiEmpresaView(@Autowired EmpresaService empresaService) {
        this.empresaService = empresaService;

        setPadding(true);
        setSpacing(true);
        getStyle().set("max-width", "900px").set("margin", "0 auto"); // Centra el formulario pro en pantalla

        // 1. RECUPERAR USUARIO DE SESIÓN
        Usuario usuarioLogueado = (Usuario) VaadinSession.getCurrent().getAttribute("usuarioLogueado");

        if (!(usuarioLogueado instanceof RepresentanteEmpresa logueado)) {
            add(new H2("Acceso denegado. Vista para representantes de empresas."));
            return;
        }

        H2 titulo = new H2("Registro Final de la Empresa");
        Paragraph subtitulo = new Paragraph("Complete los datos institucionales y legales requeridos por ENREPAVI para proceder a la asignación de tierras en el Parque Industrial.");
        subtitulo.getStyle().set("color", "#666");

        // ==========================================
        // 🔒 BLOQUE 1: DATOS PRE-CARGADOS (SOLO LECTURA)
        // ==========================================
        VerticalLayout datosExistentesCard = new VerticalLayout();
        datosExistentesCard.setWidthFull();
        datosExistentesCard.getStyle().set("background-color", "#F8FAFC").set("border", "1px solid #E2E8F0").set("border-radius", "12px");
        datosExistentesCard.setPadding(true);

        H3 titlePre = new H3("🏢 Datos Iniciales Verificados");
        titlePre.getStyle().set("margin-top", "0").set("color", "#4A5568");

        FormLayout formPreCargado = new FormLayout();

        TextField txtRazonSocial = new TextField("Razón Social / Nombre Firma");
        txtRazonSocial.setValue(logueado.getNombreCompleto()); // O el método con el que recuperes la Razón Social de tu entidad
        txtRazonSocial.setReadOnly(true); // 🎯 Bloqueado

        TextField txtCuit = new TextField("CUIT Industrial");
        txtCuit.setValue("30-12345678-9"); // Reemplazar por logueado.getCuit() real de tu BD
        txtCuit.setReadOnly(true); // 🎯 Bloqueado

        TextField txtTelefono = new TextField("Teléfono Principal");
        txtTelefono.setValue("2920-456789"); // Reemplazar por tu getter real
        txtTelefono.setReadOnly(true); // 🎯 Bloqueado

        formPreCargado.add(txtRazonSocial, txtCuit, txtTelefono);
        datosExistentesCard.add(titlePre, formPreCargado);

        // ==========================================
        // 📝 BLOQUE 2: NUEVOS CAMPOS A COMPLETAR (FASE 3)
        // ==========================================
        VerticalLayout formularioNuevoCard = new VerticalLayout();
        formularioNuevoCard.setWidthFull();
        formularioNuevoCard.setPadding(false);
        formularioNuevoCard.getStyle().set("margin-top", "20px");

        H3 titleNuevos = new H3("📋 Datos Legales e Institucionales");
        titleNuevos.getStyle().set("color", "#1A202C");

        FormLayout formNuevosDatos = new FormLayout();

        // 📍 Domicilio Fiscal Obligatorio
        TextField txtDomicilio = new TextField("Domicilio Legal / Fiscal");
        txtDomicilio.setPlaceholder("Ej: Calle Rosas 123, Viedma, Río Negro");
        txtDomicilio.setRequired(true);
        txtDomicilio.setErrorMessage("El domicilio fiscal es obligatorio para las exenciones.");

        // ⚖️ Tipo de Sociedad Dropdown (ComboBox)
        ComboBox<String> comboSociedad = new ComboBox<>("Personería Jurídica / Tipo Sociedad");
        comboSociedad.setItems("Sociedad Anónima (S.A.)", "Sociedad de Responsabilidad Limitada (S.R.L.)",
                "Sociedad por Acciones Simplificada (S.A.S.)", "Unipersonal / Monotributista", "Cooperativa");
        comboSociedad.setPlaceholder("Seleccione una opción");
        comboSociedad.setRequired(true);

        // 📱 Teléfono Corporativo de Emergencia (Opcional)
        TextField txtTelEmergencia = new TextField("Teléfono Corporativo de Emergencia (Opcional)");
        txtTelEmergencia.setPlaceholder("Ej: 2920-15443322");
        txtTelEmergencia.setHelperText("Número alternativo para contingencias en planta.");

        // 📄 Número de Inscripción Registral (Opcional)
        TextField txtInscripcion = new TextField("N° Inscripción Registral IGPJ / RPC (Opcional)");
        txtInscripcion.setPlaceholder("Tomo, Folio o N° de Registro");

        formNuevosDatos.add(txtDomicilio, comboSociedad, txtTelEmergencia, txtInscripcion);

        // ==========================================
        // 🚀 BOTONERA DE ENVÍO
        // ==========================================
        HorizontalLayout botonera = new HorizontalLayout();
        botonera.getStyle().set("margin-top", "30px");

        Button btnGuardar = new Button("Enviar Registro de Empresa", VaadinIcon.PAPERPLANE.create());
        btnGuardar.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);

        Button btnCancelar = new Button("Volver", e -> getUI().ifPresent(ui -> ui.navigate("mi-proyecto")));

        btnGuardar.addClickListener(e -> {
            // Validamos que los obligatorios tengan datos
            if (txtDomicilio.isEmpty() || comboSociedad.isEmpty()) {
                Notification n = Notification.show("Por favor, complete los campos obligatorios.", 3000, Notification.Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            // 🧠 ACÁ CONECTÁS CON TU SERVICE EN EL PRÓXIMO PASO:
            // empresaService.registrarDatosFinales(logueado, txtDomicilio.getValue(), comboSociedad.getValue(), ...);

            // Cartel Pro de confirmación de cola de espera de lotes (HU 07)
            VerticalLayout content = new VerticalLayout();
            content.add(new H3("¡Registro Enviado con Éxito! 🎉"));
            content.add(new Paragraph("Los datos institucionales de su firma fueron guardados correctamente. La empresa ha ingresado en la cola de asignación de tierras de ENREPAVI. Se le notificará cuando haya un lote compatible disponible."));

            Button btnCerrarNotif = new Button("Entendido");
            Notification notification = new Notification(content);
            notification.setDuration(0); // Se queda abierta hasta que clickee
            notification.setPosition(Notification.Position.MIDDLE);

            btnCerrarNotif.addClickListener(click -> {
                notification.close();
                getUI().ifPresent(ui -> ui.navigate("mi-proyecto")); // Lo manda al panel principal
            });

            content.add(btnCerrarNotif);
            notification.open();
        });

        botonera.add(btnGuardar, btnCancelar);
        formularioNuevoCard.add(titleNuevos, formNuevosDatos);

        add(titulo, subtitulo, datosExistentesCard, formularioNuevoCard, botonera);
    }
}
