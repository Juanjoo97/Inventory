package com.litethinking.inventory.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

public record EmpresaRequest(
        @NotBlank
        @Size(max = 40)
        @Pattern(
                regexp = "^\\d{9,10}-\\d$",
                message = "El NIT debe tener el formato 901987654-3"
        )
        String nit,

        @NotBlank
        @Size(max = 150)
        String nombre,

        @Size(max = 255)
        String direccion,

        @Pattern(
                regexp = "^$|^(3\\d{9}|\\d{7}|\\d{10})$",
                message = "Ingrese un teléfono fijo o celular válido"
        )
        String telefono
) {}