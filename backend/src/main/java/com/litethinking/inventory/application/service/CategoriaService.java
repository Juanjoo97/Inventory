package com.litethinking.inventory.application.service;

import com.litethinking.inventory.application.dto.CategoriaResponse;
import com.litethinking.inventory.application.port.out.CategoriaRepositoryPort;
import com.litethinking.inventory.domain.exception.DuplicateResourceException;
import com.litethinking.inventory.domain.model.Categoria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoriaService {

    private final CategoriaRepositoryPort repository;

    public CategoriaService(CategoriaRepositoryPort repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponse> listar() {
        return repository.findAll().stream()
                .map(c -> new CategoriaResponse(c.getId(), c.getNombre()))
                .toList();
    }

    public CategoriaResponse crear(String nombre) {
        if (repository.existsByNombre(nombre)) {
            throw new DuplicateResourceException("Ya existe la categoria " + nombre);
        }
        Categoria guardada = repository.save(Categoria.builder().nombre(nombre).build());
        return new CategoriaResponse(guardada.getId(), guardada.getNombre());
    }
}
