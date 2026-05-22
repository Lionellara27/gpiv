package com.unrn.gpiv.service;

import com.unrn.gpiv.common.EstadoLote;
import com.unrn.gpiv.model.Lote;
import com.unrn.gpiv.repository.LoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LoteService {

    private final LoteRepository repository;

     // se le inyecta el repository
    public LoteService(LoteRepository repository) {
        this.repository = repository;
    }

    public List<Lote> listarTodos() {
        return repository.findAll();
    }

    @Transactional
    public void guardar(Lote lote) {
        repository.saveAndFlush(lote); // El "Flush" obliga a escribir en la DB YA mismo
    }
    
    public void eliminar(Lote lote) {
        repository.delete(lote);
    }

    public List<Lote> buscarPorEstado(EstadoLote estado) {
        return repository.findByEstado(estado);
    }
}