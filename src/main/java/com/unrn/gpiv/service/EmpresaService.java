package com.unrn.gpiv.service;

import com.unrn.gpiv.common.EstadoSolicitud;
import com.unrn.gpiv.messaging.service.EmailService;
import com.unrn.gpiv.model.Empresa;
import com.unrn.gpiv.model.ProyectoProductivo;
import com.unrn.gpiv.model.RepresentanteEmpresa;
import com.unrn.gpiv.model.SolicitudRadicacion;
import com.unrn.gpiv.repository.EmpresaRepository;
import com.unrn.gpiv.repository.RepresentanteEmpresaRepository;
import com.unrn.gpiv.repository.SolicitudRadicacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmpresaService {

    @Autowired
    private EmpresaRepository empresaRepository;
    @Autowired
    private SolicitudRadicacionRepository solicitudRepository;
    @Autowired
    RepresentanteEmpresaRepository representanteRepository;

    @Transactional
    public RepresentanteEmpresa registrarRepresentante(RepresentanteEmpresa rep) {
        //falta encriptar la contraseña antes de guardar
        // rep.setPassword(passwordEncoder.encode(rep.getPassword()));
        return representanteRepository.save(rep);
    }

    //El representante envia proyecto
    @Transactional
    public void recibirSolicitud(ProyectoProductivo proyecto, RepresentanteEmpresa rep, String razonSocial) {
        SolicitudRadicacion solicitud = new SolicitudRadicacion();
        solicitud.setProyecto(proyecto);
        solicitud.setRepresentante(rep);
        solicitud.setRazonSocialPretendida(razonSocial);

        solicitudRepository.save(solicitud);
    }

    //El Admin aprueba y se crea la empresa
    @Transactional
    public Empresa aprobarRadicacion(Long solicitudId) {
        SolicitudRadicacion solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        Empresa nuevaEmpresa = new Empresa();
        nuevaEmpresa.setRazonSocial(solicitud.getRazonSocialPretendida());
        nuevaEmpresa.setRepresentante(solicitud.getRepresentante());
        nuevaEmpresa.setProyecto(solicitud.getProyecto());
        nuevaEmpresa.setTitulada(false); //falta la escritura

        solicitud.setEstado(EstadoSolicitud.APROBADA);

        return empresaRepository.save(nuevaEmpresa);
    }
}