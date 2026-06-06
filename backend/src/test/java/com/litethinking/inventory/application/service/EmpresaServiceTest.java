package com.litethinking.inventory.application.service;

import com.litethinking.inventory.application.dto.EmpresaRequest;
import com.litethinking.inventory.application.dto.EmpresaResponse;
import com.litethinking.inventory.application.port.out.EmpresaRepositoryPort;
import com.litethinking.inventory.domain.exception.DuplicateResourceException;
import com.litethinking.inventory.domain.exception.ResourceNotFoundException;
import com.litethinking.inventory.domain.model.Empresa;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmpresaServiceTest {

    @Mock
    EmpresaRepositoryPort repository;

    @InjectMocks
    EmpresaService service;

    private final EmpresaRequest req = new EmpresaRequest(
            "900123456-7", "ACME S.A.S", "Calle 10", "6017001234");

    @Test
    void crear_persisteYRetornaResponse() {
        when(repository.existsByNit("900123456-7")).thenReturn(false);
        when(repository.save(any(Empresa.class))).thenAnswer(inv -> inv.getArgument(0));

        EmpresaResponse response = service.crear(req);

        assertThat(response.nit()).isEqualTo("900123456-7");
        assertThat(response.nombre()).isEqualTo("ACME S.A.S");
        verify(repository).save(any(Empresa.class));
    }

    @Test
    void crear_lanzaDuplicateSiNitExiste() {
        when(repository.existsByNit("900123456-7")).thenReturn(true);

        assertThatThrownBy(() -> service.crear(req))
                .isInstanceOf(DuplicateResourceException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void obtener_lanzaNotFoundSiNoExiste() {
        when(repository.findByNit("999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtener("999"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void eliminar_lanzaNotFoundSiNoExiste() {
        when(repository.existsByNit("999")).thenReturn(false);

        assertThatThrownBy(() -> service.eliminar("999"))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(repository, never()).deleteByNit(any());
    }
}
