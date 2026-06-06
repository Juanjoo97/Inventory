package com.litethinking.inventory.infrastructure.persistence;

import com.litethinking.inventory.domain.model.Producto;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataProductoRepository extends JpaRepository<Producto, Long> {

    @EntityGraph(attributePaths = {"empresa", "precios", "categorias"})
    List<Producto> findByEmpresaNit(String nit);

    boolean existsByCodigo(String codigo);

    @Override
    @EntityGraph(attributePaths = {"empresa", "precios", "categorias"})
    List<Producto> findAll();

    @Override
    @EntityGraph(attributePaths = {"empresa", "precios", "categorias"})
    Optional<Producto> findById(Long id);
}
