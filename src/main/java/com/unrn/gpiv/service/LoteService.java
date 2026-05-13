package com.unrn.gpiv.service;

import com.unrn.gpiv.model.Lote;
import com.unrn.gpiv.repository.LoteRepository;
import org.springframework.stereotype.Service;
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

    public void guardar(Lote lote) {
        repository.save(lote);
    }
    
    public void eliminar(Lote lote) {
        repository.delete(lote);
    }
}