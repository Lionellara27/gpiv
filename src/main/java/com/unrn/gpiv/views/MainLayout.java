package com.unrn.gpiv.views;

import com.unrn.gpiv.common.Rol;
import com.unrn.gpiv.common.EstadoSolicitud;
import com.unrn.gpiv.common.EstadoEmpresa;
import com.unrn.gpiv.model.SolicitudRadicacion;
import com.unrn.gpiv.model.Usuario;
import com.unrn.gpiv.model.RepresentanteEmpresa;
import com.unrn.gpiv.service.EmpresaService;
import com.unrn.gpiv.views.admin.*;
import com.unrn.gpiv.views.empresa.*;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.beans.factory.annotation.Autowired;

public class MainLayout extends AppLayout {

    private final EmpresaService empresaService;

    @Autowired
    public MainLayout(EmpresaService empresaService) {
        this.empresaService = empresaService;
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        H1 logo = new H1("SGPIV - Viedma");
        logo.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.MEDIUM, LumoUtility.TextColor.PRIMARY);

        DrawerToggle toggle = new DrawerToggle();

        Button btnLogout = new Button("Salir", VaadinIcon.SIGN_OUT.create(), e -> {
            VaadinSession.getCurrent().setAttribute("usuarioLogueado", null);
            VaadinSession.getCurrent().close();
            VaadinSession.getCurrent().getSession().invalidate();
            UI.getCurrent().getPage().setLocation("login");
        });

        btnLogout.addClassNames(LumoUtility.Margin.Left.AUTO, LumoUtility.Margin.Right.MEDIUM);

        var header = new HorizontalLayout(toggle, logo, btnLogout);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidthFull();
        header.addClassNames(LumoUtility.Padding.Vertical.NONE, LumoUtility.Padding.Horizontal.MEDIUM);

        addToNavbar(header);
    }

    private void createDrawer() {
        VerticalLayout menu = new VerticalLayout();

        Usuario usuario = (Usuario) VaadinSession.getCurrent().getAttribute("usuarioLogueado");

        if (usuario == null) return;

        // =====================================================================
        // 🛡️ SECCIÓN 1: ROL ADMINISTRADOR (ENREPAVI)
        // =====================================================================
        if (usuario.getRol() == Rol.ADMIN) {
            Span adminSection = new Span("ADMINISTRACIÓN");
            adminSection.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY, LumoUtility.Margin.Top.MEDIUM);

            menu.add(adminSection);
            menu.add(crearLink(AdminDashboardView.class, VaadinIcon.CHART_LINE, " Dashboard"));
            menu.add(crearLink(AdminLotesView.class, VaadinIcon.MAP_MARKER, " Gestión de Lotes"));
            menu.add(crearLink(InformesEmpresasView.class, VaadinIcon.CHART_3D, " Informe de Empresas"));
            menu.add(crearLink(EvaluarSolicitudesView.class, VaadinIcon.CHECK_SQUARE_O, " Evaluar Solicitudes"));
            menu.add(crearLink(InventarioView.class, VaadinIcon.TOOLS, " Gestión de Inventario"));
        }

        // =====================================================================
        // 🏢 SECCIÓN 2: ROL EMPRESA (MENÚ DINÁMICO REACTIVO)
        // =====================================================================
        if (usuario.getRol() == Rol.EMPRESA && usuario instanceof RepresentanteEmpresa logueado) {

            Span empresaSection = new Span("ADMINISTRACIÓN\n");
            empresaSection.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY, LumoUtility.Margin.Top.MEDIUM);
            menu.add(empresaSection);

            menu.add(crearLink(MiProyectoView.class, VaadinIcon.FACTORY, " Mi Proyecto"));

            SolicitudRadicacion solicitud = empresaService.obtenerUltimaSolicitud(logueado);

            if (solicitud != null) {
                if (solicitud.getEstado() == EstadoSolicitud.APROBADA) {
                    menu.add(crearLink(MiEmpresaView.class, VaadinIcon.BUILDING, " Mi Empresa"));
                }

    // >>>>>>>>>>> cambie la condicion "logueado.getEmpresa().getLoteAsignado() != null" por que la empresa ya no tiene un solo lote asignado, esta puede tener varios lotes
                if (logueado.getEmpresa() != null && !logueado.getEmpresa().getLotesAsignados().isEmpty()) {

                    Span operacionesSection = new Span("OPERACIONES INDUSTRIALES");
                    operacionesSection.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY, LumoUtility.Margin.Top.MEDIUM);
                    menu.add(operacionesSection);

                    if (logueado.getEmpresa() != null && !logueado.getEmpresa().getLotesAsignados().isEmpty()) {

                        // Le cambiamos el nombre a la variable para que no tire error
                        Span tituloAvance = new Span("OPERACIONES INDUSTRIALES");
                        tituloAvance.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY, LumoUtility.Margin.Top.MEDIUM);
                        menu.add(tituloAvance);

                        // Nuestro botón unificado que lleva a las 3 pestañas
                        menu.add(crearLink(AvancesObraView.class, VaadinIcon.CHART_TIMELINE, " Mi Avance"));
                    }

                    /*
                    menu.add(crearLink(AvancesObraView.class, VaadinIcon.CLIPBOARD_CHECK, " Avances de Obra"));
                    menu.add(crearLink(ControlPersonalView.class, VaadinIcon.USERS, " Control de Personal"));
                    menu.add(crearLink(FlotaVehiculosView.class, VaadinIcon.TRUCK, " Flota de Vehículos"));
                    menu.add(crearLink(MedicionConsumosView.class, VaadinIcon.DASHBOARD, " Medición de Consumos"));
*/
                }
            }
        }

        // 🟢 ARREGLADO: El menú se agrega al contenedor lateral y cerramos el método limpiamente
        addToDrawer(menu);
    }

    // 🟢 ARREGLADO: El método auxiliar ahora queda perfectamente aislado a nivel de clase
    private RouterLink crearLink(Class viewClass, VaadinIcon icon, String text) {
        RouterLink link = new RouterLink();
        link.setRoute(viewClass);
        link.add(icon.create(), new Span(text));
        link.addClassNames(LumoUtility.Display.FLEX, LumoUtility.AlignItems.CENTER, LumoUtility.Gap.SMALL);
        return link;
    }
}


