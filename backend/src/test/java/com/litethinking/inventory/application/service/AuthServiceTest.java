package com.litethinking.inventory.application.service;

import com.litethinking.inventory.application.dto.AuthRequest;
import com.litethinking.inventory.application.dto.AuthResponse;
import com.litethinking.inventory.application.port.out.TokenProviderPort;
import com.litethinking.inventory.application.port.out.UsuarioRepositoryPort;
import com.litethinking.inventory.domain.exception.InvalidCredentialsException;
import com.litethinking.inventory.domain.model.Rol;
import com.litethinking.inventory.domain.model.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UsuarioRepositoryPort usuarioRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock TokenProviderPort tokenProvider;

    @InjectMocks AuthService service;

    private Usuario usuario() {
        return Usuario.builder()
                .id(1L).email("admin@litethinking.com").password("hash").rol(Rol.ADMIN).build();
    }

    @Test
    void login_exitoso_retornaToken() {
        when(usuarioRepository.findByEmail("admin@litethinking.com")).thenReturn(Optional.of(usuario()));
        when(passwordEncoder.matches("Admin123*", "hash")).thenReturn(true);
        when(tokenProvider.generarToken("admin@litethinking.com", "ADMIN")).thenReturn("jwt-token");

        AuthResponse response = service.login(new AuthRequest("admin@litethinking.com", "Admin123*"));

        assertThat(response.token()).isEqualTo("jwt-token");
        assertThat(response.rol()).isEqualTo("ADMIN");
    }

    @Test
    void login_passwordIncorrecta_lanzaInvalidCredentials() {
        when(usuarioRepository.findByEmail("admin@litethinking.com")).thenReturn(Optional.of(usuario()));
        when(passwordEncoder.matches("mala", "hash")).thenReturn(false);

        assertThatThrownBy(() -> service.login(new AuthRequest("admin@litethinking.com", "mala")))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void login_usuarioNoExiste_lanzaInvalidCredentials() {
        when(usuarioRepository.findByEmail("no@existe.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.login(new AuthRequest("no@existe.com", "x")))
                .isInstanceOf(InvalidCredentialsException.class);
    }
}
