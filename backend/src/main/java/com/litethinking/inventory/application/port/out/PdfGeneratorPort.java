package com.litethinking.inventory.application.port.out;

import com.litethinking.inventory.application.dto.InventarioItemResponse;

import java.util.List;

/** Puerto de salida para la generacion de documentos PDF. */
public interface PdfGeneratorPort {
    byte[] generarInventarioPdf(List<InventarioItemResponse> items);
}
