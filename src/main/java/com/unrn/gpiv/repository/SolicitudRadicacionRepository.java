package com.unrn.gpiv.repository;

import com.unrn.gpiv.model.SolicitudRadicacion;
import com.unrn.gpiv.common.EstadoSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SolicitudRadicacionRepository extends JpaRepository<SolicitudRadicacion, Long> {

    //Para que el Admin vea solo lo que tiene que evaluar
    List<SolicitudRadicacion> findByEstado(EstadoSolicitud estado);

    //Para que un Representante vea sus propias solicitudes enviadas
    List<SolicitudRadicacion> findByRepresentanteId(Long representanteId);

    //Buscar solicitudes por el nombre de la empresa
    List<SolicitudRadicacion> findByRazonSocialPretendidaContainingIgnoreCase(String nombre);
}