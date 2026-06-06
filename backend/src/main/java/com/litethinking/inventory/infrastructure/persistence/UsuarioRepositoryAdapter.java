package com.litethinking.inventory.infrastructure.persistence;

import com.litethinking.inventory.application.port.out.UsuarioRepositoryPort;
import com.litethinking.inventory.domain.model.Usuario;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UsuarioRepositoryAdapter implements UsuarioRepositoryPort {

    private final SpringDataUsuarioRepository repository;

    public UsuarioRepositoryAdapter(SpringDataUsuarioRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public Usuario save(Usuario usuario) {
        return repository.save(usuario);
    }
}
