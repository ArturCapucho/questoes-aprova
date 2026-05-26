package com.portfolio.questoes_aprova.service.impl;

import com.portfolio.questoes_aprova.dto.TentativaRequestDTO;
import com.portfolio.questoes_aprova.dto.TentativaResponseDTO;
import com.portfolio.questoes_aprova.entity.Alternativa;
import com.portfolio.questoes_aprova.entity.Questao;
import com.portfolio.questoes_aprova.entity.Tentativa;
import com.portfolio.questoes_aprova.entity.Usuario;
import com.portfolio.questoes_aprova.exception.BusinessException;
import com.portfolio.questoes_aprova.exception.ResourceNotFoundException;
import com.portfolio.questoes_aprova.repository.AlternativaRepository;
import com.portfolio.questoes_aprova.repository.QuestaoRepository;
import com.portfolio.questoes_aprova.repository.TentativaRepository;
import com.portfolio.questoes_aprova.repository.UsuarioRepository;
import com.portfolio.questoes_aprova.service.IAExplanationService;
import com.portfolio.questoes_aprova.service.TentativaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TentativaServiceImpl implements TentativaService {

    private final TentativaRepository tentativaRepository;
    private final UsuarioRepository usuarioRepository;
    private final QuestaoRepository questaoRepository;
    private final AlternativaRepository alternativaRepository;
    private final IAExplanationService iaExplanationService;

    @Override
    @Transactional
    public TentativaResponseDTO responder(TentativaRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado."));
        Questao questao = questaoRepository.findById(new Questao.QuestaoId(dto.questaoId(), dto.questaoAno()))
                .orElseThrow(() -> new ResourceNotFoundException("Questao nao encontrada."));
        Alternativa alternativa = alternativaRepository.findById(dto.alternativaEscolhidaId())
                .orElseThrow(() -> new ResourceNotFoundException("Alternativa nao encontrada."));

        if (!alternativa.getQuestao().getId().equals(questao.getId()) || !alternativa.getQuestao().getAno().equals(questao.getAno())) {
            throw new BusinessException("Alternativa nao pertence a questao informada.");
        }

        Tentativa tentativa = new Tentativa();
        tentativa.setUsuario(usuario);
        tentativa.setQuestao(questao);
        tentativa.setAlternativaEscolhida(alternativa);
        tentativa.setCorreta(Boolean.TRUE.equals(alternativa.getCorreta()));
        tentativa.setExplicacaoIa(iaExplanationService.gerarExplicacao(questao, alternativa));

        return TentativaResponseDTO.from(tentativaRepository.save(tentativa));
    }
}
