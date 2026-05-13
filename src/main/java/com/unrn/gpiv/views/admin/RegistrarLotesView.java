package com.unrn.gpiv.views.admin;

import com.unrn.gpiv.model.Lote;
import com.unrn.gpiv.common.EstadoLote;
import com.unrn.gpiv.service.LoteService; // Tu servicio de Spring
import com.unrn.gpiv.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Registrar Lote | SGPIV")
@Route(value = "admin/lote/registrar-lote", layout = MainLayout.class) 
public class RegistrarLotesView extends VerticalLayout {

	private final LoteService loteService;
	private BeanValidationBinder<Lote> binder = new BeanValidationBinder<>(Lote.class);

	private TextField manzana = new TextField("Manzana");
	private TextField nroLote = new TextField("Número de Lote");
	private NumberField superficie = new NumberField("Superficie (m²)");
	//private ComboBox<EstadoLote> estado = new ComboBox<>("Estado Inicial", EstadoLote.values());

	public RegistrarLotesView(LoteService loteService) {
		this.loteService = loteService;

		add(new H2("Registrar Nuevo Lote"));

		FormLayout form = new FormLayout(manzana, nroLote, superficie/*, estado*/);
		
		// Esto conecta los campos automáticamente por nombre
		binder.bindInstanceFields(this);
		
		//estado.setValue(EstadoLote.LIBRE);

		// CAMBIO AQUÍ: Llamamos al método guardarNuevoLote()
		Button guardar = new Button("Registrar Lote", e -> guardarNuevoLote());
		guardar.addThemeNames("primary");

		// Opcional: Botón para volver sin guardar
		Button cancelar = new Button("Cancelar", e -> getUI().ifPresent(ui -> ui.navigate(AdminLotesView.class)));

		add(form, new HorizontalLayout(guardar, cancelar));
	}

	private void guardarNuevoLote() {
		Lote nuevoLote = new Lote(); //lo crea vacio y en ningun lado le carga los datos de los campos
        //Clarooo

	//	nuevoLote.setEstado(estado.getValue());
		nuevoLote.setManzana(manzana.getValue().trim());
		nuevoLote.setNroLote(nroLote.getValue().trim());
		nuevoLote.setSuperficie(superficie.getValue());
		//con esto sigue sin andar
		// aun dice que el estado esta nulo

		System.out.print("entre a guardar lote");
		// Valida y pasa los datos de la vista al objeto
		if (binder.writeBeanIfValid(nuevoLote)) {
			System.out.print("entre al if");
			
			loteService.guardar(nuevoLote); 
			Notification.show("Lote registrado con éxito");
			
			// Navega de vuelta a la lista principal
			getUI().ifPresent(ui -> ui.navigate(AdminLotesView.class)); 
		} else {
					System.out.print("entre al else");
			Notification.show("Por favor, revise los errores en el formulario", 
				3000, Notification.Position.MIDDLE);
		}
	}
}