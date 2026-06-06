package com.litethinking.inventory.application.mapper;

import com.litethinking.inventory.application.dto.InventarioItemResponse;
import com.litethinking.inventory.application.dto.PrecioDto;
import com.litethinking.inventory.application.dto.ProductoResponse;
import com.litethinking.inventory.domain.model.Categoria;
import com.litethinking.inventory.domain.model.Producto;
import com.litethinking.inventory.domain.model.ProductoPrecio;

import java.util.Comparator;
import java.util.List;

/** Mapeo entre Producto y sus DTOs. */
public final class ProductoMapper {

    private ProductoMapper() {}

    public static ProductoResponse toResponse(Producto p) {
        return new ProductoResponse(
                p.getId(),
                p.getCodigo(),
                p.getNombre(),
                p.getCaracteristicas(),
                p.getEmpresa().getNit(),
                p.getEmpresa().getNombre(),
                toPrecioDtos(p),
                toCategoriaNames(p));
    }

    public static InventarioItemResponse toInventarioItem(Producto p) {
        return new InventarioItemResponse(
                p.getEmpresa().getNit(),
                p.getEmpresa().getNombre(),
                p.getCodigo(),
                p.getNombre(),
                p.getCaracteristicas(),
                toCategoriaNames(p),
                toPrecioDtos(p));
    }

    private static List<PrecioDto> toPrecioDtos(Producto p) {
        return p.getPrecios().stream()
                .sorted(Comparator.comparing(ProductoPrecio::getMoneda))
                .map(pr -> new PrecioDto(pr.getMoneda(), pr.getValor()))
                .toList();
    }

    private static List<String> toCategoriaNames(Producto p) {
        return p.getCategorias().stream()
                .map(Categoria::getNombre)
                .sorted()
                .toList();
    }
}
