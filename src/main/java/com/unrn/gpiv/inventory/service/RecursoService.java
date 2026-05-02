package com.unrn.gpiv.inventory.service;

import com.unrn.gpiv.inventory.model.Recurso;
import com.unrn.gpiv.inventory.repository.RecursoRepository;
import com.unrn.gpiv.model.Empresa;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RecursoService {

    private final RecursoRepository recursoRepository;

    // Inyección por constructor (Mejor práctica que usar @Autowired en la variable)
    public RecursoService(RecursoRepository recursoRepository) {
        this.recursoRepository = recursoRepository;
    }

    // --- MÉTODOS DE LECTURA (Buscadores) ---

    public List<Recurso> obtenerTodoElInventario() {
        return recursoRepository.findAll();
    }

    public List<Recurso> obtenerRecursosDisponibles() {
        return recursoRepository.findByDisponibleTrue();
    }

    public List<Recurso> buscarPorCategoria(String categoria) {
        return recursoRepository.findByItemCategoriaContainingIgnoreCase(categoria);
    }

    // --- MÉTODOS DE ESCRITURA (Transacciones) ---

    @Transactional
    public void prestarRecurso(Long recursoId, Empresa empresa) {
        Recurso recurso = recursoRepository.findById(recursoId)
                .orElseThrow(() -> new RuntimeException("Recurso no encontrado en el inventario"));

        if (!recurso.isDisponible()) {
            // Ahora sí avisa si alguien intenta llevarse algo que ya está prestado
            throw new RuntimeException("El recurso ya se encuentra prestado a otra empresa");
        }

        recurso.setPrestadoA(empresa);
        recurso.setDisponible(false);
        recursoRepository.save(recurso);
    }

    @Transactional
    public void devolverRecurso(Long recursoId) {
        Recurso recurso = recursoRepository.findById(recursoId)
                .orElseThrow(() -> new RuntimeException("Recurso no encontrado"));

        // Limpiamos los datos del préstamo y lo volvemos a poner disponible
        recurso.setPrestadoA(null);
        recurso.setDisponible(true);
        recursoRepository.save(recurso);
    }

    @Transactional
    public void darDeBaja(Long id) {
        // HU 19: Eliminar por rotura o pérdida
        if (!recursoRepository.existsById(id)) {
            throw new RuntimeException("El recurso que intenta eliminar no existe");
        }
        recursoRepository.deleteById(id);
    }
}