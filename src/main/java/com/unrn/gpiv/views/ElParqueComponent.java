package com.unrn.gpiv.views;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class ElParqueComponent extends VerticalLayout {

    public ElParqueComponent() {
        // Altura indefinida y ancho total para evitar bloqueos del motor de scroll de Vaadin
        setWidthFull();
        setPadding(false);
        setSpacing(false);
        getStyle().set("font-family", "'Montserrat', sans-serif");

        // ==========================================
        // 🏭 Bloque 1: Presentación Institucional Real Viedma
        // ==========================================
        VerticalLayout presentacion = new VerticalLayout();
        presentacion.setWidthFull();
        presentacion.getStyle()
                .set("background-color", "#F8FAFC")
                .set("padding", "50px 60px");

        H2 subTitulo = new H2("El Parque Industrial, Productivo y Logístico de Viedma");
        subTitulo.getStyle().set("color", "#0063BE").set("font-weight", "700").set("margin", "0 0 15px 0");

        Paragraph p1 = new Paragraph("Somos un Parque Industrial, Productivo y Logístico ubicado en la ciudad de Viedma, capital de la provincia de Río Negro. Nuestro predio se encuentra estratégicamente localizado sobre la Ruta Provincial N° 1 (antigua Ruta 300, camino al balneario El Cóndor), a unos 3 km del centro de la ciudad y con proximidad a las vías del Ferrocarril General Roca. Contamos con una superficie original que actualmente se encuentra en pleno proceso de expansión, habiéndose gestionado la expropiación de 25 hectáreas adicionales para duplicar su capacidad y albergar a nuevas empresas.");
        p1.getStyle().set("color", "#2D3748").set("font-size", "1.1em").set("line-height", "1.6").set("max-width", "1100px");

        Paragraph p2 = new Paragraph("Nuestro objetivo es diversificar la matriz económica de la capital rionegrina, sumándole a su tradicional perfil administrativo un fuerte motor de producción, manufactura y servicios. El parque es desarrollado y administrado por el ENREPAVI (Ente de Reconversión del Parque Industrial de Viedma), un organismo mixto cuyo directorio está integrado por representantes del sector público (el Gobierno de la Provincia de Río Negro y la Municipalidad de Viedma) y el sector privado (a través de la Cámara de Comercio de Viedma).");
        p2.getStyle().set("color", "#4A5568").set("font-size", "1.1em").set("line-height", "1.6").set("max-width", "1100px");

        presentacion.add(subTitulo, p1, p2);

        // ==========================================
        // 🎯 Bloque 2: Objetivos Estratégicos (Siempre Abierto y Plano)
        // ==========================================
        VerticalLayout seccionesAbiertas = new VerticalLayout();
        seccionesAbiertas.setWidthFull();
        seccionesAbiertas.getStyle().set("padding", "40px 60px").set("gap", "35px");

        // Panel del Objetivo General
        VerticalLayout panelGeneral = new VerticalLayout();
        panelGeneral.setWidthFull();
        panelGeneral.setPadding(true);
        panelGeneral.getStyle()
                .set("border-left", "5px solid #0063BE")
                .set("background-color", "#F8FAFC")
                .set("border-radius", "0 8px 8px 0");

        H3 titleGeneral = new H3("🎯 OBJETIVO GENERAL");
        titleGeneral.getStyle().set("color", "#1A202C").set("margin", "0 0 10px 0").set("font-weight", "700");

        Paragraph descGeneral = new Paragraph("Impulsar el desarrollo económico, productivo y logístico de la región, promoviening la radicación de empresas y consolidando a la capital rionegrina como un polo industrial altamente competitivo.");
        descGeneral.getStyle().set("color", "#4A5568").set("font-size", "1.05em").set("margin", "0");

        panelGeneral.add(titleGeneral, descGeneral);

        // Panel de los Objetivos Específicos
        VerticalLayout panelEspecificos = new VerticalLayout();
        panelEspecificos.setWidthFull();
        panelEspecificos.setPadding(true);
        panelEspecificos.getStyle()
                .set("border-left", "5px solid #009A3B")
                .set("background-color", "#FAFAFA")
                .set("border-radius", "0 8px 8px 0");

        H3 titleEspecificos = new H3("📋 OBJETIVOS ESPECÍFICOS");
        titleEspecificos.getStyle().set("color", "#1A202C").set("margin", "0 0 15px 0").set("font-weight", "700");
        panelEspecificos.add(titleEspecificos);

        String[] objetivos = {
                "• Atraer inversiones estratégicas de alcance nacional y regional, priorizando sectores clave como la agroindustria, la manufactura, las empresas de servicios, aserraderos y la construcción.",
                "• Diversificar la matriz económica local, potenciando el desarrollo del sector privado para la generación de empleo genuino, calificado y de calidad.",
                "• Potenciar el perfil logístico regional, proyectando al predio como un nodo estratégico de transferencia y distribución de carga a gran escala, aprovechando la conectividad vial y el desarrollo de infraestructura ferroviaria.",
                "• Fortalecer las cadenas de valor, incentivando el procesamiento local y la incorporación de valor agregado a las materias primas e insumos provenientes de las unidades productivas de la región.",
                "• Impulsar el desarrollo de pymes y nuevos emprendimientos, promoviendo entornos propicios y herramientas que faciliten la escalabilidad de los proyectos locales.",
                "• Fomentar la sinergia institucional y tecnológica, articulando activamente con el sector público, cámaras empresariales y el sistema educativo y científico (UNRN, UNCo, INTI, IDEVI) para acompañar la modernización y la innovación productiva.",
                "• Brindar herramientas de competitividad, ofreciendo un marco robusto de incentivos, servicios esenciales y exenciones impositivas provinciales que estimulen la inversión a largo plazo.",
                "• Captar oportunidades de escala energética, adecuando la oferta de infraestructura y servicios industriales para abastecer las demandas derivadas de los nuevos desarrollos energéticos y proyectos estratégicos de la provincia."
        };

        for (String obj : objetivos) {
            Span line = new Span(obj);
            line.getStyle()
                    .set("color", "#4A5568")
                    .set("margin-bottom", "10px")
                    .set("font-weight", "500")
                    .set("font-size", "1.05em")
                    .set("line-height", "1.5");
            panelEspecificos.add(line);
        }

        seccionesAbiertas.add(panelGeneral, panelEspecificos);

        // ==========================================
        // 👥 Bloque 3: Autoridades y Estructura Organizativa Diaria
        // ==========================================
        VerticalLayout autoridadesSection = new VerticalLayout();
        autoridadesSection.setWidthFull();
        autoridadesSection.getStyle().set("padding", "20px 60px 60px 60px").set("gap", "30px");

        H3 titleAutoridades = new H3("Estructura de Autoridades y Gestión del ENREPAVI");
        titleAutoridades.getStyle().set("color", "#1A202C").set("font-weight", "700").set("margin", "0");

        // Fila 1: Cargos Ejecutivos Directos (Cards Corporativas)
        HorizontalLayout gridEjecutivos = new HorizontalLayout();
        gridEjecutivos.setWidthFull();
        gridEjecutivos.getStyle().set("gap", "25px");

        // Card Presidente
        VerticalLayout cardPresi = new VerticalLayout();
        cardPresi.setWidth("380px"); cardPresi.setPadding(true); cardPresi.setSpacing(false);
        cardPresi.getStyle().set("border", "1px solid #E2E8F0").set("border-radius", "8px").set("background-color", "#F8FAFC");
        Span cargoP = new Span("PRESIDENTE DEL DIRECTORIO");
        cargoP.getStyle().set("color", "#0063BE").set("font-size", "0.85em").set("font-weight", "700").set("letter-spacing", "1px");
        Span nombreP = new Span("Marcelo Ruiz");
        nombreP.getStyle().set("color", "#1A202C").set("font-size", "1.2em").set("font-weight", "700").set("margin", "4px 0");
        Span orgP = new Span("Representante electo del Sector Privado");
        orgP.getStyle().set("color", "#718096").set("font-size", "0.95em").set("font-weight", "500");
        cardPresi.add(cargoP, nombreP, orgP);

        // Card Gerente
        VerticalLayout cardGerente = new VerticalLayout();
        cardGerente.setWidth("380px"); cardGerente.setPadding(true); cardGerente.setSpacing(false);
        cardGerente.getStyle().set("border", "1px solid #E2E8F0").set("border-radius", "8px").set("background-color", "#F8FAFC");
        Span cargoG = new Span("GERENTE / ADMINISTRADOR");
        cargoG.getStyle().set("color", "#0063BE").set("font-size", "0.85em").set("font-weight", "700").set("letter-spacing", "1px");
        Span nombreG = new Span("Martín Lemos");
        nombreG.getStyle().set("color", "#1A202C").set("font-size", "1.2em").set("font-weight", "700").set("margin", "4px 0");
        Span orgG = new Span("Funcionario Público Provincial / Exdirector Cámara de Comercio");
        orgG.getStyle().set("color", "#718096").set("font-size", "0.95em").set("font-weight", "500");
        cardGerente.add(cargoG, nombreG, orgG);

        gridEjecutivos.add(cardPresi, cardGerente);

        // Fila 2: Conformación del Directorio y Comisiones Internas (Estructuras de texto plano ordenado)
        VerticalLayout bloquesExplicativos = new VerticalLayout();
        bloquesExplicativos.setWidthFull();
        bloquesExplicativos.setPadding(false);
        bloquesExplicativos.getStyle().set("gap", "25px");

        // Bloque Directorio Mixto
        VerticalLayout bloqueDirectorio = new VerticalLayout();
        bloqueDirectorio.setPadding(true);
        bloqueDirectorio.getStyle().set("background-color", "#FAFAFA").set("border-radius", "8px").set("border", "1px solid #E2E8F0");

        Span tDir = new Span("🏢 CONFORMACIÓN DEL DIRECTORIO MIXTO (8 Miembros Ad-Honorem)");
        tDir.getStyle().set("font-weight", "700").set("color", "#1A202C").set("font-size", "1.1em").set("margin-bottom", "10px");

        Paragraph descDir = new Paragraph("El Directorio dicta las pautas operacionales generales del predio y se reúne de manera asamblearia cada tres meses. Está compuesto de forma simétrica por:");
        descDir.getStyle().set("color", "#4A5568").set("margin-bottom", "8px");

        Span dPublicos = new Span("• 4 Representantes del Sector Público: Un miembro del Ejecutivo Provincial, uno del Legislativo Provincial, uno del Ejecutivo Municipal y uno del Legislativo Municipal.");
        dPublicos.getStyle().set("color", "#4A5568").set("padding-left", "15px").set("margin-bottom", "4px").set("font-weight", "500");

        Span dPrivados = new Span("• 4 Representantes del Sector Privado: Un miembro de la Cámara de Comercio de Viedma, uno por las grandes empresas industriales, uno por las medianas empresas y uno por las pequeñas unidades productivas radicadas.");
        dPrivados.getStyle().set("color", "#4A5568").set("padding-left", "15px").set("font-weight", "500");

        bloqueDirectorio.add(tDir, descDir, dPublicos, dPrivados);

        // Bloque Comité Ejecutivo y Equipo Diario
        HorizontalLayout filaComisiones = new HorizontalLayout();
        filaComisiones.setWidthFull();
        filaComisiones.getStyle().set("gap", "25px");

        // Comité Ejecutivo Reducido
        VerticalLayout colComite = new VerticalLayout();
        colComite.setPadding(true);
        colComite.getStyle().set("background-color", "#FAFAFA").set("border-radius", "8px").set("border", "1px solid #E2E8F0");
        Span tComite = new Span("⚡ COMITÉ EJECUTIVO REDUCIDO");
        tComite.getStyle().set("font-weight", "700").set("color", "#1A202C").set("margin-bottom", "8px");
        Paragraph descComite = new Paragraph("Comisión ágil y compacta integrada por el Presidente, el Vicepresidente y un Vocal del directorio. Es la encargada de resolver las contingencias de gestión y firmar resoluciones de urgencia entre los cierres de las asambleas trimestrales.");
        descComite.getStyle().set("color", "#4A5568").set("font-size", "0.95em").set("line-height", "1.5");
        colComite.add(tComite, descComite);

        // Equipo de Gestión Operativa Diaria
        VerticalLayout colEquipo = new VerticalLayout();
        colEquipo.setPadding(true);
        colEquipo.getStyle().set("background-color", "#FAFAFA").set("border-radius", "8px").set("border", "1px solid #E2E8F0");
        Span tEquipo = new Span("👥 EQUIPO DE GESTIÓN OPERATIVA DIARIA");
        tEquipo.getStyle().set("font-weight", "700").set("color", "#1A202C").set("margin-bottom", "8px");
        Paragraph descEquipo = new Paragraph("Estructura operativa de planta fija compuesta por 4 empleados públicos: el Gerente General a cargo de la firma administrativa, un Técnico Civil dedicado a evaluar la viabilidad preliminar estructural de los proyectos civiles presentados, y dos Administrativos permanentes.");
        descEquipo.getStyle().set("color", "#4A5568").set("font-size", "0.95em").set("line-height", "1.5");
        colEquipo.add(tEquipo, descEquipo);

        filaComisiones.add(colComite, colEquipo);
        bloquesExplicativos.add(bloqueDirectorio, filaComisiones);

        autoridadesSection.add(titleAutoridades, gridEjecutivos, bloquesExplicativos);

        // Ensamblado plano de capas dinámicas sobre el layout raíz
        add(presentacion, seccionesAbiertas, autoridadesSection);
    }
}