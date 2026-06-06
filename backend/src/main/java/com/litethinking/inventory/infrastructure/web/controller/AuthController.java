package com.litethinking.inventory.infrastructure.web.controller;

import com.litethinking.inventory.application.dto.AuthRequest;
import com.litethinking.inventory.application.dto.AuthResponse;
import com.litethinking.inventory.application.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Autenticacion")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Inicia sesion y devuelve un token JWT")
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthRequest request) {
        return authService.login(request);
    }
}
