package com.litethinking.inventory.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import com.litethinking.inventory.infrastructure.validation.ISO4217;
import java.math.BigDecimal;

public record PrecioDto(
        @NotBlank
        @Size(min = 3, max = 3)
        @ISO4217
        String moneda,
        @NotNull
        @Positive
        BigDecimal valor
) {}
