package com.unrn.gpiv.repository;

import com.unrn.gpiv.model.RepresentanteEmpresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepresentanteEmpresaRepository extends JpaRepository<RepresentanteEmpresa, Long> {
    // Para validar que no se registren dos veces con el mismo DNI o CUIT
    boolean existsByDni(String dni);
    boolean existsByCuitPersonal(String cuitPersonal);
}