package com.unrn.gpiv.inventory.repository;

import com.unrn.gpiv.inventory.model.Recurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RecursoRepository extends JpaRepository<Recurso, Long> {

    // MAGIA: Al poner "ItemCategoria", Spring busca adentro del Item asociado al Recurso
    List<Recurso> findByItemCategoriaContainingIgnoreCase(String categoria);

    // Para ver qué herramientas están libres para prestar
    List<Recurso> findByDisponibleTrue();
}