package com.litethinking.inventory.infrastructure.web.controller;

import com.litethinking.inventory.application.dto.InventarioItemResponse;
import com.litethinking.inventory.application.dto.SendInventoryEmailRequest;
import com.litethinking.inventory.application.service.InventarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Inventario")
@RestController
@RequestMapping("/api/inventario")
@PreAuthorize("hasRole('ADMIN')")
public class InventarioController {

    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @Operation(summary = "Lista el inventario (productos agrupados por empresa)")
    @GetMapping
    public List<InventarioItemResponse> inventario() {
        return inventarioService.obtenerInventario();
    }

    @Operation(summary = "Descarga el inventario en formato PDF")
    @GetMapping("/pdf")
    public ResponseEntity<byte[]> descargarPdf() {
        byte[] pdf = inventarioService.generarPdf();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"inventario.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @Operation(summary = "Genera el PDF del inventario y lo envia por correo")
    @PostMapping("/enviar")
    public ResponseEntity<Map<String, String>> enviarPorCorreo(@Valid @RequestBody SendInventoryEmailRequest request) {
        inventarioService.enviarPorCorreo(request.email());
        return ResponseEntity.ok(Map.of(
                "mensaje", "Inventario enviado a " + request.email()));
    }
}
