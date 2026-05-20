package com.unrn.gpiv.repository;

import com.unrn.gpiv.common.EstadoMovimientoRecurso; // 🟢 Importamos tu Enum fuerte
import com.unrn.gpiv.model.Recurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RecursoRepository extends JpaRepository<Recurso, Long> {

    // 🎯 BUSCADOR POR ENUM: Trae la lista de recursos según su estado de movimiento (Ej: DISPONIBLE)
    List<Recurso> findByEstadoMovimiento(EstadoMovimientoRecurso estadoMovimiento);

    // 🎯 LA MAGIA INTER-TABLAS: Como la categoría ahora le pertenece al "molde" (Item),
    // Spring viaja a través de la relación 'item' y filtra por su campo 'categoria' ignorando mayúsculas.
    List<Recurso> findByItemCategoriaContainingIgnoreCase(String categoria);

    // 📊 EL REY DEL DASHBOARD: Cuenta de forma exacta cuántos registros físicos hay en la base de datos
    // según el Enum seleccionado. Clave para que tus tarjetas azules muestren data real.
    long countByEstadoMovimiento(EstadoMovimientoRecurso estadoMovimiento);
}