package com.portfolio.questoes_aprova.service;

import com.portfolio.questoes_aprova.entity.Alternativa;
import com.portfolio.questoes_aprova.entity.Questao;

// Strategy Pattern: outras estrategias de IA podem substituir esta interface sem mudar TentativaService.
public interface IAExplanationService {
    String gerarExplicacao(Questao questao, Alternativa alternativaEscolhida);
}
