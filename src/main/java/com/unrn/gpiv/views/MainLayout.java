package com.unrn.gpiv.views;

import com.unrn.gpiv.views.admin.AdminDashboardView;
import com.unrn.gpiv.views.admin.AdminLotesView;
import com.unrn.gpiv.views.admin.InformesEmpresasView;
import com.unrn.gpiv.views.admin.InventarioView;
import com.unrn.gpiv.views.empresa.MiProyectoView;
import com.unrn.gpiv.views.evaluadores.EvaluarSolicitudesView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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
        VerticalLayout menu = new VerticalLayout();

        // LEEMOS EL ROL DE LA SESIÓN
        String rol = (String) com.vaadin.flow.server.VaadinSession.getCurrent().getAttribute("rol");

        // --- SI ES ADMIN, MOSTRAMOS SU SECCIÓN ---
        if ("ADMIN".equals(rol)) {
            Span adminSection = new Span("ADMINISTRACIÓN");
            adminSection.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY, LumoUtility.Margin.Top.MEDIUM);

            RouterLink adminDashboard = new RouterLink();
            adminDashboard.setRoute(AdminDashboardView.class);
            adminDashboard.add(VaadinIcon.CHART_LINE.create(), new Span(" Dashboard"));
            adminDashboard.addClassNames(LumoUtility.Display.FLEX, LumoUtility.AlignItems.CENTER, LumoUtility.Gap.SMALL);

            RouterLink adminLotes = new RouterLink();
            adminLotes.setRoute(AdminLotesView.class);
            adminLotes.add(VaadinIcon.MAP_MARKER.create(), new Span(" Gestión de Lotes"));
            adminLotes.addClassNames(LumoUtility.Display.FLEX, LumoUtility.AlignItems.CENTER, LumoUtility.Gap.SMALL);

            RouterLink adminInformes = new RouterLink();
            adminInformes.setRoute(InformesEmpresasView.class);
            adminInformes.add(VaadinIcon.CHART_3D.create(), new Span(" Informe de Empresas"));
            adminInformes.addClassNames(LumoUtility.Display.FLEX, LumoUtility.AlignItems.CENTER, LumoUtility.Gap.SMALL);

            RouterLink evaluar = new RouterLink();
            evaluar.setRoute(EvaluarSolicitudesView.class);
            evaluar.add(VaadinIcon.CHECK_SQUARE_O.create(), new Span(" Evaluar Solicitudes"));
            evaluar.addClassNames(LumoUtility.Display.FLEX, LumoUtility.AlignItems.CENTER, LumoUtility.Gap.SMALL);

            RouterLink adminInventario = new RouterLink();
            adminInventario.setRoute(InventarioView.class);
            adminInventario.add(VaadinIcon.TOOLS.create(), new Span(" Gestión de Inventario"));
            adminInventario.addClassNames(LumoUtility.Display.FLEX, LumoUtility.AlignItems.CENTER, LumoUtility.Gap.SMALL);

            menu.add(adminSection, adminDashboard, adminLotes, adminInformes, evaluar, adminInventario);
        }

        // --- SI ES EMPRESA, MOSTRAMOS SU SECCIÓN ---
        if ("EMPRESA".equals(rol)) {
            Span empresaSection = new Span("MI EMPRESA");
            empresaSection.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY, LumoUtility.Margin.Top.MEDIUM);

            RouterLink miProyecto = new RouterLink();
            miProyecto.setRoute(MiProyectoView.class);
            miProyecto.add(VaadinIcon.FACTORY.create(), new Span(" Mi Proyecto"));
            miProyecto.addClassNames(LumoUtility.Display.FLEX, LumoUtility.AlignItems.CENTER, LumoUtility.Gap.SMALL);

            menu.add(empresaSection, miProyecto);
        }

        addToDrawer(menu);
    }
}


/*package com.unrn.gpiv.views;


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
        Span empresaSection = new Span("EMPRESAS");
        empresaSection.addClassNames(
                LumoUtility.FontSize.SMALL,
                LumoUtility.TextColor.SECONDARY,
                LumoUtility.Margin.Top.MEDIUM
        );

        RouterLink misProyectos = new RouterLink();
        misProyectos.setRoute(MisProyectosView.class);
        misProyectos.add(VaadinIcon.FACTORY.create());
        misProyectos.add(new Span(" Informes de empresa"));
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
}*/