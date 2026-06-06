package com.litethinking.inventory.application.service;

import com.litethinking.inventory.application.dto.EmpresaRequest;
import com.litethinking.inventory.application.dto.EmpresaResponse;
import com.litethinking.inventory.application.mapper.EmpresaMapper;
import com.litethinking.inventory.application.port.out.EmpresaRepositoryPort;
import com.litethinking.inventory.domain.exception.DuplicateResourceException;
import com.litethinking.inventory.domain.exception.ResourceNotFoundException;
import com.litethinking.inventory.domain.model.Empresa;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EmpresaService {

    private final EmpresaRepositoryPort repository;

    public EmpresaService(EmpresaRepositoryPort repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<EmpresaResponse> listar() {
        return repository.findAll().stream().map(EmpresaMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public EmpresaResponse obtener(String nit) {
        return EmpresaMapper.toResponse(buscarOError(nit));
    }

    public EmpresaResponse crear(EmpresaRequest req) {
        if (repository.existsByNit(req.nit())) {
            throw new DuplicateResourceException("Ya existe una empresa con NIT " + req.nit());
        }
        Empresa guardada = repository.save(EmpresaMapper.toEntity(req));
        return EmpresaMapper.toResponse(guardada);
    }

    public EmpresaResponse actualizar(String nit, EmpresaRequest req) {
        Empresa empresa = buscarOError(nit);
        empresa.setNombre(req.nombre());
        empresa.setDireccion(req.direccion());
        empresa.setTelefono(req.telefono());
        return EmpresaMapper.toResponse(repository.save(empresa));
    }

    public void eliminar(String nit) {
        if (!repository.existsByNit(nit)) {
            throw new ResourceNotFoundException("No existe la empresa con NIT " + nit);
        }
        repository.deleteByNit(nit);
    }

    private Empresa buscarOError(String nit) {
        return repository.findByNit(nit)
                .orElseThrow(() -> new ResourceNotFoundException("No existe la empresa con NIT " + nit));
    }
}
