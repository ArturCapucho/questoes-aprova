package com.portfolio.questoes_aprova.controller;

import com.portfolio.questoes_aprova.dto.AuthResponseDTO;
import com.portfolio.questoes_aprova.dto.LoginRequestDTO;
import com.portfolio.questoes_aprova.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public AuthResponseDTO login(@Valid @RequestBody LoginRequestDTO dto) {
        return authService.autenticar(dto);
    }
}
