package com.litethinking.inventory.infrastructure.pdf;

import com.litethinking.inventory.application.dto.InventarioItemResponse;
import com.litethinking.inventory.application.dto.PrecioDto;
import com.litethinking.inventory.application.port.out.PdfGeneratorPort;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementacion del puerto de generacion de PDF usando OpenPDF (com.lowagie.text).
 */
@Component
public class OpenPdfGeneratorAdapter implements PdfGeneratorPort {

    private static final Font FONT_TITULO = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Color.BLACK);
    private static final Font FONT_SUBTITULO = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.DARK_GRAY);
    private static final Font FONT_HEADER = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);
    private static final Font FONT_CELDA = FontFactory.getFont(FontFactory.HELVETICA, 8, Color.BLACK);
    private static final Color COLOR_HEADER = new Color(33, 37, 41);

    @Override
    public byte[] generarInventarioPdf(List<InventarioItemResponse> items) {
        Document document = new Document(PageSize.A4.rotate(), 36, 36, 36, 36);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Paragraph titulo = new Paragraph("Inventario - Lite Thinking", FONT_TITULO);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);

            String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            Paragraph sub = new Paragraph("Generado: " + fecha + "  |  Total de productos: " + items.size(), FONT_SUBTITULO);
            sub.setAlignment(Element.ALIGN_CENTER);
            sub.setSpacingAfter(12f);
            document.add(sub);

            PdfPTable table = new PdfPTable(new float[]{2.2f, 1.2f, 2.2f, 3.0f, 2.0f, 2.6f});
            table.setWidthPercentage(100);
            table.setHeaderRows(1);

            for (String h : List.of("Empresa", "Codigo", "Producto", "Caracteristicas", "Categorias", "Precios")) {
                table.addCell(headerCell(h));
            }

            for (InventarioItemResponse item : items) {
                table.addCell(cell(item.empresaNombre() + " (" + item.empresaNit() + ")"));
                table.addCell(cell(item.codigo()));
                table.addCell(cell(item.nombre()));
                table.addCell(cell(safe(item.caracteristicas())));
                table.addCell(cell(String.join(", ", item.categorias())));
                table.addCell(cell(formatPrecios(item.precios())));
            }

            if (items.isEmpty()) {
                PdfPCell vacio = new PdfPCell(new Phrase("No hay productos registrados.", FONT_CELDA));
                vacio.setColspan(6);
                vacio.setHorizontalAlignment(Element.ALIGN_CENTER);
                vacio.setPadding(8f);
                table.addCell(vacio);
            }

            document.add(table);
            document.close();
            return out.toByteArray();
        } catch (DocumentException ex) {
            throw new IllegalStateException("Error generando el PDF de inventario", ex);
        }
    }

    private PdfPCell headerCell(String texto) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, FONT_HEADER));
        cell.setBackgroundColor(COLOR_HEADER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(6f);
        return cell;
    }

    private PdfPCell cell(String texto) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, FONT_CELDA));
        cell.setPadding(5f);
        return cell;
    }

    private String formatPrecios(List<PrecioDto> precios) {
        return precios.stream()
                .map(p -> p.moneda() + " " + p.valor().toPlainString())
                .collect(Collectors.joining("\n"));
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
