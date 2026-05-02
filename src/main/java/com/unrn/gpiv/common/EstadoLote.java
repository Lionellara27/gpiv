package com.unrn.gpiv.common;

public enum EstadoLote {
    LIBRE,      // Disponible para adjudicar
    RESERVADO,  // Con solicitud en trámite
    OCUPADO,    // Con empresa trabajando
    OCIOSO      // Ocupado pero sin actividad productiva (alerta para desadjudicar)
}