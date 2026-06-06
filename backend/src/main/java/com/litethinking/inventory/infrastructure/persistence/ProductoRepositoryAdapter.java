package com.litethinking.inventory.infrastructure.persistence;

import com.litethinking.inventory.application.port.out.ProductoRepositoryPort;
import com.litethinking.inventory.domain.model.Producto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ProductoRepositoryAdapter implements ProductoRepositoryPort {

    private final SpringDataProductoRepository repository;

    public ProductoRepositoryAdapter(SpringDataProductoRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Producto> findAll() {
        return repository.findAll();
    }

    @Override
    public List<Producto> findByEmpresaNit(String nit) {
        return repository.findByEmpresaNit(nit);
    }

    @Override
    public Optional<Producto> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public boolean existsByCodigo(String codigo) {
        return repository.existsByCodigo(codigo);
    }

    @Override
    public Producto save(Producto producto) {
        return repository.save(producto);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
