package com.unrn.gpiv.views.empresa;

import com.unrn.gpiv.common.EstadoEmpresa;
import com.unrn.gpiv.common.EstadoSolicitud;
import com.unrn.gpiv.model.Empresa;
import com.unrn.gpiv.model.Item;
import com.unrn.gpiv.model.RepresentanteEmpresa;
import com.unrn.gpiv.model.Usuario;
import com.unrn.gpiv.service.EmpresaService;
import com.unrn.gpiv.service.RecursoService;
import com.unrn.gpiv.service.SolicitudRecursoService;
import com.unrn.gpiv.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility;

@PageTitle("Solicitar Herramientas | SGPIV")
@Route(value = "empresa/solicitar-recurso", layout = MainLayout.class)
public class SolicitarRecursoView extends VerticalLayout {

    private final SolicitudRecursoService solicitudService;
    private final EmpresaService empresaService;
    private final RecursoService recursoService;

    private Empresa empresaLogueada;

    public SolicitarRecursoView(SolicitudRecursoService solicitudService, EmpresaService empresaService, RecursoService recursoService) {
        this.solicitudService = solicitudService;
        this.empresaService = empresaService;
        this.recursoService = recursoService;

        setPadding(true);
        setSpacing(true);
        setMaxWidth("600px");

        H2 titulo = new H2("Solicitud de Herramientas y Maquinaria");
        add(titulo);

        if (!validarAccesoEmpresa()) {
            return;
        }

        ComboBox<Item> cmbItems = new ComboBox<>("Seleccione el recurso que necesita");
        cmbItems.setItemLabelGenerator(Item::getNombre);
        cmbItems.setItems(recursoService.obtenerTodoElInventario().stream().map(r -> r.getItem()).distinct().toList());
        cmbItems.setWidthFull();

        IntegerField txtCantidad = new IntegerField("Cantidad");
        txtCantidad.setValue(1);
        txtCantidad.setMin(1);
        txtCantidad.setStepButtonsVisible(true);
        txtCantidad.setWidthFull();

        TextArea txtMotivo = new TextArea("Motivo de la solicitud");
        txtMotivo.setPlaceholder("Describa brevemente para qué tarea requiere el recurso...");
        txtMotivo.setRequired(true);
        txtMotivo.setHeight("120px");
        txtMotivo.setWidthFull();

        Button btnEnviar = new Button("Enviar Pedido al Parque", VaadinIcon.PAPERPLANE.create(), e -> {
            if (cmbItems.getValue() == null || txtCantidad.getValue() == null || txtMotivo.getValue().trim().isEmpty()) {
                Notification.show("Error: Todos los campos son obligatorios y la cantidad debe ser mayor a 0", 4000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            try {
                solicitudService.crearSolicitud(
                        empresaLogueada,
                        cmbItems.getValue(),
                        txtCantidad.getValue(),
                        txtMotivo.getValue()
                );
                Notification.show("Solicitud enviada con éxito. El Administrador ha sido notificado.", 4000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                getUI().ifPresent(ui -> ui.navigate("mi-empresa"));
            } catch (Exception ex) {
                Notification.show("Error: " + ex.getMessage(), 4000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        btnEnviar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button btnCancelar = new Button("Cancelar", e -> {
            getUI().ifPresent(ui -> ui.navigate("mi-empresa"));
        });
        btnCancelar.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);

        HorizontalLayout botonera = new HorizontalLayout(btnEnviar, btnCancelar);
        botonera.setSpacing(true);
        botonera.getStyle().set("margin-top", "15px");

        add(cmbItems, txtCantidad, txtMotivo, botonera);
    }

    private boolean validarAccesoEmpresa() {
        Usuario usuario = (Usuario) VaadinSession.getCurrent().getAttribute("usuarioLogueado");

        if (usuario == null || !(usuario instanceof RepresentanteEmpresa logueado)) {
            add(new Paragraph("No se detectó una sesión válida de empresa."));
            return false;
        }

        empresaLogueada = empresaService.obtenerEmpresaPorRepresentante(logueado);

        if (empresaLogueada == null || (empresaLogueada.getEstadoEmpresa() != EstadoEmpresa.RADICADA && empresaLogueada.getEstadoEmpresa() != EstadoEmpresa.TITULADA)) {

            setSizeFull();
            setJustifyContentMode(JustifyContentMode.CENTER);
            setAlignItems(Alignment.CENTER);

            Span cartelAlerta = new Span("⚠️ Acceso Restringido: Esta sección es exclusiva para empresas efectivamente radicadas o titulares en el parque.");
            cartelAlerta.addClassNames(
                    LumoUtility.TextColor.ERROR,
                    LumoUtility.FontWeight.BOLD,
                    LumoUtility.FontSize.MEDIUM
            );

            cartelAlerta.getStyle()
                    .set("background-color", "#fff5f5")
                    .set("padding", "20px")
                    .set("border", "1px solid #ffcdd2")
                    .set("border-radius", "8px")
                    .set("text-align", "center");

            add(cartelAlerta);
            return false;
        }
        return true;
    }
}