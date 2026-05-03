package com.unrn.gpiv.views.empresa;

import com.unrn.gpiv.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Mi Proyecto | SGPIV")
@Route(value = "mi-proyecto", layout = MainLayout.class)
public class MiProyectoView extends VerticalLayout {

    // Cambiá este valor ("PROPUESTO", "EN_EVALUACION", "APROBADO") para ver cómo cambia la vista
    private String estadoActual = "PROPUESTO";

    public MiProyectoView() {
        setPadding(true);
        setSpacing(true);

        H2 titulo = new H2("Mi Proyecto Productivo");

        // --- TARJETA DE ESTADO ---
        VerticalLayout statusCard = new VerticalLayout();
        statusCard.setWidthFull();
        statusCard.getStyle().set("background-color", "#f8f9fa");
        statusCard.getStyle().set("border-radius", "15px");
        statusCard.setPadding(true);

        H3 sub = new H3("Estado de la Solicitud");

        Span badgeEstado = new Span(estadoActual);
        configurarEstiloBadge(badgeEstado);

        Paragraph infoEstado = new Paragraph(obtenerMensajeEstado());
        infoEstado.getStyle().set("color", "#666");

        // --- BOTONES DE ACCIÓN ---
        HorizontalLayout acciones = new HorizontalLayout();
        acciones.setMargin(true);

        Button btnModificar = new Button("Modificar Solicitud", VaadinIcon.EDIT.create());
        btnModificar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        // Regla: Solo se modifica si está en PROPUESTO
        if (!estadoActual.equals("PROPUESTO")) {
            btnModificar.setEnabled(false);
            btnModificar.setTooltipText("No se puede modificar un proyecto que ya está siendo evaluado o aprobado.");
        }

        btnModificar.addClickListener(e -> {
            Notification.show("Redirigiendo al editor del formulario...");
        });

        Button btnVerDetalles = new Button("Ver PDF Solicitud", VaadinIcon.FILE.create());
        btnVerDetalles.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        acciones.add(btnModificar, btnVerDetalles);

        statusCard.add(sub, badgeEstado, infoEstado, acciones);

        // --- RESUMEN DE DATOS (MOCKUP) ---
        VerticalLayout datosProyecto = new VerticalLayout();
        datosProyecto.add(new H3("Resumen del Proyecto"));
        datosProyecto.add(new Paragraph("Nombre: Planta de Ensamblaje Metalúrgico Viedma"));
        datosProyecto.add(new Paragraph("Superficie Requerida: 2500 m²"));
        datosProyecto.add(new Paragraph("Servicios: Agua, Luz Trifásica, Gas."));

        add(titulo, statusCard, datosProyecto);
    }

    private void configurarEstiloBadge(Span badge) {
        badge.getStyle().set("padding", "0.5em 1em");
        badge.getStyle().set("border-radius", "20px");
        badge.getStyle().set("font-weight", "bold");
        badge.getStyle().set("color", "white");

        switch (estadoActual) {
            case "PROPUESTO":
                badge.getStyle().set("background-color", "#6c757d"); // Gris
                break;
            case "EN_EVALUACION":
                badge.getStyle().set("background-color", "#0063BE"); // Azul
                break;
            case "APROBADO":
                badge.getStyle().set("background-color", "#009A3B"); // Verde
                break;
            case "RECHAZADO":
                badge.getStyle().set("background-color", "#d9534f"); // Rojo
                break;
            default:
                badge.getStyle().set("background-color", "#000");
        }
    }

    private String obtenerMensajeEstado() {
        return switch (estadoActual) {
            case "PROPUESTO" -> "Tu solicitud fue recibida correctamente. Podés editarla hasta que el Directorio comience la evaluación.";
            case "EN_EVALUACION" -> "El Directorio de ENREPAVI está analizando tu proyecto. Ya no es posible realizar modificaciones.";
            case "APROBADO" -> "¡Felicidades! Tu proyecto fue aprobado. Próximamente se te asignará un lote.";
            case "RECHAZADO" -> "Tu solicitud no ha sido aprobada en esta instancia. Por favor, revisá las observaciones enviadas a tu mail.";
            default -> "Estado desconocido.";
        };
    }
}