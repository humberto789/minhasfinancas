package com.humberto789.minhasfinancas.model.repository;

import com.humberto789.minhasfinancas.model.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    public boolean existsByEmail(String email);

    Optional<Usuario> findByEmail(String email);


}
