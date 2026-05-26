package com.portfolio.questoes_aprova.controller;

import com.portfolio.questoes_aprova.dto.QuestaoRequestDTO;
import com.portfolio.questoes_aprova.dto.QuestaoResponseDTO;
import com.portfolio.questoes_aprova.service.QuestaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/questoes")
@RequiredArgsConstructor
public class QuestaoController {

    private final QuestaoService questaoService;

    @PostMapping
    public QuestaoResponseDTO criar(@Valid @RequestBody QuestaoRequestDTO dto) {
        return questaoService.criar(dto);
    }

    @GetMapping("/{ano}/{id}")
    public QuestaoResponseDTO buscar(@PathVariable Integer ano, @PathVariable Long id) {
        return questaoService.buscar(id, ano);
    }

    @GetMapping
    public Page<QuestaoResponseDTO> listar(@PageableDefault(size = 20, sort = "ano") Pageable pageable) {
        return questaoService.listar(pageable);
    }
}
