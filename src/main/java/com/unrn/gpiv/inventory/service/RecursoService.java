package com.unrn.gpiv.inventory.service;

import com.unrn.gpiv.inventory.model.Recurso;
import com.unrn.gpiv.inventory.repository.RecursoRepository;
import com.unrn.gpiv.model.Empresa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RecursoService {

    @Autowired
    private RecursoRepository recursoRepository;

    public List<Recurso> obtenerTodoElInventario() {
        return recursoRepository.findAll();
    }

    @Transactional
    public void prestarRecurso(Long recursoId, Empresa empresa) {
        Recurso recurso = recursoRepository.findById(recursoId)
                .orElseThrow(() -> new RuntimeException("Recurso no encontrado"));

        if (recurso.isDisponible()) {
            recurso.setPrestadoA(empresa);
            recurso.setDisponible(false);
            recursoRepository.save(recurso);
        }
    }

    @Transactional
    public void darDeBaja(Long id) {
        // HU 19: Eliminar por rotura o pérdida
        recursoRepository.deleteById(id);
    }
}