package com.portfolio.questoes_aprova.service.impl;

import com.portfolio.questoes_aprova.dto.AlternativaRequestDTO;
import com.portfolio.questoes_aprova.dto.QuestaoRequestDTO;
import com.portfolio.questoes_aprova.entity.Categoria;
import com.portfolio.questoes_aprova.entity.Questao;
import com.portfolio.questoes_aprova.exception.BusinessException;
import com.portfolio.questoes_aprova.exception.ResourceNotFoundException;
import com.portfolio.questoes_aprova.repository.CategoriaRepository;
import com.portfolio.questoes_aprova.repository.QuestaoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuestaoServiceImplTest {

    @Mock
    private QuestaoRepository questaoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private QuestaoServiceImpl questaoService;

    @Test
    void criarDeveMontarQuestaoComAlternativasECategoria() {
        Categoria categoria = categoria();
        QuestaoRequestDTO dto = questaoRequest(List.of(
                new AlternativaRequestDTO('a', "Certo", true),
                new AlternativaRequestDTO('b', "Errado", false)
        ));

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(questaoRepository.save(any(Questao.class))).thenAnswer(invocation -> {
            Questao questao = invocation.getArgument(0);
            questao.setId(10L);
            return questao;
        });

        var response = questaoService.criar(dto);

        ArgumentCaptor<Questao> captor = ArgumentCaptor.forClass(Questao.class);
        verify(questaoRepository).save(captor.capture());
        Questao questaoSalva = captor.getValue();

        assertThat(questaoSalva.getCategoria()).isEqualTo(categoria);
        assertThat(questaoSalva.getOrigem()).isEqualTo("MANUAL");
        assertThat(questaoSalva.getAlternativas()).hasSize(2);
        assertThat(questaoSalva.getAlternativas().getFirst().getLetra()).isEqualTo('A');
        assertThat(questaoSalva.getAlternativas().getFirst().getQuestao()).isSameAs(questaoSalva);
        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.alternativas()).hasSize(2);
    }

    @Test
    void criarDeveExigirExatamenteUmaAlternativaCorreta() {
        QuestaoRequestDTO dto = questaoRequest(List.of(
                new AlternativaRequestDTO('A', "Certo", true),
                new AlternativaRequestDTO('B', "Tambem certo", true)
        ));

        assertThatThrownBy(() -> questaoService.criar(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Questao deve ter exatamente uma alternativa correta.");

        verify(categoriaRepository, never()).findById(any());
        verify(questaoRepository, never()).save(any());
    }

    @Test
    void criarDeveFalharQuandoCategoriaNaoExistir() {
        QuestaoRequestDTO dto = questaoRequest(List.of(
                new AlternativaRequestDTO('A', "Certo", true),
                new AlternativaRequestDTO('B', "Errado", false)
        ));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> questaoService.criar(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Categoria nao encontrada.");

        verify(questaoRepository, never()).save(any());
    }

    private QuestaoRequestDTO questaoRequest(List<AlternativaRequestDTO> alternativas) {
        return new QuestaoRequestDTO(
                2026,
                "Enunciado da questao",
                "Banca",
                "Orgao",
                null,
                1L,
                alternativas
        );
    }

    private Categoria categoria() {
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("Direito Constitucional");
        categoria.setSlug("direito-constitucional");
        return categoria;
    }
}
