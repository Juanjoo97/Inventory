package com.litethinking.inventory.application.service;

import com.litethinking.inventory.application.dto.InventarioItemResponse;
import com.litethinking.inventory.application.mapper.ProductoMapper;
import com.litethinking.inventory.application.port.out.EmailSenderPort;
import com.litethinking.inventory.application.port.out.PdfGeneratorPort;
import com.litethinking.inventory.application.port.out.ProductoRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class InventarioService {

    private final ProductoRepositoryPort productoRepository;
    private final PdfGeneratorPort pdfGenerator;
    private final EmailSenderPort emailSender;

    public InventarioService(ProductoRepositoryPort productoRepository,
                             PdfGeneratorPort pdfGenerator,
                             EmailSenderPort emailSender) {
        this.productoRepository = productoRepository;
        this.pdfGenerator = pdfGenerator;
        this.emailSender = emailSender;
    }

    public List<InventarioItemResponse> obtenerInventario() {
        return productoRepository.findAll().stream()
                .map(ProductoMapper::toInventarioItem)
                .sorted(Comparator.comparing(InventarioItemResponse::empresaNombre)
                        .thenComparing(InventarioItemResponse::nombre))
                .toList();
    }

    public byte[] generarPdf() {
        return pdfGenerator.generarInventarioPdf(obtenerInventario());
    }

    public void enviarPorCorreo(String destinatario) {
        byte[] pdf = generarPdf();
        emailSender.enviarConAdjunto(
                destinatario,
                "Inventario - Lite Thinking",
                "Adjunto encontrara el reporte de inventario en formato PDF.",
                pdf,
                "inventario.pdf");
    }
}
