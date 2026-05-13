package com.unrn.gpiv.service;

import com.unrn.gpiv.common.EstadoSolicitud;
import com.unrn.gpiv.messaging.service.EmailService;
import com.unrn.gpiv.model.*;
import com.unrn.gpiv.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmpresaService {

    @Autowired
    private EmpresaRepository empresaRepository;
    @Autowired
    private SolicitudRadicacionRepository solicitudRepository;
    @Autowired
    RepresentanteEmpresaRepository representanteRepository;
    @Autowired
    private ProyectoProductivoRepository proyectoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository; // <--- AGREGÁ ESTA LÍNEA

    /*
    @Transactional
    public RepresentanteEmpresa registrarRepresentante(RepresentanteEmpresa rep) {
        //falta encriptar la contraseña antes de guardar
        // rep.setPassword(passwordEncoder.encode(rep.getPassword()));
        return representanteRepository.save(rep);
    }*/
    // cositas NEWWWWWWWWW 12/05
    @Transactional
    public RepresentanteEmpresa registrarRepresentante(RepresentanteEmpresa rep) {
        // 1. CHEQUEO DE EMAIL (El que evita el bug de Mariano)
        // Usamos usuarioRepository porque el mail está en la clase madre 'Usuario'
        if (usuarioRepository.existsByEmail(rep.getEmail())) {
            throw new IllegalArgumentException("Este correo electrónico ya está registrado. Por favor, iniciá sesión.");
        }

        // 2. CHEQUEO DE USERNAME (Por si el username no fuera el mail)
        if (usuarioRepository.existsByUsername(rep.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario ya está en uso.");
        }

        // 3. CHEQUEOS DE IDENTIDAD (DNI y CUIT)
        if (representanteRepository.existsByDni(rep.getDni())) {
            throw new IllegalArgumentException("Ya existe un usuario registrado con este DNI.");
        }

        if (representanteRepository.existsByCuitPersonal(rep.getCuitPersonal())) {
            throw new IllegalArgumentException("Este CUIT ya está vinculado a otra cuenta.");
        }

        try {
            return representanteRepository.save(rep);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // La última red de seguridad por si falla lo anterior
            throw new IllegalArgumentException("Error de base de datos: Algunos de los datos ya existen.");
        }
    }
    /* nuevo pero 11/05
    @Transactional
    public RepresentanteEmpresa registrarRepresentante(RepresentanteEmpresa rep) {
        // 1. PREVENCIÓN: Evitás el choque antes de que ocurra.
        // Como el username es el email, este frena el duplicado de cuenta.
        if (representanteRepository.existsByUsername(rep.getUsername())) {
            throw new IllegalArgumentException("Ya existe una cuenta con este correo.");
        }

        // Aprovechamos que agregaste los otros métodos al Repository para dejarlo blindado:
        if (representanteRepository.existsByDni(rep.getDni())) {
            throw new IllegalArgumentException("Ya existe un usuario registrado con este DNI.");
        }

        if (representanteRepository.existsByCuitPersonal(rep.getCuitPersonal())) {
            throw new IllegalArgumentException("Este CUIT ya está vinculado a otra cuenta.");
        }

        try {
            return representanteRepository.save(rep);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // 2. DEFENSA: Por si falla la prevención (ej: dos registros al mismo milisegundo)
            throw new IllegalArgumentException("Error de duplicado: Los datos ya existen en el sistema.");
        }
    }
    */

    //El representante envia proyecto
/*@Transactional
    public void recibirSolicitud(ProyectoProductivo proyecto, RepresentanteEmpresa rep, String razonSocial) {
        SolicitudRadicacion solicitud = new SolicitudRadicacion();
        solicitud.setProyecto(proyecto);
        solicitud.setRepresentante(rep);
        solicitud.setRazonSocialPretendida(razonSocial);

        solicitudRepository.save(solicitud);
    }*/
    // El representante envia proyecto
    @Transactional
    public void recibirSolicitud(ProyectoProductivo proyecto, RepresentanteEmpresa rep, String razonSocial) {

        // 1. REGLA DE NEGOCIO: Validamos que haya mandado el PDF
        if (proyecto.getPdfProyecto() == null || proyecto.getPdfProyecto().length == 0) {
            throw new IllegalArgumentException("El archivo PDF del proyecto es obligatorio.");
        }

        // 2. Guardamos el proyecto PRIMERO para que tenga un ID en la BD
        ProyectoProductivo proyectoGuardado = proyectoRepository.save(proyecto);

        // 3. Creamos y guardamos la Solicitud
        SolicitudRadicacion solicitud = new SolicitudRadicacion();
        solicitud.setProyecto(proyectoGuardado); // Usamos el proyecto guardado
        solicitud.setRepresentante(rep);
        solicitud.setRazonSocialPretendida(razonSocial);
        solicitud.setEstado(EstadoSolicitud.PENDIENTE); // Siempre es bueno inicializar el estado

        solicitudRepository.save(solicitud);
    }

    //El Admin aprueba y se crea la empresa
    /*@Transactional
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
    }*/
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


    // --- NUEVO MÉTODO DE AUTENTICACIÓN GENERAL ---
    public Usuario loginGeneral(String username, String password) {
        return usuarioRepository.findByUsername(username)
                .filter(u -> u.getPassword().equals(password))
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));
    }

    // --- NUEVO: Para que Mariano pueda ver su proyecto al loguearse ---
    @Transactional(readOnly = true)
    public Empresa obtenerEmpresaPorRepresentante(RepresentanteEmpresa rep) {
        return empresaRepository.findByRepresentante(rep).orElse(null);
    }

    // En EmpresaService.java
    public RepresentanteEmpresa login(String email, String password) {
        // Buscamos por email (o username, según como lo guardes)
        return representanteRepository.findByEmail(email)
                .filter(u -> u.getPassword().equals(password)) // Comparación simple
                .orElseThrow(() -> new RuntimeException("Usuario o password inválidos"));
    }

    // --- MÉTODO CLAVE PARA EL BUG GRANDE ---
    @Transactional(readOnly = true)
    public SolicitudRadicacion obtenerUltimaSolicitud(RepresentanteEmpresa rep) {
        // Usamos el repository que ya tenés para buscar por el ID del representante
        List<SolicitudRadicacion> solicitudes = solicitudRepository.findByRepresentanteId(rep.getId());

        if (solicitudes.isEmpty()) {
            return null;
        }

        // Devolvemos la última cargada (la más reciente)
        return solicitudes.get(solicitudes.size() - 1);
    }

    // 1. Para rescatar los datos y mostrarlos en los TextFields al editar
    public SolicitudRadicacion obtenerSolicitudPorId(Long id) {
        return solicitudRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No encontré la solicitud con ID: " + id));
    }

    // 2. Para guardar los cambios cuando Mariano termina de editar
    @Transactional
    public void actualizarSolicitud(SolicitudRadicacion solicitud) {
        // Como la solicitud ya tiene un ID, el .save() de Spring se da cuenta
        // solo que tiene que hacer un UPDATE y no un INSERT nuevo.
        solicitudRepository.save(solicitud);
    }


    //si toca el ojito odescarga chau posibilidad de modificar
    @Transactional
    public void marcarComoEnEvaluacion(Long solicitudId) {
        SolicitudRadicacion sol = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("No se encontró la solicitud"));

        // Solo hacemos el cambio si está en PENDIENTE.
        // Si ya está APROBADA o RECHAZADA, no queremos volver atrás.
        if (sol.getEstado() == EstadoSolicitud.PENDIENTE) {
            sol.setEstado(EstadoSolicitud.EN_EVALUACION);
            solicitudRepository.save(sol);
        }
    }


    //contabilizar pendientes
    public long contarSolicitudesPendientes() {
        return solicitudRepository.countByEstado(EstadoSolicitud.PENDIENTE);
    }
}