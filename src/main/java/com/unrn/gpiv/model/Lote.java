package com.unrn.gpiv.model;

import java.util.ArrayList;
import java.util.List;

public class Lote {
    private static final boolean DISPONIBLE = true;
    private static final boolean OCUPADO = false;

    private int id;
    private String numeroDeLote;
    private double metrosCuadrados;
    private List<Servicio> servicios;
    private boolean estadoActual;

    public Lote(String numeroDeLote, double metrosCuadrados) {
        this.numeroDeLote = numeroDeLote;
        this.metrosCuadrados = metrosCuadrados;
        this.servicios = new ArrayList<>();
        this.estadoActual = DISPONIBLE;
    }

    public void agregarServicio(Servicio servicio){
        servicios.add(servicio);
    }

    private boolean validarNumeroDeLoteNoExistente(){
        return true;
    }

    private boolean validarMetrosCuadrados(){
        return metrosCuadrados > 0;
    }
    public boolean estado(){
        return estadoActual;
    }

    private void cambiarEstadoADisponible(){
        this.estadoActual = DISPONIBLE;
    }
    private void cambiarEstadoAOcupado(){
        this.estadoActual = OCUPADO;
    }
    public String numeroDeLote(){
        return numeroDeLote;
    }
    public int id(){
        return id;
    }

}


