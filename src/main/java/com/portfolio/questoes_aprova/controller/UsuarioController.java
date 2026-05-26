package com.portfolio.questoes_aprova.controller;

import com.portfolio.questoes_aprova.dto.UsuarioRequestDTO;
import com.portfolio.questoes_aprova.dto.UsuarioResponseDTO;
import com.portfolio.questoes_aprova.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    public UsuarioResponseDTO criar(@Valid @RequestBody UsuarioRequestDTO dto) {
        return usuarioService.criar(dto);
    }

    @GetMapping
    public List<UsuarioResponseDTO> listar() {
        return usuarioService.listar();
    }
}