/* esta esta FULLLLLLLL GAGA
package com.unrn.gpiv.views;



import com.unrn.gpiv.common.Rol;
import com.unrn.gpiv.model.Usuario;
import com.unrn.gpiv.views.admin.*;
import com.unrn.gpiv.views.empresa.MiProyectoView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class MainLayout extends AppLayout {

    public MainLayout() {
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        H1 logo = new H1("SGPIV - Viedma");
        logo.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.MEDIUM, LumoUtility.TextColor.PRIMARY);

        DrawerToggle toggle = new DrawerToggle();

        // Botón de salir (Opcional, pero muy útil - anda mal cambiar)
        /*
        Button btnLogout = new Button("Salir", VaadinIcon.SIGN_OUT.create(), e -> {
            VaadinSession.getCurrent().getSession().invalidate();
            getUI().ifPresent(ui -> ui.navigate("login"));
        });*/
/*
        Button btnLogout = new Button("Salir", VaadinIcon.SIGN_OUT.create(), e -> {
            // 1. Limpiamos nuestro atributo manual por las dudas
            VaadinSession.getCurrent().setAttribute("usuarioLogueado", null);

            // 2. Cerramos la sesión de Vaadin y la de HTTP
            VaadinSession.getCurrent().close();
            VaadinSession.getCurrent().getSession().invalidate();

            // 3. REDIRECCIÓN TOTAL (La clave)
            // En vez de navigate(), usamos setLocation.
            // Esto obliga al navegador a recargar la página desde cero.
            UI.getCurrent().getPage().setLocation("login");
        });

        btnLogout.addClassNames(LumoUtility.Margin.Left.AUTO, LumoUtility.Margin.Right.MEDIUM);

        var header = new HorizontalLayout(toggle, logo, btnLogout);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidthFull();
        header.addClassNames(LumoUtility.Padding.Vertical.NONE, LumoUtility.Padding.Horizontal.MEDIUM);

        addToNavbar(header);
    }

    private void createDrawer() {
        VerticalLayout menu = new VerticalLayout();

        // --- EL CAMBIO CLAVE: LEEMOS EL OBJETO USUARIO ---
        Usuario usuario = (Usuario) VaadinSession.getCurrent().getAttribute("usuarioLogueado");

        if (usuario == null) return; // Si no hay nadie, no mostramos nada

        // --- SI ES ADMIN ---
        if (usuario.getRol() == Rol.ADMIN) {
            Span adminSection = new Span("ADMINISTRACIÓN");
            adminSection.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY, LumoUtility.Margin.Top.MEDIUM);

            menu.add(adminSection);
            menu.add(crearLink(AdminDashboardView.class, VaadinIcon.CHART_LINE, " Dashboard"));
            menu.add(crearLink(AdminLotesView.class, VaadinIcon.MAP_MARKER, " Gestión de Lotes"));
            menu.add(crearLink(InformesEmpresasView.class, VaadinIcon.CHART_3D, " Informe de Empresas"));
            menu.add(crearLink(EvaluarSolicitudesView.class, VaadinIcon.CHECK_SQUARE_O, " Evaluar Solicitudes"));
            menu.add(crearLink(InventarioView.class, VaadinIcon.TOOLS, " Gestión de Inventario"));
        }

        // --- SI ES EMPRESA ---
        if (usuario.getRol() == Rol.EMPRESA) {
            Span empresaSection = new Span("MI EMPRESA");
            empresaSection.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY, LumoUtility.Margin.Top.MEDIUM);

            menu.add(empresaSection);
            menu.add(crearLink(MiProyectoView.class, VaadinIcon.FACTORY, " Mi Proyecto"));
        }

        addToDrawer(menu);
    }

    // Método auxiliar para no repetir tanto código de los links
    private RouterLink crearLink(Class viewClass, VaadinIcon icon, String text) {
        RouterLink link = new RouterLink();
        link.setRoute(viewClass);
        link.add(icon.create(), new Span(text));
        link.addClassNames(LumoUtility.Display.FLEX, LumoUtility.AlignItems.CENTER, LumoUtility.Gap.SMALL);
        return link;
    }
}

*/
/*package com.unrn.gpiv.views; anda pero esta gaga


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