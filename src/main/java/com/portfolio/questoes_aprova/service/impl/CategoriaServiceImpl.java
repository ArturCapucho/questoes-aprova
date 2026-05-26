package com.portfolio.questoes_aprova.service.impl;

import com.portfolio.questoes_aprova.dto.CategoriaRequestDTO;
import com.portfolio.questoes_aprova.dto.CategoriaResponseDTO;
import com.portfolio.questoes_aprova.entity.Categoria;
import com.portfolio.questoes_aprova.exception.BusinessException;
import com.portfolio.questoes_aprova.exception.ResourceNotFoundException;
import com.portfolio.questoes_aprova.repository.CategoriaRepository;
import com.portfolio.questoes_aprova.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;

    @Override
    public CategoriaResponseDTO criar(CategoriaRequestDTO dto) {
        if (categoriaRepository.existsBySlug(dto.slug())) {
            throw new BusinessException("Ja existe categoria com este slug.");
        }

        Categoria categoria = new Categoria();
        categoria.setNome(dto.nome());
        categoria.setSlug(dto.slug());
        categoria.setDescricao(dto.descricao());
        if (dto.categoriaPaiId() != null) {
            categoria.setCategoriaPai(categoriaRepository.findById(dto.categoriaPaiId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoria pai nao encontrada.")));
        }

        return CategoriaResponseDTO.from(categoriaRepository.save(categoria));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> listar() {
        return categoriaRepository.findAll().stream().map(CategoriaResponseDTO::from).toList();
    }
}
