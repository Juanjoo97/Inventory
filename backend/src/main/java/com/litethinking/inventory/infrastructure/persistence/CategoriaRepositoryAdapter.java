package com.litethinking.inventory.infrastructure.persistence;

import com.litethinking.inventory.application.port.out.CategoriaRepositoryPort;
import com.litethinking.inventory.domain.model.Categoria;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CategoriaRepositoryAdapter implements CategoriaRepositoryPort {

    private final SpringDataCategoriaRepository repository;

    public CategoriaRepositoryAdapter(SpringDataCategoriaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Categoria> findAll() {
        return repository.findAll();
    }

    @Override
    public List<Categoria> findAllByIds(List<Long> ids) {
        return repository.findAllById(ids);
    }

    @Override
    public Optional<Categoria> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public boolean existsByNombre(String nombre) {
        return repository.existsByNombre(nombre);
    }

    @Override
    public Categoria save(Categoria categoria) {
        return repository.save(categoria);
    }
}
