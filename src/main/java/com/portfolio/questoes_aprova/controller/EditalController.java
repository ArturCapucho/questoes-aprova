package com.portfolio.questoes_aprova.controller;

import com.portfolio.questoes_aprova.dto.EditalMapeamentoDTO;
import com.portfolio.questoes_aprova.service.EditalCrawlerAgent;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/editais")
@RequiredArgsConstructor
public class EditalController {

    private final EditalCrawlerAgent editalCrawlerAgent;

    @PostMapping("/mapear-categorias")
    public EditalMapeamentoDTO mapear(@RequestBody String textoEdital) {
        return editalCrawlerAgent.mapearCategorias(textoEdital);
    }
}
