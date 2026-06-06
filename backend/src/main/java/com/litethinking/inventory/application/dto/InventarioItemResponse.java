package com.litethinking.inventory.application.dto;

import java.util.List;

public record InventarioItemResponse(
        String empresaNit,
        String empresaNombre,
        String codigo,
        String nombre,
        String caracteristicas,
        List<String> categorias,
        List<PrecioDto> precios
) {}
