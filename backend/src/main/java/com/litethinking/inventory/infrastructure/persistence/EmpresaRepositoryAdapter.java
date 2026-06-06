package com.litethinking.inventory.infrastructure.persistence;

import com.litethinking.inventory.application.port.out.EmpresaRepositoryPort;
import com.litethinking.inventory.domain.model.Empresa;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class EmpresaRepositoryAdapter implements EmpresaRepositoryPort {

    private final SpringDataEmpresaRepository repository;

    public EmpresaRepositoryAdapter(SpringDataEmpresaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Empresa> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<Empresa> findByNit(String nit) {
        return repository.findById(nit);
    }

    @Override
    public boolean existsByNit(String nit) {
        return repository.existsById(nit);
    }

    @Override
    public Empresa save(Empresa empresa) {
        return repository.save(empresa);
    }

    @Override
    public void deleteByNit(String nit) {
        repository.deleteById(nit);
    }
}
