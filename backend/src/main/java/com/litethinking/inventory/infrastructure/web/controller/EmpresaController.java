package com.litethinking.inventory.infrastructure.web.controller;

import com.litethinking.inventory.application.dto.EmpresaRequest;
import com.litethinking.inventory.application.dto.EmpresaResponse;
import com.litethinking.inventory.application.service.EmpresaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Empresas")
@RestController
@RequestMapping("/api/empresas")
public class EmpresaController {

    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','EXTERNO')")
    public List<EmpresaResponse> listar() {
        return empresaService.listar();
    }

    @GetMapping("/{nit}")
    @PreAuthorize("hasAnyRole('ADMIN','EXTERNO')")
    public EmpresaResponse obtener(@PathVariable String nit) {
        return empresaService.obtener(nit);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public EmpresaResponse crear(@Valid @RequestBody EmpresaRequest request) {
        return empresaService.crear(request);
    }

    @PutMapping("/{nit}")
    @PreAuthorize("hasRole('ADMIN')")
    public EmpresaResponse actualizar(@PathVariable String nit, @Valid @RequestBody EmpresaRequest request) {
        return empresaService.actualizar(nit, request);
    }

    @DeleteMapping("/{nit}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable String nit) {
        empresaService.eliminar(nit);
    }
}
