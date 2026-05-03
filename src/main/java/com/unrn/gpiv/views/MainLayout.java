package com.unrn.gpiv.views;


import com.unrn.gpiv.views.admin.AdminLotesView;
import com.unrn.gpiv.views.empresa.MisProyectosView;
import com.unrn.gpiv.views.evaluadores.EvaluarSolicitudesView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class MainLayout extends AppLayout {

    public MainLayout() {
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        H1 logo = new H1("SGPIV - Viedma");
        logo.addClassNames(
                LumoUtility.FontSize.LARGE,
                LumoUtility.Margin.MEDIUM,
                LumoUtility.TextColor.PRIMARY
        );

        // El Toggle es el botoncito de "hamburguesa" para abrir/cerrar el menú
        DrawerToggle toggle = new DrawerToggle();

        var header = new HorizontalLayout(toggle, logo);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidthFull();
        header.addClassNames(LumoUtility.Padding.Vertical.NONE, LumoUtility.Padding.Horizontal.MEDIUM);

        addToNavbar(header);
    }

    private void createDrawer() {
        // Aquí armamos el menú lateral (Drawer)
        VerticalLayout menu = new VerticalLayout();

        // --- SECCIÓN ADMINISTRADOR (HU 3, 5, 13) ---
        Span adminSection = new Span("ADMINISTRACIÓN");
        adminSection.addClassNames(
                LumoUtility.FontSize.SMALL,
                LumoUtility.TextColor.SECONDARY,
                LumoUtility.Margin.Top.MEDIUM
        );

        RouterLink adminLotes = new RouterLink();
        adminLotes.setRoute(AdminLotesView.class);
        adminLotes.add(VaadinIcon.MAP_MARKER.create()); // Agregamos el ícono
        adminLotes.add(new Span(" Gestión de Lotes")); // Agregamos el texto con un espacio inicial
        adminLotes.addClassNames(LumoUtility.Display.FLEX, LumoUtility.AlignItems.CENTER, LumoUtility.Gap.SMALL);

        // --- SECCIÓN EMPRESAS (HU 4, 9) ---
        Span empresaSection = new Span("MI EMPRESA");
        empresaSection.addClassNames(
                LumoUtility.FontSize.SMALL,
                LumoUtility.TextColor.SECONDARY,
                LumoUtility.Margin.Top.MEDIUM
        );

        RouterLink misProyectos = new RouterLink();
        misProyectos.setRoute(MisProyectosView.class);
        misProyectos.add(VaadinIcon.FACTORY.create());
        misProyectos.add(new Span(" Mis Proyectos"));
        misProyectos.addClassNames(LumoUtility.Display.FLEX, LumoUtility.AlignItems.CENTER, LumoUtility.Gap.SMALL);

        // --- SECCIÓN EVALUADORES (HU 1) ---
        Span evalSection = new Span("EVALUACIÓN");
        evalSection.addClassNames(
                LumoUtility.FontSize.SMALL,
                LumoUtility.TextColor.SECONDARY,
                LumoUtility.Margin.Top.MEDIUM
        );

        RouterLink evaluar = new RouterLink();
        evaluar.setRoute(EvaluarSolicitudesView.class);
        evaluar.add(VaadinIcon.CHECK_SQUARE_O.create());
        evaluar.add(new Span(" Evaluar Solicitudes"));
        evaluar.addClassNames(LumoUtility.Display.FLEX, LumoUtility.AlignItems.CENTER, LumoUtility.Gap.SMALL);

        // Agregamos todo al layout del menú
        menu.add(adminSection, adminLotes, empresaSection, misProyectos, evalSection, evaluar);

        // Finalmente lo mandamos al Drawer del AppLayout
        addToDrawer(menu);
    }
}