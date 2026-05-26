package com.portfolio.questoes_aprova.controller;

import com.portfolio.questoes_aprova.dto.CategoriaRequestDTO;
import com.portfolio.questoes_aprova.dto.CategoriaResponseDTO;
import com.portfolio.questoes_aprova.service.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    @PostMapping
    public CategoriaResponseDTO criar(@Valid @RequestBody CategoriaRequestDTO dto) {
        return categoriaService.criar(dto);
    }

    @GetMapping
    public List<CategoriaResponseDTO> listar() {
        return categoriaService.listar();
    }
}
