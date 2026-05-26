package com.portfolio.questoes_aprova.controller;

import com.portfolio.questoes_aprova.dto.TentativaRequestDTO;
import com.portfolio.questoes_aprova.dto.TentativaResponseDTO;
import com.portfolio.questoes_aprova.service.TentativaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tentativas")
@RequiredArgsConstructor
public class TentativaController {

    private final TentativaService tentativaService;

    @PostMapping
    public TentativaResponseDTO responder(@Valid @RequestBody TentativaRequestDTO dto) {
        return tentativaService.responder(dto);
    }
}
