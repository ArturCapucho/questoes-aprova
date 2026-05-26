package com.portfolio.questoes_aprova.service.impl;

import com.portfolio.questoes_aprova.dto.QuestaoRequestDTO;
import com.portfolio.questoes_aprova.dto.QuestaoResponseDTO;
import com.portfolio.questoes_aprova.entity.Alternativa;
import com.portfolio.questoes_aprova.entity.Categoria;
import com.portfolio.questoes_aprova.entity.Questao;
import com.portfolio.questoes_aprova.exception.BusinessException;
import com.portfolio.questoes_aprova.exception.ResourceNotFoundException;
import com.portfolio.questoes_aprova.repository.CategoriaRepository;
import com.portfolio.questoes_aprova.repository.QuestaoRepository;
import com.portfolio.questoes_aprova.service.QuestaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestaoServiceImpl implements QuestaoService {

    private final QuestaoRepository questaoRepository;
    private final CategoriaRepository categoriaRepository;

    @Override
    @Transactional
    public QuestaoResponseDTO criar(QuestaoRequestDTO dto) {
        long corretas = dto.alternativas().stream().filter(item -> Boolean.TRUE.equals(item.correta())).count();
        if (corretas != 1) {
            throw new BusinessException("Questao deve ter exatamente uma alternativa correta.");
        }

        Categoria categoria = categoriaRepository.findById(dto.categoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria nao encontrada."));

        Questao questao = new Questao();
        questao.setAno(dto.ano());
        questao.setEnunciado(dto.enunciado());
        questao.setBanca(dto.banca());
        questao.setOrgao(dto.orgao());
        questao.setOrigem(dto.origem() == null || dto.origem().isBlank() ? "MANUAL" : dto.origem());
        questao.setCategoria(categoria);

        List<Alternativa> alternativas = dto.alternativas().stream().map(item -> {
            Alternativa alternativa = new Alternativa();
            alternativa.setQuestao(questao);
            alternativa.setLetra(Character.toUpperCase(item.letra()));
            alternativa.setTexto(item.texto());
            alternativa.setCorreta(Boolean.TRUE.equals(item.correta()));
            return alternativa;
        }).toList();
        questao.setAlternativas(alternativas);

        return QuestaoResponseDTO.from(questaoRepository.save(questao));
    }

    @Override
    @Transactional(readOnly = true)
    public QuestaoResponseDTO buscar(Long id, Integer ano) {
        return QuestaoResponseDTO.from(questaoRepository.findById(new Questao.QuestaoId(id, ano))
                .orElseThrow(() -> new ResourceNotFoundException("Questao nao encontrada.")));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuestaoResponseDTO> listar(Pageable pageable) {
        return questaoRepository.findAll(pageable).map(QuestaoResponseDTO::from);
    }
}
