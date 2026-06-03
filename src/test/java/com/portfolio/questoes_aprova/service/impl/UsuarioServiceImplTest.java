package com.portfolio.questoes_aprova.service.impl;

import com.portfolio.questoes_aprova.dto.UsuarioRequestDTO;
import com.portfolio.questoes_aprova.entity.Usuario;
import com.portfolio.questoes_aprova.exception.BusinessException;
import com.portfolio.questoes_aprova.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    @Test
    void criarDeveCriptografarSenhaEUsarRoleAlunoQuandoRoleNaoForInformada() {
        UsuarioRequestDTO dto = new UsuarioRequestDTO("Artur", "artur@email.com", "123456", null);
        when(usuarioRepository.existsByEmail(dto.email())).thenReturn(false);
        when(passwordEncoder.encode(dto.senha())).thenReturn("senha-criptografada");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario usuario = invocation.getArgument(0);
            usuario.setId(1L);
            return usuario;
        });

        var response = usuarioService.criar(dto);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        Usuario usuarioSalvo = captor.getValue();

        assertThat(usuarioSalvo.getNome()).isEqualTo("Artur");
        assertThat(usuarioSalvo.getEmail()).isEqualTo("artur@email.com");
        assertThat(usuarioSalvo.getSenhaHash()).isEqualTo("senha-criptografada");
        assertThat(usuarioSalvo.getRole()).isEqualTo(Usuario.Role.ALUNO);
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.email()).isEqualTo("artur@email.com");
    }

    @Test
    void criarDeveBloquearEmailDuplicado() {
        UsuarioRequestDTO dto = new UsuarioRequestDTO("Artur", "artur@email.com", "123456", "ALUNO");
        when(usuarioRepository.existsByEmail(dto.email())).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.criar(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Ja existe usuario com este e-mail.");

        verify(usuarioRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void criarDeveBloquearRoleInvalida() {
        UsuarioRequestDTO dto = new UsuarioRequestDTO("Artur", "artur@email.com", "123456", "MASTER");
        when(usuarioRepository.existsByEmail(dto.email())).thenReturn(false);
        when(passwordEncoder.encode(dto.senha())).thenReturn("senha-criptografada");

        assertThatThrownBy(() -> usuarioService.criar(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Role invalida. Use ALUNO, PROFESSOR ou ADMIN.");

        verify(usuarioRepository, never()).save(any());
    }
}
