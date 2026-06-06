package com.litethinking.inventory.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ProductoRequest(
        @NotBlank @Size(max = 60) String codigo,
        @NotBlank @Size(max = 150) String nombre,
        @Size(max = 2000) String caracteristicas,
        @NotBlank String empresaNit,
        @NotEmpty @Valid List<PrecioDto> precios,
        List<Long> categoriaIds
) {}
