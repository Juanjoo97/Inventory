package com.litethinking.inventory.application.dto;

public record EmpresaResponse(
        String nit,
        String nombre,
        String direccion,
        String telefono
) {}
