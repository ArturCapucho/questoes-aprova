package com.portfolio.questoes_aprova.service.impl;

import com.portfolio.questoes_aprova.dto.TentativaRequestDTO;
import com.portfolio.questoes_aprova.entity.Alternativa;
import com.portfolio.questoes_aprova.entity.Categoria;
import com.portfolio.questoes_aprova.entity.Questao;
import com.portfolio.questoes_aprova.entity.Tentativa;
import com.portfolio.questoes_aprova.entity.Usuario;
import com.portfolio.questoes_aprova.exception.BusinessException;
import com.portfolio.questoes_aprova.repository.AlternativaRepository;
import com.portfolio.questoes_aprova.repository.QuestaoRepository;
import com.portfolio.questoes_aprova.repository.TentativaRepository;
import com.portfolio.questoes_aprova.repository.UsuarioRepository;
import com.portfolio.questoes_aprova.service.IAExplanationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TentativaServiceImplTest {

    @Mock
    private TentativaRepository tentativaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private QuestaoRepository questaoRepository;

    @Mock
    private AlternativaRepository alternativaRepository;

    @Mock
    private IAExplanationService iaExplanationService;

    @InjectMocks
    private TentativaServiceImpl tentativaService;

    @Test
    void responderDeveUsarUsuarioAutenticadoERegistrarTentativa() {
        Usuario usuario = usuario();
        Questao questao = questao(10L, 2026);
        Alternativa alternativa = alternativa(5L, questao, true);
        TentativaRequestDTO dto = new TentativaRequestDTO(10L, 2026, 5L);

        when(usuarioRepository.findByEmail("artur@email.com")).thenReturn(Optional.of(usuario));
        when(questaoRepository.findById(new Questao.QuestaoId(10L, 2026))).thenReturn(Optional.of(questao));
        when(alternativaRepository.findById(5L)).thenReturn(Optional.of(alternativa));
        when(iaExplanationService.gerarExplicacao(questao, alternativa)).thenReturn("Explicacao gerada.");
        when(tentativaRepository.save(any(Tentativa.class))).thenAnswer(invocation -> {
            Tentativa tentativa = invocation.getArgument(0);
            tentativa.setId(99L);
            return tentativa;
        });

        var response = tentativaService.responder("artur@email.com", dto);

        ArgumentCaptor<Tentativa> captor = ArgumentCaptor.forClass(Tentativa.class);
        verify(tentativaRepository).save(captor.capture());
        Tentativa tentativaSalva = captor.getValue();

        assertThat(tentativaSalva.getUsuario()).isEqualTo(usuario);
        assertThat(tentativaSalva.getQuestao()).isEqualTo(questao);
        assertThat(tentativaSalva.getAlternativaEscolhida()).isEqualTo(alternativa);
        assertThat(tentativaSalva.getCorreta()).isTrue();
        assertThat(tentativaSalva.getExplicacaoIa()).isEqualTo("Explicacao gerada.");
        assertThat(response.id()).isEqualTo(99L);
        assertThat(response.usuarioId()).isEqualTo(1L);
    }

    @Test
    void responderDeveBloquearAlternativaDeOutraQuestao() {
        Usuario usuario = usuario();
        Questao questao = questao(10L, 2026);
        Questao outraQuestao = questao(11L, 2026);
        Alternativa alternativa = alternativa(5L, outraQuestao, true);
        TentativaRequestDTO dto = new TentativaRequestDTO(10L, 2026, 5L);

        when(usuarioRepository.findByEmail("artur@email.com")).thenReturn(Optional.of(usuario));
        when(questaoRepository.findById(new Questao.QuestaoId(10L, 2026))).thenReturn(Optional.of(questao));
        when(alternativaRepository.findById(5L)).thenReturn(Optional.of(alternativa));

        assertThatThrownBy(() -> tentativaService.responder("artur@email.com", dto))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Alternativa nao pertence a questao informada.");

        verify(tentativaRepository, never()).save(any());
        verify(iaExplanationService, never()).gerarExplicacao(any(), any());
    }

    @Test
    void buscarDesempenhoDeveCalcularResumoDoAluno() {
        Usuario usuario = usuario();
        Questao questao = questao(10L, 2026);
        Alternativa alternativa = alternativa(5L, questao, true);

        when(usuarioRepository.findByEmail("artur@email.com")).thenReturn(Optional.of(usuario));
        when(tentativaRepository.findByUsuarioIdOrderByRespondidoEmDesc(1L)).thenReturn(List.of(
                tentativa(1L, usuario, questao, alternativa, true),
                tentativa(2L, usuario, questao, alternativa, false),
                tentativa(3L, usuario, questao, alternativa, true)
        ));

        var desempenho = tentativaService.buscarDesempenho("artur@email.com");

        assertThat(desempenho.totalTentativas()).isEqualTo(3);
        assertThat(desempenho.totalAcertos()).isEqualTo(2);
        assertThat(desempenho.totalErros()).isEqualTo(1);
        assertThat(desempenho.percentualAcerto()).isEqualTo(66.67);
        assertThat(desempenho.ultimasTentativas()).hasSize(3);
    }

    private Usuario usuario() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("artur@email.com");
        usuario.setNome("Artur");
        usuario.setSenhaHash("hash");
        return usuario;
    }

    private Questao questao(Long id, Integer ano) {
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("Direito Constitucional");
        categoria.setSlug("direito-constitucional");

        Questao questao = new Questao();
        questao.setId(id);
        questao.setAno(ano);
        questao.setEnunciado("Enunciado");
        questao.setCategoria(categoria);
        return questao;
    }

    private Alternativa alternativa(Long id, Questao questao, boolean correta) {
        Alternativa alternativa = new Alternativa();
        alternativa.setId(id);
        alternativa.setQuestao(questao);
        alternativa.setLetra('A');
        alternativa.setTexto("Certo");
        alternativa.setCorreta(correta);
        return alternativa;
    }

    private Tentativa tentativa(Long id, Usuario usuario, Questao questao, Alternativa alternativa, boolean correta) {
        Tentativa tentativa = new Tentativa();
        tentativa.setId(id);
        tentativa.setUsuario(usuario);
        tentativa.setQuestao(questao);
        tentativa.setAlternativaEscolhida(alternativa);
        tentativa.setCorreta(correta);
        tentativa.setExplicacaoIa("Explicacao");
        tentativa.setRespondidoEm(OffsetDateTime.now());
        return tentativa;
    }
}
