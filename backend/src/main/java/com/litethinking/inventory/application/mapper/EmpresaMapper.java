package com.litethinking.inventory.application.mapper;

import com.litethinking.inventory.application.dto.EmpresaRequest;
import com.litethinking.inventory.application.dto.EmpresaResponse;
import com.litethinking.inventory.domain.model.Empresa;

/** Mapeo entre Empresa y sus DTOs. */
public final class EmpresaMapper {

    private EmpresaMapper() {}

    public static Empresa toEntity(EmpresaRequest req) {
        return Empresa.builder()
                .nit(req.nit())
                .nombre(req.nombre())
                .direccion(req.direccion())
                .telefono(req.telefono())
                .build();
    }

    public static EmpresaResponse toResponse(Empresa e) {
        return new EmpresaResponse(e.getNit(), e.getNombre(), e.getDireccion(), e.getTelefono());
    }
}
