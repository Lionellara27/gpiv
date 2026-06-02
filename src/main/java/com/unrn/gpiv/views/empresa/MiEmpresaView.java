package com.unrn.gpiv.views.empresa;

import com.unrn.gpiv.model.Empresa;
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
import com.unrn.gpiv.common.EstadoEmpresa;
import com.unrn.gpiv.common.EstadoSolicitud;
import com.unrn.gpiv.model.Empresa;
import com.unrn.gpiv.model.RepresentanteEmpresa;
import com.unrn.gpiv.model.Usuario;
import com.unrn.gpiv.service.EmpresaService;
import com.unrn.gpiv.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.beans.factory.annotation.Autowired;

@PageTitle("Mi Empresa | SGPIV")
@Route(value = "mi-empresa", layout = MainLayout.class)
public class MiEmpresaView extends VerticalLayout {

    private final EmpresaService empresaService;

    public MiEmpresaView(@Autowired EmpresaService empresaService) {
        this.empresaService = empresaService;

        setPadding(true);
        setSpacing(true);
        getStyle().set("max-width", "900px").set("margin", "0 auto");

        // 1. RECUPERAR USUARIO
        Usuario usuarioLogueado = (Usuario) VaadinSession.getCurrent().getAttribute("usuarioLogueado");
        if (!(usuarioLogueado instanceof RepresentanteEmpresa logueado)) {
            add(new H2("Acceso denegado."));
            return;
        }

        // 2. RECUPERAR DATOS EMPRESA
        Empresa empresaData = empresaService.obtenerEmpresaPorRepresentante(logueado);
        boolean yaRegistrada = (empresaData != null && empresaData.getFechaRadicacion() != null);

        H2 titulo = new H2("Registro Final de la Empresa");
        add(titulo);

        // DATOS PRE-CARGADOS ---------------------------------------------
        VerticalLayout datosExistentesCard = new VerticalLayout();
        datosExistentesCard.getStyle().set("background-color", "#F8FAFC").set("border", "1px solid #E2E8F0").set("border-radius", "12px");

        TextField txtRazonSocial = new TextField("Razón Social");
        txtRazonSocial.setValue(logueado.getNombreCompleto());
        txtRazonSocial.setReadOnly(true);

        TextField txtCuit = new TextField("CUIT");
        txtCuit.setValue(logueado.getCuitPersonal() != null ? logueado.getCuitPersonal() : "No registrado");
        txtCuit.setReadOnly(true);

        TextField txtTelefono = new TextField("Teléfono Personal");
        txtTelefono.setValue(logueado.getTelefono() != null ? logueado.getTelefono() : "No registrado");
        txtTelefono.setReadOnly(true);

        datosExistentesCard.add(new H3("🏢 Datos Iniciales"), new FormLayout(txtRazonSocial, txtCuit, txtTelefono));

        // FORMULARIO-----------------------------------------------
        FormLayout formNuevosDatos = new FormLayout();
        TextField txtDomicilio = new TextField("Domicilio Legal");
        ComboBox<String> comboSociedad = new ComboBox<>("Tipo Sociedad", "S.A.", "S.R.L.", "S.A.S.", "Monotributista", "Cooperativa");
        TextField txtTelEmergencia = new TextField("Teléfono Emergencia");
        TextField txtInscripcion = new TextField("N° Inscripción Registral");

        // Logica de Registro/Lectura (Precarga si ya existe)
        if (yaRegistrada) {
            txtDomicilio.setValue(empresaData.getDireccion() != null ? empresaData.getDireccion() : "");
            comboSociedad.setValue(empresaData.getTipoSociedad() != null ? empresaData.getTipoSociedad() : "");
            txtTelEmergencia.setValue(empresaData.getTelefonoEmergencia() != null ? empresaData.getTelefonoEmergencia() : "");
            txtInscripcion.setValue(empresaData.getInscripcionRegistral() != null ? empresaData.getInscripcionRegistral() : "");

            txtDomicilio.setReadOnly(true);
            comboSociedad.setReadOnly(true);
            txtTelEmergencia.setReadOnly(true);
            txtInscripcion.setReadOnly(true);
        }

        formNuevosDatos.add(txtDomicilio, comboSociedad, txtTelEmergencia, txtInscripcion);

        // BOTONERA-----------------------------------------------
        Button btnGuardar = new Button("Enviar Registro", VaadinIcon.PAPERPLANE.create());
        btnGuardar.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);

        if (yaRegistrada) {
            btnGuardar.setVisible(false);
            add(datosExistentesCard, new H3("📋 Datos Legales"), formNuevosDatos, new Span("Los datos ya han sido enviados."));
        } else {
            btnGuardar.addClickListener(e -> {
                if (txtDomicilio.isEmpty()) {
                    Notification.show("El domicilio es obligatorio").addThemeVariants(NotificationVariant.LUMO_ERROR);
                    return;
                }

                // EVITAMOS EL NULL POINTER EXCEPTION: Si es null, creamos el objeto, sino usamos el existente
                Empresa empresaAGuardar = (empresaData != null) ? empresaData : new Empresa();

                // Llenamos los datos del formulario
                empresaAGuardar.setRazonSocial(logueado.getNombreCompleto());
                empresaAGuardar.setCuit(logueado.getCuitPersonal() != null ? logueado.getCuitPersonal() : "A DEFINIR");
                empresaAGuardar.setRepresentante(logueado);

                empresaAGuardar.setDireccion(txtDomicilio.getValue());
                empresaAGuardar.setTipoSociedad(comboSociedad.getValue());
                empresaAGuardar.setTelefonoEmergencia(txtTelEmergencia.getValue());
                empresaAGuardar.setInscripcionRegistral(txtInscripcion.getValue());

                // 🚀 ESTADOS DE TU NUEVA LOGICA SINKRONIZADOS
                empresaAGuardar.setEstado(EstadoSolicitud.APROBADA);
                empresaAGuardar.setEstadoEmpresa(EstadoEmpresa.RADICADA);
                empresaAGuardar.setFechaRadicacion(java.time.LocalDate.now());

                empresaService.actualizarEmpresa(empresaAGuardar);

                Notification.show("Registro exitoso").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                getUI().ifPresent(ui -> ui.navigate("mi-proyecto"));
            });

            add(datosExistentesCard, new H3("📋 Datos Legales"), formNuevosDatos, btnGuardar);
        }
    }
}
