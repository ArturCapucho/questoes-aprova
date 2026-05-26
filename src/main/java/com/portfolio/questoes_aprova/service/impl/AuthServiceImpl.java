package com.portfolio.questoes_aprova.service.impl;

import com.portfolio.questoes_aprova.config.JwtService;
import com.portfolio.questoes_aprova.dto.AuthResponseDTO;
import com.portfolio.questoes_aprova.dto.LoginRequestDTO;
import com.portfolio.questoes_aprova.entity.Usuario;
import com.portfolio.questoes_aprova.exception.UnauthorizedException;
import com.portfolio.questoes_aprova.repository.UsuarioRepository;
import com.portfolio.questoes_aprova.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public AuthResponseDTO autenticar(LoginRequestDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.email())
                .orElseThrow(() -> new UnauthorizedException("Credenciais invalidas."));

        if (!passwordEncoder.matches(dto.senha(), usuario.getSenhaHash())) {
            throw new UnauthorizedException("Credenciais invalidas.");
        }

        return new AuthResponseDTO(jwtService.gerarToken(usuario));
    }
}
