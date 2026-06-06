package com.litethinking.inventory.application.port.out;

import com.litethinking.inventory.domain.model.Usuario;

import java.util.Optional;

/** Puerto de salida para la persistencia de usuarios. */
public interface UsuarioRepositoryPort {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
    Usuario save(Usuario usuario);
}
