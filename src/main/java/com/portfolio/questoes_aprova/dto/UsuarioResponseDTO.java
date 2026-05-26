package com.portfolio.questoes_aprova.dto;

import com.portfolio.questoes_aprova.entity.Usuario;

import java.util.UUID;

// DTO Pattern: exposicao controlada dos dados, sem vazar senha_hash nem detalhes internos da entidade.
public record UsuarioResponseDTO(Long id, UUID uuid, String nome, String email, String role, Boolean ativo) {
    public static UsuarioResponseDTO from(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getUuid(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole().name(),
                usuario.getAtivo()
        );
    }
}
