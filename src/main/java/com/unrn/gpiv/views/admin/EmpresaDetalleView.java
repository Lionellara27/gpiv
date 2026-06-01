package com.unrn.gpiv.views.admin;

import com.unrn.gpiv.model.Empresa;
import com.unrn.gpiv.model.Lote;
import com.unrn.gpiv.model.InformeAvance;
import com.unrn.gpiv.model.Recurso;
import com.unrn.gpiv.service.EmpresaService;
import com.unrn.gpiv.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@PageTitle("Detalle de Empresa | SGPIV")
@Route(value = "admin/empresa-detalle", layout = MainLayout.class)
public class EmpresaDetalleView extends VerticalLayout implements HasUrlParameter<Long> {

	private final EmpresaService empresaService;

	// Componentes de Cabecera
	private H2 nombreElement = new H2();
	private Span rubroElement = new Span();
	private Paragraph descElement = new Paragraph();

	// Grids de Datos Reales
	private Grid<Lote> gridLotes = new Grid<>(Lote.class, false);
	private Grid<Recurso> gridHerramientas = new Grid<>(Recurso.class, false);
	private VerticalLayout timelineAvances = new VerticalLayout();

	public EmpresaDetalleView(EmpresaService empresaService) {
		this.empresaService = empresaService;

		setPadding(true);
		setSpacing(true);
		getStyle().set("background-color", "#f5f7fa");

		Button btnVolver = new Button("Volver al listado", VaadinIcon.ARROW_LEFT.create());
		btnVolver.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(InformesEmpresasView.class)));
		add(btnVolver);

		// --- 1. CABECERA ---
		HorizontalLayout header = new HorizontalLayout();
		header.setWidthFull();
		header.setPadding(true);
		header.getStyle().set("background-color", "white").set("border-radius", "15px");
		header.setAlignItems(Alignment.CENTER);

		Image logoEmpresa = new Image("https://via.placeholder.com/100", "Logo Empresa");
		logoEmpresa.setWidth("100px");
		logoEmpresa.setHeight("100px");
		logoEmpresa.getStyle().set("border-radius", "10px");

		VerticalLayout infoGral = new VerticalLayout();
		infoGral.setSpacing(false);
		infoGral.setPadding(false);

		nombreElement.addClassNames(LumoUtility.Margin.Vertical.NONE);
		rubroElement.getStyle().set("color", "#0063BE").set("font-weight", "bold");
		descElement.getStyle().set("color", "#666");

		infoGral.add(nombreElement, rubroElement, descElement);
		header.add(logoEmpresa, infoGral);

		// --- 2. CUERPO: LOTES (IZQ) Y HERRAMIENTAS (DER) ---
		HorizontalLayout cuerpo = new HorizontalLayout();
		cuerpo.setWidthFull();
		cuerpo.setSpacing(true);

		// Panel Izquierdo: Lotes Asignados
		VerticalLayout sectionLotes = new VerticalLayout();
		sectionLotes.setWidth("50%");
		sectionLotes.getStyle().set("background-color", "white").set("border-radius", "15px").set("padding", "1.5em");
		H3 tLotes = new H3("Lotes Asignados");
		configurarTablaLotes();
		sectionLotes.add(tLotes, gridLotes);

		// Panel Derecho: Inventario Integral (Aportado + Prestado)
		VerticalLayout sectionRecursos = new VerticalLayout();
		sectionRecursos.setWidth("50%");
		sectionRecursos.getStyle().set("background-color", "white").set("border-radius", "15px").set("padding", "1.5em");
		H3 tRecursos = new H3("Inventario de Herramientas y Recursos");
		configurarTablaHerramientas();
		sectionRecursos.add(tRecursos, gridHerramientas);

		cuerpo.add(sectionLotes, sectionRecursos);

		// --- 3. SECCIÓN DE HISTORIAL DE AVANCES ---
		VerticalLayout sectionAvances = new VerticalLayout();
		sectionAvances.setWidthFull();
		sectionAvances.getStyle().set("background-color", "white").set("border-radius", "15px").set("padding", "1.5em");

		H3 tAvance = new H3("Historial de Avances de Proyecto");
		timelineAvances.setSpacing(true);
		timelineAvances.setPadding(false);
		sectionAvances.add(tAvance, timelineAvances);

		add(header, cuerpo, sectionAvances);
	}

	private void configurarTablaLotes() {
		gridLotes.setWidthFull();
		gridLotes.setAllRowsVisible(true);
		gridLotes.addColumn(Lote::getManzana).setHeader("Manzana");
		gridLotes.addColumn(Lote::getNroLote).setHeader("Nro. Lote");
		gridLotes.addColumn(Lote::getUbicacion).setHeader("Ubicación");
		gridLotes.addColumn(lote -> lote.getSuperficie() + " m²").setHeader("Superficie");
	}

	private void configurarTablaHerramientas() {
		gridHerramientas.setWidthFull();
		gridHerramientas.setAllRowsVisible(true);

		// Mapeo seguro navegando desde Recurso hacia el Item relacionado
		gridHerramientas.addColumn(recurso -> recurso.getItem() != null ? recurso.getItem().getNombre() : "Recurso sin nombre")
				.setHeader("Nombre Item").setSortable(true);

		gridHerramientas.addColumn(recurso -> recurso.getItem() != null ? recurso.getItem().getCategoria() : "General")
				.setHeader("Categoría");

		gridHerramientas.addColumn(Recurso::getNumeroSerie).setHeader("Nº Serie");

		// Columna para calcular dinámicamente si es capital propio o préstamo del Parque Industrial
		gridHerramientas.addColumn(recurso -> {
			if (recurso.getPropietarioEmpresa() != null) {
				return "Aportado (Capital Propio)";
			} else {
				return "Prestado por el Parque";
			}
		}).setHeader("Origen / Condición").setSortable(true);
	}

	@Override
	public void setParameter(BeforeEvent event, Long idEmpresa) {
		if (idEmpresa == null) {
			event.rerouteTo(InformesEmpresasView.class);
			return;
		}

		// Usamos el nuevo método del servicio que inicializa las colecciones @OneToMany
		Optional<Empresa> empresaOpt = empresaService.obtenerEmpresaCompletaPorId(idEmpresa);

		if (empresaOpt.isPresent()) {
			Empresa empresa = empresaOpt.get();

			// 1. Cargar datos de la Cabecera
			nombreElement.setText(empresa.getRazonSocial());
			descElement.setText("CUIT: " + empresa.getCuit() + " | Dirección Legal: " + empresa.getDireccion());

			if (empresa.getProyecto() != null) {
				rubroElement.setText("PROYECTO: " + empresa.getProyecto().getNombre() +
						" [" + empresa.getProyecto().getCategoria() + "]");
			} else {
				rubroElement.setText("Sin Proyecto Productivo Registrado");
			}

			// 2. Cargar tabla de Lotes
			gridLotes.setItems(empresa.getLotesAsignados());

			// 3. Combinar Listas de Herramientas (Aportadas + Prestadas)
			List<Recurso> inventarioTotal = new ArrayList<>();
			if (empresa.getHerramientasAportadas() != null) {
				inventarioTotal.addAll(empresa.getHerramientasAportadas());
			}
			if (empresa.getHerramientasPrestadas() != null) {
				inventarioTotal.addAll(empresa.getHerramientasPrestadas());
			}
			gridHerramientas.setItems(inventarioTotal);

			// 4. Cargar Historial de Informes de Avance de forma dinámica
			timelineAvances.removeAll();
			List<InformeAvance> informes = empresa.getInformesDeAvance();

			if (informes == null || informes.isEmpty()) {
				timelineAvances.add(new Paragraph("No se registran informes de avance cargados hasta la fecha."));
			} else {
				for (InformeAvance informe : informes) {
					timelineAvances.add(crearCardInformeAvance(informe));
				}
			}

		} else {
			Notification.show("La empresa seleccionada no existe.", 3000, Notification.Position.MIDDLE)
					.addThemeVariants(NotificationVariant.LUMO_ERROR);
			event.rerouteTo(InformesEmpresasView.class);
		}
	}

	private VerticalLayout crearCardInformeAvance(InformeAvance informe) {
		VerticalLayout card = new VerticalLayout();
		card.setSpacing(false);
		card.getStyle()
				.set("border-left", "4px solid " + getColorPorEstado(informe.getEstadoCumplimiento()))
				.set("background-color", "#f9f9f9")
				.set("padding", "15px")
				.set("margin-bottom", "10px")
				.set("border-radius", "0 10px 10px 0");

		// Fila superior: Fecha y Badge de Estado
		HorizontalLayout filaEncabezado = new HorizontalLayout();
		filaEncabezado.setWidthFull();
		filaEncabezado.setJustifyContentMode(JustifyContentMode.BETWEEN);

		Span txtFecha = new Span("Evaluado el: " +
				(informe.getFechaEvaluacion() != null ? informe.getFechaEvaluacion().toString() : "Pendiente"));
		txtFecha.getStyle().set("font-size", "0.85em").set("color", "#666");

		Span badgeEstado = new Span(informe.getEstadoCumplimiento().toString());
		badgeEstado.getStyle()
				.set("background-color", getColorPorEstado(informe.getEstadoCumplimiento()))
				.set("color", "white")
				.set("padding", "2px 8px")
				.set("border-radius", "12px")
				.set("font-size", "0.75em")
				.set("font-weight", "bold");

		filaEncabezado.add(txtFecha, badgeEstado);

		// Contenido
		H4 titulo = new H4(informe.getTitulo() != null ? informe.getTitulo() : "Informe sin título");
		Paragraph obs = new Paragraph(informe.getObservaciones());
		obs.getStyle().set("font-size", "0.9em").set("margin-top", "5px");

		// Botón de descarga de PDF (si existe)
		if (informe.getArchivoPdf() != null) {
			Button btnDescargar = new Button("Descargar PDF: " + informe.getNombreArchivoPdf(), VaadinIcon.DOWNLOAD.create());
			btnDescargar.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);

			// Aquí podrías usar un StreamResource para la descarga
			btnDescargar.addClickListener(e -> {
				Notification.show("Iniciando descarga del informe...");
				// Lógica de descarga aquí
			});
			card.add(filaEncabezado, titulo, obs, btnDescargar);
		} else {
			card.add(filaEncabezado, titulo, obs);
		}

		return card;
	}

	private String getColorPorEstado(com.unrn.gpiv.common.EstadoCumplimiento estado) {
		if (estado == null) return "#999";
		switch (estado) {
			case TOTAL: return "#28a745";    // Verde
			case PARCIAL: return "#ffc107";  // Amarillo/Naranja
			case INCUMPLIDO: return "#dc3545"; // Rojo
			case NULO: return "#6c757d";     // Gris
			default: return "#0063BE";
		}
	}
}