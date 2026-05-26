package com.portfolio.questoes_aprova.service;

import com.portfolio.questoes_aprova.dto.EditalMapeamentoDTO;

public interface EditalCrawlerAgent {
    EditalMapeamentoDTO mapearCategorias(String textoEdital);
}
