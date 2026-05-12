package com.unrn.gpiv.repository;

import com.unrn.gpiv.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Este es el método que va a buscar por email/username en toda la tabla
    Optional<Usuario> findByUsername(String username);
}