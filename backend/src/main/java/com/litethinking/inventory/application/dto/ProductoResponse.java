package com.litethinking.inventory.application.dto;

import java.util.List;

public record ProductoResponse(
        Long id,
        String codigo,
        String nombre,
        String caracteristicas,
        String empresaNit,
        String empresaNombre,
        List<PrecioDto> precios,
        List<String> categorias
) {}
