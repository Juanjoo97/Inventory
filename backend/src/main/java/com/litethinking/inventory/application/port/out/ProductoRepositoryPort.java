package com.litethinking.inventory.application.port.out;

import com.litethinking.inventory.domain.model.Producto;

import java.util.List;
import java.util.Optional;

/** Puerto de salida para la persistencia de productos. */
public interface ProductoRepositoryPort {
    List<Producto> findAll();
    List<Producto> findByEmpresaNit(String nit);
    Optional<Producto> findById(Long id);
    boolean existsByCodigo(String codigo);
    Producto save(Producto producto);
    void deleteById(Long id);
}
