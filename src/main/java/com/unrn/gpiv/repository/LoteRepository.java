package com.unrn.gpiv.repository;

import org.springframework.stereotype.Repository;
import com.unrn.gpiv.model.Lote;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface LoteRepository extends JpaRepository<Lote, Long> {
    //permite buscar, guardar o borrar datos sin escribir SQL manualmente.
    
}
