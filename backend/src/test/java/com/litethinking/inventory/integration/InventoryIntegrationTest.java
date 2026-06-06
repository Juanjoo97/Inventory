package com.litethinking.inventory.integration;

import com.litethinking.inventory.infrastructure.persistence.SpringDataEmpresaRepository;
import com.litethinking.inventory.infrastructure.persistence.SpringDataUsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Prueba de integracion contra un PostgreSQL real (Testcontainers).
 * Se omite automaticamente si Docker no esta disponible.
 */
@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
class InventoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void datasourceProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    SpringDataUsuarioRepository usuarioRepository;

    @Autowired
    SpringDataEmpresaRepository empresaRepository;

    @Test
    void contextoCarga_yElSeederCreaUsuariosYEmpresas() {
        assertThat(usuarioRepository.findByEmail("admin@litethinking.com")).isPresent();
        assertThat(usuarioRepository.findByEmail("externo@litethinking.com")).isPresent();
        assertThat(empresaRepository.count()).isGreaterThanOrEqualTo(2);
    }
}
