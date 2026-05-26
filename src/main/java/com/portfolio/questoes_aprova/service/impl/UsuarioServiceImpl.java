package com.portfolio.questoes_aprova.service.impl;

import com.portfolio.questoes_aprova.dto.UsuarioRequestDTO;
import com.portfolio.questoes_aprova.dto.UsuarioResponseDTO;
import com.portfolio.questoes_aprova.entity.Usuario;
import com.portfolio.questoes_aprova.exception.BusinessException;
import com.portfolio.questoes_aprova.repository.UsuarioRepository;
import com.portfolio.questoes_aprova.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UsuarioResponseDTO criar(UsuarioRequestDTO dto) {
        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new BusinessException("Ja existe usuario com este e-mail.");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setSenhaHash(passwordEncoder.encode(dto.senha()));
        usuario.setRole(parseRole(dto.role()));

        return UsuarioResponseDTO.from(usuarioRepository.save(usuario));
    }

    @Override
    public List<UsuarioResponseDTO> listar() {
        return usuarioRepository.findAll().stream().map(UsuarioResponseDTO::from).toList();
    }

    private Usuario.Role parseRole(String role) {
        if (role == null || role.isBlank()) {
            return Usuario.Role.ALUNO;
        }
        try {
            return Usuario.Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BusinessException("Role invalida. Use ALUNO, PROFESSOR ou ADMIN.");
        }
    }
}
