package com.litethinking.inventory.infrastructure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(
                "clave-secreta-de-prueba-con-al-menos-32-bytes-1234", 3600000L);
    }

    @Test
    void generarToken_yValidar_funciona() {
        String token = jwtService.generarToken("admin@litethinking.com", "ADMIN");

        assertThat(jwtService.esValido(token)).isTrue();
        assertThat(jwtService.extraerEmail(token)).isEqualTo("admin@litethinking.com");
    }

    @Test
    void esValido_retornaFalseParaTokenInvalido() {
        assertThat(jwtService.esValido("token.basura.invalido")).isFalse();
    }
}
