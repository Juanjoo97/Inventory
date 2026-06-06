package com.litethinking.inventory.application.service;

import com.litethinking.inventory.application.dto.AuthRequest;
import com.litethinking.inventory.application.dto.AuthResponse;
import com.litethinking.inventory.application.port.out.TokenProviderPort;
import com.litethinking.inventory.application.port.out.UsuarioRepositoryPort;
import com.litethinking.inventory.domain.exception.InvalidCredentialsException;
import com.litethinking.inventory.domain.model.Usuario;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepositoryPort usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProviderPort tokenProvider;

    public AuthService(UsuarioRepositoryPort usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       TokenProviderPort tokenProvider) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    public AuthResponse login(AuthRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new InvalidCredentialsException("Credenciales invalidas"));

        if (!passwordEncoder.matches(request.password(), usuario.getPassword())) {
            throw new InvalidCredentialsException("Credenciales invalidas");
        }

        String token = tokenProvider.generarToken(usuario.getEmail(), usuario.getRol().name());
        return new AuthResponse(token, usuario.getEmail(), usuario.getRol().name());
    }
}
