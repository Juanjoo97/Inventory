package com.litethinking.inventory.infrastructure.web.controller;

import com.litethinking.inventory.application.dto.CategoriaRequest;
import com.litethinking.inventory.application.dto.CategoriaResponse;
import com.litethinking.inventory.application.service.CategoriaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Categorias")
@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','EXTERNO')")
    public List<CategoriaResponse> listar() {
        return categoriaService.listar();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoriaResponse crear(@Valid @RequestBody CategoriaRequest request) {
        return categoriaService.crear(request.nombre());
    }
}
