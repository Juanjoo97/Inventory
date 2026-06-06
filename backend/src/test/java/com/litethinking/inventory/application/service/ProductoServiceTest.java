package com.litethinking.inventory.application.service;

import com.litethinking.inventory.application.dto.PrecioDto;
import com.litethinking.inventory.application.dto.ProductoRequest;
import com.litethinking.inventory.application.dto.ProductoResponse;
import com.litethinking.inventory.application.port.out.CategoriaRepositoryPort;
import com.litethinking.inventory.application.port.out.EmpresaRepositoryPort;
import com.litethinking.inventory.application.port.out.ProductoRepositoryPort;
import com.litethinking.inventory.domain.exception.DuplicateResourceException;
import com.litethinking.inventory.domain.exception.ResourceNotFoundException;
import com.litethinking.inventory.domain.model.Empresa;
import com.litethinking.inventory.domain.model.Producto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock ProductoRepositoryPort productoRepository;
    @Mock EmpresaRepositoryPort empresaRepository;
    @Mock CategoriaRepositoryPort categoriaRepository;

    @InjectMocks ProductoService service;

    private ProductoRequest request() {
        return new ProductoRequest(
                "P-001", "Laptop", "16GB RAM", "900123456-7",
                List.of(new PrecioDto("USD", new BigDecimal("1150.00")),
                        new PrecioDto("COP", new BigDecimal("4500000"))),
                List.of());
    }

    private Empresa empresa() {
        return Empresa.builder().nit("900123456-7").nombre("ACME S.A.S").build();
    }

    @Test
    void crear_conPreciosMultimoneda_retornaResponse() {
        when(productoRepository.existsByCodigo("P-001")).thenReturn(false);
        when(empresaRepository.findByNit("900123456-7")).thenReturn(Optional.of(empresa()));
        when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

        ProductoResponse response = service.crear(request());

        assertThat(response.codigo()).isEqualTo("P-001");
        assertThat(response.empresaNit()).isEqualTo("900123456-7");
        assertThat(response.precios()).hasSize(2);
        assertThat(response.precios()).extracting(PrecioDto::moneda).contains("USD", "COP");
    }

    @Test
    void crear_lanzaDuplicateSiCodigoExiste() {
        when(productoRepository.existsByCodigo("P-001")).thenReturn(true);

        assertThatThrownBy(() -> service.crear(request()))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void crear_lanzaNotFoundSiEmpresaNoExiste() {
        when(productoRepository.existsByCodigo("P-001")).thenReturn(false);
        when(empresaRepository.findByNit("900123456-7")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.crear(request()))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
