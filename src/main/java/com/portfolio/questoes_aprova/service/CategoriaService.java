package com.portfolio.questoes_aprova.service;

import com.portfolio.questoes_aprova.dto.CategoriaRequestDTO;
import com.portfolio.questoes_aprova.dto.CategoriaResponseDTO;

import java.util.List;

public interface CategoriaService {
    CategoriaResponseDTO criar(CategoriaRequestDTO dto);

    List<CategoriaResponseDTO> listar();
}
