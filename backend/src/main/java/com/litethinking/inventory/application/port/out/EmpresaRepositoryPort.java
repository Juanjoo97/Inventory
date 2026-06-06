package com.litethinking.inventory.application.port.out;

import com.litethinking.inventory.domain.model.Empresa;

import java.util.List;
import java.util.Optional;

/** Puerto de salida para la persistencia de empresas. */
public interface EmpresaRepositoryPort {
    List<Empresa> findAll();
    Optional<Empresa> findByNit(String nit);
    boolean existsByNit(String nit);
    Empresa save(Empresa empresa);
    void deleteByNit(String nit);
}
