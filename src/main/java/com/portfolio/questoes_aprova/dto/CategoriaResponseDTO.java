package com.portfolio.questoes_aprova.dto;

import com.portfolio.questoes_aprova.entity.Categoria;

public record CategoriaResponseDTO(Long id, String nome, String slug, String descricao, Long categoriaPaiId) {
    public static CategoriaResponseDTO from(Categoria categoria) {
        Long paiId = categoria.getCategoriaPai() == null ? null : categoria.getCategoriaPai().getId();
        return new CategoriaResponseDTO(categoria.getId(), categoria.getNome(), categoria.getSlug(), categoria.getDescricao(), paiId);
    }
}
