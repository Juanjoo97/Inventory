package com.litethinking.inventory.application.port.out;

import com.litethinking.inventory.domain.model.Categoria;

import java.util.List;
import java.util.Optional;

/** Puerto de salida para la persistencia de categorias. */
public interface CategoriaRepositoryPort {
    List<Categoria> findAll();
    List<Categoria> findAllByIds(List<Long> ids);
    Optional<Categoria> findById(Long id);
    boolean existsByNombre(String nombre);
    Categoria save(Categoria categoria);
}
