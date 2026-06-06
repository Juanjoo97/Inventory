package com.litethinking.inventory.infrastructure.web.advice;

import java.time.Instant;
import java.util.Map;

/** Cuerpo estandar de respuesta de error. */
public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        Map<String, String> validaciones
) {
    public static ApiError of(int status, String error, String message) {
        return new ApiError(Instant.now(), status, error, message, null);
    }

    public static ApiError validation(int status, String error, String message, Map<String, String> validaciones) {
        return new ApiError(Instant.now(), status, error, message, validaciones);
    }
}
