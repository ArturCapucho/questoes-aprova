package com.portfolio.questoes_aprova.service.impl;

import com.portfolio.questoes_aprova.dto.EditalMapeamentoDTO;
import com.portfolio.questoes_aprova.repository.CategoriaRepository;
import com.portfolio.questoes_aprova.service.EditalCrawlerAgent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EditalCrawlerAgentImpl implements EditalCrawlerAgent {

    private final CategoriaRepository categoriaRepository;

    @Override
    public EditalMapeamentoDTO mapearCategorias(String textoEdital) {
        String textoNormalizado = textoEdital == null ? "" : textoEdital.toLowerCase();
        List<String> encontradas = new ArrayList<>();

        categoriaRepository.findAll().forEach(categoria -> {
            if (textoNormalizado.contains(categoria.getNome().toLowerCase()) || textoNormalizado.contains(categoria.getSlug().toLowerCase())) {
                encontradas.add(categoria.getNome());
            }
        });

        return new EditalMapeamentoDTO(encontradas, List.of());
    }
}
